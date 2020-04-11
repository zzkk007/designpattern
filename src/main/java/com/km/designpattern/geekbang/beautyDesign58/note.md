模板模式：剖析模板模式在JDK、Servlet、JUnit等中的应用

1、模板模式的原理和实现：
    
    模板模式主要是用来解决复用和扩展两个问题
    
    模板方法模式在一个方法中定义一个算法骨架，并将某些步骤推迟到子类中实现。
    模板方法模式可以让子类在不改变算法整体结构的情况下，重新定义算法中的某些步骤。
    这里的“算法”，我们可以理解为广义上的“业务逻辑”，并不特指数据结构和算法中的“算法”。
    这里的算法骨架就是“模板”，包含算法骨架的方法就是“模板方法”，这也是模板方法模式名字的由来。    
    
    templateMethod() 函数定义为 final，是为了避免子类重写它。
    method1() 和 method2() 定义为 abstract，是为了强迫子类去实现。
    不过，这些都不是必须的，在实际的项目开发中，模板模式的代码实现比较灵活，
        
        public abstract class AbstractClass {
          public final void templateMethod() {
            //...
            method1();
            //...
            method2();
            //...
          }
          
          protected abstract void method1();
          protected abstract void method2();
        }
        
        public class ConcreteClass1 extends AbstractClass {
          @Override
          protected void method1() {
            //...
          }
          
          @Override
          protected void method2() {
            //...
          }
        }
        
        public class ConcreteClass2 extends AbstractClass {
          @Override
          protected void method1() {
            //...
          }
          
          @Override
          protected void method2() {
            //...
          }
        }
        
        AbstractClass demo = ConcreteClass1();
        demo.templateMethod();
       
模板模式的作用一：复用
    
    模板模式把一个算法中不变的流程抽象到父类的模板方法 templateMethod() 中，
    将可变的部分 method1()、method2() 留给子类 ContreteClass1 和 ContreteClass2 来实现。
    所有的子类都可以复用父类中模板方法定义的流程代码。
    
模板模式作用二：扩展:

    模板模式的第二大作用的是扩展。这里所说的扩展，并不是指代码的扩展性，
    而是指框架的扩展性，有点类似我们之前讲到的控制反转，       
    
回调:回调的原理解析   

    相对于普通的函数调用来说，回调是一种双向调用关系。
    A 类事先注册某个函数 F 到 B 类，A 类在调用 B 类的 P 函数的时候，B 类反过来调用 A 类注册给它的 F 函数。
    这里的 F 函数就是“回调函数”。A 调用 B，B 反过来又调用 A，这种调用机制就叫作“回调”。
    A 类如何将回调函数传递给 B 类呢？不同的编程语言，有不同的实现方法。
    C 语言可以使用函数指针，Java 则需要使用包裹了回调函数的类对象，我们简称为回调对象。
    这里我用 Java 语言举例说明一下。代码如下所示:
    
        public interface ICallback {
          void methodToCallback();
        }
        
        public class BClass {
          public void process(ICallback callback) {
            //...
            callback.methodToCallback();
            //...
          }
        }
        
        public class AClass {
          public static void main(String[] args) {
            BClass b = new BClass();
            b.process(new ICallback() { //回调对象
              @Override
              public void methodToCallback() {
                System.out.println("Call back me.");
              }
            });
          }
        }
                 
    回调可以分为同步回调和异步回调（或者延迟回调）。同步回调指在函数返回之前执行回调函数；
    异步回调指的是在函数返回之后执行回调函数。
    上面的代码实际上是同步回调的实现方式，在 process() 函数返回之前，执行完回调函数 methodToCallback()。
    
