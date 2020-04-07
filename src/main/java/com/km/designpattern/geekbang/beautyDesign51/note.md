适配器模式

1、适配器模式的原理与实现:

    //类适配器: 基于继承
    其中，ITarget 表示要转化成的接口定义。
    Adaptee 是一组不兼容 ITarget 接口定义的接口，
    Adaptor 将 Adaptee 转化成一组符合 ITarget 接口定义的接口。
        
    public interface ITarget{    
        void f1();
        void f2();
        void fc();
    }
    
    public class Adaptee{
        public void fa(){//...}
        public void fb(){//...}
        public void fc(){//...}
    }

    public class Adaptor extends Adaptee implements Itarget{
        
        public void f1(){
            super.fa();
        }    
        
        public void f2(){
            super.fb();
        }
        
        // 这里fc()不需要实现，直接继承自Adaptee，这是跟对象适配器最大的不同点
    }
    
    
2 对象适配器, 基于组合：    

    public interface ITarget{    
        void f1();
        void f2();
        void fc();
    }
       
    public class Adaptee{
        public void fa(){//...}
        public void fb(){//...}
        public void fc(){//...}
    } 
    
    public class Adaptor implements Itarget{
        
        private Adaptee adaptee;
        
        public Aaptor(Adaptee adaptee){
            this.adaptee = adaptee;
        }
        
        public void f1(){
            adaptee.fa();
        }
        
        public void f2(){
            adaptee.f2();
        }
        
        public void fc(){
            adaptee.fc();
        }
    }
    
    针对这两种实现方式，在实际的开发中，到底该如何选择使用哪一种呢？
    判断的标准主要有两个，一个是 Adaptee 接口的个数，另一个是 Adaptee 和 ITarget 的契合程度。
    
    如果 Adaptee 接口并不多，那两种实现方式都可以。
    
    如果 Adaptee 接口很多，而且 Adaptee 和 ITarget 接口定义大部分都相同，那我们推荐使用类适配器，
    因为 Adaptor 复用父类 Adaptee 的接口，比起对象适配器的实现方式，Adaptor 的代码量要少一些。
    
    如果 Adaptee 接口很多，而且 Adaptee 和 ITarget 接口定义大部分都不相同，
    那我们推荐使用对象适配器，因为组合结构相对于继承更加灵活。
    
3、适配器模式应用场景总结：

    一般来说，适配器模式可以看作一种“补偿模式”，用来补救设计上的缺陷。
    应用这种模式算是“无奈之举”。如果在设计初期，我们就能协调规避接口不兼容的问题，
    那这种模式就没有应用的机会了。  

    那在实际的开发中，什么情况下才会出现接口不兼容呢？
    我建议你先自己思考一下这个问题，然后再来看我下面的总结 。

    a、封装有缺陷的接口设计
       假设我们依赖的外部系统在接口设计方面有缺陷（比如包含大量静态方法），
       引入之后会影响到我们自身代码的可测试性。
       为了隔离设计上的缺陷，我们希望对外部系统提供的接口进行二次封装，抽象出更好的接口设计，
       这个时候就可以使用适配器模式了。 
            public class CD { //这个类来自外部sdk，我们无权修改它的代码
              //...
              public static void staticFunction1() { //... }
              
              public void uglyNamingFunction2() { //... }
            
              public void tooManyParamsFunction3(int paramA, int paramB, ...) { //... }
              
               public void lowPerformanceFunction4() { //... }
            }
            
            // 使用适配器模式进行重构
            public class ITarget {
              void function1();
              void function2();
              void fucntion3(ParamsWrapperDefinition paramsWrapper);
              void function4();
              //...
            }
            // 注意：适配器类的命名不一定非得末尾带Adaptor
            public class CDAdaptor extends CD implements ITarget {
              //...
              public void function1() {
                 super.staticFunction1();
              }
              
              public void function2() {
                super.uglyNamingFucntion2();
              }
              
              public void function3(ParamsWrapperDefinition paramsWrapper) {
                 super.tooManyParamsFunction3(paramsWrapper.getParamA(), ...);
              }
              
              public void function4() {
                //...reimplement it...
              }
            }    
        
    b、统一多个类的接口设计：
        
        某个功能的实现依赖多个外部系统（或者说类）。通过适配器模式，将它们的接口适配为统一的接口定义，
        然后我们就可以使用多态的特性来复用代码逻辑。具体我还是举个例子来解释一下。  
        
        public class ASensitiveWordsFilter { // A敏感词过滤系统提供的接口
          //text是原始文本，函数输出用***替换敏感词之后的文本
          public String filterSexyWords(String text) {
            // ...
          }
          
          public String filterPoliticalWords(String text) {
            // ...
          } 
        }
        
        public class BSensitiveWordsFilter  { // B敏感词过滤系统提供的接口
          public String filter(String text) {
            //...
          }
        }
        
        public class CSensitiveWordsFilter { // C敏感词过滤系统提供的接口
          public String filter(String text, String mask) {
            //...
          }
        }
        
        // 未使用适配器模式之前的代码：代码的可测试性、扩展性不好
        public class RiskManagement {
          private ASensitiveWordsFilter aFilter = new ASensitiveWordsFilter();
          private BSensitiveWordsFilter bFilter = new BSensitiveWordsFilter();
          private CSensitiveWordsFilter cFilter = new CSensitiveWordsFilter();
          
          public String filterSensitiveWords(String text) {
            String maskedText = aFilter.filterSexyWords(text);
            maskedText = aFilter.filterPoliticalWords(maskedText);
            maskedText = bFilter.filter(maskedText);
            maskedText = cFilter.filter(maskedText, "***");
            return maskedText;
          }
        }
        
        // 使用适配器模式进行改造
        public interface ISensitiveWordsFilter { // 统一接口定义
          String filter(String text);
        }
        
        public class ASensitiveWordsFilterAdaptor implements ISensitiveWordsFilter {
          private ASensitiveWordsFilter aFilter;
          public String filter(String text) {
            String maskedText = aFilter.filterSexyWords(text);
            maskedText = aFilter.filterPoliticalWords(maskedText);
            return maskedText;
          }
        }
        //...省略BSensitiveWordsFilterAdaptor、CSensitiveWordsFilterAdaptor...
        
        // 扩展性更好，更加符合开闭原则，如果添加一个新的敏感词过滤系统，
        // 这个类完全不需要改动；而且基于接口而非实现编程，代码的可测试性更好。
        public class RiskManagement { 
          private List<ISensitiveWordsFilter> filters = new ArrayList<>();
         
          public void addSensitiveWordsFilter(ISensitiveWordsFilter filter) {
            filters.add(filter);
          }
          
          public String filterSensitiveWords(String text) {
            String maskedText = text;
            for (ISensitiveWordsFilter filter : filters) {
              maskedText = filter.filter(maskedText);
            }
            return maskedText;
          }
        } 
            
    C、替换依赖的外部系统：
       
       当我们把项目中依赖的一个外部系统替换为另一个外部系统的时候，
       利用适配器模式，可以减少对代码的改动。具体的代码示例如下所示： 
       
        // 外部系统A
        public interface IA {
          //...
          void fa();
        }
        public class A implements IA {
          //...
          public void fa() { //... }
        }
        // 在我们的项目中，外部系统A的使用示例
        public class Demo {
          private IA a;
          public Demo(IA a) {
            this.a = a;
          }
          //...
        }
        Demo d = new Demo(new A());
        
        // 将外部系统A替换成外部系统B
        public class BAdaptor implemnts IA {
          private B b;
          public BAdaptor(B b) {
            this.b= b;
          }
          public void fa() {
            //...
            b.fb();
          }
        }
        // 借助BAdaptor，Demo的代码中，调用IA接口的地方都无需改动，
        // 只需要将BAdaptor如下注入到Demo即可。
        Demo d = new Demo(new BAdaptor(new B())); 

    d、兼容老版本接口：
    
    e、 适配不同格式的数据

4、剖析适配器模式在 Java 日志中的应用

    // slf4j统一的接口定义
    package org.slf4j;
    public interface Logger {
      public boolean isTraceEnabled();
      public void trace(String msg);
      public void trace(String format, Object arg);
      public void trace(String format, Object arg1, Object arg2);
      public void trace(String format, Object[] argArray);
      public void trace(String msg, Throwable t);
     
      public boolean isDebugEnabled();
      public void debug(String msg);
      public void debug(String format, Object arg);
      public void debug(String format, Object arg1, Object arg2)
      public void debug(String format, Object[] argArray)
      public void debug(String msg, Throwable t);
    
      //...省略info、warn、error等一堆接口
    }
    
    // log4j日志框架的适配器
    // Log4jLoggerAdapter实现了LocationAwareLogger接口，
    // 其中LocationAwareLogger继承自Logger接口，
    // 也就相当于Log4jLoggerAdapter实现了Logger接口。
    package org.slf4j.impl;
    public final class Log4jLoggerAdapter extends MarkerIgnoringBase
      implements LocationAwareLogger, Serializable {
      final transient org.apache.log4j.Logger logger; // log4j
     
      public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
      }
     
      public void debug(String msg) {
        logger.log(FQCN, Level.DEBUG, msg, null);
      }
     
      public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) {
          FormattingTuple ft = MessageFormatter.format(format, arg);
          logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
      }
     
      public void debug(String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) {
          FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
          logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
      }
     
      public void debug(String format, Object[] argArray) {
        if (logger.isDebugEnabled()) {
          FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
          logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
      }
     
      public void debug(String msg, Throwable t) {
        logger.log(FQCN, Level.DEBUG, msg, t);
      }
      //...省略一堆接口的实现...
    }        

            