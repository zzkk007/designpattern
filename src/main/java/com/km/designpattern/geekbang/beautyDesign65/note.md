迭代器模式: 相比直接遍历集合数据，使用迭代器有哪些优势？
    
    迭代器模式,它用来遍历集合对象。不过，很多编程语言都将迭代器作为一个基础的类库，直接提供出来了。
    
1、迭代器模式的原理和实现:
    
    迭代器模式（Iterator Design Pattern），也叫作游标模式（Cursor Design Pattern）。
    迭代器模式,它用来遍历集合对象。这里说的“集合对象”也可以叫“容器”“聚合对象”，实际上就是包含一组对象的对象，
    比如数组、链表、树、图、跳表。迭代器模式将集合对象的遍历操作从集合类中拆分出来，放到迭代器类中，让两者的职责更加单一。  
    
    迭代器是用来遍历容器的，所以，一个完整的迭代器模式一般会涉及容器和容器迭代器两部分内容。
    为了达到基于接口而非实现编程的目的，容器又包含容器接口、容器实现类，迭代器又包含迭代器接口、迭代器实现类。
    
    我们先来看下 Iterator 接口的定义。具体的代码如下所示：
    
        // 接口定义方式一
        public interface Iterator<E> {
          boolean hasNext();
          void next();
          E currentItem();
        }
        
        // 接口定义方式二
        public interface Iterator<E> {
          boolean hasNext();
          E next();
        }  
    
    Iterator 接口有两种定义方式。在第一种定义中，next() 函数用来将游标后移一位元素，currentItem() 函数用来返回当前游标指向的元素。
    在第二种定义中，返回当前元素与后移一位这两个操作，要放到同一个函数 next() 中完成。
    第一种定义方式更加灵活一些，比如我们可以多次调用 currentItem() 查询当前元素，而不移动游标。
    所以，在接下来的实现中，我们选择第一种接口定义方式。
    现在，我们再来看下 ArrayIterator 的代码实现，具体如下所示。
    
        public class ArrayIterator<E> implements Iterator<E> {
          private int cursor;
          private ArrayList<E> arrayList;
        
          public ArrayIterator(ArrayList<E> arrayList) {
            this.cursor = 0;
            this.arrayList = arrayList;
          }
        
          @Override
          public boolean hasNext() {
            return cursor != arrayList.size(); //注意这里，cursor在指向最后一个元素的时候，hasNext()仍旧返回true。
          }
        
          @Override
          public void next() {
            cursor++;
          }
        
          @Override
          public E currentItem() {
            if (cursor >= arrayList.size()) {
              throw new NoSuchElementException();
            }
            return arrayList.get(cursor);
          }
        }
        
        public class Demo {
          public static void main(String[] args) {
            ArrayList<String> names = new ArrayList<>();
            names.add("xzg");
            names.add("wang");
            names.add("zheng");
            
            Iterator<String> iterator = new ArrayIterator(names);
            while (iterator.hasNext()) {
              System.out.println(iterator.currentItem());
              iterator.next();
            }
          }
        }
    
    在上面的代码实现中，我们需要将待遍历的容器对象，通过构造函数传递给迭代器类。
    实际上，为了封装迭代器的创建细节，我们可以在容器中定义一个 iterator() 方法，来创建对应的迭代器。
    为了能实现基于接口而非实现编程，我们还需要将这个方法定义在 List 接口中。
    具体的代码实现和使用示例如下所示：
    
        public interface List<E> {
          Iterator iterator();
          //...省略其他接口函数...
        }
        
        public class ArrayList<E> implements List<E> {
          //...
          public Iterator iterator() {
            return new ArrayIterator(this);
          }
          //...省略其他代码
        }
        
        public class Demo {
          public static void main(String[] args) {
            List<String> names = new ArrayList<>();
            names.add("xzg");
            names.add("wang");
            names.add("zheng");
            
            Iterator<String> iterator = names.iterator();
            while (iterator.hasNext()) {
              System.out.println(iterator.currentItem());
              iterator.next();
            }
          }
        }
    
