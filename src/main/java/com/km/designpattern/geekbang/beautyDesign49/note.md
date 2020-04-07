1、桥接模式:

    桥接模式，也叫做桥梁模式，英文是 Bridge Design Pattern。
    对于这个模式有两种不同的理解方式。
        (1) 将抽象和实现解耦，让它们可以独立变化
        (2) 一个类存在两个（或多个）独立变化的维度，我们通过组合的方式，让这两个（或多个）维度可以独立进行扩展。
            通过组合关系来替代继承关系，避免继承层次的指数级爆炸。
            这种理解方式非常类似于，我们之前讲过的“组合优于继承”设计原则
    
    JDBC 驱动是桥接模式的经典应用。我们先来看一下，如何利用 JDBC 驱动来查询数据库。具体的代码如下所示：  
    
        Class.forName("com.mysql.jdbc.Driver");//加载及注册JDBC驱动程序
        String url = "jdbc:mysql://localhost:3306/sample_db?user=root&password=your_password";
        Connection con = DriverManager.getConnection(url);
        Statement stmt = con.createStatement()；
        String query = "select * from test";
        ResultSet rs=stmt.executeQuery(query);
        while(rs.next()) {
          rs.getString(1);
          rs.getInt(2);
        }      
            
    如果我们想要把 MySQL 数据库换成 Oracle 数据库，只需要把第一行代码中的 com.mysql.jdbc.Driver 
    换成 oracle.jdbc.driver.OracleDriver 就可以了。
    当然，也有更灵活的实现方式，我们可以把需要加载的 Driver 类写到配置文件中，当程序启动的时候，
    自动从配置文件中加载，这样在切换数据库的时候，我们都不需要修改代码，只需要修改配置文件就可以了。
    
    不管是改代码还是改配置，在项目中，从一个数据库切换到另一种数据库，都只需要改动很少的代码，
    或者完全不需要改动代码，那如此优雅的数据库切换是如何实现的呢？
    
    源码之下无秘密。要弄清楚这个问题，我们先从 com.mysql.jdbc.Driver 这个类的代码看起。
    我摘抄了部分相关代码，放到了这里，你可以看一下。        
    
        package com.mysql.jdbc;
        import java.sql.SQLException;
        
        public class Driver extends NonRegisteringDriver implements java.sql.Driver {
          static {
            try {
              java.sql.DriverManager.registerDriver(new Driver());
            } catch (SQLException E) {
              throw new RuntimeException("Can't register driver!");
            }
          }
        
          /**
           * Construct a new driver and register it with DriverManager
           * @throws SQLException if a database error occurs.
           */
          public Driver() throws SQLException {
            // Required for Class.forName().newInstance()
          }
        }
        
    结合 com.mysql.jdbc.Driver 的代码实现，我们可以发现，
    当执行 Class.forName(“com.mysql.jdbc.Driver”) 这条语句的时候，实际上是做了两件事情。
    第一件事情是要求 JVM 查找并加载指定的 Driver 类，
    第二件事情是执行该类的静态代码，也就是将 MySQL Driver 注册到 DriverManager 类中。
    
    现在，我们再来看一下，DriverManager 类是干什么用的。具体的代码如下所示。
    当我们把具体的 Driver 实现类（比如，com.mysql.jdbc.Driver）注册到 DriverManager 之后，
    后续所有对 JDBC 接口的调用，都会委派到对具体的 Driver 实现类来执行。
    而 Driver 实现类都实现了相同的接口（java.sql.Driver ），这也是可以灵活切换 Driver 的原因。   
    
        public class DriverManager {
          private final static CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList<DriverInfo>();
        
          //...
          static {
            loadInitialDrivers();
            println("JDBC DriverManager initialized");
          }
          //...
        
          public static synchronized void registerDriver(java.sql.Driver driver) throws SQLException {
            if (driver != null) {
              registeredDrivers.addIfAbsent(new DriverInfo(driver));
            } else {
              throw new NullPointerException();
            }
          }
        
          public static Connection getConnection(String url, String user, String password) throws SQLException {
            java.util.Properties info = new java.util.Properties();
            if (user != null) {
              info.put("user", user);
            }
            if (password != null) {
              info.put("password", password);
            }
            return (getConnection(url, info, Reflection.getCallerClass()));
          }
          //...
        }
        
    桥接模式的定义是“将抽象和实现解耦，让它们可以独立变化”。那弄懂定义中“抽象”和“实现”两个概念，就是理解桥接模式的关键。
    那在 JDBC 这个例子中，什么是“抽象”？什么是“实现”呢？    
    
    实际上，JDBC 本身就相当于“抽象”。
    注意，这里所说的“抽象”，指的并非“抽象类”或“接口”，而是跟具体的数据库无关的、被抽象出来的一套“类库”。
    具体的 Driver（比如，com.mysql.jdbc.Driver）就相当于“实现”。
    注意，这里所说的“实现”，也并非指“接口的实现类”，而是跟具体数据库相关的一套“类库”。
    JDBC 和 Driver 独立开发，通过对象之间的组合关系，组装在一起。
    JDBC 的所有逻辑操作，最终都委托给 Driver 来执行。
    
    我们讲过一个 API 接口监控告警的例子：根据不同的告警规则，触发不同类型的告警。
    告警支持多种通知渠道，包括：邮件、短信、微信、自动语音电话。通知的紧急程度有多种类型，
    包括：SEVERE（严重）、URGENCY（紧急）、NORMAL（普通）、TRIVIAL（无关紧要）。
    不同的紧急程度对应不同的通知渠道。比如，SERVE（严重）级别的消息会通过“自动语音电话”告知相关人员。   
   
        public enum NotificationEmergencyLevel {
          SEVERE, URGENCY, NORMAL, TRIVIAL
        }
        
        public class Notification {
          private List<String> emailAddresses;
          private List<String> telephones;
          private List<String> wechatIds;
        
          public Notification() {}
        
          public void setEmailAddress(List<String> emailAddress) {
            this.emailAddresses = emailAddress;
          }
        
          public void setTelephones(List<String> telephones) {
            this.telephones = telephones;
          }
        
          public void setWechatIds(List<String> wechatIds) {
            this.wechatIds = wechatIds;
          }
        
          public void notify(NotificationEmergencyLevel level, String message) {
            if (level.equals(NotificationEmergencyLevel.SEVERE)) {
              //...自动语音电话
            } else if (level.equals(NotificationEmergencyLevel.URGENCY)) {
              //...发微信
            } else if (level.equals(NotificationEmergencyLevel.NORMAL)) {
              //...发邮件
            } else if (level.equals(NotificationEmergencyLevel.TRIVIAL)) {
              //...发邮件
            }
          }
        }
        
        //在API监控告警的例子中，我们如下方式来使用Notification类：
        public class ErrorAlertHandler extends AlertHandler {
          public ErrorAlertHandler(AlertRule rule, Notification notification){
            super(rule, notification);
          }
        
        
          @Override
          public void check(ApiStatInfo apiStatInfo) {
            if (apiStatInfo.getErrorCount() > rule.getMatchedRule(apiStatInfo.getApi()).getMaxErrorCount()) {
              notification.notify(NotificationEmergencyLevel.SEVERE, "...");
            }
          }
        } 
    
    我们对代码进行重构。重构之后的代码如下所示：
    
        public interface MsgSender {
          void send(String message);
        }
        
        public class TelephoneMsgSender implements MsgSender {
          private List<String> telephones;
        
          public TelephoneMsgSender(List<String> telephones) {
            this.telephones = telephones;
          }
        
          @Override
          public void send(String message) {
            //...
          }
        
        }
        
        public class EmailMsgSender implements MsgSender {
          // 与TelephoneMsgSender代码结构类似，所以省略...
        }
        
        public class WechatMsgSender implements MsgSender {
          // 与TelephoneMsgSender代码结构类似，所以省略...
        }
        
        public abstract class Notification {
          protected MsgSender msgSender;
        
          public Notification(MsgSender msgSender) {
            this.msgSender = msgSender;
          }
        
          public abstract void notify(String message);
        }
        
        public class SevereNotification extends Notification {
          public SevereNotification(MsgSender msgSender) {
            super(msgSender);
          }
        
          @Override
          public void notify(String message) {
            msgSender.send(message);
          }
        }
        
        public class UrgencyNotification extends Notification {
          // 与SevereNotification代码结构类似，所以省略...
        }
        public class NormalNotification extends Notification {
          // 与SevereNotification代码结构类似，所以省略...
        }
        public class TrivialNotification extends Notification {
          // 与SevereNotification代码结构类似，所以省略...
        }
                