职责链模式：如何实现可灵活扩展算法的敏感信息过滤框架？

1、职责链模式的原理和实现：
    
    将请求的发送和接收解耦，让多个接收对象都有机会处理这个请求。
    将这些接收对象串成一条链，并沿着这条链传递这个请求，直到链上的某个接收对象能够处理它为止。
    
    在职责链模式中，多个处理器（也就是刚刚定义中说的“接收对象”）依次处理同一个请求。
    一个请求先经过 A 处理器处理，然后再把请求传递给 B 处理器，
    B 处理器处理完后再传递给 C 处理器，以此类推，形成一个链条。
    链条上的每个处理器各自承担各自的处理职责，所以叫作职责链模式。
    
    
    第一种实现方式如下所示。其中，Handler 是所有处理器类的抽象父类，handle() 是抽象方法。
    每个具体的处理器类（HandlerA、HandlerB）的 handle() 函数的代码结构类似，如果它能处理该请求，
    就不继续往下传递；如果不能处理，则交由后面的处理器来处理（也就是调用 successor.handle()）。
    HandlerChain 是处理器链，从数据结构的角度来看，它就是一个记录了链头、链尾的链表。
    其中，记录链尾是为了方便添加处理器。 
    
        public abstract class Handler {
          protected Handler successor = null;
        
          public void setSuccessor(Handler successor) {
            this.successor = successor;
          }
        
          public abstract void handle();
        }
        
        public class HandlerA extends Handler {
          @Override
          public boolean handle() {
            boolean handled = false;
            //...
            if (!handled && successor != null) {
              successor.handle();
            }
          }
        }
        
        public class HandlerB extends Handler {
          @Override
          public void handle() {
            boolean handled = false;
            //...
            if (!handled && successor != null) {
              successor.handle();
            } 
          }
        }
        
        public class HandlerChain {
          private Handler head = null;
          private Handler tail = null;
        
          public void addHandler(Handler handler) {
            handler.setSuccessor(null);
        
            if (head == null) {
              head = handler;
              tail = handler;
              return;
            }
        
            tail.setSuccessor(handler);
            tail = handler;
          }
        
          public void handle() {
            if (head != null) {
              head.handle();
            }
          }
        }
        
        // 使用举例
        public class Application {
          public static void main(String[] args) {
            HandlerChain chain = new HandlerChain();
            chain.addHandler(new HandlerA());
            chain.addHandler(new HandlerB());
            chain.handle();
          }
        }
    
    对代码进行重构，利用模板模式，将调用 successor.handle() 的逻辑从具体的处理器类中剥离出来，放到抽象父类中。
    这样具体的处理器类只需要实现自己的业务逻辑就可以了。重构之后的代码如下所示：
  
        public abstract class Handler {
          protected Handler successor = null;
        
          public void setSuccessor(Handler successor) {
            this.successor = successor;
          }
        
          public final void handle() {
            boolean handled = doHandle();
            if (successor != null && !handled) {
              successor.handle();
            }
          }
        
          protected abstract boolean doHandle();
        }
        
        public class HandlerA extends Handler {
          @Override
          protected boolean doHandle() {
            boolean handled = false;
            //...
            return handled;
          }
        }
        
        public class HandlerB extends Handler {
          @Override
          protected boolean doHandle() {
            boolean handled = false;
            //...
            return handled;
          }
        }
        
        // HandlerChain和Application代码不变
    

    我们再来看第二种实现方式，代码如下所示。这种实现方式更加简单。HandlerChain 类用数组而非链表来保存所有的处理器，
    并且需要在 HandlerChain 的 handle() 函数中，依次调用每个处理器的 handle() 函数。

        public interface IHandler {
          boolean handle();
        }
        
        public class HandlerA implements IHandler {
          @Override
          public boolean handle() {
            boolean handled = false;
            //...
            return handled;
          }
        }
        
        public class HandlerB implements IHandler {
          @Override
          public boolean handle() {
            boolean handled = false;
            //...
            return handled;
          }
        }
        
        public class HandlerChain {
          private List<IHandler> handlers = new ArrayList<>();
        
          public void addHandler(IHandler handler) {
            this.handlers.add(handler);
          }
        
          public void handle() {
            for (IHandler handler : handlers) {
              boolean handled = handler.handle();
              if (handled) {
                break;
              }
            }
          }
        }
        
        // 使用举例
        public class Application {
          public static void main(String[] args) {
            HandlerChain chain = new HandlerChain();
            chain.addHandler(new HandlerA());
            chain.addHandler(new HandlerB());
            chain.handle();
          }
        }

