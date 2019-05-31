package cn.itlou.mylock;

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

    //如何判断一个锁的状态或拥有者
    volatile AtomicReference<Thread> owner = new AtomicReference<>();

    //保存正在等待的线程
    volatile LinkedBlockingDeque<Thread> waiters = new LinkedBlockingDeque<>();

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public void lock() {
        boolean addQueue = true;
        while (!tryLock()){
            if (addQueue){
                //没拿到锁，进入等待集合
                waiters.offer(Thread.currentThread());
                addQueue = false;
            }else {
                //阻塞 挂起当前线程，不要继续运行
                LockSupport.park();//伪唤醒，要while
            }
        }
        waiters.remove(Thread.currentThread());
    }

    @Override
    public void unlock() {
        //释放拥有者
        if (owner.compareAndSet(Thread.currentThread(), null)){//释放成功
            //通知等待者
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()){
                Thread next = iterator.next();
                //唤醒
                LockSupport.unpark(next);
            }
        }
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
