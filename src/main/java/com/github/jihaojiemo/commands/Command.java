package com.github.jihaojiemo.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Description: 每一个命令对应到一个CommandName类型的对象
 * Author: admin
 * Create: 2019-08-02 17:13
 */
public interface  Command {

    void setArgs(List<Object> args);

    void run(OutputStream os) throws IOException;
}

