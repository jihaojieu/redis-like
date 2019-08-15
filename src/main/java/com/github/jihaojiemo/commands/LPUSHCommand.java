package com.github.jihaojiemo.commands;

import com.github.jihaojiemo.DataBase;
import com.github.jihaojiemo.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Description: lpush key value 返回插入后的长度Integer
 * Author: admin
 * Create: 2019-08-02 17:16
 */
public class LPUSHCommand implements Command {

    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        if (args.size() != 2) {
            Protocol.writeError(os, "命令至少需要两个参数");
            return;
        }
        String key = new String((byte[]) args.get(0));
        String value = new String((byte[]) args.get(1));

        List<String> list = DataBase.getList(key);
        list.add(0, value);//头插，所以index是0
        Protocol.writeInteger(os, list.size());
    }
}
