策略模式（Strategy Design Pattern）：
   
    利用它来避免冗长的 if-else 或 switch 分支判断。  

1、策略模式的原理与实现：
    
    定义一族算法类，将每个算法分别封装起来，让它们可以互相替换。
    策略模式可以使算法的变化独立于使用它们的客户端（这里的客户端代指使用算法的代码）。
    
    工厂模式是解耦对象的创建和使用，观察者模式是解耦观察者和被观察者。
    策略模式跟两者类似，也能起到解耦的作用，不过，它解耦的是策略的定义、创建、使用这三部分
    

2、策略的定义：

    策略类的定义比较简单，包含一个策略接口和一组实现这个接口的策略类。
    因为所有的策略类都实现相同的接口，所以，客户端代码基于接口而非实现编程，可以灵活地替换不同的策略。
    示例代码如下所示：    
    
    
        public interface Strategy {
          void algorithmInterface();
        }
        
        public class ConcreteStrategyA implements Strategy {
          @Override
          public void  algorithmInterface() {
            //具体的算法...
          }
        }
        
        public class ConcreteStrategyB implements Strategy {
          @Override
          public void  algorithmInterface() {
            //具体的算法...
          }
        }
        
2、策略的创建：
    
    因为策略模式会包含一组策略，在使用它们的时候，一般会通过类型（type）来判断创建哪个策略来使用。
    为了封装创建逻辑，我们需要对客户端代码屏蔽创建细节。
    我们可以把根据 type 创建策略的逻辑抽离出来，放到工厂类中。            
    
    
    public class StrategyFactory {
      private static final Map<String, Strategy> strategies = new HashMap<>();
    
      static {
        strategies.put("A", new ConcreteStrategyA());
        strategies.put("B", new ConcreteStrategyB());
      }
    
      public static Strategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
          throw new IllegalArgumentException("type should not be empty.");
        }
        return strategies.get(type);
      }
    }
    
    一般来讲，如果策略类是无状态的，不包含成员变量，只是纯粹的算法实现，这样的策略对象是可以被共享使用的，
    不需要在每次调用 getStrategy() 的时候，都创建一个新的策略对象。
    针对这种情况，我们可以使用上面这种工厂类的实现方式，事先创建好每个策略对象，
    缓存到工厂类中，用的时候直接返回。        
    
    相反，如果策略类是有状态的，根据业务场景的需要，我们希望每次从工厂方法中，
    获得的都是新创建的策略对象，而不是缓存好可共享的策略对象，
    那我们就需要按照如下方式来实现策略工厂类。
    
        public class StrategyFactory {
          public static Strategy getStrategy(String type) {
            if (type == null || type.isEmpty()) {
              throw new IllegalArgumentException("type should not be empty.");
            }
        
            if (type.equals("A")) {
              return new ConcreteStrategyA();
            } else if (type.equals("B")) {
              return new ConcreteStrategyB();
            }
        
            return null;
          }
        }
            
3、策略的使用：

    策略模式包含一组可选策略，客户端代码一般如何确定使用哪个策略呢？
    最常见的是运行时动态确定使用哪种策略，这也是策略模式最典型的应用场景。    
    
    这里的“运行时动态”指的是，我们事先并不知道会使用哪个策略，
    而是在程序运行期间，根据配置、用户输入、计算结果等这些不确定因素，动态决定使用哪种策略。
    
    
        // 策略接口：EvictionStrategy
        // 策略类：LruEvictionStrategy、FifoEvictionStrategy、LfuEvictionStrategy...
        // 策略工厂：EvictionStrategyFactory
        
        public class UserCache {
          private Map<String, User> cacheData = new HashMap<>();
          private EvictionStrategy eviction;
        
          public UserCache(EvictionStrategy eviction) {
            this.eviction = eviction;
          }
        
          //...
        }
        
        // 运行时动态确定，根据配置文件的配置决定使用哪种策略
        public class Application {
          public static void main(String[] args) throws Exception {
            EvictionStrategy evictionStrategy = null;
            Properties props = new Properties();
            props.load(new FileInputStream("./config.properties"));
            String type = props.getProperty("eviction_type");
            evictionStrategy = EvictionStrategyFactory.getEvictionStrategy(type);
            UserCache userCache = new UserCache(evictionStrategy);
            //...
          }
        }
        
        // 非运行时动态确定，在代码中指定使用哪种策略
        public class Application {
          public static void main(String[] args) {
            //...
            EvictionStrategy evictionStrategy = new LruEvictionStrategy();
            UserCache userCache = new UserCache(evictionStrategy);
            //...
          }
        }
    
    从上面的代码中，我们也可以看出，“非运行时动态确定”，也就是第二个 Application 中的使用方式，并不能发挥策略模式的优势。
    在这种应用场景下，策略模式实际上退化成了“面向对象的多态特性”或“基于接口而非实现编程原则”。    

