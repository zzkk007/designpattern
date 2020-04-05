
 对于创建型模式，前面我们已经讲了单例模式、工厂模式、建造者模式，今天我们来讲最后一个：原型模式。

1、原型模式：如何最快速地clone 一个HashMap散列表

    JavaScript 是一种基于原型的面向对象编程语言。

2、原型模式的原理与应用

    如果对象的创建成本比较大，而同一个类的不同对象之间差别不大（大部分字段都相同），
    在这种情况下，我们可以利用对已有对象（原型）进行复制（或者叫拷贝）的方式来创建新对象，
    以达到节省创建时间的目的。这种基于原型来创建对象的方式就叫作原型设计模式（Prototype Design Pattern），
    简称原型模式。     
    
3、何为“对象的创建成本比较大”
    
    实际上，创建对象包含的申请内存、给成员变量赋值这一过程，本身并不会花费太多时间。
    或者说对于大部分业务系统来说，这点时间完全是可以忽略的。
    应用一个复杂的模式，只得到一点点的性能提升，这就是所谓的过度设计，得不偿失。    
    
    但是，如果对象中的数据需要经过复杂的计算才能得到（比如排序、计算哈希值），
    或者需要从 RPC、网络、数据库、文件系统等非常慢速的 IO 中读取，
    这种情况下，我们就可以利用原型模式，从其他已有对象中直接拷贝得到，
    而不用每次在创建新对象的时候，都重复执行这些耗时的操作。
    
        public class Demo {
          private HashMap<String, SearchWord> currentKeywords=new HashMap<>();
          private long lastUpdateTime = -1;
        
          public void refresh() {
            // 原型模式就这么简单，拷贝已有对象的数据，更新少量差值
            HashMap<String, SearchWord> newKeywords = (HashMap<String, SearchWord>) currentKeywords.clone();
        
            // 从数据库中取出更新时间>lastUpdateTime的数据，放入到newKeywords中
            List<SearchWord> toBeUpdatedSearchWords = getSearchWords(lastUpdateTime);
            long maxNewUpdatedTime = lastUpdateTime;
            for (SearchWord searchWord : toBeUpdatedSearchWords) {
              if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                maxNewUpdatedTime = searchWord.getLastUpdateTime();
              }
              if (newKeywords.containsKey(searchWord.getKeyword())) {
                SearchWord oldSearchWord = newKeywords.get(searchWord.getKeyword());
                oldSearchWord.setCount(searchWord.getCount());
                oldSearchWord.setLastUpdateTime(searchWord.getLastUpdateTime());
              } else {
                newKeywords.put(searchWord.getKeyword(), searchWord);
              }
            }
        
            lastUpdateTime = maxNewUpdatedTime;
            currentKeywords = newKeywords;
          }
        
          private List<SearchWord> getSearchWords(long lastUpdateTime) {
            // TODO: 从数据库中取出更新时间>lastUpdateTime的数据
            return null;
          }
        }
        
    
    2. 原型模式的两种实现方法原型模式有两种实现方法，深拷贝和浅拷贝。
        浅拷贝只会复制对象中基本数据类型数据和引用对象的内存地址，不会递归地复制引用对象，
        以及引用对象的引用对象……而深拷贝得到的是一份完完全全独立的对象。
        所以，深拷贝比起浅拷贝来说，更加耗时，更加耗内存空间。
        
        如果要拷贝的对象是不可变对象，浅拷贝共享不可变对象是没问题的，
        但对于可变对象来说，浅拷贝得到的对象和原始对象会共享部分数据，
        就有可能出现数据被修改的风险，也就变得复杂多了。
        除非像我们今天实战中举的那个例子，需要从数据库中加载 10 万条数据并构建散列表索引，
        操作非常耗时，比较推荐使用浅拷贝，否则，没有充分的理由，不要为了一点点的性能提升而使用浅拷贝。   
        
    3、那如何实现深拷贝呢？总结一下的话，有下面两种方法。
    
        第一种方法：递归拷贝对象、对象的引用对象以及引用对象的引用对象……直到要拷贝的对象只包含基本数据类型数据，没有引用对象为止。
        根据这个思路对之前的代码进行重构。重构之后的代码如下所示：
            public class Demo {
              private HashMap<String, SearchWord> currentKeywords=new HashMap<>();
              private long lastUpdateTime = -1;
            
              public void refresh() {
                // Deep copy
                HashMap<String, SearchWord> newKeywords = new HashMap<>();
                for (HashMap.Entry<String, SearchWord> e : currentKeywords.entrySet()) {
                  SearchWord searchWord = e.getValue();
                  SearchWord newSearchWord = new SearchWord(
                          searchWord.getKeyword(), searchWord.getCount(), searchWord.getLastUpdateTime());
                  newKeywords.put(e.getKey(), newSearchWord);
                }
            
                // 从数据库中取出更新时间>lastUpdateTime的数据，放入到newKeywords中
                List<SearchWord> toBeUpdatedSearchWords = getSearchWords(lastUpdateTime);
                long maxNewUpdatedTime = lastUpdateTime;
                for (SearchWord searchWord : toBeUpdatedSearchWords) {
                  if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                    maxNewUpdatedTime = searchWord.getLastUpdateTime();
                  }
                  if (newKeywords.containsKey(searchWord.getKeyword())) {
                    SearchWord oldSearchWord = newKeywords.get(searchWord.getKeyword());
                    oldSearchWord.setCount(searchWord.getCount());
                    oldSearchWord.setLastUpdateTime(searchWord.getLastUpdateTime());
                  } else {
                    newKeywords.put(searchWord.getKeyword(), searchWord);
                  }
                }
            
                lastUpdateTime = maxNewUpdatedTime;
                currentKeywords = newKeywords;
              }
            
              private List<SearchWord> getSearchWords(long lastUpdateTime) {
                // TODO: 从数据库中取出更新时间>lastUpdateTime的数据
                return null;
              }
            
            }
                    

        第二种方法：先将对象序列化，然后再反序列化成新的对象。具体的示例代码如下所示：
         
            public Object deepCopy(Object object) {
              ByteArrayOutputStream bo = new ByteArrayOutputStream();
              ObjectOutputStream oo = new ObjectOutputStream(bo);
              oo.writeObject(object);
              
              ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
              ObjectInputStream oi = new ObjectInputStream(bi);
              
              return oi.readObject();
            }   

