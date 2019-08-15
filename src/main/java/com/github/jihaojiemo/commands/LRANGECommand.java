package com.github.jihaojiemo.commands;

import com.github.jihaojiemo.DataBase;
import com.github.jihaojiemo.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Description: lrange key start end 返回数组array
 * Author: admin
 * Create: 2019-08-02 17:13
 */
public class LRANGECommand implements Command {

    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        int start = Integer.parseInt(new String((byte[]) args.get(1)));
        int end = Integer.parseInt(new String((byte[]) args.get(2)));

        List<String> list = DataBase.getList(key);
        if (end < 0) {
            end = list.size() + end;
        }
        List<String> res = list.subList(start, end + 1);
        Protocol.writeArray(os, res);
    }
}
