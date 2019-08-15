package com.github.jihaojiemo;

import java.io.IOException;
import java.util.*;

/**
 * Description: redis能存储的五种数据类型
 * Author: admin
 * Create: 2019-08-02 17:16
 */
public class DataBase {

    //字符串
    private Map<String, String> strings = new HashMap<>();
    //哈希，类似java中的Map
    private static Map<String, Map<String, String>> hashes = new HashMap<>();
    //列表
    private static Map<String, List<String>> lists = new HashMap<>();
    //集合
    private Map<String, Set<String>> sets = new HashMap<>();
    //sorted Set 有序集合
    private Map<String, LinkedHashSet<String>> zsets = new HashMap<>();

    private static DataBase dataBase = new DataBase();
    public static DataBase getInstance() {
        return dataBase;
    }

    public static List<String> getList(String key) {
        List<String> list = lists.get(key);
        if (list == null) {
            list = new ArrayList<>();
            lists.put(key, list);
        }
        return list;
    }

    public static Map<String, String> getHashes(String key) throws IOException {
        Map<String, String> hash = hashes.get(key);
        if (hash == null) {
            hash = new HashMap<>();
            hashes.put(key, hash);
        }
        return hash;
    }
}
