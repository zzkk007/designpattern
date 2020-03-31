
工厂模式: Factory Design Pattern

工厂模式分为三种更加细分的类型：简单工厂、工厂方法和抽象工厂。

1、简单工厂 （Simple Factory）
    
    
      public RuleConfig load(String ruleConfigFilePath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = createParser(ruleConfigFileExtension);
        if (parser == null) {
          throw new InvalidRuleConfigException(
                  "Rule config file format is not supported: " + ruleConfigFilePath);
        }
    
        String configText = "";
        //从ruleConfigFilePath文件中读取配置文本到configText中
        RuleConfig ruleConfig = parser.parse(configText);
        return ruleConfig;
      }
    
      private String getFileExtension(String filePath) {
        //...解析文件名获取扩展名，比如rule.json，返回json
        return "json";
      }
    
      private IRuleConfigParser createParser(String configFormat) {
        IRuleConfigParser parser = null;
        if ("json".equalsIgnoreCase(configFormat)) {
          parser = new JsonRuleConfigParser();
        } else if ("xml".equalsIgnoreCase(configFormat)) {
          parser = new XmlRuleConfigParser();
        } else if ("yaml".equalsIgnoreCase(configFormat)) {
          parser = new YamlRuleConfigParser();
        } else if ("properties".equalsIgnoreCase(configFormat)) {
          parser = new PropertiesRuleConfigParser();
        }
        return parser;
      }
    }

    "================================================================================="

    public class RuleConfigParserFactory {
      private static final Map<String, RuleConfigParser> cachedParsers = new HashMap<>();
    
      static {
        cachedParsers.put("json", new JsonRuleConfigParser());
        cachedParsers.put("xml", new XmlRuleConfigParser());
        cachedParsers.put("yaml", new YamlRuleConfigParser());
        cachedParsers.put("properties", new PropertiesRuleConfigParser());
      }
    
      public static IRuleConfigParser createParser(String configFormat) {
        if (configFormat == null || configFormat.isEmpty()) {
          return null;//返回null还是IllegalArgumentException全凭你自己说了算
        }
        IRuleConfigParser parser = cachedParsers.get(configFormat.toLowerCase());
        return parser;
      }
    }
    
    
