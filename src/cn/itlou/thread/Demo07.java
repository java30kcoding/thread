package cn.itlou.thread;

/**
 * ThreadLocal线程封闭
 */
public class Demo07 {

    public static ThreadLocal<String> value = new ThreadLocal<>();

    /**
     * threadlocal测试
     *
     */
    public void threadLcaolTest() throws Exception{

        //threadlocal线程封闭测试
        value.set("这是主线程设置的123");
        String v = value.get();
        System.out.println("线程1执行后，主线程的值" + v);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String v = value.get();
                System.out.println("线程1取到的值：" + v);
                //设置threadlocal
                value.set("这是线程1设置的456");
                v = value.get();
                System.out.println("重新设置后，线程1取到的值：" + v);
                System.out.println("线程1执行结束");
            }
        }).start();

        Thread.sleep(5000L);

        v = value.get();
        System.out.println("线程1执行后主线程的值：" + v);
    }

    public static void main(String[] args) throws Exception {
        new Demo07().threadLcaolTest();//不同线程访问得到的值是不同的，互不干扰
    }

}
