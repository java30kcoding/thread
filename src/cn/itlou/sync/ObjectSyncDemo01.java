package cn.itlou.sync;

/**
 * 锁 方法(静态/非静态)，代码块(对象/类)
 */
public class ObjectSyncDemo01 {

    public synchronized /*static*/ void test1(){
        try {
            System.out.println(Thread.currentThread() + "我开始执行");
            Thread.sleep(3000L);
            System.out.println(Thread.currentThread() + "我执行结束");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        new Thread(() -> {
            new ObjectSyncDemo01().test1();
        }).start();

        Thread.sleep(1000L);
        new Thread(() -> {
            new ObjectSyncDemo01().test1();
        }).start();
    }

}
