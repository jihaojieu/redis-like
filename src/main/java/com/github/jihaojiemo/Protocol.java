package com.github.jihaojiemo;

import com.github.jihaojiemo.commands.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 协议解析，字节流->对象
 * Author: admin
 * Create: 2019-08-02 11:21
 */
public class Protocol {

    //处理所有的输入，第一个字符表示的是类型
    private static Object process(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new RuntimeException("读到了末尾...");
        }
        switch (b) {
            case '+':
                return processSimpleString(is);
            case '-':
                throw new RemoteException(processError(is));
            case ':':
                return processInteger(is);
            case '$':
                return processBulkString(is);
            case '*':
                return processArray(is);
            default:
                throw new RuntimeException("不识别的类型");
        }
    }

    //SimpleString类型  "+hello\r\n"
    private static String processSimpleString(InputStream is) throws IOException {
        return readLine(is);
    }

    //Error类型  "-ERR unknown command 'put'\r\n"
    private static String processError(InputStream is) throws IOException {
        return readLine(is);
    }

    //“:” Integer类型  "100/-100"
    private static long processInteger(InputStream is) throws IOException {
        return readInteger(is);
    }

    //BulkString类型  "$5\r\nhello\r\n"
    private static byte[] processBulkString(InputStream is) throws IOException {
        int len = (int) readInteger(is);//BulkingString的长度
        if (len == -1) {
            return null;//"$-1\r\n" -> null
        }
        byte[] bytes = new byte[len];
        is.read(bytes, 0, len);
        is.read();
        is.read();
        return bytes;
    }

    //Array类型  "*5\r\n$5\r\nlpush\r\n$3\r\nkey\r\n$1\r\n1\r\n$1\r\n2\r\n$1\r\n3\r\n"
    private static List<Object> processArray(InputStream is) throws IOException {
        int len = (int) readInteger(is);
        if (len == -1) {
            return null;//"*-1\r\n" -> null
        }

        List<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            try {
                list.add(process(is));
            } catch (RemoteException e) {//对端的错误
                list.add(e);
            }
        }
        return list;
    }

    //处理SimpleString、Error相同代码
    private static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int b = is.read();
            if (b == -1) {
                throw new RuntimeException("读到了末尾...");
            }
            if (b == '\r') {
                int c = is.read();
                if (c == -1) {
                    throw new RuntimeException("读到了末尾...");
                }
                if (c == '\n') {
                    break;
                }
            }else {
                sb.append((char)b);
            }
        }
        return sb.toString();
    }

    //处理Integer
    private static long readInteger(InputStream is) throws IOException {
        boolean isNagetive = false;
        StringBuilder sb = new StringBuilder();
        //先读一个字符，看是否是负数
        int b = is.read();
        if (b == -1) {
            throw new RuntimeException("读到了末尾...");
        }
        if (b == '-') {
            isNagetive = true;//是负数
        } else {
            sb.append((char) b);
        }

        while (true) {
            b = is.read();
            if (b == -1) {
                throw new RuntimeException("读到了末尾...");
            }
            if (b == '\r') {
                int c = is.read();
                if (c == -1) {
                    throw new RuntimeException("读到了末尾...");
                }
                if (c == '\n') {
                    break;
                }
                throw new RuntimeException("没有读到内容\\r\n");
            }else {
                sb.append((char)b);
            }
        }
        if (isNagetive) {
            return -Long.parseLong(sb.toString());
        }else {
            return Long.parseLong(sb.toString());
        }
    }

    public static Command readCommand(InputStream is) throws Exception {
        Object obj1 = process(is);
        //作为服务器来说，收到的一定是list（以*开头的）
        if (!(obj1 instanceof List)) {
            throw new Exception("命令必须是Array类型");
        }
        
        List<Object> list = (List<Object>) obj1;
        if (list.size() < 1) {//list.size()=1 没有参数的命令
            throw new RuntimeException("命令元素个数必须大于1");
        }

        Object obj2 = list.remove(0);//；类似于得到lpush这样的命令
        if (!(obj2 instanceof byte[])) {//外层是list（Array），内层是byte[]（Bulk String）
            throw new RuntimeException("错误的命令类型");
        }
        
        byte[] array = (byte[]) obj2;
        String commandName = new String(array);
        String className = String.format("com.github.jihaojiemo.commands.%sCommand", commandName.toUpperCase());
        Class<?> cls = Class.forName(className);
        if (!Command.class.isAssignableFrom(cls)) {
            throw new RuntimeException("错误的命令");
        }
        //通过反射产生命令的对象
        Command command = (Command) cls.newInstance();
        command.setArgs(list);
        return command;
    }

    public static void writeError(OutputStream os, String message) throws IOException {
        os.write('-');
        os.write(message.getBytes());
        os.write("\r\n".getBytes());
    }

    public static void writeInteger(OutputStream os, Integer value) throws IOException {
        os.write(':');
        os.write(String.valueOf(value).getBytes());
        os.write("\r\n".getBytes());
    }

    public static void writeArray(OutputStream os, List<?> res) throws IOException {
        os.write('*');
        os.write(String.valueOf(res.size()).getBytes());
        os.write("\r\n".getBytes());
        for (Object obj : res) {
            if (obj instanceof String) {
                writeBulkingString(os, (String) obj);
            } else if (obj instanceof Integer) {
                writeInteger(os, (Integer) obj);
            } else if (obj instanceof Long) {
                writeInteger(os, (Integer) obj);
            } else {
                throw new RuntimeException("错误的类型");
            }
        }
    }

    public static void writeBulkingString(OutputStream os, String string) throws IOException {
        byte[] buf = string.getBytes();
        os.write('$');
        os.write(String.valueOf(buf.length).getBytes());
        os.write("\r\n".getBytes());
        os.write(buf);
        os.write("\r\n".getBytes());
    }

    public static void writeNull(OutputStream os) throws IOException {
        os.write('$');
        os.write('-');
        os.write('1');
        os.write('\r');
        os.write('\n');
    }
}
