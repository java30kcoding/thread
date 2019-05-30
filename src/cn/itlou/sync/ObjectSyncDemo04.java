package cn.itlou.sync;

/**
 * 锁消除
 */
public class ObjectSyncDemo04 {

    //热点代码优化后消除了锁
    public void test1(Object arg){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("a");
        stringBuffer.append(arg);
        stringBuffer.append("c");
        System.out.println(stringBuffer.toString());
    }

    public void test2(Object arg){
        String a = "a";
        String c = "c";
        System.out.println(a + arg +c);
    }

    public void test3(Object arg){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a");
        stringBuilder.append(arg);
        stringBuilder.append("c");
        System.out.println(arg.toString());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            new ObjectSyncDemo04().test1("123");
        }
    }

}
