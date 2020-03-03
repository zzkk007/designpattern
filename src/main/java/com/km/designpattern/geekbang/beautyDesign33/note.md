
编程技巧：

1、把代码分割成更小的单元块：
    
2、避免函数参数过多
    
    函数包含3、4 个参数的时候还是能接受的，大于等于5个的时候就有点多了。
    考虑函数是否职责单一，是否能通过拆分成多个函数的方式来减少参数。   
    
    将函数的参数封装成对象。
    
        public void postBlog(String title, String summary, String keywords, String content, String category, long authorId);
       
        // 将参数封装成对象
        public class Blog {
          private String title;
          private String summary;
          private String keywords;
          private Strint content;
          private String category;
          private long authorId;
        }
        public void postBlog(Blog blog);
        
    除此之外，如果函数是对外暴露的远程接口，将参数封装成对象，还可以提高接口的兼容性。
    在往接口中添加新的参数的时候，老的远程接口调用者有可能就不需要修改代码来兼容新的接口了。    
    
3、 勿用函数参数来控制逻辑：
    
4、函数设计要职责单一
    
    public boolean checkUserIfExisting(String telephone, String username, String email)  { 
      if (!StringUtils.isBlank(telephone)) {
        User user = userRepo.selectUserByTelephone(telephone);
        return user != null;
      }
      
      if (!StringUtils.isBlank(username)) {
        User user = userRepo.selectUserByUsername(username);
        return user != null;
      }
      
      if (!StringUtils.isBlank(email)) {
        User user = userRepo.selectUserByEmail(email);
        return user != null;
      }
      
      return false;
    }
    
    // 拆分成三个函数
    public boolean checkUserIfExistingByTelephone(String telephone);
    public boolean checkUserIfExistingByUsername(String username);
    public boolean checkUserIfExistingByEmail(String email);

5. 移除过深的嵌套层次：


    代码嵌套层次过深往往是因为 if-else、switch-case、for 循环过度嵌套导致的。
    我个人建议，嵌套最好不超过两层，超过两层之后就要思考一下是否可以减少嵌套。
    过深的嵌套本身理解起来就比较费劲，除此之外，嵌套过深很容易因为代码多次缩进，
    导致嵌套内部的语句超过一行的长度而折成两行，影响代码的整洁。解决嵌套过深的方法也比较成熟，
        
    有下面 4 种常见的思路。

    （1）去掉多余的 if 或 else 语句。代码示例如下所示：
        
     
        // 示例一
        public double caculateTotalAmount(List<Order> orders) {
          if (orders == null || orders.isEmpty()) {
            return 0.0;
          } else { // 此处的else可以去掉
            double amount = 0.0;
            for (Order order : orders) {
              if (order != null) {
                amount += (order.getCount() * order.getPrice());
              }
            }
            return amount;
          }
        }
        
        // 示例二
        public List<String> matchStrings(List<String> strList,String substr) {
          List<String> matchedStrings = new ArrayList<>();
          if (strList != null && substr != null) {
            for (String str : strList) {
              if (str != null) { // 跟下面的if语句可以合并在一起
                if (str.contains(substr)) {
                  matchedStrings.add(str);
                }
              }
            }
          }
          return matchedStrings;
        }   

    (2)使用编程语言提供的 continue、break、return 关键字，提前退出嵌套。代码示例如下所示：
        
        
        // 重构前的代码
        public List<String> matchStrings(List<String> strList,String substr) {
          List<String> matchedStrings = new ArrayList<>();
          if (strList != null && substr != null){ 
            for (String str : strList) {
              if (str != null && str.contains(substr)) {
                matchedStrings.add(str);
                // 此处还有10行代码...
              }
            }
          }
          return matchedStrings;
        }
        
        // 重构后的代码：使用continue提前退出
        public List<String> matchStrings(List<String> strList,String substr) {
          List<String> matchedStrings = new ArrayList<>();
          if (strList != null && substr != null){ 
            for (String str : strList) {
              if (str == null || !str.contains(substr)) {
                continue; 
              }
              matchedStrings.add(str);
              // 此处还有10行代码...
            }
          }
          return matchedStrings;
        }  
    
    3、调整执行顺序来减少嵌套。具体的代码示例如下所示 
        
        
        // 重构前的代码
        public List<String> matchStrings(List<String> strList,String substr) {
          List<String> matchedStrings = new ArrayList<>();
          if (strList != null && substr != null) {
            for (String str : strList) {
              if (str != null) {
                if (str.contains(substr)) {
                  matchedStrings.add(str);
                }
              }
            }
          }
          return matchedStrings;
        }
        
        // 重构后的代码：先执行判空逻辑，再执行正常逻辑
        public List<String> matchStrings(List<String> strList,String substr) {
          if (strList == null || substr == null) { //先判空
            return Collections.emptyList();
          }
        
          List<String> matchedStrings = new ArrayList<>();
          for (String str : strList) {
            if (str != null) {
              if (str.contains(substr)) {
                matchedStrings.add(str);
              }
            }
          }
          return matchedStrings;
        }     

    4、将部分嵌套逻辑封装成函数调用，以此来减少嵌套。具体的代码示例如下所示：
           
        // 重构前的代码
        public List<String> appendSalts(List<String> passwords) {
          if (passwords == null || passwords.isEmpty()) {
            return Collections.emptyList();
          }
          
          List<String> passwordsWithSalt = new ArrayList<>();
          for (String password : passwords) {
            if (password == null) {
              continue;
            }
            if (password.length() < 8) {
              // ...
            } else {
              // ...
            }
          }
          return passwordsWithSalt;
        }
        
        // 重构后的代码：将部分逻辑抽成函数
        public List<String> appendSalts(List<String> passwords) {
          if (passwords == null || passwords.isEmpty()) {
            return Collections.emptyList();
          }
        
          List<String> passwordsWithSalt = new ArrayList<>();
          for (String password : passwords) {
            if (password == null) {
              continue;
            }
            passwordsWithSalt.add(appendSalt(password));
          }
          return passwordsWithSalt;
        }
        
        private String appendSalt(String password) {
          String passwordWithSalt = password;
          if (password.length() < 8) {
            // ...
          } else {
            // ...
          }
          return passwordWithSalt;
        }    








6、学会使用解释性变量：

