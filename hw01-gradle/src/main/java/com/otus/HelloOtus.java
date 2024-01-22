package com.otus;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;

public class HelloOtus {
    public static void main(String[] args) {
        ImmutableMap<String, String> map = ImmutableMap.of("Hello", "Otus");
        System.out.println(map);
        ArrayList<Animal> animals = new ArrayList<>();
        ArrayList<Cat> cats = new ArrayList<>();
        Collections.<Cat>copy(animals, cats);
    }
}