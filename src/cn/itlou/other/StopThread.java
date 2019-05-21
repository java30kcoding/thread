package cn.itlou.other;

public class StopThread extends Thread {

    private int i = 0;
    private int j = 0;

    @Override
    public void run(){
        synchronized (this){
            //增加同步锁，确保线程安全
            ++i;
            try {
                //休眠10S，模拟耗时操作
                Thread.sleep(10000L);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            ++j;
        }
    }

    public void print(){
        System.out.println("i = " + i + "; j = " + j);
    }

}
