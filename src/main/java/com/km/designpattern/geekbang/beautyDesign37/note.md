
 
    程序 bug 往往都是出现在一些边界条件和异常情况下。
    所以异常处理得好坏直接影响了代码的健壮性。

1、是应该直接抛出错异常哈还是应该封装成新的异常

    要看函数跟异常是否有业务相关性，
    getLastFieldOfHostName() 函数用来获取主机名的最后一个字段，
    UnknownHostException 异常表示主机名获取失败，两者算是业务相关，
    所以可以直接将 UnknownHostException 抛出，不需要重新包裹成新的异常。 
     
     private String getLastFieldOfHostName() throws UnknownHostException{
        String substrOfHostName = null;
        String hostName = InetAddress.getLocalHost().getHostName();
        substrOfHostName = getLastSubstrSplittedByDot(hostName);
        return substrOfHostName;
     }
    
    getLastFieldOfHostName() 函数修改之后，generate() 函数也要做相应的修改。
    我们需要在 generate() 函数中，捕获 getLastFieldOfHostName() 抛出的 
    UnknownHostException 异常。当我们捕获到这个异常之后，应该怎么处理呢？     
    
    按照之前的分析，ID 生成失败的时候，我们需要明确地告知调用者。
    所以，我们不能在 generate() 函数中，将 UnknownHostException 这个异常吞掉。
    那我们应该原封不动地抛出，还是封装成新的异常抛出呢？
    
    我们选择后者。在 generate() 函数中，我们需要捕获 UnknownHostException 异常，
    并重新包裹成新的异常 IdGenerationFailureException 往上抛出。
    之所以这么做，有下面三个原因。
    
    调用者在使用 generate() 函数的时候，只需要知道它生成的是随机唯一 ID，
    并不关心 ID 是如何生成的。也就说是，这是依赖抽象而非实现编程。
    如果 generate() 函数直接抛出 UnknownHostException 异常，实际上是暴露了实现细节。
    
    从代码封装的角度来讲，我们不希望将 UnknownHostException 这个比较底层的异常，
    暴露给更上层的代码，也就是调用 generate() 函数的代码。而且，调用者拿到这个异常的时候，
    并不能理解这个异常到底代表了什么，也不知道该如何处理。
    
    UnknownHostException 异常跟 generate() 函数，在业务概念上没有相关性。        

    
      public String generate() throws IdGenerationFailureException {
        String substrOfHostName = null;
        try {
          substrOfHostName = getLastFieldOfHostName();
        } catch (UnknownHostException e) {
          throw new IdGenerationFailureException("host name is empty.");
        }
        long currentTimeMillis = System.currentTimeMillis();
        String randomString = generateRandomAlphameric(8);
        String id = String.format("%s-%d-%s",
                substrOfHostName, currentTimeMillis, randomString);
        return id;
      }

2、请求参数为空字段的问题：

    
      @VisibleForTesting
      protected String getLastSubstrSplittedByDot(String hostName) {
        String[] tokens = hostName.split("\\.");
        String substrOfHostName = tokens[tokens.length - 1];
        return substrOfHostName;
      }    
      
      理论上讲，参数传递的正确性应该有程序员来保证，我们无需做 NULL 值或者空字符串的判断和特殊处理。
      调用者本不应该把 NULL 值或者空字符串传递给 getLastSubstrSplittedByDot() 函数。
      如果传递了，那就是 code bug，需要修复。
      但是，话说回来，谁也保证不了程序员就一定不会传递 NULL 值或者空字符串。
      那我们到底该不该做 NULL 值或空字符串的判断呢？
      
      如果函数是 private 类私有的，只在类内部被调用，完全在你自己的掌控之下，自己保证这个调用的 private
      函数的时候，不要传递NULL值或空字符串就可以了。所以，我们可以不在private函数中做null值或者空字符串的判断。
      如果函数是public的，你无法掌控会被调用已经如何调用，为了尽可能提高代码的健壮性，我们最好是在public函数
      中做null值或空字符串校验。虽然加上有些冗余，但多加些检验总归不会错的。
      
      
      @VisibleForTesting
      protected String getLastSubstrSplittedByDot(String hostName) {
        if (hostName == null || hostName.isEmpty()) {
          throw IllegalArgumentException("..."); //运行时异常
        }
        String[] tokens = hostName.split("\\.");
        String substrOfHostName = tokens[tokens.length - 1];
        return substrOfHostName;
      }
      
     按照上面讲的，我们在使用这个函数的时候，自己也要保证不传递 NULL 值或者空字符串进去。
     所以，getLastFieldOfHostName() 函数的代码也要作相应的修改。
     修改之后的代码如下所示： 
         private String getLastFieldOfHostName() throws UnknownHostException{
            String substrOfHostName = null;
            String hostName = InetAddress.getLocalHost().getHostName();
            if (hostName == null || hostName.isEmpty()) { // 此处做判断
              throw new UnknownHostException("...");
            }
            substrOfHostName = getLastSubstrSplittedByDot(hostName);
            return substrOfHostName;
         }
          
      
总结：
    
    再简单的代码，看上去再完美的代码，只要我们下功夫去推敲，总有可以优化的空间，
    就看你愿不愿把事情做到极致。
    
    如果你内功不够深厚，理论知识不够扎实，那你就很难参透开源项目的代码到底优秀在哪里。
    就像如果我们没有之前的理论学习，没有今天我给你一点一点重构、讲解、分析，
    只是给你最后重构好的 RandomIdGenerator 的代码，你真的能学到它的设计精髓吗？
    
    对比第 34 节课最初小王的 IdGenerator 代码和最终的 RandomIdGenerator 代码，
    它们一个是“能用”，一个是“好用”，天壤之别。作为一名程序员，起码对代码要有追求啊，
    不然跟咸鱼有啥区别！        
    
    