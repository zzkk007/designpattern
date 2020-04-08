亨元模式(Flyweight Design Pattern)：

1、 亨元模式原理与实现:
    
    所谓“亨元”, 顾名思义就是被共享单元。亨元模式的意图是复用对象，节省内存，提前是亨元对象是不可变对象。
    
    具体来讲，当一个系统中存在大量重复对象的时候，如果这些重复的对象是不可变对象，
    我们就可以利用享元模式将对象设计成享元，在内存中只保留一份实例，供多处代码引用。    
    这样可以减少内存中对象的数量，起到节省内存的目的。
    
    实际上，不仅仅相同对象可以设计成享元，对于相似对象，我们也可以将这些对象中相同的部分（字段）提取出来，
    设计成享元，让这些大量相似对象引用这些享元。
    
    定义中的“不可变对象”指的是，一旦通过构造函数初始化完成之后，它的状态（对象的成员变量或者属性）就不会再被修改了。
    所以，不可变对象不能暴露任何 set() 等修改内部状态的方法。之所以要求享元是不可变对象，
    那是因为它会被多处代码共享使用，避免一处代码对享元进行了修改，影响到其他使用它的代码。
    
    
    假设我们在开发一个棋牌游戏（比如象棋）。一个游戏厅中有成千上万个“房间”，每个房间对应一个棋局。
    棋局要保存每个棋子的数据，比如：棋子类型（将、相、士、炮等）、棋子颜色（红方、黑方）、棋子在棋局中的位置。
    利用这些数据，我们就能显示一个完整的棋盘给玩家。
    具体的代码如下所示。其中，ChessPiece 类表示棋子，ChessBoard 类表示一个棋局，
    里面保存了象棋中 30 个棋子的信息。    
    
        public class ChessPiece {//棋子
          private int id;
          private String text;
          private Color color;
          private int positionX;
          private int positionY;
        
          public ChessPiece(int id, String text, Color color, int positionX, int positionY) {
            this.id = id;
            this.text = text;
            this.color = color;
            this.positionX = positionX;
            this.positionY = positionX;
          }
        
          public static enum Color {
            RED, BLACK
          }
        
          // ...省略其他属性和getter/setter方法...
        }
        
        public class ChessBoard {//棋局
          private Map<Integer, ChessPiece> chessPieces = new HashMap<>();
        
          public ChessBoard() {
            init();
          }
        
          private void init() {
            chessPieces.put(1, new ChessPiece(1, "車", ChessPiece.Color.BLACK, 0, 0));
            chessPieces.put(2, new ChessPiece(2,"馬", ChessPiece.Color.BLACK, 0, 1));
            //...省略摆放其他棋子的代码...
          }
        
          public void move(int chessPieceId, int toPositionX, int toPositionY) {
            //...省略...
          }
        }
            
    这个时候，享元模式就可以派上用场了。像刚刚的实现方式，在内存中会有大量的相似对象。
    这些相似对象的 id、text、color 都是相同的，唯独 positionX、positionY 不同。
    实际上，我们可以将棋子的 id、text、color 属性拆分出来，设计成独立的类，并且作为享元供多个棋盘复用。
    这样，棋盘只需要记录每个棋子的位置信息就可以了。具体的代码实现如下所示：
        
        
        // 享元类
        public class ChessPieceUnit {
          private int id;
          private String text;
          private Color color;
        
          public ChessPieceUnit(int id, String text, Color color) {
            this.id = id;
            this.text = text;
            this.color = color;
          }
        
          public static enum Color {
            RED, BLACK
          }
        
          // ...省略其他属性和getter方法...
        }
        
        public class ChessPieceUnitFactory {
          private static final Map<Integer, ChessPieceUnit> pieces = new HashMap<>();
        
          static {
            pieces.put(1, new ChessPieceUnit(1, "車", ChessPieceUnit.Color.BLACK));
            pieces.put(2, new ChessPieceUnit(2,"馬", ChessPieceUnit.Color.BLACK));
            //...省略摆放其他棋子的代码...
          }
        
          public static ChessPieceUnit getChessPiece(int chessPieceId) {
            return pieces.get(chessPieceId);
          }
        }
        
        public class ChessPiece {
          private ChessPieceUnit chessPieceUnit;
          private int positionX;
          private int positionY;
        
          public ChessPiece(ChessPieceUnit unit, int positionX, int positionY) {
            this.chessPieceUnit = unit;
            this.positionX = positionX;
            this.positionY = positionY;
          }
          // 省略getter、setter方法
        }
        
        public class ChessBoard {
          private Map<Integer, ChessPiece> chessPieces = new HashMap<>();
        
          public ChessBoard() {
            init();
          }
        
          private void init() {
            chessPieces.put(1, new ChessPiece(
                    ChessPieceUnitFactory.getChessPiece(1), 0,0));
            chessPieces.put(1, new ChessPiece(
                    ChessPieceUnitFactory.getChessPiece(2), 1,0));
            //...省略摆放其他棋子的代码...
          }
        
          public void move(int chessPieceId, int toPositionX, int toPositionY) {
            //...省略...
          }
        }    
            
    那享元模式的原理讲完了，我们来总结一下它的代码结构。
    实际上，它的代码实现非常简单，主要是通过工厂模式，
    在工厂类中，通过一个 Map 来缓存已经创建过的享元对象，来达到复用的目的。
    
