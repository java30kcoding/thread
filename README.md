# Java运行原理

## JVM划分

**JVM运行时数据区**：

- 线程共享：随线程生命周期创建和销毁

  - 方法区

    > JVM用来存储加载的类信息、常量、静态变量、编译后的代码等，虚拟机规范中这是一个逻辑区划分。根据具体实现有所不同。
    >
    > Oracle的HotSpot在java7中方法区放在永久代，java8放在元数据空间，并通过GC机制对这个区域进行管理。

  - 堆内存

    > 堆内存可以细分为：老年代、新生代(Eden、From Survivor、To Survivor)
    >
    > JVM启动时创建，存放对象的实例。垃圾回收器主要就是管理堆内存。如果满了，就会出现OutOfMemoryError(OOM)。

- 线程独占：随JVM或GC创建和销毁

  - 虚拟机栈

    > 虚拟机栈，每个线程都在这个空间有一个私有的空间。
    >
    > 线程栈由多个栈帧(Stack Frame)组成。
    >
    > 一个线程会执行一个或多个方法，一个方法对应一个栈帧。
    >
    > 栈帧内容包含：局部变量表、操作数栈、动态链接、方法返回地址、附加信息等。
    >
    > 栈内存默认最大1M，超出则抛出StackOverflowError。

  - 本地方法栈

    > 和虚拟机栈类似，是为虚拟机执行Java方法而准备的，本地方发栈是为虚拟机使用Native本地方法准备的。
    >
    > 虚拟机规范没有规定具体的实现，由不同的虚拟机厂商去实现。
    >
    > HotSpot虚拟机中虚拟机栈和本地方法栈的实现方式一样。同样超出大小会抛出StackOverflowError。

  - 程序计数器

    > 记录当前线程执行字节码的位置，存储的事字节码指令地址，如果执行Native方法，则计数器值为空。
    >
    > 每个线程都在这个空间有一个私有的空间，占用内存空间很少。
    >
    > CPU同一时间，只会执行一条线程中的指令。JVM多线程会轮流切换并分配CPU执行时间的方式。为了线程切换后，需要通过程序计数器，来恢复正确的执行位置。

## 运行图

![1](C:\Users\yuanyulou\Desktop\博客图片\Java运行\1.png)

------



# 线程状态

1.`New`：尚未启动的线程的线程状态

2.`Runnable`：可运行线程的线程状态，等待CPU调度

3.`Blocked`：线程阻塞等待监视器锁定的线程状态

4.`Waiting`：等待线程的线程状态(wait、join、park)

5.`Timed Waiting`：具有指定等待时间的等待线程的线程状态(sleep、wait、join、parkNanos、parkUntil)

6.`Terminated`：终止线程的线程状态。线程正常执行完成或出现异常

​	代码：Demo01

## 线程切换状态图

![2](C:\Users\yuanyulou\Desktop\博客图片\Java运行\2.png)

------



# 线程终止

- 不正确的线程终止-**Stop**

  > ~~Stop~~：终止线程，并且清除监视器锁的信息，但是可能导致线程安全问题，JDK不建议使用。

  代码：Demo02

- 正确的线程中止-**interrupt**

  > 如果目标线程在调用Object class的wait()、wait(long)或wait(long millis, int nanos)方法或wait(long millis, int nanos)方法、join()、join(long millis, int nanos)或sleep(long millis, int nanos)方法时被阻塞，那么interrupt会失效，该线程中断状态将被清除，并抛出InterruptedException异常。
  >
  > 如果目标线程是被I/O或者NIO中的Channel所阻塞，同样，I/O操作会被中断或者返回特殊异常值。达到中止目的

  代码：Demo02

- 其他方法-**标志位**

  代码：Demo03

  ------

  

# 内存屏障和CPU缓存

## CPU的优化手段-缓存

![3](C:\Users\yuanyulou\Desktop\博客图片\Java运行\3.png)

- L1 一级缓存是CPU第一层高速缓存，分为数据缓存和指令缓存。一般服务器CPU的L1缓存容量通常在32 - 4096KB。

- L2 由于L1级高速缓存容量的限制，为了再次提高CPU的运算速度，在CPU外部放置一个高速存储器，即二级缓存。

- L3 现在的都是内置的。它的实际作用是进一步降低内存延迟，同时提升大数据量计算时处理器的性能。具有较大L3缓存的处理器提供更有效的文件系统缓存行为及较短消息和处理器队列长度。一般是多核共享一个L3缓存。

  **CPU读取数据顺序 L1->L2->L3->内存->外部存储器**

## 缓存同步协议

​	多CPU读取同样的数据进行缓存，进行不同运算后，最终写入主内存以哪个为准呢？

​	MESI协议规定每条缓存有一个状态位，同时定义了下面四个状态：

- 修改态(Modified)：此cache行已被修改过(脏行)，内容与主内存不同，为此cache专有

- 专有态(Exclusive)：此cache行内容同于主存，但不出现于其他cache中

- 共享态(Shared)：此cache行内容同于主存，但也出现在其他cache中

- 无效态(Lnvalid)：此cache行内容无效(空行)

  多处理器时，单个CPU对缓存中数据进行了改动，需要通知给其他CPU。

  这意味着，CPU处理要控制自己的读写操作，还要监听其他CPU发出的通知，从而保证最终的一致性。

## 运行时指令重排

![4](C:\Users\yuanyulou\Desktop\博客图片\Java运行\4.png)

**指令重拍场景**：当CPU写缓存时发现缓存区块正在被其他CPU占用，为了提高CPU处理性能，可能将后面的读缓存命令优先执行。

但是并非随便重拍，需要遵守`as-if-serial`语义

as-if-serial语义是：不管怎么重排序，程序的执行结果不能被改变。编译器，runtime和处理器都必须遵守as-if-serial语义。

也就是说：编译器和处理器**不会对存在数据依赖关系的操作重新排序**

## CPU存在的两个问题

1.CPU高速缓存下：

​	缓存中的数据与主内存的数据并不是实时同步的，各CPU间缓存的数据也不是实时同步。**在同一个时间点，各CPU所看到的同一内存地址的数据的值可能不一致**。

2.CPU指令重排序下：

​	虽然遵守了as-if-serial语义，但那时仅在单个CPU自己执行的情况下才能保证正确。在多核多线程中，指令逻辑无法分辨因果关联，可能出现**乱序执行**，导致运行结果错误。

## 内存屏障解决上两个问题

`写内存屏障`(Store Memeor Barrier)：在指令后插入Store Barrier，能让写入缓存中的最新数据更新写入主内存，让其他线程可见。强制写入主内存，这种显示调用，CPU就不会因为性能考虑去进行重排序。

`读内存屏障`(Load Memory Barrier)：在指令前插入Load Barrier，可以让高速缓存中的数据失效，强制重新从主内存加载数据。强制读取主内存内容，让CPU缓存与主内存保持一致，避免导致了缓存一致性的问题。

# 线程通信

## 通信的方式

线程通信涉及以下四类：

- 文件共享
- 网络共享
- 共享变量
- **jdk提供的线程协调API**

如：~~suspend/resume~~、wait/notify、park/unpark

​	代码：Demo04

