package cn.itlou.sync;

/**
 * 锁粗化(运行时 jit  编译优化)
 */
public class ObjectSyncDemo03 {

    int i;

    public void test1(Object arg){
        synchronized (this){
            i++;
        }
        synchronized (this){
            i++;
        }
        //粗化
        /**synchronized (this){
            i++;
            i++;
        }*/
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            new ObjectSyncDemo03().test1("a");
        }
    }

}
