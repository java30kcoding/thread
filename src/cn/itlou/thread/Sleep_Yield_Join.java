package cn.itlou.thread;

/**
 * Sleep_Yield_Join三个方法
 *
 * @author yuanyl
 * @date 2020/6/6 18:48
 **/
public class Sleep_Yield_Join {

    public static void main(String[] args) {
//        testSleep();
//        testYield();
        testJoin();
    }

    static void testSleep(){
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(100L);
                    System.out.println("A" + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void testYield(){
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.yield();
                    System.out.println("B" + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void testJoin(){
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.yield();
                    System.out.println("C1 " + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    //t1执行完，t2再执行
                    t1.join();
                    System.out.println("C2 " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
        t1.start();
    }

}
