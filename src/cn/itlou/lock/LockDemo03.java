package cn.itlou.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * CAS
 */
public class LockDemo03 {

    volatile int value = 0;

    //不能直接new
    static Unsafe unsafe;//直接操作内存，修改对象，数组内存
    private static long valueOffset;

    static {
        try {
            //通过反射获取Unsafe
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            //获得value属性的偏移量(用于定位value属性在内存中的具体地址)
            valueOffset = unsafe.objectFieldOffset(LockDemo03.class.getDeclaredField("value"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void add() {
        //可能会失败
        int current;
        //CAS + 循环重试
        do {
            //操作耗时的话，线程就会占用大量的CPU执行时间
            current = unsafe.getIntVolatile(this, valueOffset);
            //只有失败了才会重试
        }while (!unsafe.compareAndSwapInt(this, valueOffset, value, value + 1));//类似乐观锁，版本号)
    }

    public static void main(String[] args) throws Exception {
        LockDemo03 lockDemo01 = new LockDemo03();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lockDemo01.add();
                }
            }).start();
        }
        Thread.sleep(2000L);
        System.out.println(lockDemo01.value);
    }

}
