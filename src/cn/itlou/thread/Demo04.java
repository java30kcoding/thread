package cn.itlou.thread;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件共享
 */
public class Demo04 {

    public static void main(String[] args) {

        //线程1 写入数据
        new Thread(() -> {
            try {
                while (true){
                    Files.write(Paths.get("demo04.txt"),
                            ("写入数据当前时间：" + String.valueOf(System.currentTimeMillis())).getBytes());
                    Thread.sleep(1000L);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        //线程2 读取数据
        new Thread(() -> {
            try{
                while (true){
                    Thread.sleep(1000L);
                    byte[] allBytes = Files.readAllBytes(Paths.get("demo04.txt"));
                    System.out.println("读取数据："+new String(allBytes));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }

}
