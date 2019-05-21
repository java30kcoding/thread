package cn.itlou.thread;

/**
 * 使用标志位来实现线程的中止
 */
public class Demo03 {

    public volatile static boolean flag = true;

    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
           try {
               while (flag){
                   System.out.println("运行中！！！");
                   Thread.sleep(1000L);
               }
           }catch (InterruptedException e){
               e.printStackTrace();
           }
        }).start();

        Thread.sleep(3000L);
        flag = false;
        System.out.println("程序运行结束！");

    }

}