2、享元模式 vs 单例、缓存、对象池：

    享元模式跟单例的区别：
        在单例模式中，一个类只能创建一个对象，而在享元模式中，一个类可以创建多个对象，每个对象被多处代码引用共享。            
    
    享元模式跟缓存的区别：
        在享元模式的实现中，我们通过工厂类来“缓存”已经创建好的对象。这里的“缓存”实际上是“存储”的意思，
        跟我们平时所说的“数据库缓存”“CPU 缓存”“MemCache 缓存”是两回事。
        我们平时所讲的缓存，主要是为了提高访问效率，而非复用。
        
    享元模式跟对象池的区别:
        虽然对象池、连接池、线程池、享元模式都是为了复用，
        但是，如果我们再细致地抠一抠“复用”这个字眼的话，
        对象池、连接池、线程池等池化技术中的“复用”和享元模式中的“复用”实际上是不同的概念。
        
        池化技术中的“复用”可以理解为“重复使用”，主要目的是节省时间（比如从数据库池中取一个连接，不需要重新创建）。
        在任意时刻，每一个对象、连接、线程，并不会被多处使用，而是被一个使用者独占，
        当使用完成之后，放回到池中，再由其他使用者重复利用。
        
        享元模式中的“复用”可以理解为“共享使用”，在整个生命周期中，
        都是被所有使用者共享的，主要目的是节省空间。       
    
    
3、剖析享元模式在Java Integer、String中的应用:
    
    Integer i1 = 56;
    Integer i2 = 56;
    Integer i3 = 129;
    Integer i4 = 129;
    System.out.println(i1 == i2);
    System.out.println(i3 == i4);   
    
    数值 56 是基本数据类型 int，当赋值给包装器类型（Integer）变量的时候，触发自动装箱操作，
    创建一个 Integer 类型的对象，并且赋值给变量 i。其底层相当于执行了下面这条语句：     
        Integer i = 59；底层执行了：Integer i = Integer.valueOf(59);        

    前 4 行赋值语句都会触发自动装箱操作，也就是会创建 Integer 对象并且赋值给 i1、i2、i3、i4 这四个变量。
    根据刚刚的讲解，i1、i2 尽管存储的数值相同，都是 56，但是指向不同的 Integer 对象，
    所以通过“==”来判定是否相同的时候，会返回 false。同理，i3==i4 判定语句也会返回 false。
    
    不过，上面的分析还是不对，答案并非是两个 false，而是一个 true，一个 false。
    看到这里，你可能会比较纳闷了。实际上，这正是因为 Integer 用到了享元模式来复用对象，
    才导致了这样的运行结果。当我们通过自动装箱，也就是调用 valueOf() 来创建 Integer 对象的时候，
    如果要创建的 Integer 对象的值在 -128 到 127 之间，会从 IntegerCache 类中直接返回，
    否则才调用 new 方法创建。看代码更加清晰一些，Integer 类的 valueOf() 函数的
    具体代码如下所示：
    
        public static Integer valueOf(int i) {
            if (i >= IntegerCache.low && i <= IntegerCache.high)
                return IntegerCache.cache[i + (-IntegerCache.low)];
            return new Integer(i);
        }
        
        
        /**
         * Cache to support the object identity semantics of autoboxing for values between
         * -128 and 127 (inclusive) as required by JLS.
         *
         * The cache is initialized on first usage.  The size of the cache
         * may be controlled by the {@code -XX:AutoBoxCacheMax=<size>} option.
         * During VM initialization, java.lang.Integer.IntegerCache.high property
         * may be set and saved in the private system properties in the
         * sun.misc.VM class.
         */
        private static class IntegerCache {
            static final int low = -128;
            static final int high;
            static final Integer cache[];
        
            static {
                // high value may be configured by property
                int h = 127;
                String integerCacheHighPropValue =
                    sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
                if (integerCacheHighPropValue != null) {
                    try {
                        int i = parseInt(integerCacheHighPropValue);
                        i = Math.max(i, 127);
                        // Maximum array size is Integer.MAX_VALUE
                        h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
                    } catch( NumberFormatException nfe) {
                        // If the property cannot be parsed into an int, ignore it.
                    }
                }
                high = h;
        
                cache = new Integer[(high - low) + 1];
                int j = low;
                for(int k = 0; k < cache.length; k++)
                    cache[k] = new Integer(j++);
        
                // range [-128, 127] must be interned (JLS7 5.1.7)
                assert IntegerCache.high >= 127;
            }
        
            private IntegerCache() {}
        }     
        
    实际上，除了 Integer 类型之外，其他包装器类型，比如 Long、Short、Byte 等，
    也都利用了享元模式来缓存 -128 到 127 之间的数据。
    比如，Long 类型对应的 LongCache 享元工厂类及 valueOf() 函数代码如下所示：
    
    在我们平时的开发中，对于下面这样三种创建整型对象的方式，我们优先使用后两种。  
        Integer a = new Integer(123);
        Integer a = 123;
        Integer a = Integer.valueOf(123); 
    
    第一种创建方式并不会使用到 IntegerCache，而后面两种创建方法可以利用 IntegerCache 缓存，
    返回共享的对象，以达到节省内存的目的。    
    
    实际上，享元模式对 JVM 的垃圾回收并不友好。因为享元工厂类一直保存了对享元对象的引用，
    这就导致享元对象在没有任何代码使用的情况下，也并不会被 JVM 垃圾回收机制自动回收掉。
    因此，在某些情况下，如果对象的生命周期很短，也不会被密集使用，利用享元模式反倒可能会浪费更多的内存。
    所以，除非经过线上验证，利用享元模式真的可以大大节省内存，否则，就不要过度使用这个模式，
    为了一点点内存的节省而引入一个复杂的设计模式，得不偿失啊。
    
        