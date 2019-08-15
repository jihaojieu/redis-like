package com.github.jihaojiemo;

import com.github.jihaojiemo.commands.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description:
 * Author: admin
 * Create: 2019-08-11 19:34
 */
public class MutliThread implements Runnable {

    private Socket client;

    public MutliThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = client.getOutputStream();

                while (true) {
                    Command command = Protocol.readCommand(inputStream);
                    command.run(outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