迭代器模式的优势:

    一般来讲，遍历集合数据有三种方法：for 循环、foreach 循环、iterator 迭代器。
    对于这三种方式，我拿 Java 语言来举例说明一下。具体的代码如下所示：        
    
        List<String> names = new ArrayList<>();
        names.add("xzg");
        names.add("wang");
        names.add("zheng");
        
        // 第一种遍历方式：for循环
        for (int i = 0; i < names.size(); i++) {
          System.out.print(names.get(i) + ",");
        }
        
        // 第二种遍历方式：foreach循环
        for (String name : names) {
          System.out.print(name + ",")
        }
        
        // 第三种遍历方式：迭代器遍历
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
          System.out.print(iterator.next() + ",");//Java中的迭代器接口是第二种定义方式，next()既移动游标又返回数据
        }
            
    实际上，foreach 循环只是一个语法糖而已，底层是基于迭代器来实现的。
    也就是说，上面代码中的第二种遍历方式（foreach 循环代码）的底层实现，就是第三种遍历方式（迭代器遍历代码）。
    这两种遍历方式可以看作同一种遍历方式，也就是迭代器遍历方式。
    
    从上面的代码来看，for 循环遍历方式比起迭代器遍历方式，代码看起来更加简洁。
    那我们为什么还要用迭代器来遍历容器呢？为什么还要给容器设计对应的迭代器呢？
    
    原因有以下三个。首先，对于类似数组和链表这样的数据结构，遍历方式比较简单，直接使用 for 循环来遍历就足够了。
    但是，对于复杂的数据结构（比如树、图）来说，有各种复杂的遍历方式。比如，树有前中后序、按层遍历，图有深度优先、广度优先遍历等等。
    如果由客户端代码来实现这些遍历算法，势必增加开发成本，而且容易写错。
    如果将这部分遍历的逻辑写到容器类中，也会导致容器类代码的复杂性。
    前面也多次提到，应对复杂性的方法就是拆分。我们可以将遍历操作拆分到迭代器类中。
    比如，针对图的遍历，我们就可以定义 DFSIterator、BFSIterator 两个迭代器类，让它们分别来实现深度优先遍历和广度优先遍历。
    其次，将游标指向的当前位置等信息，存储在迭代器类中，每个迭代器独享游标信息。
    这样，我们就可以创建多个不同的迭代器，同时对同一个容器进行遍历而互不影响。
    最后，容器和迭代器都提供了抽象的接口，方便我们在开发的时候，基于接口而非具体的实现编程。
    当需要切换新的遍历算法的时候，比如，从前往后遍历链表切换成从后往前遍历链表
    ，客户端代码只需要将迭代器类从 LinkedIterator 切换为 ReversedLinkedIterator 即可，其他代码都不需要修改。
    除此之外，添加新的遍历算法，我们只需要扩展新的迭代器类，也更符合开闭原则。
    
