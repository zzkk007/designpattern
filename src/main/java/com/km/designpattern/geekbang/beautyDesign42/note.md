
1、 单例存在那些问题?
    
    项目中使用单例，都是用它来表示一些全局唯一类，比如配置信息，连接池、Id生产器类。
    
    a、单例对 OOP 特性的支持不友好
    
    b、单例会隐藏类之间的依赖关系
    
    c、单例对代码的扩展性不友好
    
    d、单例对代码的可测试性不友好
    
    e、单例不支持有参数的构造函数

2、如何理解单例模式中的唯一性？

    单例定义：一个类只允许创建唯一一个对象(或者实例)，那这个类就是一个单例类，
    一个类只允许创建一个对象，对象的唯一性的作用范围是什么呢？
    是指进程内指允许创建一个对象。单例模式创建的对象是进程唯一的。
    
     “进程唯一”指的是进程内唯一，进程间不唯一。类比一下，“线程唯一”指的是线程内唯一，
     线程间可以不唯一。实际上，“进程唯一”还代表了线程内、线程间都唯一，
     这也是“进程唯一”和“线程唯一”的区别之处。   
     
        如何实现集群环境下的单例？
            public class IdGenerator {
              private AtomicLong id = new AtomicLong(0);
              private static IdGenerator instance;
              private static SharedObjectStorage storage = FileSharedObjectStorage(/*入参省略，比如文件地址*/);
              private static DistributedLock lock = new DistributedLock();
              
              private IdGenerator() {}
            
              public synchronized static IdGenerator getInstance() 
                if (instance == null) {
                  lock.lock();
                  instance = storage.load(IdGenerator.class);
                }
                return instance;
              }
              
              public synchroinzed void freeInstance() {
                storage.save(this, IdGeneator.class);
                instance = null; //释放对象
                lock.unlock();
              }
              
              public long getId() { 
                return id.incrementAndGet();
              }
            }
            
            // IdGenerator使用举例
            IdGenerator idGeneator = IdGenerator.getInstance();
            long id = idGenerator.getId();
            IdGenerator.freeInstance(); 
            
    如何实现一个多例模式？ 
    
     “单例”指的是，一个类只能创建一个对象。对应地，
     “多例”指的就是，一个类可以创建多个对象，但是个数是有限制的，
     比如只能创建 3 个对象。如果用代码来简单示例一下的话，就是下面这个样子：      
        public class BackendServer {
          private long serverNo;
          private String serverAddress;
        
          private static final int SERVER_COUNT = 3;
          private static final Map<Long, BackendServer> serverInstances = new HashMap<>();
        
          static {
            serverInstances.put(1L, new BackendServer(1L, "192.134.22.138:8080"));
            serverInstances.put(2L, new BackendServer(2L, "192.134.22.139:8080"));
            serverInstances.put(3L, new BackendServer(3L, "192.134.22.140:8080"));
          }
        
          private BackendServer(long serverNo, String serverAddress) {
            this.serverNo = serverNo;
            this.serverAddress = serverAddress;
          }
        
          public BackendServer getInstance(long serverNo) {
            return serverInstances.get(serverNo);
          }
        
          public BackendServer getRandomInstance() {
            Random r = new Random();
            int no = r.nextInt(SERVER_COUNT)+1;
            return serverInstances.get(no);
          }
        }
     