2、工厂方法（Factory Method）

    如果我们非得要将 if 分支逻辑去掉，那该怎么办呢？比较经典处理方法就是利用多态。
    按照多态的实现思路，对上面的代码进行重构。重构之后的代码如下所示：
   
    
    public interface IRuleConfigParserFactory {
      IRuleConfigParser createParser();
    }
    
    public class JsonRuleConfigParserFactory implements IRuleConfigParserFactory {
      @Override
      public IRuleConfigParser createParser() {
        return new JsonRuleConfigParser();
      }
    }
    
    public class XmlRuleConfigParserFactory implements IRuleConfigParserFactory {
      @Override
      public IRuleConfigParser createParser() {
        return new XmlRuleConfigParser();
      }
    }
    
    public class YamlRuleConfigParserFactory implements IRuleConfigParserFactory {
      @Override
      public IRuleConfigParser createParser() {
        return new YamlRuleConfigParser();
      }
    }
    
    public class PropertiesRuleConfigParserFactory implements IRuleConfigParserFactory {
      @Override
      public IRuleConfigParser createParser() {
        return new PropertiesRuleConfigParser();
      }
    }
    
    实际上，这就是工厂方法模式的典型代码实现。这样当我们新增一种 parser 的时候，
    只需要新增一个实现了 IRuleConfigParserFactory 接口的 Factory 类即可。
    所以，工厂方法模式比起简单工厂模式更加符合开闭原则。
    
    从上面的工厂方法的实现来看，一切都很完美，但是实际上存在挺大的问题。
    问题存在于这些工厂类的使用上。接下来，我们看一下，如何用这些工厂类来实现 RuleConfigSource 的 load() 函数。
    具体的代码如下所示： 
    
            
        public class RuleConfigSource {
          public RuleConfig load(String ruleConfigFilePath) {
            String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        
            IRuleConfigParserFactory parserFactory = null;
            if ("json".equalsIgnoreCase(ruleConfigFileExtension)) {
              parserFactory = new JsonRuleConfigParserFactory();
            } else if ("xml".equalsIgnoreCase(ruleConfigFileExtension)) {
              parserFactory = new XmlRuleConfigParserFactory();
            } else if ("yaml".equalsIgnoreCase(ruleConfigFileExtension)) {
              parserFactory = new YamlRuleConfigParserFactory();
            } else if ("properties".equalsIgnoreCase(ruleConfigFileExtension)) {
              parserFactory = new PropertiesRuleConfigParserFactory();
            } else {
              throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
            }
            IRuleConfigParser parser = parserFactory.createParser();
        
            String configText = "";
            //从ruleConfigFilePath文件中读取配置文本到configText中
            RuleConfig ruleConfig = parser.parse(configText);
            return ruleConfig;
          }
        
          private String getFileExtension(String filePath) {
            //...解析文件名获取扩展名，比如rule.json，返回json
            return "json";
          }
        }
            
    从上面的代码实现来看，工厂类对象的创建逻辑又耦合进了 load() 函数中，跟我们最初的代码版本非常相似，
    引入工厂方法非但没有解决问题，反倒让设计变得更加复杂了。那怎么来解决这个问题呢？
    
    我们可以为工厂类再创建一个简单工厂，也就是工厂的工厂，用来创建工厂类对象。
    这段话听起来有点绕，我把代码实现出来了，你一看就能明白了。
    其中，RuleConfigParserFactoryMap 类是创建工厂对象的工厂类，
    getParserFactory() 返回的是缓存好的单例工厂对象。
    
        public class RuleConfigSource {
          public RuleConfig load(String ruleConfigFilePath) {
            String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        
            IRuleConfigParserFactory parserFactory = RuleConfigParserFactoryMap.getParserFactory(ruleConfigFileExtension);
            if (parserFactory == null) {
              throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
            }
            IRuleConfigParser parser = parserFactory.createParser();
        
            String configText = "";
            //从ruleConfigFilePath文件中读取配置文本到configText中
            RuleConfig ruleConfig = parser.parse(configText);
            return ruleConfig;
          }
        
          private String getFileExtension(String filePath) {
            //...解析文件名获取扩展名，比如rule.json，返回json
            return "json";
          }
        }
        
        //因为工厂类只包含方法，不包含成员变量，完全可以复用，
        //不需要每次都创建新的工厂类对象，所以，简单工厂模式的第二种实现思路更加合适。
        public class RuleConfigParserFactoryMap { //工厂的工厂
          private static final Map<String, IRuleConfigParserFactory> cachedFactories = new HashMap<>();
        
          static {
            cachedFactories.put("json", new JsonRuleConfigParserFactory());
            cachedFactories.put("xml", new XmlRuleConfigParserFactory());
            cachedFactories.put("yaml", new YamlRuleConfigParserFactory());
            cachedFactories.put("properties", new PropertiesRuleConfigParserFactory());
          }
        
          public static IRuleConfigParserFactory getParserFactory(String type) {
            if (type == null || type.isEmpty()) {
              return null;
            }
            IRuleConfigParserFactory parserFactory = cachedFactories.get(type.toLowerCase());
            return parserFactory;
          }
        }
    
    
    当我们需要添加新的规则配置解析器的时候，我们只需要创建新的 parser 类和 parser factory 类，
    并且在 RuleConfigParserFactoryMap 类中，将新的 parser factory 对象添加到 cachedFactories 中即可。
    代码的改动非常少，基本上符合开闭原则。
    实际上，对于规则配置文件解析这个应用场景来说，工厂模式需要额外创建诸多 Factory 类，
    也会增加代码的复杂性，而且，每个 Factory 类只是做简单的 new 操作，功能非常单薄（只有一行代码），
    也没必要设计成独立的类，所以，在这个应用场景下，简单工厂模式简单好用，比工方法厂模式更加合适。
    
总结：

    当创建逻辑比较复杂，是一个 "大工程" 的时候，我们就考虑使用工厂模式，
    封装对象的创建过程，将对象的创建和使用相分离。何为创建逻辑比较复杂呢？我总结了下面两种情况。
        第一中情况：类似规则配置解析的例子，代码中存在 if-else 分支判断，动态地根据不同的类型创建不同的对象。
        针对这种情况，我们就考虑使用工厂模式，将这一大坨 if-else 创建对象的代码抽离出来，放到工厂类中。
        
        第二种情况：还有一种情况，尽管我们不需要根据不同的类型创建不同的对象，
        但是，单个对象本身的创建过程比较复杂，比如前面提到的要组合其他类对象，
        做各种初始化操作。在这种情况下，我们也可以考虑使用工厂模式，
        将对象的创建过程封装到工厂类中。   
    
    现在，我们上升一个思维层面来看工厂模式，它的作用无外乎下面这四个。
    这也是判断要不要使用工厂模式的最本质的参考标准。
        封装变化：创建逻辑有可能变化，封装成工厂类之后，创建逻辑的变更对调用者透明。
        代码复用：创建代码抽离到独立的工厂类之后可以复用。
        隔离复杂性：封装复杂的创建逻辑，调用者无需了解如何创建对象。
        控制复杂度：将创建代码抽离出来，让原本的函数或类职责更单一，代码更简洁。
    