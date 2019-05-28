# Java多线程

# 线程状态

1.`New`：尚未启动的线程的线程状态

2.`Runnable`：可运行线程的线程状态，等待CPU调度

3.`Blocked`：线程阻塞等待监视器锁定的线程状态

4.`Waiting`：等待线程的线程状态(wait、join、park)

5.`Timed Waiting`：具有指定等待时间的等待线程的线程状态(sleep、wait、join、parkNanos、parkUntil)

6.`Terminated`：终止线程的线程状态。线程正常执行完成或出现异常

​	代码：Demo01

## 线程切换状态图

![2](http://prvyof0n9.bkt.clouddn.com/2.png)

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

![3](http://prvyof0n9.bkt.clouddn.com/3.png)

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

![4](http://prvyof0n9.bkt.clouddn.com/4.png)

​	**指令重拍场景**：当CPU写缓存时发现缓存区块正在被其他CPU占用，为了提高CPU处理性能，可能将后面的读缓存命令优先执行。

​	但是并非随便重拍，需要遵守`as-if-serial`语义

​	as-if-serial语义是：不管怎么重排序，程序的执行结果不能被改变。编译器，runtime和处理器都必须遵守as-if-serial语义。

​	也就是说：编译器和处理器**不会对存在数据依赖关系的操作重新排序**

## CPU存在的两个问题

1.CPU高速缓存下：

​	缓存中的数据与主内存的数据并不是实时同步的，各CPU间缓存的数据也不是实时同步。**在同一个时间点，各CPU所看到的同一内存地址的数据的值可能不一致**。

2.CPU指令重排序下：

​	虽然遵守了as-if-serial语义，但那时仅在单个CPU自己执行的情况下才能保证正确。在多核多线程中，指令逻辑无法分辨因果关联，可能出现**乱序执行**，导致运行结果错误。

## 内存屏障解决上两个问题

​	`写内存屏障`(Store Memeor Barrier)：在指令后插入Store Barrier，能让写入缓存中的最新数据更新写入主内存，让其他线程可见。强制写入主内存，这种显示调用，CPU就不会因为性能考虑去进行重排序。

​	`读内存屏障`(Load Memory Barrier)：在指令前插入Load Barrier，可以让高速缓存中的数据失效，强制重新从主内存加载数据。强制读取主内存内容，让CPU缓存与主内存保持一致，避免导致了缓存一致性的问题。

# 线程通信

## 通信的方式

线程通信涉及以下四类：

- 文件共享

  ![](http://prvyof0n9.bkt.clouddn.com/5.png)

  代码：Demo04

- 网络共享

- 共享变量

  ![](http://prvyof0n9.bkt.clouddn.com/6.png)

  代码：Demo05

- **jdk提供的线程协调API**

如：~~suspend/resume~~、wait/notify、park/unpark

示例：线程1去买包子，没包子则不执行。线程2生产包子，通知线程1继续执行。

![](http://prvyof0n9.bkt.clouddn.com/7.png)

​	

## API-被弃用的suspend和resume

​	**作用**：调用suspend挂起目标线程，通过resume可以恢复线程执行。

​	**弃用原因**：容易写出死锁代码。

​	代码：Demo06

## wait/notify机制

​	这些方法只能由同一对象锁的持有者线程调用，也就是写在同步块里面，因为他们是基于对象的等待集合，监视器的机制实现的，否则会抛出`IllegalMonitorStateException`异常。

​	wait方法导致当前线程等待，加入该对象的等待集合中，并且放弃当前对象持有的对象锁。notify/notifyAll方法唤醒一个或所有正在等待这个对象锁的进程。

​	注意：虽然wait会自动解锁，但是**对顺序有要求**，如果在notify调用后再调用wait的话，线程会永远处于WAITING状态。就像我们去等火车，错过了就等不到了。

​	代码：Demo06

## park/unpark机制

​	线程调用park是等待`许可`，unpark方法是为指定线程提供`许可`。

​	*没有调用顺序的要求*。**但不会释放锁**。

​	多次调用unpark后，在调用park，线程会直接运行。但不会叠加，也就是说，连续多次调用park方法，第一次会拿到`许可`执行，之后则不会。

# 伪唤醒

**警告！代码中用if语句来判断是否进入等待状态是错误的！**

官方建议**应该在循环中检查等待条件**，原因是处于等待状态的线程可能会收到**错误警报和伪唤醒**，如果不在循环中检查等待条件，程序就会在没有满足条件的情况下推出。



# 线程封闭

​	多线程访问共享可变数据时，涉及到线程间数据同步的问题。但并不是所有时候都需要共享数据，所以线程封闭的概念就提出来了。

​	线程封闭是数据都被封闭在各自的线程之中，就不需要同步，通过将数据封闭在线程内避免使用同步的技术成为线程封闭。

具体有：**ThreadLocal**、**局部变量**

## ThreadLocal

​	ThreadLocal是Java里的一种特殊变量。

​	它是一个线程级别的变量，每个线程都有一个ThreadLocal，就是每个线程都拥有了自己独立的一个变量。竞争条件被彻底消除了，在并发模式下是绝对安全的变量。

​	用法：

```java
ThreadLocal<T> var = new ThreadLoacl<T>();
```

​	会自动在每一个线程上创建一个T的副本，副本之间彼此独立，互不影响。

​	可以用ThreadLocal存储一些参数，以方便线程中多个方法中使用，用来替代方法传参的做法。

​	代码：Demo07

## 栈封闭

​	**局部变量**的固有属性之一就是封闭在线程中。

​	它们位于执行线程的栈中，其他线程无法访问这个栈。

# 线程池应用及原理

## 为什么要用线程池

​	线程是不是越多越好？

​	1.线程在java中是一个对象，更是操作系统的资源，线程创建、销毁需要时间。如果创建时 + 销毁时间 > 执行任务时间就很不划算。

​	2.java对象占用堆内存，操作系统线程占用系统内存，根据jvm规范，一个线程默认栈大小为1M，这个栈空间是需要从系统内存中分配的。线程过多会消耗很多内存。

​	3.操作系统在多线程状态下需要频繁切换线程上下文。

## 线程池的基本概念

​	1.`线程池管理器`：用于创建并管理线程池，包括创建线程池，销毁线程池，添加新任务。

​	2.`工作线程`：线程池中线程，在没有任务时处于等待状态，可以循环的执行任务。

​	3.`任务接口`：每个任务必须实现的接口，以提供线程调度任务的执行，它主要规定了任务的入口，任务执行完后的收尾工作，任务的执行状态等。

​	4.`任务队列`：用于存放没有处理的任务。提供一种缓冲机制。

![](http://prvyof0n9.bkt.clouddn.com/8.png)

## 线程池API - 接口定义和实现类

| 类型   | 名称                        | 描述                                                         |
| ------ | --------------------------- | ------------------------------------------------------------ |
| 接口   | Executor                    | 最上层的接口定义了执行的方法Execute                          |
| 接口   | ExecutorService             | 继承Executor接口，拓展了Callable、Future、关闭方法           |
| 接口   | ScheduleExecutorService     | 继承了ExecutorService，增加了定时任务的相关方法              |
| 实现类 | **ThreadPoolExecutor**      | **基础、标准的线程实现**                                     |
| 实现类 | ScheduledThreadPoolExecutor | 继承了ThreadPoolExecutor，实现了ScheduleExecutorService中相关的定时任务方法 |

## 线程池API - 方法定义

<center>ExecutorServcie</center>

```java
//检测ExecutorService是否已经关闭，直到所有任务完成执行，或超市发生，或当前线程被中断
awaitTermination(long timeout, TimeUtnit unit)
//执行给定的任务集合，执行完毕后，返回结果
invokeAll(Collection<? extends Callable<T>> tasks)
//执行给定的任务集合，执行完毕或者超时后，返回结果，其他任务终止
invokeAll(Collection<? extends Callabl<T>> tasks, long timeout, TimeUnit unit)
//执行给定的任务，任意一个执行成功则返回结果，其他任务结束
invokeAny(Collection<? extends Callable<T>> tasks)
//执行给定的任务，任意一个执行成功或者超时后，则返回结果，其他任务终止
invokeAny(Collection<? extends Callabl<T>> tasks, long timeout, TimeUnit unit)
//如果此线程池已关闭，则返回true
isShutdown()
//如果关闭后所有任务都已完成，则返回true
isTerminated()
//优雅关闭线程池，之前提交的任务将被执行，但是不会接受新的任务
shutdown()
//尝试停止所有正在执行的任务，停止等待任务的处理，并返回等待执行任务的列表
shutdownNow()
//提交一个用于执行的Callable返回任务，并返回一个Future，用于获取Callable执行结果
submit(Callable<T> task)
//提交可运行任务以执行，并发回一个Future对象，执行结果为null
submit(Runnalbe task)
//提交可运行任务以执行，并返回Future，执行结果为传入的result
submit(Runnable task, T result)

```

<center>ScheduleExecutorService</center>

```java
//创建并执行一个一次性任务，过了延迟时间就会被执行
schedule(Callable<V> callable, long delay, TimeUnit unit)
//同上
schedule(Runnable command, long delay, TimeUnit unti)
//创建并执行一个周期任务
//过了给定的初始延迟时间，会被第一次执行
//执行过程中发生异常，则任务停止
//一次任务执行时间超过了周期时间，下一次任务会等到该次任务执行结束后，立即执行
//这也是它和scheduleWithFixedDelay的区别
scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
//创建并执行一个周期任务
//过了初始延迟时间，第一次被执行，后续以给定的周期时间执行
//执行过程中发生异常，则任务停止
//一次任务执行时长超过了周期时间，下次任务会在该次任务执行结束的时间基础上
//再计算执行延时(任务结束的时间点 + 延时时间)
scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit)
```

## 线程池API - Executors工具类

<center>常用方法</center>

```java
//创建一个固定大小、任务队列容量无界的线程池。核心数量=最大线程数
newFixedThreadPool(int nThreads)
//创建一个大小无界的缓冲线程池。它的任务队列是一个同步队列
//任务加入到池中如果有空闲线程，则用空闲线程执行，如果没有则创建新的线程执行
//池中的线程空闲超过60秒会自动销毁释放，线程数随任务变化。适用于执行耗时较小的异步任务
//核心线程数=0，最大线程数=Integer.MAX_VALUE
newCachedThreadPool()
//只有一个线程来执行无界队列任务的单一线程池
//该池保证任务的执行顺序，当唯一的线程因任务异常中止时
//创建一个新的线程来继续执行后续任务
//与newFixedThreadPool(1)的区别在于不能再改变
newSingleThreadExector()
//能定时执行任务的线程池。核心数量由参数指定，最大数量=Integer.MAX_VALUE
newScheduledThreadPool(int corePoolSize)
```

## 线程池的用法

​	代码：Demo08

## 线程池原理 - 任务execute过程

​	1.是否达到核心线程数量？没有，创建一个工作线程执行任务。

​	2.工作队列是否已经满了？没有，将新任务提交到队列中。

​	3.是否达到线程池最大数量？没有，创建一个新的工作线程执行任务

​	4.最后，执行拒绝策略来处理多出的任务。

![](http://prvyof0n9.bkt.clouddn.com/9.png)

​	线程池execute源码：

```java
int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
```

## 线程数量

​	如何确定合适数量的线程？

​	计算性任务：cpu数量的1-2倍。

​	IO型任务：相比计算型任务，需要多一些，根据具体的**IO阻塞时长**进行考量决定。如tomcat中默认的最大线程数为：200。

​	也可以考虑根据需要在一个**最小数量和最大数量**间自动增减线程数。

​	通过查看CPU利用率去调整线程数量，大概达到80%就可以。

# 线程安全之可见性问题

## Java内存模型

![1](http://prvyof0n9.bkt.clouddn.com/1.png)

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

## Java内存模型含义

​	**内存模型决定了在程序的每个点上可以读取什么值**。

​	内存模型描述程序的可能行为。

​	Java语言内存模型通过检查执行跟踪中的每个读操作，并根据某些规则检查该读操作观察到的写操作是否有效来工作。

​	只要程序的所有执行产生的结果都可以由内存模型预测。具体的实现者任意实现，包括操作的重新排序和删除不必要的同步。

​	**如果在T1线程中执行T2.interrupt()，T3线程执行T2.join()，那么无论T2是否中断，T3都应当知道T2的状态**。

## Shared Variables共享变量描述

​	可以在线程之间共享的内存成为**共享内存或堆内存**。

​	所有实例字段、静态字段和数组元素都存在堆内存中。

​	如果至少有一个访问时写的，那么对同一变量的两次访问(读或写)是冲突的。

## 线程间操作的定义

- write：要写的变量以及要写的值。

- read：要读的变量以及可见的写入值(由此，我们可以确定可见的值)。

- lock：要锁定的管程(监视器monitor)。

- unlock：要解锁的管程。

- 外部操作(socket等等..)。

- 启动和终止

  程序顺序：如果一个程序没有数据竞争，那么程序的所有执行看起来都是数据一致的。本规范只涉及线程间的操作。**共享变量必须存放在主内存中**。

## 对于同步(先后顺序)的规则定义

- 对于监视器m的解锁与所有后续操作对于m的加锁同步。
- 对volatile变量v的写入，与所有其他线程后续对v的读同步。
- 启动线程的操作与线程中的第一个操作同步。
- 对于每个属性写入默认值(0, false, null)与每个线程对其进行的操作同步。
- 线程T1的最后操作与线程T2发现线程T1已经结束同步(isAlive，join可以判断线程是否终结)。
- 如果线程T1中断了T2，那么线程T1的中断操作与其他所有线程发现T2被中断了同步通过抛出InterruptedException异常，或者调用Thread.interrupted或Thread.isInterrupted。

## Happens-before先行发生原则

​	**happens-before关系**主要用于强调两个有冲突的动作之间的顺序，以及定义数据争用的发生时机。

​	**具体的虚拟机实现**，有必要确保以下原则的成立。

- 某个线程中的每个动作都happens-before该线程中该动作后面的动作。

- 某个管程上的unlock动作happens-before同一个管程上后续的lock动作。

- 对某个volatile字段的写操作happens-before每个后续对该volatile字段的读操作。

- 在某个线程对象上调用start()方法happens-before该启动了的线程中的任意动作。

- 某个线程中的所有动作happens-before任意其他线程成功从该线程对象上的join()中返回。

- 如果某个动作a happens-before动作b，且 b happens-before动作c，则有a happens-before c。

  当程序包含两个没有被happens-before关系排序的冲突访问时，就称存在**数据争用**。

  **遵守了这个原则，也就意味着有些代码不能进行重排序，有些数据不能缓存**。

## 工作内存缓存

![](http://prvyof0n9.bkt.clouddn.com/14.png)

## 指令重排序导致的可见性问题

​	Java的语义允许编译器和微处理器执行优化，这些优化可以与不正确的代码交互，从而产生看似矛盾的行为。(在运行时发生)

​	为什么重排序遵循as-if-serial原则还会出现问题？

​	因为线程在多个CPU上运行，在各个CPU自己看来自己的重排序是没有问题的，但是整合到一起就可能发生问题。

![](http://prvyof0n9.bkt.clouddn.com/12.png)

![](http://prvyof0n9.bkt.clouddn.com/13.png)

​	Java jit编译器会把如下代码(前提是Hot Code热点代码执行次数很多)：

```java
while(flag){
    i++;
}
```

​	优化成

```java
if(flag){
    while(true){
        i++;
    }
}
```

​	代码：VisibilityDemo

## volatile关键字

​	**可见性问题**：让一个线程对共享变量的修改，能够及时的被其他线程看到

​	根据JMM中规定的happen before和同步原则：

> 对某个volation字段的写操作happens-before每个后续对该volatile字段的读操作。
>
> 对volatile变量v的写入，与所有其他线程后续对v的度同步。

​	要满足这些条件，所以volatile关键字就有这些功能：

- 禁止所有的缓存(JDK文档中)。

  volatile变量的访问控制符会加个ACC_VOLATILE。

- 对volatile变量相关的指令不做重排序。

## final在JMM中的处理

- final在该对象的构造函数中设置对象的字段，当线程看到该对象时，将始终看到该对象的final字段的正确构造版本。

伪代码示例：f = new finalDemo();读取到的f.x一定最新，x为final字段。

- 如果在构造函数中设置字段后发生读取，则会看到该final字段分配的值，否则它将会看到默认值。

伪代码示例：public finalDemo(){x = 1;y = x;}	y会等于1.

- 读取该共享对象的final成员变量前先要读取共享对象。

伪代码示例：r = new ReferenceObj(); k = r.f;	这两个操作不能重排序

- 通常static final是不可以修改字段。然而System.in，System.out和System.err是static final字段，遗留原因，必须允许通过set方法改变，我们将这些字段成为写保护，以区别对于普通的final字段。

## Word Tearing字节处理

​	一个字段或元素的更新不得与任何其他字段或元素的读取或更新交互。

​	特别是，分别更新字节数组的相邻元素的两个线程不得干涉或交互，也不需要保证同步以确保顺序性。

​	有些处理器(尤其是早起的Alphas处理器)没有提供写单个字节的功能。

​	在这样的处理器上更新byte数组，若只是简单的读取整个内容，更新对应的字节，然后将整个内存再写回内存，将是不合法的。

​	这个问题有时候称为“自分裂(word tearing)”，在单独更新单个字节有难度的处理器上，就需要寻求其他方法了。

​	基本不需要考虑这个，了解就好。

## double和long的特殊处理

​	虚拟机规范中，写64位的double和long分成了两次32位值的操作。

​	由于不是原子操作，可能导致读取到某次写操作中64位的前32位，以及另一次写之后的后32位。

​	读写volatile的long和double总是原子的。读写引用也总是原子的。

​	商业JVM不会存在这个问题，虽然规范没要求实现原子性，但是考虑到实际应用，大部分都实现了原子性。

# 线程安全之原子操作

## 静态条件与临界区

​	多个线程访问了相同的资源，向这些资源做了写操作时，对执行顺序有要求。

```java
public class Demo{
    public int i = 0;
    public void incr(){
        i++;
    }
}
```

- 临界区：incr方法内部就是临界区域，关键部分代码的多线程并发执行，会对执行结果产生影响。
- 竞态条件：可能发生在临界区域内的特殊条件。多线程执行incr方法中的i++关键代码时，产生了竞态条件。

## 共享资源

- 如果一段代码是线程安全的，则它不包含静态条件。只有当多个线程更新共享资源时，才会发生竞态条件

- 栈封闭时，不会再线程之间共享的变量，都是线程安全的。

- 局部对象引用本身不共享，但是引用的对象存储在共享的堆中。如果方法内创建的对象，只在方法内传递，并且不会其他线程可用，那么也是线程安全的。

  判断规则：如果创建、使用和处理资源，永远不会逃脱单个线程的控制，该资源的使用时线程安全的。

## 不可变对象

```java
public class Demo{
    private int value = 0;
    pbulic Demo(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
}
```

​	创建不可变的共享对象来保证在线程间共享时不会被修改，从而实现线程安全。

​	实例被创建，value变量就不能再被修改，这就是不可变性。

## 原子操作定义

​	原子操作可以使一个步骤，也可以是多个操作步骤，但是其顺序不可以被打乱，也不可以被切割而执行其中的一部分(不可中断性)。

​	**将整个操作视作一个整体是原子性的核心特征**。

```
public class Demo{
    public int i = 0;
    public void incr(){
    	//1.加载i
    	//2.计算i + 1
    	//3.赋值i
        i++;
    }
}
```

​	以上代码存在竞态条件，线程不安全，需要转变为原子操作才能安全。

​	方式：**循环CAS、锁**。

## CAS机制

​	Compare and swap比较和交换。属于硬件同步原语，处理器提供了基本内存操作的原子性保证。Cas操作需要输入两个数值，一个旧值A(期望操作前的值)和一个新值B，在操作期间先比较下旧值有没有变化，如果没有变化，才交换成新值，发生了变化则不交换。

> Java中的sun.misc.Unsafe类提供了compareAndSwapInt()和compareAndSwapLong()等几个方法实现CAS。

​	代码：LockDemo03

## J.U.C包内的原子操作封装类

| AtomicBoolean                 | 原子更新布尔类型               |
| ----------------------------- | ------------------------------ |
| AtomicInteger                 | 原子更新整形                   |
| AtomicLong                    | 原子更新长整型                 |
| **AtomicIntegerArray**        | **原子更新整型数组里的元素**   |
| AtomicLongArray               | 原子更新长整型数组里的元素     |
| AtomicReferenceArray          | 原子更新引用类型数组里的元素   |
| **AtomicIntegerFieldUpdater** | **原子更新整型的字段的更新器** |
| AtomicLongFieldUpdater        | 原子更新长整型字段的更新器     |
| AtomicReferenceFieldUpdater   | 原子更新引用类型里的字段       |
| **AtomicReference**           | **原子更新引用类型**           |
| AtomicStampedReference        | 原子更新带有版本号的引用类型   |
| AtomicMarkableReference       | 原子更新带有标记位的引用类型   |

​	1.8以后更新

- 更新器：DoubleAccumulator、longAccumulator
- 计数器：DoubleAddr、LongAddr

计数器增强版，高并发下性能更好

**频繁更新但不太频繁读取的汇总统计信息时使用**。分成多个操作单元，只有需要汇总的时候才计算所有单元的操作。









