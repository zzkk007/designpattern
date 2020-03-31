
工厂模式(下): 如何设计实现一个 Dependency Injection 框架

1、工厂模式和DI容器有何区别？

       实际上，DI容器底层最基本的设计思路就是基于工厂模式的。
       DI容器相当于一个大的工厂类，负责在程序启动的时候，根据配置（要创建那些类对象，
       每个类对象的创建需要依赖那些其他类对象）事先创建好对象。当程序需要使用那个类对象
       的时候，直接从容器中获取即可。正是因为它持有一堆对象，所以这个框架被称为"容器"。
       
       DI 容器相对于我们上节课讲的工厂模式的例子来说，它处理的是更大的对象创建工程。
       上节课讲的工厂模式中，一个工厂类只负责某个类对象或者某一组相关类对象
       （继承自同一抽象类或者接口的子类）的创建，而 DI 容器负责的是整个应用中所有类对象的创建。

       除此之外，DI 容器负责的事情要比单纯的工厂模式要多。比如，它还包括配置的解析、对象生命周期的管理。
       
2、 DI 容器的核心功能有哪些？
    
    DI 容器的核心功能一般有三个：配置解析、对象创建和对象生命周期管理。
    
    (1) 配置解析
        作为一个通用的框架来说，框架代码跟应用代码应该是高度解耦的，DI 容器事先并不知道应用会创建哪些对象，
        不可能把某个应用要创建的对象写死在框架代码中。
        所以，我们需要通过一种形式，让应用告知 DI 容器要创建哪些对象。这种形式就是我们要讲的配置。 
        
       下面是一个典型的 Spring 容器的配置文件。Spring 容器读取这个配置文件，
       解析出要创建的两个对象：rateLimiter 和 redisCounter，
       并且得到两者的依赖关系：rateLimiter 依赖 redisCounter。 
       
       
        public class RateLimiter {
          private RedisCounter redisCounter;
          public RateLimiter(RedisCounter redisCounter) {
            this.redisCounter = redisCounter;
          }
          public void test() {
            System.out.println("Hello World!");
          }
          //...
        }
        
        public class RedisCounter {
          private String ipAddress;
          private int port;
          public RedisCounter(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
          }
          //...
        }
        
        配置文件beans.xml：
        <beans>
           <bean id="rateLimiter" class="com.xzg.RateLimiter">
              <constructor-arg ref="redisCounter"/>
           </bean>
         
           <bean id="redisCounter" class="com.xzg.redisCounter">
             <constructor-arg type="String" value="127.0.0.1">
             <constructor-arg type="int" value=1234>
           </bean>
        </beans>  
        
    (2)其次，我们再来看对象创建。
        
        在 DI 容器中，如果我们给每个类都对应创建一个工厂类，那项目中类的个数会成倍增加，
        这会增加代码的维护成本。要解决这个问题并不难。
        我们只需要将所有类对象的创建都放到一个工厂类中完成就可以了，比如 BeansFactory。        
        
        你可能会说，如果要创建的类对象非常多，BeansFactory 中的代码会不会线性膨胀
        （代码量跟创建对象的个数成正比）呢？实际上并不会。
        待会讲到 DI 容器的具体实现的时候，我们会讲“反射”这种机制，
        它能在程序运行的过程中，动态地加载类、创建对象，不需要事先在代码中写死要创建哪些对象。
        所以，不管是创建一个对象还是十个对象，BeansFactory 工厂类代码都是一样的。
    
    (3)最后，我们来看对象的生命周期管理。
    
        简单工厂模式有两种实现方式，一种是每次都返回新创建的对象，另一种是每次都返回同一个事先创建好的对象，也就是所谓的单例对象。
        在 Spring 框架中，我们可以通过配置 scope 属性，来区分这两种不同类型的对象。scope=prototype 表示返回新创建的对象，
        scope=singleton 表示返回单例对象。
        
        除此之外，我们还可以配置对象是否支持懒加载。如果 lazy-init=true，对象在真正被使用到的时候
        （比如：BeansFactory.getBean(“userService”)）才被被创建；如果 lazy-init=false，
        对象在应用启动的时候就事先创建好。不仅如此，我们还可以配置对象的 init-method 和 destroy-method 方法，
        比如 init-method=loadProperties()，destroy-method=updateConfigFile()。
        
        DI 容器在创建好对象之后，会主动调用 init-method 属性指定的方法来初始化对象。
        在对象被最终销毁之前，DI 容器会主动调用 destroy-method 属性指定的方法来做一些清理工作，
        比如释放数据库连接池、关闭文件。
    