4、最终方案         
   
    我们可以先采用浅拷贝的方式创建 newKeywords。对于需要更新的 SearchWord 对象，
    我们再使用深度拷贝的方式创建一份新的对象，替换 newKeywords 中的老对象。
    毕竟需要更新的数据是很少的。
    这种方式即利用了浅拷贝节省时间、空间的优点，又能保证 currentKeywords 中的中数据都是老版本的数据。
    具体的代码实现如下所示。这也是标题中讲到的，在我们这个应用场景下，最快速 clone 散列表的方式。        
    
            
        public class Demo {
          private HashMap<String, SearchWord> currentKeywords=new HashMap<>();
          private long lastUpdateTime = -1;
        
          public void refresh() {
            // Shallow copy
            HashMap<String, SearchWord> newKeywords = (HashMap<String, SearchWord>) currentKeywords.clone();
        
            // 从数据库中取出更新时间>lastUpdateTime的数据，放入到newKeywords中
            List<SearchWord> toBeUpdatedSearchWords = getSearchWords(lastUpdateTime);
            long maxNewUpdatedTime = lastUpdateTime;
            for (SearchWord searchWord : toBeUpdatedSearchWords) {
              if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                maxNewUpdatedTime = searchWord.getLastUpdateTime();
              }
              if (newKeywords.containsKey(searchWord.getKeyword())) {
                newKeywords.remove(searchWord.getKeyword());
              }
              newKeywords.put(searchWord.getKeyword(), searchWord);
            }
        
            lastUpdateTime = maxNewUpdatedTime;
            currentKeywords = newKeywords;
          }
        
          private List<SearchWord> getSearchWords(long lastUpdateTime) {
            // TODO: 从数据库中取出更新时间>lastUpdateTime的数据
            return null;
          }
        }    
        
    