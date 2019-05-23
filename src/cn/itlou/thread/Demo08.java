package cn.itlou.thread;

import sun.misc.InnocuousThread;

import java.util.concurrent.*;

/**
 * 线程池的用法
 */
public class Demo08 {

    /**
     * 1.线程池信息：核心线程数5，最大数量10，无界队列，超出核心线程数量的线程存活时间：5秒，指定拒绝策略的
     */
    private void threadPoolExecutorTest1() throws Exception{
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        testCommon(threadPoolExecutor);
    }

    /**
     * 2.线程池信息：核心线程数量5，最大数量10，队列大小3，超出核心线程数量的线程存活时间：5秒，指定拒绝策略的
     */
    public void threadPoolExecutorTest2() throws Exception{
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.err.println("有任务被拒绝了");
            }
        });
        testCommon(threadPoolExecutor);
    }

    /**
     * 3.线程池信息：核心线程数量5，最大数量5，无界队列，超出核心线程数量的线程存活时间：0秒，指定拒绝策略的
     */
    public void threadPoolExecutorTest3() throws Exception{
        //和Executors.newFixedThreadPool(int nThreads)是一样的
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        testCommon(threadPoolExecutor);
    }

    /**
     * 4.线程池信息：核心线程数量0，最大数量Integer.MAX_VALUE，SynchronousQueue队列，超出核心线程数量的线程存活时间：60秒
     */
    public void threadPoolExecutorTest4() throws Exception{
        //SynchronousQueue实际上不是一个真正的队列，因为它不会为队列中的元素维护存储空间，与其他队列不同的是
        //它维护一组线程，这些线程在等待着把元素加入或移出队列
        //使用该队列作为线程池的队列，客户端向线程池提交任务时
        //线程池没有空闲的线程能够从SynchronousQueue队列实例中取出一个任务
        //那么相应的offer方法调用就会失败(即没有任务存入队列)
        //那么ThreadPoolExecutor就会创建一个新的线程对任务进行处理
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>());
        //和Executors.newCachedThreadPool一样
        testCommon(threadPoolExecutor);
    }

    /**
     * 5.定时执行线程池信息：3秒后执行，一次性任务，时间到就执行
     * 核心线程数量5，最大数量Integer.MAX_VALUE，DelayedWorkQueue延时队列，超出核心线程数存活时间：0秒
     */
    public void threadPoolExecutorTest5() throws Exception{
        //和Executors.newScheduledThreadPool()一样
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务被执行，现在时间：" + System.currentTimeMillis());
            }
        }, 3000, TimeUnit.MILLISECONDS);
        System.out.println("定时任务提交成功，时间是" + System.currentTimeMillis() + "当前线程池中线程数量" + scheduledThreadPoolExecutor.getPoolSize());
    }

    /**
     * 6.定时执行线程池信息：3秒后执行，一次性任务，时间到就执行
     * 核心线程数量5，最大数量Integer.MAX_VALUE，DelayedWorkQueue延时队列，超出核心线程数存活时间：0秒
     */
    public void threadPoolExecutorTest6() throws Exception{
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        //周期性的执行某一个任务，线程池提供了两种调度方式
        //测试场景：提交的任务需要3秒才能执行完
        //任务不会并行执行，必须等到上一次任务执行完才能执行
//        scheduledThreadPoolExecutor.scheduleAtFixedRate(() ->{
//            try {
//                Thread.sleep(3000L);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            System.out.println("任务1被执行，此时时间：" + System.currentTimeMillis());
//            //2秒后第一次执行，之后每间隔一秒执行一次
//        }, 2000, 1000, TimeUnit.MILLISECONDS);

        scheduledThreadPoolExecutor.scheduleWithFixedDelay(() ->{
            try {
                Thread.sleep(3000L);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("任务2被执行，此时时间：" + System.currentTimeMillis());
        }, 2000, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 测试：提交15个执行时间需要3秒的任务，看线程池状况
     *
     * @param threadPoolExecutor
     * @throws Exception
     */
    public void testCommon(ThreadPoolExecutor threadPoolExecutor) throws Exception{
        for (int i = 0; i < 15; i++) {
            int n = i;
            threadPoolExecutor.submit(() -> {
                try {
                    System.out.println("开始执行：" + n);
                    Thread.sleep(3000L);
                    System.err.println("执行结束：" + n);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            System.out.println("任务提交成功");
        }
        //查看线程池线程数量，查看队列等待数量
        Thread.sleep(500L);
        System.out.println("当前线程池线程数量为：" + threadPoolExecutor.getPoolSize());
        System.out.println("当前线程池等待的线程数量为：" + threadPoolExecutor.getQueue().size());
        //等待15秒查看线程数量和队列数量
        Thread.sleep(15000L);
        System.out.println("15秒后线程池线程数量为：" + threadPoolExecutor.getPoolSize());
        System.out.println("15秒后线程池等待的线程数量为：" + threadPoolExecutor.getQueue().size());
    }

    public static void main(String[] args) throws Exception {
//        new Demo08().threadPoolExecutorTest1();
//        new Demo08().threadPoolExecutorTest2();
//        new Demo08().threadPoolExecutorTest3();
//        new Demo08().threadPoolExecutorTest4();
//        new Demo08().threadPoolExecutorTest5();
        new Demo08().threadPoolExecutorTest6();
    }

}