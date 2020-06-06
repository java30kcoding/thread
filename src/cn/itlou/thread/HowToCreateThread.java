package cn.itlou.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 如何创建线程
 *
 * @author yuanyl
 * @date 2020/6/6 18:37
 **/
public class HowToCreateThread {

    static class MyThread extends Thread {
        @Override
        public void run(){
            System.out.println("MyThread run!");
        }
    }

    static class MyRun implements Runnable {
        @Override
        public void run(){
            System.out.println("MyRun run!");
        }
    }

    public static void main(String[] args) {
//        Executors.newCachedThreadPool().submit();
        new MyThread().start();
        new Thread(new MyRun()).start();
        new Thread(() -> {
            System.out.println("Lambda run!");
        }).start();
    }

}
