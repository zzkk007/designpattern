单例模式：

1、为什么要使用单例？

    单例设计模式(Singleton Design Pattern)一个类只允许创建一个对象(或者实例)。
    那这个类就是一个单例类, 这种设计模式就是单例设计模式。
    
    a: 处理资源访问冲突：单例相当于把对象级别的锁，换成类级别的锁，让所有对象共享同一把锁。
    
        我们将 Logger 设计成一个单例类，程序中至允许创建一个 Logger 对象，所有的线程共享
        使用的这个Logger对象，共享一个 FileWriter 对象，而 FileWriter 本身是对象级别
        线程安全的。
    
    b: 表示全局唯一类。
        
        从业务概念上，如果有些数据在系统中只应该保存一份，那就比较合适设计为单例类。
        比如，配置信息类。在系统中，我们只有一个配置文件，当配置文件被加载到内存之后，
        以对象的形式存在，也理所应当只有一份。   
  
        再比如，唯一递增 ID 号码生成器,如果程序中有两个对象，那就会存在生成重复 ID 的情况，
        所以，我们应该将 ID 生成器类设计为单例。
   
        
2、如何实现一个单例：

    a、构造函数需要 private 访问权限的，这样才能避免外部通过 new 创建实例。
    
    b、考虑对象创建时的线程安全问题。
    
    c、考虑是否支持延迟加载
    
    d、考虑 getInstance() 性能是否高(是否加锁)
    
    （1）饿汉式：
        饿汉式的实现方法比较简单，在类加载的时候，instance 静态实例就已经创建并初始化好了。
        所以 instance 实例的创建过程是线程安全的。        
        public class IdGenerator { 
          private AtomicLong id = new AtomicLong(0);
          private static final IdGenerator instance = new IdGenerator();
          private IdGenerator() {}
          public static IdGenerator getInstance() {
            return instance;
          }
          public long getId() { 
            return id.incrementAndGet();
          }
        }
    
    (2) 懒汉式：
    
        public class IdGenerator { 
          private AtomicLong id = new AtomicLong(0);
          private static IdGenerator instance;
          private IdGenerator() {}
          public static synchronized IdGenerator getInstance() {
            if (instance == null) {
              instance = new IdGenerator();
            }
            return instance;
          }
          public long getId() { 
            return id.incrementAndGet();
          }
        } 

    (3) 双重检测：
    
        饿汉式不支持延迟加载，懒汉式有性能问题，不支持高并发。
        那我们再来看一种既支持延迟加载、又支持高并发的单例实现方式，
        也就是双重检测实现方式。

        在这种实现方式中，只要 instance 被创建之后，即便再调用 getInstance() 
        函数也不会再进入到加锁逻辑中了。所以，这种实现方式解决了懒汉式并发度低的问题。
        具体的代码实现如下所示：
        
        public class IdGenerator { 
          private AtomicLong id = new AtomicLong(0);
          private static IdGenerator instance;
          private IdGenerator() {}
          public static IdGenerator getInstance() {
            if (instance == null) {
              synchronized(IdGenerator.class) { // 此处为类级别的锁
                if (instance == null) {
                  instance = new IdGenerator();
                }
              }
            }
            return instance;
          }
          public long getId() { 
            return id.incrementAndGet();
          }
        }
    
    (4) 静态内部类
        
        我们再来看一种比双重检测更加简单的实现方法，那就是利用 Java 的静态内部类。
        它有点类似饿汉式，但又能做到了延迟加载。
        具体是怎么做到的呢？我们先来看它的代码实现。
       
        public class IdGenerator { 
          private AtomicLong id = new AtomicLong(0);
          private IdGenerator() {}
        
          private static class SingletonHolder{
            private static final IdGenerator instance = new IdGenerator();
          }
          
          public static IdGenerator getInstance() {
            return SingletonHolder.instance;
          }
         
          public long getId() { 
            return id.incrementAndGet();
          }
        }
            
    （5）枚举：
        
    
    
    
        
2、单例存在哪些问题？

3、单例与静态类的区别？

4、有何替代的解决方案？

