package cn.itlou.volatiletest;

import java.util.concurrent.TimeUnit;

// 1、 jre/bin/server  放置hsdis动态链接库
//  测试代码 将运行模式设置为-server， 变成死循环   。 没加默认就是client模式，就是正常（可见性问题）
// 2、 通过设置JVM的参数，打印出jit编译的内容 （这里说的编译非class文件），通过可视化工具jitwatch进行查看
// -server -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:+LogCompilation -XX:LogFile=jit.log
//  关闭jit优化-Djava.compiler=NONE
//如果flag加了volatile那么必然不会指令重拍
public class VisibilityDemo {

    private boolean flag = true;

    public static void main(String[] args) throws Exception {

        VisibilityDemo visibilityDemo = new VisibilityDemo();

        /**
         * 1.出结果
         * 2.死循环
         */
        new Thread(() -> {
            int i = 0;
            while (visibilityDemo.flag){//指令重排序
                i++;
            }
            System.out.println(i);
        }).start();

        TimeUnit.SECONDS.sleep(2);
        //设置flag为false，结束上面的循环
        visibilityDemo.flag = false;
        System.out.println("被设置为false了！");

    }

}