4、如何利用策略模式避免分支判断：
    
    策略模式适用于根据不同类型的动态，决定使用哪种策略这样一种应用场景。
    
    
        public class OrderService {
          public double discount(Order order) {
            double discount = 0.0;
            OrderType type = order.getType();
            if (type.equals(OrderType.NORMAL)) { // 普通订单
              //...省略折扣计算算法代码
            } else if (type.equals(OrderType.GROUPON)) { // 团购订单
              //...省略折扣计算算法代码
            } else if (type.equals(OrderType.PROMOTION)) { // 促销订单
              //...省略折扣计算算法代码
            }
            return discount;
          }
        }

    我们使用策略模式对上面的代码重构，将不同类型订单的打折策略设计成策略类，
    并由工厂类来负责创建策略对象。具体的代码如下所示：
    
        
        // 策略的定义
        public interface DiscountStrategy {
          double calDiscount(Order order);
        }
        // 省略NormalDiscountStrategy、GrouponDiscountStrategy、PromotionDiscountStrategy类代码...
        
        // 策略的创建
        public class DiscountStrategyFactory {
          private static final Map<OrderType, DiscountStrategy> strategies = new HashMap<>();
        
          static {
            strategies.put(OrderType.NORMAL, new NormalDiscountStrategy());
            strategies.put(OrderType.GROUPON, new GrouponDiscountStrategy());
            strategies.put(OrderType.PROMOTION, new PromotionDiscountStrategy());
          }
        
          public static DiscountStrategy getDiscountStrategy(OrderType type) {
            return strategies.get(type);
          }
        }
        
        // 策略的使用
        public class OrderService {
          public double discount(Order order) {
            OrderType type = order.getType();
            DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(type);
            return discountStrategy.calDiscount(order);
          }
        }
            
    但是，如果业务场景需要每次都创建不同的策略对象，我们就要用另外一种工厂类的实现方式了。具体的代码如下所示：
        
        public class DiscountStrategyFactory {
          public static DiscountStrategy getDiscountStrategy(OrderType type) {
            if (type == null) {
              throw new IllegalArgumentException("Type should not be null.");
            }
            if (type.equals(OrderType.NORMAL)) {
              return new NormalDiscountStrategy();
            } else if (type.equals(OrderType.GROUPON)) {
              return new GrouponDiscountStrategy();
            } else if (type.equals(OrderType.PROMOTION)) {
              return new PromotionDiscountStrategy();
            }
            return null;
          }
        }        
    
    
    策略模式定义一族算法类，将每个算法分别封装起来，让它们可以互相替换。
    策略模式可以使算法的变化独立于使用它们的客户端（这里的客户端代指使用算法的代码）。
    
    策略模式用来解耦策略的定义、创建、使用。实际上，一个完整的策略模式就是由这三个部分组成的。
    策略类的定义比较简单，包含一个策略接口和一组实现这个接口的策略类。
    策略的创建由工厂类来完成，封装策略创建的细节。
    策略模式包含一组策略可选，客户端代码如何选择使用哪个策略，
    有两种确定方法：编译时静态确定和运行时动态确定。其中，“运行时动态确定”才是策略模式最典型的应用场景。
    
    除此之外，我们还可以通过策略模式来移除 if-else 分支判断。
    实际上，这得益于策略工厂类，更本质上点讲，是借助“查表法”，根据 type 查表替代根据 type 分支判断。
    