迭代器模式（中）：遍历集合的同时，为什么不能增删集合元素？:

    如果在使用迭代器遍历集合的同时增加、删除集合中的元素，会发生什么情况？
    应该如何应对？如何在遍历的同时安全地删除集合元素？    
    
    在通过迭代器来遍历集合元素的同时，增加或者删除集合中的元素，有可能会导致某个元素被重复遍历或遍历不到。
    不过，并不是所有情况下都会遍历出错，有的时候也可以正常遍历，所以，这种行为称为结果不可预期行为或者未决行为，
    也就是说，运行结果到底是对还是错，要视情况而定。
    
        public interface Iterator<E> {
          boolean hasNext();
          void next();
          E currentItem();
        }
        
        public class ArrayIterator<E> implements Iterator<E> {
          private int cursor;
          private ArrayList<E> arrayList;
        
          public ArrayIterator(ArrayList<E> arrayList) {
            this.cursor = 0;
            this.arrayList = arrayList;
          }
        
          @Override
          public boolean hasNext() {
            return cursor < arrayList.size();
          }
        
          @Override
          public void next() {
            cursor++;
          }
        
          @Override
          public E currentItem() {
            if (cursor >= arrayList.size()) {
              throw new NoSuchElementException();
            }
            return arrayList.get(cursor);
          }
        }
        
        public interface List<E> {
          Iterator iterator();
        }
        
        public class ArrayList<E> implements List<E> {
          //...
          public Iterator iterator() {
            return new ArrayIterator(this);
          }
          //...
        }
        
        public class Demo {
          public static void main(String[] args) {
            List<String> names = new ArrayList<>();
            names.add("a");
            names.add("b");
            names.add("c");
            names.add("d");
        
            Iterator<String> iterator = names.iterator();
            iterator.next();
            names.remove("a");
          }
        }
    
    在遍历的过程中删除集合元素，有可能会导致某个元素遍历不到，那在遍历的过程中添加集合元素，会发生什么情况呢？
    还是结合刚刚那个例子来讲解，我们将上面的代码稍微改造一下，把删除元素改为添加元素。具体的代码如下所示：
    
        public class Demo {
          public static void main(String[] args) {
            List<String> names = new ArrayList<>();
            names.add("a");
            names.add("b");
            names.add("c");
            names.add("d");
        
            Iterator<String> iterator = names.iterator();
            iterator.next();
            names.add(0, "x");
          }
        }
    
    跟删除情况类似，如果我们在游标的后面添加元素，就不会存在任何问题。所以，在遍历的同时添加集合元素也是一种不可预期行为。
    当通过迭代器来遍历集合的时候，增加、删除集合元素会导致不可预期的遍历结果。
    实际上，“不可预期”比直接出错更加可怕，有的时候运行正确，有的时候运行错误，一些隐藏很深、很难 debug 的 bug 就是这么产生的。
    那我们如何才能避免出现这种不可预期的运行结果呢？
    有两种比较干脆利索的解决方案：一种是遍历的时候不允许增删元素，另一种是增删元素之后让遍历报错。
    
    实际上，第二种解决方法更加合理。Java 语言就是采用的这种解决方案，增删元素之后，让遍历报错。
    怎么确定在遍历时候，集合有没有增删元素呢？我们在 ArrayList 中定义一个成员变量 modCount，记录集合被修改的次数，
    集合每调用一次增加或删除元素的函数，就会给 modCount 加 1。当通过调用集合上的 iterator() 函数来创建迭代器的时候，
    我们把 modCount 值传递给迭代器的 expectedModCount 成员变量，之后每次调用迭代器上的 hasNext()、next()、currentItem() 函数，
    我们都会检查集合上的 modCount 是否等于 expectedModCount，也就是看，在创建完迭代器之后，modCount 是否改变过。
    
    如果两个值不相同，那就说明集合存储的元素已经改变了，要么增加了元素，要么删除了元素，之前创建的迭代器已经不能正确运行了，】
    再继续使用就会产生不可预期的结果，所以我们选择 fail-fast 解决方式，抛出运行时异常，结束掉程序，
    让程序员尽快修复这个因为不正确使用迭代器而产生的 bug。
    
        public class ArrayIterator implements Iterator {
          private int cursor;
          private ArrayList arrayList;
          private int expectedModCount;
        
          public ArrayIterator(ArrayList arrayList) {
            this.cursor = 0;
            this.arrayList = arrayList;
            this.expectedModCount = arrayList.modCount;
          }
        
          @Override
          public boolean hasNext() {
            checkForComodification();
            return cursor < arrayList.size();
          }
        
          @Override
          public void next() {
            checkForComodification();
            cursor++;
          }
        
          @Override
          public Object currentItem() {
            checkForComodification();
            return arrayList.get(cursor);
          }
          
          private void checkForComodification() {
            if (arrayList.modCount != expectedModCount)
                throw new ConcurrentModificationException();
          }
        }
        
        //代码示例
        public class Demo {
          public static void main(String[] args) {
            List<String> names = new ArrayList<>();
            names.add("a");
            names.add("b");
            names.add("c");
            names.add("d");
        
            Iterator<String> iterator = names.iterator();
            iterator.next();
            names.remove("a");
            iterator.next();//抛出ConcurrentModificationException异常
          }
        }
    
    
    如何在遍历的同时安全地删除集合元素:
        
        像 Java 语言，迭代器类中除了前面提到的几个最基本的方法之外，还定义了一个 remove() 方法，能够在遍历集合的同时，安全地删除集合中的元素。
        不过，需要说明的是，它并没有提供添加元素的方法。毕竟迭代器的主要作用是遍历，添加元素放到迭代器里本身就不合适。
    
        我个人觉得，Java 迭代器中提供的 remove() 方法还是比较鸡肋的，作用有限。
        它只能删除游标指向的前一个元素，而且一个 next() 函数之后，只能跟着最多一个 remove() 操作，
        多次调用 remove() 操作会报错。我还是通过一个例子来解释一下。
        
            public class Demo {
              public static void main(String[] args) {
                List<String> names = new ArrayList<>();
                names.add("a");
                names.add("b");
                names.add("c");
                names.add("d");
            
                Iterator<String> iterator = names.iterator();
                iterator.next();
                iterator.remove();
                iterator.remove(); //报错，抛出IllegalStateException异常
              }
            }
    
        现在，我们一块来看下，为什么通过迭代器就能安全的删除集合中的元素呢？
        源码之下无秘密。我们来看下 remove() 函数是如何实现的，代码如下所示。
        稍微提醒一下，在 Java 实现中，迭代器类是容器类的内部类，并且 next() 函数不仅将游标后移一位，还会返回当前的元素
    
        
            public class ArrayList<E> {
              transient Object[] elementData;
              private int size;
            
              public Iterator<E> iterator() {
                return new Itr();
              }
            
              private class Itr implements Iterator<E> {
                int cursor;       // index of next element to return
                int lastRet = -1; // index of last element returned; -1 if no such
                int expectedModCount = modCount;
            
                Itr() {}
            
                public boolean hasNext() {
                  return cursor != size;
                }
            
                @SuppressWarnings("unchecked")
                public E next() {
                  checkForComodification();
                  int i = cursor;
                  if (i >= size)
                    throw new NoSuchElementException();
                  Object[] elementData = ArrayList.this.elementData;
                  if (i >= elementData.length)
                    throw new ConcurrentModificationException();
                  cursor = i + 1;
                  return (E) elementData[lastRet = i];
                }
                
                public void remove() {
                  if (lastRet < 0)
                    throw new IllegalStateException();
                  checkForComodification();
            
                  try {
                    ArrayList.this.remove(lastRet);
                    cursor = lastRet;
                    lastRet = -1;
                    expectedModCount = modCount;
                  } catch (IndexOutOfBoundsException ex) {
                    throw new ConcurrentModificationException();
                  }
                }
              }
            }
    
