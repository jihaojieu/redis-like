package com.github.jihaojiemo;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 服务器主类
 * Author: admin
 * Create: 2019-08-09 11:42
 */
public class Server {

//    public void run(int port) throws IOException, IOException {
//
//        //监听客户端连接
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//
//            while (true) {
//                try (Socket socket = serverSocket.accept()) {
//                    System.out.println(socket.getInetAddress().getHostAddress() + " 已连接");
//
//                    //读取用户输入，将结果返回给用户
//                    InputStream is = socket.getInputStream();
//                    OutputStream os = socket.getOutputStream();
//
//                    while (true) {
//                        try {
//                            Command command = Protocol.readCommand(is);
//                            command.run(os);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Protocol.writeError(os, "不识别的命令...");
//                        }
//                    }
//                }
//            }
//        }
//    }

    public static void main(String[] args) throws Exception {
        //new Server().run(6379);
        ServerSocket serverSocket = new ServerSocket(6379);
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待客户端连接...");
        for (int i = 0; i < 20; i++) {
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为 " + client.getPort());
            //执行一个线程，每当有一个客户端来把他包装成一个线程来处理
            executorService.execute(new MutliThread(client));
        }
        //关闭线程池与服务端
        executorService.shutdown();
        serverSocket.close();
    }
}
