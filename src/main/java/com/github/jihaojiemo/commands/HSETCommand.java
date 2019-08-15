package com.github.jihaojiemo.commands;

import com.github.jihaojiemo.DataBase;
import com.github.jihaojiemo.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Description: hset key filed value 返回0表示更新，返回1表示插入
 * Author: admin
 * Create: 2019-08-09 15:14
 */
public class HSETCommand implements Command {

    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        String filed = new String((byte[]) args.get(1));
        String value = new String((byte[]) args.get(2));

        Map<String, String> hash = DataBase.getHashes(key);
        boolean isUpdate = hash.containsKey(filed);
        hash.put(filed, value);
        if (isUpdate) {
            Protocol.writeInteger(os, 0);//更新
        }else {
            Protocol.writeInteger(os, 1);//插入
        }
    }
}
