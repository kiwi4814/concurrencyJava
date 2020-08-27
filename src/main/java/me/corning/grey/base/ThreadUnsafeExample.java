package me.corning.grey.base;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadUnsafeExample {

    private int cnt = 0;

    public synchronized void add() {
        cnt++;
    }

    public int get() {
        return cnt;
    }

    public void add10K() {
        int idx = 0;
        while (idx++ < 10000) {
            add();
        }
        System.out.println(get());
    }

    public static void main(String[] args) throws InterruptedException {
        // 测试1
        final int threadSize = 100000;
        ThreadUnsafeExample example = new ThreadUnsafeExample();
        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        ExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        for (int i = 0; i < threadSize; i++) {
            executorService.execute(() -> {
                example.add();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println(example.get());
        // 测试2
        ThreadUnsafeExample example2 = new ThreadUnsafeExample();
        new Thread(example2::add10K, "线程A").start();
        new Thread(example2::add10K, "线程B").start();
    }
}