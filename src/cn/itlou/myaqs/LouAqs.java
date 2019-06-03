package cn.itlou.myaqs;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * 抽象队列同步器
 * state, owner, waiters
 */
public abstract class LouAqs {

    //如何判断一个锁的状态或拥有者
    volatile AtomicReference<Thread> owner = new AtomicReference<>();

    //保存正在等待的线程
    volatile LinkedBlockingDeque<Thread> waiters = new LinkedBlockingDeque<>();

    //state资源状态
    volatile AtomicInteger state = new AtomicInteger(0);

    //tryAcquire、tryAcquireShared：实际执行占用资源的操作
    public boolean tryAcquire(){//交给使用者去实现
        throw new UnsupportedOperationException();
    }

    //acquire、acquireShared：定义了资源争用的逻辑，如果没拿到，则等待
    public void acquire(){
        boolean addQueue = true;
        while (!tryAcquire()){
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

    //release、releaseShared：定义释放资源的逻辑，释放之后，通知后续节点进行争抢
    public void release(){//定义了释放资源后要做的操作
        if (tryRelease()){
            //通知等待者
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()){
                Thread next = iterator.next();
                //唤醒
                LockSupport.unpark(next);
            }
        }
    }

    public boolean tryRelease(){
        throw new UnsupportedOperationException();
    }

}
