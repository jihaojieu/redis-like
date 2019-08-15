package com.github.jihaojiemo.commands;

import com.github.jihaojiemo.DataBase;
import com.github.jihaojiemo.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Description: hget key filed 返回BulkString或者null
 * Author: admin
 * Create: 2019-08-09 15:59
 */
public class HGETCommand implements Command {

    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        String filed = new String((byte[]) args.get(1));

        Map<String, String> hash = DataBase.getHashes(key);
        String value = hash.get(filed);
        if (value != null) {
            Protocol.writeBulkingString(os, value);
        } else {
            Protocol.writeNull(os);
        }
    }
}
