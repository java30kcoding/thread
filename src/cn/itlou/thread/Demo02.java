package cn.itlou.thread;

import cn.itlou.other.StopThread;

/**
 * stop强制中止破坏线程安全(原子性)
 */
public class Demo02 {

    public static void main(String[] args) throws InterruptedException {
        StopThread stopThread = new StopThread();
        stopThread.start();
        //休眠1秒确定i变量自增成功
        Thread.sleep(1000L);
        //暂停线程
        stopThread.stop();/* 错误的中止 */
        //stopThread.interrupt();正确的中止
        while (stopThread.isAlive()){
            //确认线程是否中止
        }
        //输出结果
        stopThread.print();
    }

}
