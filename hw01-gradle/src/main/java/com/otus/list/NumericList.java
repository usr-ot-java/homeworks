package com.otus.list;

public interface NumericList<T extends Number> {
    int size();
    void add(T item);
    void remove(T item);
    double getMedian();
}
