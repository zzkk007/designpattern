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
    
    1、Java InputStream
    
        Java IO 类库中，有很多类的设计用到了模板模式，
        比如 InputStream、OutputStream、Reader、Writer。我们拿 InputStream 来举例说明一下
        
            
        
        
    
        
    