package cn.itlou.thread;

/**
 *
 */
public class Demo01 {

    public static Thread thread1;
    public static Demo02 obj;

    public static void main(String[] args) throws InterruptedException {
        //第一种状态->新建->运行->终止
        System.out.println("************第一种状态->新建->运行->终止***********************************");
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1当前状态：" + Thread.currentThread().getState().toString());
                System.out.println("thread1执行了！");
            }
        });
        System.out.println("没有调用start方法，thread1当前状态：" + thread1.getState().toString());
        thread1.start();
        Thread.sleep(2000L);//等待thread1执行结束
        System.out.println("thread1执行结束后的状态：" + thread1.getState().toString());
//        thread1.start();线程结束后调用会抛出IllegalThreadStateException异常
        System.out.println();

        //第二种状态->新建->运行->终止
        System.out.println("************第二种状态->新建->运行->等待->运行->终止(sleep方式)************");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{//让thread2进入等待状态
                    Thread.sleep(2000L);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("thread2当前状态：" + Thread.currentThread().getState().toString());
                System.out.println("thread2执行了！");
            }
        });
        System.out.println("没有调用start方法，thread2当前状态：" + thread2.getState().toString());
        thread2.start();
        System.out.println("调用start方法，thread2当前状态：" + thread2.getState().toString());
        Thread.sleep(200L);
        System.out.println("等待200毫秒，thread2当前状态：" + thread2.getState().toString());
        Thread.sleep(3000L);
        System.out.println("等待3秒，thread2当前状态：" + thread2.getState().toString());
        System.out.println();

        //第三种状态->新建->运行->终止
        System.out.println("************第三种状态->新建->运行->阻塞->运行->终止(sleep方式)************");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Demo01.class){
                    System.out.println("thread2当前状态：" + Thread.currentThread().getState().toString());
                    System.out.println("thread2执行了！");
                }
            }
        });
        synchronized (Demo01.class){
            System.out.println("没有调用start方法，thread3当前状态：" + thread3.getState().toString());
            thread3.start();
            System.out.println("调用start方法，thread3当前状态：" + thread3.getState().toString());
            Thread.sleep(200L);
            System.out.println("等待200毫秒，thread2当前状态：" + thread3.getState().toString());
        }
        Thread.sleep(3000L);
        System.out.println("等待3秒，thread3当前状态：" + thread3.getState().toString());
    }

}