迭代器模式（下）：如何设计实现一个支持“快照”功能的iterator:
    
    理解这个问题最关键的是理解“快照”两个字。所谓“快照”，指我们为容器创建迭代器的时候，相当于给容器拍了一张快照（Snapshot）。
    之后即便我们增删容器中的元素，快照中的元素并不会做相应的改动。而迭代器遍历的对象是快照而非容器，这样就避免了在使用迭代器遍历的过程中，
    增删容器中的元素，导致的不可预期的结果或者报错。
        
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(8);
        list.add(2);
        
        Iterator<Integer> iter1 = list.iterator();//snapshot: 3, 8, 2
        list.remove(new Integer(2));//list：3, 8
        Iterator<Integer> iter2 = list.iterator();//snapshot: 3, 8
        list.remove(new Integer(3));//list：8
        Iterator<Integer> iter3 = list.iterator();//snapshot: 3
        
        // 输出结果：3 8 2
        while (iter1.hasNext()) {
          System.out.print(iter1.next() + " ");
        }
        System.out.println();
        
        // 输出结果：3 8
        while (iter2.hasNext()) {
          System.out.print(iter1.next() + " ");
        }
        System.out.println();
        
        // 输出结果：8
        while (iter3.hasNext()) {
          System.out.print(iter1.next() + " ");
        }
        System.out.println();
             
    如果由你来实现上面的功能，你会如何来做呢
        
        public ArrayList<E> implements List<E> {
          // TODO: 成员变量、私有函数等随便你定义
          
          @Override
          public void add(E obj) {
            //TODO: 由你来完善
          }
          
          @Override
          public void remove(E obj) {
            // TODO: 由你来完善
          }
          
          @Override
          public Iterator<E> iterator() {
            return new SnapshotArrayIterator(this);
          }
        }
        
        public class SnapshotArrayIterator<E> implements Iterator<E> {
          // TODO: 成员变量、私有函数等随便你定义
          
          @Override
          public boolean hasNext() {
            // TODO: 由你来完善
          }
          
          @Override
          public E next() {//返回当前元素，并且游标后移一位
            // TODO: 由你来完善
          }
        }

解决方案一:
    
    我们先来看最简单的一种解决办法。在迭代器类中定义一个成员变量 snapshot 来存储快照。
    每当创建迭代器的时候，都拷贝一份容器中的元素到快照中，后续的遍历操作都基于这个迭代器自己持有的快照来进行。
    具体的代码实现如下所示：    
 
        public class SnapshotArrayIterator<E> implements Iterator<E> {
          private int cursor;
          private ArrayList<E> snapshot;
        
          public SnapshotArrayIterator(ArrayList<E> arrayList) {
            this.cursor = 0;
            this.snapshot = new ArrayList<>();
            this.snapshot.addAll(arrayList);
          }
        
          @Override
          public boolean hasNext() {
            return cursor < snapshot.size();
          }
        
          @Override
          public E next() {
            E currentItem = snapshot.get(cursor);
            cursor++;
            return currentItem;
          }
        }
    
    
    
    
    
    
    