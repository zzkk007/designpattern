一：代理模式：

1、代理模式的原理解析：

    public class UserController {
      //...省略其他属性和方法...
      private MetricsCollector metricsCollector; // 依赖注入
    
      public UserVo login(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();
    
        // ... 省略login逻辑...
    
        long endTimeStamp = System.currentTimeMillis();
        long responseTime = endTimeStamp - startTimestamp;
        RequestInfo requestInfo = new RequestInfo("login", responseTime, startTimestamp);
        metricsCollector.recordRequest(requestInfo);
    
        //...返回UserVo数据...
      }
    
      public UserVo register(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();
    
        // ... 省略register逻辑...
    
        long endTimeStamp = System.currentTimeMillis();
        long responseTime = endTimeStamp - startTimestamp;
        RequestInfo requestInfo = new RequestInfo("register", responseTime, startTimestamp);
        metricsCollector.recordRequest(requestInfo);
    
        //...返回UserVo数据...
      }
    }
    
    很明显，上面的写法有两个问题。
    第一，性能计数器框架代码侵入到业务代码中，跟业务代码高度耦合。如果未来需要替换这个框架，那替换的成本会比较大。
    第二，收集接口请求的代码跟业务代码无关，本就不应该放到一个类中。业务类最好职责更加单一，只聚焦业务处理。
    
    为了将框架代码和业务代码解耦，代理模式就派上用场了。代理类 UserControllerProxy 和原始类 UserController
    实现相同的接口 IUserController。 UserController 类只负责业务功能。代理类 UserControllerProxy 负责
    在业务代码执行前后附加其他逻辑代码，并通过委托的方式调用原始类来执行业务代码。
    
        public interface IUserController {
          UserVo login(String telephone, String password);
          UserVo register(String telephone, String password);
        }
        
        public class UserController implements IUserController {
          //...省略其他属性和方法...
        
          @Override
          public UserVo login(String telephone, String password) {
            //...省略login逻辑...
            //...返回UserVo数据...
          }
        
          @Override
          public UserVo register(String telephone, String password) {
            //...省略register逻辑...
            //...返回UserVo数据...
          }
        }
        
        public class UserControllerProxy implements IUserController {
          private MetricsCollector metricsCollector;
          private UserController userController;
        
          public UserControllerProxy(UserController userController) {
            this.userController = userController;
            this.metricsCollector = new MetricsCollector();
          }
        
          @Override
          public UserVo login(String telephone, String password) {
            long startTimestamp = System.currentTimeMillis();
        
            // 委托
            UserVo userVo = userController.login(telephone, password);
        
            long endTimeStamp = System.currentTimeMillis();
            long responseTime = endTimeStamp - startTimestamp;
            RequestInfo requestInfo = new RequestInfo("login", responseTime, startTimestamp);
            metricsCollector.recordRequest(requestInfo);
        
            return userVo;
          }
        
          @Override
          public UserVo register(String telephone, String password) {
            long startTimestamp = System.currentTimeMillis();
        
            UserVo userVo = userController.register(telephone, password);
        
            long endTimeStamp = System.currentTimeMillis();
            long responseTime = endTimeStamp - startTimestamp;
            RequestInfo requestInfo = new RequestInfo("register", responseTime, startTimestamp);
            metricsCollector.recordRequest(requestInfo);
        
            return userVo;
          }
        }
        
        //UserControllerProxy使用举例
        //因为原始类和代理类实现相同的接口，是基于接口而非实现编程
        //将UserController类对象替换为UserControllerProxy类对象，不需要改动太多代码
        IUserController userController = new UserControllerProxy(new UserController());
    
    参照基于接口而非实现编程的设计思想，将原始类对象替换成代理类对象的时候，为了让代码改动尽量减少，
    在刚刚的代理模式的代码实现中，代理类和原始类需要实现相同的接口。
    但是，如果原始类并没有定义接口，并且原始类代码并不是我们开发维护的（比如它来自一个第三方的类库），
    我们也没办法直接修改原始类，给它重新定义一个接口。在这种情况下，我们该如何实现代理模式呢？
    
    对于这种外部类的扩展，我们一般都是采用继承的方式。这里也不例外。
    我们让代理类继承原始类，然后扩展附加功能。原理很简单，不需要过多解释，
    你直接看代码就能明白。具体代码如下所示：
        public class UserControllerProxy extends UserController {
          private MetricsCollector metricsCollector;
        
          public UserControllerProxy() {
            this.metricsCollector = new MetricsCollector();
          }
        
          public UserVo login(String telephone, String password) {
            long startTimestamp = System.currentTimeMillis();
        
            UserVo userVo = super.login(telephone, password);
        
            long endTimeStamp = System.currentTimeMillis();
            long responseTime = endTimeStamp - startTimestamp;
            RequestInfo requestInfo = new RequestInfo("login", responseTime, startTimestamp);
            metricsCollector.recordRequest(requestInfo);
        
            return userVo;
          }
        
          public UserVo register(String telephone, String password) {
            long startTimestamp = System.currentTimeMillis();
        
            UserVo userVo = super.register(telephone, password);
        
            long endTimeStamp = System.currentTimeMillis();
            long responseTime = endTimeStamp - startTimestamp;
            RequestInfo requestInfo = new RequestInfo("register", responseTime, startTimestamp);
            metricsCollector.recordRequest(requestInfo);
        
            return userVo;
          }
        }
        //UserControllerProxy使用举例
        UserController userController = new UserControllerProxy();

