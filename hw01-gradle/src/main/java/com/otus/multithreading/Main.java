package com.otus.multithreading;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Slf4j
public class Main {

    private static final int LIMIT_LOOP = 10;

    private final CyclicBarrier cyclicBarrier1 = new CyclicBarrier(2);
    private final CyclicBarrier cyclicBarrier2 = new CyclicBarrier(2);

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }

    private void run() throws InterruptedException {
        Thread t1 = new Thread(this::counter1);
        Thread t2 = new Thread(this::counter2);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    private void counter1() {
        for (int i = 1; i <= LIMIT_LOOP; i++) {
            loop1(i);
        }
        for (int i = LIMIT_LOOP - 1; i > 0; i--) {
            loop1(i);
        }
    }

    private void loop1(int i) {
        log.info("{}", i);
        try {
            cyclicBarrier1.await();
            cyclicBarrier2.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void counter2() {
        for (int i = 1; i <= LIMIT_LOOP; i++) {
            loop2(i);
        }
        for (int i = LIMIT_LOOP - 1; i > 0; i--) {
            loop2(i);
        }
    }

    private void loop2(int i) {
        try {
            cyclicBarrier1.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        log.info("{}", i);
        try {
            cyclicBarrier2.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