如何实现一个简单的 DI 容器？

    用 Java 语言来实现一个简单的 DI 容器，核心逻辑只需要包括这样两个部分：配置文件解析、根据配置文件通过“反射”语法来创建对象。
    
    1、最小原型设计  
        
        配置文件beans.xml
        <beans>
           <bean id="rateLimiter" class="com.xzg.RateLimiter">
              <constructor-arg ref="redisCounter"/>
           </bean>
         
           <bean id="redisCounter" class="com.xzg.redisCounter" scope="singleton" lazy-init="true">
             <constructor-arg type="String" value="127.0.0.1">
             <constructor-arg type="int" value=1234>
           </bean>
        </bean
    
        
        配置文件beans.xml
        <beans>
           <bean id="rateLimiter" class="com.xzg.RateLimiter">
              <constructor-arg ref="redisCounter"/>
           </bean>
         
           <bean id="redisCounter" class="com.xzg.redisCounter" scope="singleton" lazy-init="true">
             <constructor-arg type="String" value="127.0.0.1">
             <constructor-arg type="int" value=1234>
           </bean>
        </bean    
        
    2. 提供执行入口:
    
        面向对象设计的最后一步是：组装类并提供执行入口。在这里，执行入口就是一组暴露给外部使用的接口和类。
        
        通过刚刚的最小原型使用示例代码，我们可以看出，执行入口主要包含两部分：ApplicationContext 和 ClassPathXmlApplicationContext。
        其中，ApplicationContext 是接口，ClassPathXmlApplicationContext 是接口的实现类。两个类具体实现如下所示    
        
        
            public interface ApplicationContext {
              Object getBean(String beanId);
            }
            
            public class ClassPathXmlApplicationContext implements ApplicationContext {
              private BeansFactory beansFactory;
              private BeanConfigParser beanConfigParser;
            
              public ClassPathXmlApplicationContext(String configLocation) {
                this.beansFactory = new BeansFactory();
                this.beanConfigParser = new XmlBeanConfigParser();
                loadBeanDefinitions(configLocation);
              }
            
              private void loadBeanDefinitions(String configLocation) {
                InputStream in = null;
                try {
                  in = this.getClass().getResourceAsStream("/" + configLocation);
                  if (in == null) {
                    throw new RuntimeException("Can not find config file: " + configLocation);
                  }
                  List<BeanDefinition> beanDefinitions = beanConfigParser.parse(in);
                  beansFactory.addBeanDefinitions(beanDefinitions);
                } finally {
                  if (in != null) {
                    try {
                      in.close();
                    } catch (IOException e) {
                      // TODO: log error
                    }
                  }
                }
              }
            
              @Override
              public Object getBean(String beanId) {
                return beansFactory.getBean(beanId);
              }
            }
        
        从上面的代码中，我们可以看出，ClassPathXmlApplicationContext 负责组装 BeansFactory 和 BeanConfigParser 两个类，
        串联执行流程：从 classpath 中加载 XML 格式的配置文件，通过 BeanConfigParser 解析为统一的 BeanDefinition 格式，
        然后，BeansFactory 根据 BeanDefinition 来创建对象。
        
    3. 配置文件解析
    
        配置文件解析主要包含 BeanConfigParser 接口和 XmlBeanConfigParser 实现类，
        负责将配置文件解析为 BeanDefinition 结构，以便 BeansFactory 根据这个结构来创建对象。           
        
        
        public interface BeanConfigParser {
          List<BeanDefinition> parse(InputStream inputStream);
          List<BeanDefinition> parse(String configContent);
        }
        
        public class XmlBeanConfigParser implements BeanConfigParser {
        
          @Override
          public List<BeanDefinition> parse(InputStream inputStream) {
            String content = null;
            // TODO:...
            return parse(content);
          }
        
          @Override
          public List<BeanDefinition> parse(String configContent) {
            List<BeanDefinition> beanDefinitions = new ArrayList<>();
            // TODO:...
            return beanDefinitions;
          }
        
        }
        
        public class BeanDefinition {
          private String id;
          private String className;
          private List<ConstructorArg> constructorArgs = new ArrayList<>();
          private Scope scope = Scope.SINGLETON;
          private boolean lazyInit = false;
          // 省略必要的getter/setter/constructors
         
          public boolean isSingleton() {
            return scope.equals(Scope.SINGLETON);
          }
        
        
          public static enum Scope {
            SINGLETON,
            PROTOTYPE
          }
          
          public static class ConstructorArg {
            private boolean isRef;
            private Class type;
            private Object arg;
            // 省略必要的getter/setter/constructors
          }
        }
        
    4、核心工厂类设计
        
        最后，我们来看，BeansFactory 是如何设计和实现的。这也是我们这个 DI 容器最核心的一个类了。
        它负责根据从配置文件解析得到的 BeanDefinition 来创建对象。
        
        实际上，BeansFactory 创建对象用到的主要技术点就是 Java 中的反射语法：一种动态加载类和创建对象的机制。
        我们知道，JVM 在启动的时候会根据代码自动地加载类、创建对象。
        至于都要加载哪些类、创建哪些对象，这些都是在代码中写死的，或者说提前写好的。
        但是，如果某个对象的创建并不是写死在代码中，而是放到配置文件中，
        我们需要在程序运行期间，动态地根据配置文件来加载类、创建对象，
        那这部分工作就没法让 JVM 帮我们自动完成了，我们需要利用 Java 提供的反射语法自己去编写代码。
        
        
        public class BeansFactory {
          private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
          private ConcurrentHashMap<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
        
          public void addBeanDefinitions(List<BeanDefinition> beanDefinitionList) {
            for (BeanDefinition beanDefinition : beanDefinitionList) {
              this.beanDefinitions.putIfAbsent(beanDefinition.getId(), beanDefinition);
            }
        
            for (BeanDefinition beanDefinition : beanDefinitionList) {
              if (beanDefinition.isLazyInit() == false && beanDefinition.isSingleton()) {
                createBean(beanDefinition);
              }
            }
          }
        
          public Object getBean(String beanId) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanId);
            if (beanDefinition == null) {
              throw new NoSuchBeanDefinitionException("Bean is not defined: " + beanId);
            }
            return createBean(beanDefinition);
          }
        
          @VisibleForTesting
          protected Object createBean(BeanDefinition beanDefinition) {
            if (beanDefinition.isSingleton() && singletonObjects.contains(beanDefinition.getId())) {
              return singletonObjects.get(beanDefinition.getId());
            }
        
            Object bean = null;
            try {
              Class beanClass = Class.forName(beanDefinition.getClassName());
              List<BeanDefinition.ConstructorArg> args = beanDefinition.getConstructorArgs();
              if (args.isEmpty()) {
                bean = beanClass.newInstance();
              } else {
                Class[] argClasses = new Class[args.size()];
                Object[] argObjects = new Object[args.size()];
                for (int i = 0; i < args.size(); ++i) {
                  BeanDefinition.ConstructorArg arg = args.get(i);
                  if (!arg.getIsRef()) {
                    argClasses[i] = arg.getType();
                    argObjects[i] = arg.getArg();
                  } else {
                    BeanDefinition refBeanDefinition = beanDefinitions.get(arg.getArg());
                    if (refBeanDefinition == null) {
                      throw new NoSuchBeanDefinitionException("Bean is not defined: " + arg.getArg());
                    }
                    argClasses[i] = Class.forName(refBeanDefinition.getClassName());
                    argObjects[i] = createBean(refBeanDefinition);
                  }
                }
                bean = beanClass.getConstructor(argClasses).newInstance(argObjects);
              }
            } catch (ClassNotFoundException | IllegalAccessException
                    | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
              throw new BeanCreationFailureException("", e);
            }
        
            if (bean != null && beanDefinition.isSingleton()) {
              singletonObjects.putIfAbsent(beanDefinition.getId(), bean);
              return singletonObjects.get(beanDefinition.getId());
            }
            return bean;
          }
        }
     