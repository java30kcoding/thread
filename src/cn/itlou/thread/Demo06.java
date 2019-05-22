package cn.itlou.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * 线程1去买包子，没包子则不执行。
 * 线程2生产包子，通知线程1继续执行。
 * 三种线程通信方式：suspend/resume, wait/notify, park/unpark
 */
public class Demo06 {

    //包子店
    public static Object baozidian = null;

    /**
     * 正常的suspend/resume
     * @throws Exception
     */
    public void suspendResumeTest() throws Exception{
        //启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null){
                System.out.println("1.没有包子，进入等待");
                Thread.currentThread().suspend();
            }
            System.out.println("2.买到包子，回家");
        });
        consumerThread.start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        consumerThread.resume();
        System.out.println("3.通知消费者");
    }

    /**
     * suspend不会释放锁，容易写出死锁代码
     * @throws Exception
     */
    public void suspendResumeDeadLockTest1() throws Exception{
        //启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null){
                System.out.println("1.没有包子，进入等待");
                //当线程拿到锁，调用suspend挂起
                synchronized (this){
                    Thread.currentThread().suspend();
                }
            }
            System.out.println("2.买到包子，回家");
        });
        consumerThread.start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        //此时consumerThread拿不到锁永远无法执行，因为锁呗suspend拿走了
        synchronized (this){
            consumerThread.resume();
        }
        System.out.println("3.通知消费者");
    }

    /**
     * 先执行suspend，再执行resume，否则会死锁
     * @throws Exception
     */
    public void suspendResumeDeadLockTest2() throws Exception{
        //启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null){
                System.out.println("1.没有包子，进入等待");
                try {
                    //先休眠5秒
                    Thread.sleep(5000L);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                Thread.currentThread().suspend();
            }
            System.out.println("2.买到包子，回家");
        });
        consumerThread.start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        //由于线程先休眠了5秒，resume先执行，所以会发生死锁
        consumerThread.resume();
        System.out.println("3.通知消费者");
    }

    /**
     * 正常的wait/notify
     * @throws Exception
     */
    public void waitNotifyTest() throws Exception{
        //启动线程
        new Thread(() -> {
            if (baozidian == null){
                synchronized (this){
                    try {
                        System.out.println("1.没有包子，进入等待");
                        this.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("2.买到包子，回家");
        }).start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        synchronized (this){
            this.notify();//this.notifyAll();
            System.out.println("3.通知消费者");
        }
    }

    /**
     * 导致死锁的wait/notify
     * @throws Exception
     */
    public void waitNotifyLockTest() throws Exception{
        //启动线程
        new Thread(() -> {
            if (baozidian == null){
                try {
                    //让notify先执行
                    Thread.sleep(5000L);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                synchronized (this){
                    try {
                        System.out.println("1.进入等待");
                        this.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2.买到包子，回家");
        }).start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        synchronized (this){
            this.notify();//this.notifyAll();
            System.out.println("3.通知消费者");
        }
    }

    /**
     * 正常的park/unpark
     * @throws Exception
     */
    public void parkUnparkTest() throws Exception{
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null){
                System.out.println("1.进入等待");
                LockSupport.park();
            }
            System.out.println("2.买包子回家");
        });
        consumerThread.start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        LockSupport.unpark(consumerThread);
        System.out.println("3.通知消费者");
    }

    /**
     * 死锁的park/unpark
     * @throws Exception
     */
    public void parkUnparkDeadLockTest() throws Exception{
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null){
                System.out.println("1.进入等待");
                //拿到锁挂起
                synchronized (this){
                    LockSupport.park();
                }
            }
            System.out.println("2.买包子回家");
        });
        consumerThread.start();
        //3秒后生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        //因为park/unpark不会释放锁，所以不会执行
        synchronized (this){
            LockSupport.unpark(consumerThread);
        }
        System.out.println("3.通知消费者");
    }

    public static void main(String[] args) throws Exception {
//        new Demo06().suspendResumeTest();
//        new Demo06().suspendResumeDeadLockTest1();
//        new Demo06().suspendResumeDeadLockTest2();
//        new Demo06().waitNotifyTest();
//        new Demo06().waitNotifyLockTest();
//        new Demo06().parkUnparkTest();
        new Demo06().parkUnparkDeadLockTest();
    }


}