2、动态代理解析：

    不过，刚刚的代码实现还是有点问题。一方面，我们需要在代理类中，将原始类中的所有的方法，都重新实现一遍，
    并且为每个方法都附加相似的代码逻辑。另一方面，如果要添加的附加功能的类有不止一个，
    我们需要针对每个类都创建一个代理类。
    
    如果有 50 个要添加附加功能的原始类，那我们就要创建 50 个对应的代理类。
    这会导致项目中类的个数成倍增加，增加了代码维护成本。并且，每个代理类中的代码都有点像模板式的“重复”代码，
    也增加了不必要的开发成本。那这个问题怎么解决呢？            
    
    我们可以使用动态代理来解决这个问题。所谓动态代理（Dynamic Proxy），就是我们不事先为每个原始类编写代理类，
    而是在运行的时候，动态地创建原始类对应的代理类，然后在系统中用代理类替换掉原始类。
    
    动态代理底层依赖的就是 Java 的反射语法，MetricsCollectorProxy 作为一个动态代理类，
    动态地给每个需要收集接口请求信息的类创建代理类。
        
        
        public class MetricsCollectorProxy {
          private MetricsCollector metricsCollector;
        
          public MetricsCollectorProxy() {
            this.metricsCollector = new MetricsCollector();
          }
        
          public Object createProxy(Object proxiedObject) {
            Class<?>[] interfaces = proxiedObject.getClass().getInterfaces();
            DynamicProxyHandler handler = new DynamicProxyHandler(proxiedObject);
            return Proxy.newProxyInstance(proxiedObject.getClass().getClassLoader(), interfaces, handler);
          }
        
          private class DynamicProxyHandler implements InvocationHandler {
            private Object proxiedObject;
        
            public DynamicProxyHandler(Object proxiedObject) {
              this.proxiedObject = proxiedObject;
            }
        
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
              long startTimestamp = System.currentTimeMillis();
              Object result = method.invoke(proxiedObject, args);
              long endTimeStamp = System.currentTimeMillis();
              long responseTime = endTimeStamp - startTimestamp;
              String apiName = proxiedObject.getClass().getName() + ":" + method.getName();
              RequestInfo requestInfo = new RequestInfo(apiName, responseTime, startTimestamp);
              metricsCollector.recordRequest(requestInfo);
              return result;
            }
          }
        }
        
        //MetricsCollectorProxy使用举例
        MetricsCollectorProxy proxy = new MetricsCollectorProxy();
        IUserController userController = (IUserController) proxy.createProxy(new UserController());
    
    实际上，Spring AOP 底层的实现原理就是基于动态代理。
    
    
    
3. 代理模式的应用场景:
    
    
    代理模式常用在业务系统中开发一些非功能性需求，比如：监控、统计、鉴权、限流、事务、幂等、日志。
    我们将这些附加功能与业务功能解耦，放到代理类统一处理，让程序员只需要关注业务方面的开发。
    除此之外，代理模式还可以用在 RPC、缓存等应用场景中。    