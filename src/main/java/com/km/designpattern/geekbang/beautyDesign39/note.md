
1、上帝类：
    
    面向对象设计中的最后一步是组装类并提供执行入口，也就是上帝类要做的事情。
    这个上帝类是没办法去掉的，但我们可以将上帝类做的很轻量级，把核心逻辑都
    剥离出去，下沉形成独立的类。上帝类只负责组装和串联执行流程。
    这样做的好处是代码结构更加清晰，底层核心逻辑更容易被复用。
    
    面向对象设计和实现要做的事情，就是把合适的代码放到合适的类中。
    当我们要实现某个功能的时候，不管如何设计，所需要编写的代码量基本上是一样的，
    唯一的区别就是如何将这些代码划分到不同的类中。
    不同的人有不同的划分方法，对应得到的代码结构（比如类与类之间交互等）也不尽相同。
    好的设计一定是结构清晰、有条理、逻辑性强，看起来一目了然，读完之后常常有一种原来如此的感觉。
    差的设计往往逻辑、代码乱塞一通，没有什么设计思路可言，看起来莫名其妙，读完之后一头雾水。
    