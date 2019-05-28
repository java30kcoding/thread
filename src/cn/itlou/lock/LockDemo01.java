package cn.itlou.lock;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 两个线程对i变量进行递增操作
 */
public class LockDemo01 {

    volatile int i = 0;//volatile也不行，应该使用原子递增

    public void add(){
        i++;//三个步骤 javap -p -v
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
