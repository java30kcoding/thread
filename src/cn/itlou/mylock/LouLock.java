package cn.itlou.mylock;

import cn.itlou.myaqs.LouAqs;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 自己实现一把独享锁-常用的
 */
public class LouLock implements Lock {

    LouAqs aqs = new LouAqs(){
        @Override
        public boolean tryAcquire(){
            return owner.compareAndSet(null, Thread.currentThread());
        }

        @Override
        public boolean tryRelease() {
            return owner.compareAndSet(Thread.currentThread(), null);
        }
    };

    //如何判断一个锁的状态或拥有者
    volatile AtomicReference<Thread> owner = new AtomicReference<>();

    //保存正在等待的线程
    volatile LinkedBlockingDeque<Thread> waiters = new LinkedBlockingDeque<>();

    @Override
    public boolean tryLock() {
        return aqs.tryAcquire();
    }

    @Override
    public void lock() {
        aqs.acquire();
    }

    @Override
    public void unlock() {
        aqs.release();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
