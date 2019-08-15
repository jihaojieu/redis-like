package com.github.jihaojiemo;

import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * Description:
 * Author: admin
 * Create: 2019-08-11 18:16
 */
public class Test {
    public static void main(String[] args) throws IOException {
        // 测并发 启动 20 个线程，去连接 redis
        // 测响应
        // lpush, lrange, lpop
        // 循环 10000 次

        Jedis client = new Jedis();
        long b = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            client.lpush("key", "1");
        }
        long e = System.nanoTime();

        System.out.println(e-b);
    }
}
