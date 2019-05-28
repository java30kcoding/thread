package cn.itlou.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 */
public class LockDemo02 {

    volatile int i = 0;

    Lock lock = new ReentrantLock();

    public void add(){

        lock.lock();
        try {
            i++;
        }finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) throws Exception {
        LockDemo01 lockDemo01 = new LockDemo01();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lockDemo01.add();
                }
            }).start();
        }
        Thread.sleep(2000L);
        System.out.println(lockDemo01.i);
    }

}
