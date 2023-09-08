package com.otus;

import com.google.common.collect.ImmutableMap;

public class HelloOtus {
    public static void main(String[] args) {
        ImmutableMap<String, String> map = ImmutableMap.of("Hello", "Otus");
        System.out.println(map);
    }
}