2、职责链模式最常用来开发框架的过滤器和拦截器:
    
    Servlet Filter:
        Servlet Filter 是 Java Servlet 规范中定义的组件，翻译成中文就是过滤器，
        它可以实现对 HTTP 请求的过滤功能，比如鉴权、限流、记录日志、验证参数等等。
        因为它是 Servlet 规范的一部分，所以，只要是支持 Servlet 的 Web 容器（比如，Tomcat、Jetty 等），
        都支持过滤器功能。
        
        在实际项目中，我们该如何使用 Servlet Filter 呢？我写了一个简单的示例代码，如下所示。
        添加一个过滤器，我们只需要定义一个实现 javax.servlet.Filter 接口的过滤器类，并且将它配置在 web.xml 配置文件中。
        Web 容器启动的时候，会读取 web.xml 中的配置，创建过滤器对象。当有请求到来的时候，会先经过过滤器，然后才由 Servlet 来处理。
        
            public class LogFilter implements Filter {
              @Override
              public void init(FilterConfig filterConfig) throws ServletException {
                // 在创建Filter时自动调用，
                // 其中filterConfig包含这个Filter的配置参数，比如name之类的（从配置文件中读取的）
              }
            
              @Override
              public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                System.out.println("拦截客户端发送来的请求.");
                chain.doFilter(request, response);
                System.out.println("拦截发送给客户端的响应.");
              }
            
              @Override
              public void destroy() {
                // 在销毁Filter时自动调用
              }
            }
            
            // 在web.xml配置文件中如下配置：
            <filter>
              <filter-name>logFilter</filter-name>
              <filter-class>com.xzg.cd.LogFilter</filter-class>
            </filter>
            <filter-mapping>
                <filter-name>logFilter</filter-name>
                <url-pattern>/*</url-pattern>
            </filter-mapping>

        职责链模式的实现包含处理器接口（IHandler）或抽象类（Handler），以及处理器链（HandlerChain）。
        对应到 Servlet Filter，javax.servlet.Filter 就是处理器接口，FilterChain 就是处理器链。
        接下来，我们重点来看 FilterChain 是如何实现的。
        
        不过，我们前面也讲过，Servlet 只是一个规范，并不包含具体的实现，所以，Servlet 中的 FilterChain 只是一个接口定义。
        具体的实现类由遵从 Servlet 规范的 Web 容器来提供，比如，ApplicationFilterChain 类就是 Tomcat 提供的 FilterChain 的实现类，
        源码如下所示。为了让代码更易读懂，我对代码进行了简化，只保留了跟设计思路相关的代码片段。完整的代码你可以自行去 Tomcat 中查看。
        
            public final class ApplicationFilterChain implements FilterChain {
              private int pos = 0; //当前执行到了哪个filter
              private int n; //filter的个数
              private ApplicationFilterConfig[] filters;
              private Servlet servlet;
              
              @Override
              public void doFilter(ServletRequest request, ServletResponse response) {
                if (pos < n) {
                  ApplicationFilterConfig filterConfig = filters[pos++];
                  Filter filter = filterConfig.getFilter();
                  filter.doFilter(request, response, this);
                } else {
                  // filter都处理完毕后，执行servlet
                  servlet.service(request, response);
                }
              }
              
              public void addFilter(ApplicationFilterConfig filterConfig) {
                for (ApplicationFilterConfig filter:filters)
                  if (filter==filterConfig)
                     return;
            
                if (n == filters.length) {//扩容
                  ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[n + INCREMENT];
                  System.arraycopy(filters, 0, newFilters, 0, n);
                  filters = newFilters;
                }
                filters[n++] = filterConfig;
              }
            }

        ApplicationFilterChain 中的 doFilter() 函数的代码实现比较有技巧，实际上是一个递归调用。
        你可以用每个 Filter（比如 LogFilter）的 doFilter() 的代码实现，直接替换 ApplicationFilterChain 的第 12 行代码，
        一眼就能看出是递归调用了。我替换了一下，如下所示。
            
           @Override
           public void doFilter(ServletRequest request, ServletResponse response) {
             if (pos < n) {
               ApplicationFilterConfig filterConfig = filters[pos++];
               Filter filter = filterConfig.getFilter();
               //filter.doFilter(request, response, this);
               //把filter.doFilter的代码实现展开替换到这里
               System.out.println("拦截客户端发送来的请求.");
               chain.doFilter(request, response); // chain就是this
               System.out.println("拦截发送给客户端的响应.")
             } else {
               // filter都处理完毕后，执行servlet
               servlet.service(request, response);
             }
          }
        



















    
