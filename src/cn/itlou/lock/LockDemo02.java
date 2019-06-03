package cn.itlou.lock;

import cn.itlou.mylock.LouLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 */
public class LockDemo02 {

    volatile int i = 0;

    Lock lock = new LouLock();
//    Lock lock = new ReentrantLock();

    public void add(){

        lock.lock();
        try {
            i++;
        }finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) throws Exception {
        LockDemo02 lockDemo02 = new LockDemo02();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lockDemo02.add();
                }
            }).start();
        }
        Thread.sleep(2000L);
        System.out.println(lockDemo02.i);
    }

}