1、JdbcTemplate:
    
    Spring 提供了很多 Template 类，比如，JdbcTemplate、RedisTemplate、RestTemplate。
    尽管都叫作 xxxTemplate，但它们并非基于模板模式来实现的，而是基于回调来实现的，确切地说应该是同步回调。
    而同步回调从应用场景上很像模板模式，所以，在命名上，这些类使用 Template（模板）这个单词作为后缀。 
    
    Java 提供了 JDBC 类库来封装不同类型的数据库操作。
    不过，直接使用 JDBC 来编写操作数据库的代码，还是有点复杂的。
    比如，下面这段是使用 JDBC 来查询用户信息的代码。

        public class JdbcDemo {
          public User queryUser(long id) {
            Connection conn = null;
            Statement stmt = null;
            try {
              //1.加载驱动
              Class.forName("com.mysql.jdbc.Driver");
              conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "xzg", "xzg");
        
              //2.创建statement类对象，用来执行SQL语句
              stmt = conn.createStatement();
        
              //3.ResultSet类，用来存放获取的结果集
              String sql = "select * from user where id=" + id;
              ResultSet resultSet = stmt.executeQuery(sql);
        
              String eid = null, ename = null, price = null;
        
              while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setTelephone(resultSet.getString("telephone"));
                return user;
              }
            } catch (ClassNotFoundException e) {
              // TODO: log...
            } catch (SQLException e) {
              // TODO: log...
            } finally {
              if (conn != null)
                try {
                  conn.close();
                } catch (SQLException e) {
                  // TODO: log...
                }
              if (stmt != null)
                try {
                  stmt.close();
                } catch (SQLException e) {
                  // TODO: log...
                }
            }
            return null;
          }
        
        }
    
    queryUser() 函数包含很多流程性质的代码，跟业务无关，比如，加载驱动、创建数据库连接、
    创建 statement、关闭连接、关闭 statement、处理异常。针对不同的 SQL 执行请求，
    这些流程性质的代码是相同的、可以复用的，我们不需要每次都重新敲一遍。   
    
    针对这个问题，Spring 提供了 JdbcTemplate，对 JDBC 进一步封装，来简化数据库编程。
    使用 JdbcTemplate 查询用户信息，我们只需要编写跟这个业务有关的代码，
    其中包括，查询用户的 SQL 语句、查询结果与 User 对象之间的映射关系。
    其他流程性质的代码都封装在了 JdbcTemplate 类中，不需要我们每次都重新编写。
    我用 JdbcTemplate 重写了上面的例子，代码简单了很多，如下所示：    
    
        public class JdbcTemplateDemo {
          private JdbcTemplate jdbcTemplate;
        
          public User queryUser(long id) {
            String sql = "select * from user where id="+id;
            return jdbcTemplate.query(sql, new UserRowMapper()).get(0);
          }
        
          class UserRowMapper implements RowMapper<User> {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
              User user = new User();
              user.setId(rs.getLong("id"));
              user.setName(rs.getString("name"));
              user.setTelephone(rs.getString("telephone"));
              return user;
            }
          }
        }
    
    那 JdbcTemplate 底层具体是如何实现的呢？
    我们来看一下它的源码。因为 JdbcTemplate 代码比较多，我只摘抄了部分相关代码，贴到了下面。
    其中，JdbcTemplate 通过回调的机制，将不变的执行流程抽离出来，放到模板方法 execute() 中，
    将可变的部分设计成回调 StatementCallback，由用户来定制。
    query() 函数是对 execute() 函数的二次封装，让接口用起来更加方便。
    
        @Override
        public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
         return query(sql, new RowMapperResultSetExtractor<T>(rowMapper));
        }
        
        @Override
        public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
         Assert.notNull(sql, "SQL must not be null");
         Assert.notNull(rse, "ResultSetExtractor must not be null");
         if (logger.isDebugEnabled()) {
          logger.debug("Executing SQL query [" + sql + "]");
         }
        
         class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
          @Override
          public T doInStatement(Statement stmt) throws SQLException {
           ResultSet rs = null;
           try {
            rs = stmt.executeQuery(sql);
            ResultSet rsToUse = rs;
            if (nativeJdbcExtractor != null) {
             rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
            }
            return rse.extractData(rsToUse);
           }
           finally {
            JdbcUtils.closeResultSet(rs);
           }
          }
          @Override
          public String getSql() {
           return sql;
          }
         }
        
         return execute(new QueryStatementCallback());
        }
        
        @Override
        public <T> T execute(StatementCallback<T> action) throws DataAccessException {
         Assert.notNull(action, "Callback object must not be null");
        
         Connection con = DataSourceUtils.getConnection(getDataSource());
         Statement stmt = null;
         try {
          Connection conToUse = con;
          if (this.nativeJdbcExtractor != null &&
            this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
           conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
          }
          stmt = conToUse.createStatement();
          applyStatementSettings(stmt);
          Statement stmtToUse = stmt;
          if (this.nativeJdbcExtractor != null) {
           stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
          }
          T result = action.doInStatement(stmtToUse);
          handleWarnings(stmt);
          return result;
         }
         catch (SQLException ex) {
          // Release Connection early, to avoid potential connection pool deadlock
          // in the case when the exception translator hasn't been initialized yet.
          JdbcUtils.closeStatement(stmt);
          stmt = null;
          DataSourceUtils.releaseConnection(con, getDataSource());
          con = null;
          throw getExceptionTranslator().translate("StatementCallback", getSql(action), ex);
         }
         finally {
          JdbcUtils.closeStatement(stmt);
          DataSourceUtils.releaseConnection(con, getDataSource());
         }
        }

2、模板模式 VS 回调:

    从应用场景上来看，同步回调跟模板模式几乎一致。
    它们都是在一个大的算法骨架中，自由替换其中的某个步骤，起到代码复用和扩展的目的。
    而异步回调跟模板模式有较大差别，更像是观察者模式。
    
    从代码实现上来看，回调和模板模式完全不同。回调基于组合关系来实现，把一个对象传递给另一个对象，
    是一种对象之间的关系；模板模式基于继承关系来实现，子类重写父类的抽象方法，是一种类之间的关系。
    
    前面我们也讲到，组合优于继承。实际上，这里也不例外。在代码实现上，回调相对于模板模式会更加灵活，
    主要体现在下面几点。
        像 Java 这种只支持单继承的语言，基于模板模式编写的子类，已经继承了一个父类，不再具有继承的能力。
        回调可以使用匿名类来创建回调对象，可以不用事先定义类；而模板模式针对不同的实现都要定义不同的子类。
        如果某个类中定义了多个模板方法，每个方法都有对应的抽象方法，那即便我们只用到其中的一个模板方法，
        子类也必须实现所有的抽象方法。而回调就更加灵活，我们只需要往用到的模板方法中注入回调对象即可。
    
    
    
    
    
    
        
    
        
    
    
    
    
    
    
    
    