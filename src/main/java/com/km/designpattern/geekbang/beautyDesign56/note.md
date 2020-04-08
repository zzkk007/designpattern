观察者模式:

1、原理及应用场景刨析：
    
    观察者模式(Observer Design Pattern) 也被称为 发布订阅模式(Publish-Subscribe Design Pattern)
    
    翻译称中文就是: 在对象之间定义一个一对多的依赖，当一个对象状态改变的时候，所有依赖的对象都会自动收到通知。
    一般情况下，被依赖的对象叫作被观察者（Observable），依赖的对象叫作观察者（Observer）。
    
    
        public interface Subject {
          void registerObserver(Observer observer);
          void removeObserver(Observer observer);
          void notifyObservers(Message message);
        }
        
        public interface Observer {
          void update(Message message);
        }
        
        public class ConcreteSubject implements Subject {
          private List<Observer> observers = new ArrayList<Observer>();
        
          @Override
          public void registerObserver(Observer observer) {
            observers.add(observer);
          }
        
          @Override
          public void removeObserver(Observer observer) {
            observers.remove(observer);
          }
        
          @Override
          public void notifyObservers(Message message) {
            for (Observer observer : observers) {
              observer.update(message);
            }
          }
        
        }
        
        public class ConcreteObserverOne implements Observer {
          @Override
          public void update(Message message) {
            //TODO: 获取消息通知，执行自己的逻辑...
            System.out.println("ConcreteObserverOne is notified.");
          }
        }
        
        public class ConcreteObserverTwo implements Observer {
          @Override
          public void update(Message message) {
            //TODO: 获取消息通知，执行自己的逻辑...
            System.out.println("ConcreteObserverTwo is notified.");
          }
        }
        
        public class Demo {
          public static void main(String[] args) {
            ConcreteSubject subject = new ConcreteSubject();
            subject.registerObserver(new ConcreteObserverOne());
            subject.registerObserver(new ConcreteObserverTwo());
            subject.notifyObservers(new Message());
          }
        } 
        
    实际上，上面的代码算是观察者模式的“模板代码”，只能反映大体的设计思路。
    在真实的软件开发中，并不需要照搬上面的模板代码。
    观察者模式的实现方法各式各样，函数、类的命名等会根据业务场景的不同有很大的差别，
    比如 register 函数还可以叫作 attach，remove 函数还可以叫作 detach 等等。
    不过，万变不离其宗，设计思路都是差不多的。

    假设我们在开发一个 P2P 投资理财系统，用户注册成功之后，我们会给用户发放投资体验金。
    代码实现大致是下面这个样子的：
        
        public class UserController {
          private UserService userService; // 依赖注入
          private PromotionService promotionService; // 依赖注入
        
          public Long register(String telephone, String password) {
            //省略输入参数的校验代码
            //省略userService.register()异常的try-catch代码
            long userId = userService.register(telephone, password);
            promotionService.issueNewUserExperienceCash(userId);
            return userId;
          }
        }    
        
    这个时候，观察者模式就能派上用场了。利用观察者模式，我对上面的代码进行了重构。重构之后的代码如下所示：
        
        public interface RegObserver {
          void handleRegSuccess(long userId);
        }
        
        public class RegPromotionObserver implements RegObserver {
          private PromotionService promotionService; // 依赖注入
        
          @Override
          public void handleRegSuccess(long userId) {
            promotionService.issueNewUserExperienceCash(userId);
          }
        }
        
        public class RegNotificationObserver implements RegObserver {
          private NotificationService notificationService;
        
          @Override
          public void handleRegSuccess(long userId) {
            notificationService.sendInboxMessage(userId, "Welcome...");
          }
        }
        
        public class UserController {
          private UserService userService; // 依赖注入
          private List<RegObserver> regObservers = new ArrayList<>();
        
          // 一次性设置好，之后也不可能动态的修改
          public void setRegObservers(List<RegObserver> observers) {
            regObservers.addAll(observers);
          }
        
          public Long register(String telephone, String password) {
            //省略输入参数的校验代码
            //省略userService.register()异常的try-catch代码
            long userId = userService.register(telephone, password);
        
            for (RegObserver observer : regObservers) {
              observer.handleRegSuccess(userId);
            }
        
            return userId;
          }
        }
    
    当我们需要添加新的观察者的时候，比如，用户注册成功之后，推送用户注册信息给大数据征信系统，
    基于观察者模式的代码实现，UserController 类的 register() 函数完全不需要修改，
    只需要再添加一个实现了 RegObserver 接口的类，并且通过 setRegObservers() 函数
    将它注册到 UserController 类中即可。      
    
    具体到观察者模式，它是将观察者和被观察者代码解耦。
    借助设计模式，我们利用更好的代码结构，将一大坨代码拆分成职责更单一的小类，
    让其满足开闭原则、高内聚松耦合等特性，以此来控制和应对代码的复杂性，提高代码的可扩展性。  