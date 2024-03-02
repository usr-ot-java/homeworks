package com.otus.list;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MedianList <T extends Number & Comparable<T>> implements NumericList<T> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<T, Integer> map = new TreeMap<>();
    private volatile Integer size = 0;
    // Cache calculated median
    private volatile Double cachedMedian = Double.NaN;
    private volatile Boolean actualCachedMedian = true;

    /**
     * Time complexity: O(1)
     * */
    @Override
    public int size() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return size;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Time complexity: O(log N)
     * */
    @Override
    public void add(T item) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            int amount = map.getOrDefault(item, 0);
            map.put(item, amount + 1);
            size++;
            actualCachedMedian = false;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Time complexity: O(log N)
     * */
    @Override
    public void remove(T item) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            int amount = map.getOrDefault(item, 0);
            if (amount > 0) {
                map.put(item, amount - 1);
                size--;
                actualCachedMedian = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Time complexity: O(N)
     * */
    @Override
    public double getMedian() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            if (actualCachedMedian) {
                return cachedMedian;
            }

            if (size >= 0 && size % 2 == 1) {
                // Take the middle element in the list
                int target = size / 2;
                int currentOffset = -1;

                for (var entry : map.entrySet()) {
                    int entryAmount = entry.getValue();
                    currentOffset += entryAmount;
                    if (currentOffset >= target) {
                        double median = entry.getKey().doubleValue();
                        cachedMedian = median;
                        return median;
                    }
                }
            } else if (size >= 0) {
                // Take average of two elements in the middle of the list
                int targetFirst = size / 2 - 1;
                boolean foundFirst = false;
                double firstElem = 0.0;
                int targetSecond = size / 2;
                int currentOffset = -1;

                for (var entry : map.entrySet()) {
                    int entryAmount = entry.getValue();
                    currentOffset += entryAmount;
                    if (currentOffset >= targetFirst && !foundFirst) {
                        firstElem = entry.getKey().doubleValue();
                        foundFirst = true;
                    }
                    if (currentOffset >= targetSecond) {
                        double secondElem = entry.getKey().doubleValue();
                        double median = (firstElem + secondElem) / 2;
                        cachedMedian = median;
                        return median;
                    }
                }
            }

            return Double.NaN;
        } finally {
            readLock.unlock();
        }
    }

    public String toString() {
        return map.toString();
    }
}
