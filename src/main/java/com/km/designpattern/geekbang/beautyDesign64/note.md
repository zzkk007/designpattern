状态模式：游戏、工作流引擎中常用的状态机是如何实现的

    状态模式一般用来实现状态机，而状态机常用在游戏、工作流引擎等系统开发中。
    不过，状态机的实现方式有多种，除了状态模式，比较常用的还有分支逻辑法和查表法。

1、什么是有限状态机

    有限状态机，英文翻译是 Finite State Machine，缩写为 FSM，简称为状态机。
    状态机有 3 个组成部分：状态（State）、事件（Event）、动作（Action）。
    其中，事件也称为转移条件（Transition Condition）。事件触发状态的转移及动作的执行。
    不过，动作不是必须的，也可能只转移状态，不执行任何动作。
    
    我写了一个骨架代码，如下所示。其中，obtainMushRoom()、obtainCape()、obtainFireFlower()、meetMonster() 这几个函数，
    能够根据当前的状态和事件，更新状态和增减积分。
    
    
        public enum State {
          SMALL(0),
          SUPER(1),
          FIRE(2),
          CAPE(3);
        
          private int value;
        
          private State(int value) {
            this.value = value;
          }
        
          public int getValue() {
            return this.value;
          }
        }
        
        public class MarioStateMachine {
          private int score;
          private State currentState;
        
          public MarioStateMachine() {
            this.score = 0;
            this.currentState = State.SMALL;
          }
        
          public void obtainMushRoom() {
            //TODO
          }
        
          public void obtainCape() {
            //TODO
          }
        
          public void obtainFireFlower() {
            //TODO
          }
        
          public void meetMonster() {
            //TODO
          }
        
          public int getScore() {
            return this.score;
          }
        
          public State getCurrentState() {
            return this.currentState;
          }
        }
        
        public class ApplicationDemo {
          public static void main(String[] args) {
            MarioStateMachine mario = new MarioStateMachine();
            mario.obtainMushRoom();
            int score = mario.getScore();
            State state = mario.getCurrentState();
            System.out.println("mario score: " + score + "; state: " + state);
          }
        }
    
状态机实现方式一：分支逻辑法:
    
    最简单直接的实现方式是，参照状态转移图，将每一个状态转移，原模原样地直译成代码。
    这样编写的代码会包含大量的 if-else 或 switch-case 分支判断逻辑，甚至是嵌套的分支判断逻辑，
    所以，我把这种方法暂且命名为分支逻辑法。
    
        public class MarioStateMachine {
          private int score;
          private State currentState;
        
          public MarioStateMachine() {
            this.score = 0;
            this.currentState = State.SMALL;
          }
        
          public void obtainMushRoom() {
            if (currentState.equals(State.SMALL)) {
              this.currentState = State.SUPER;
              this.score += 100;
            }
          }
        
          public void obtainCape() {
            if (currentState.equals(State.SMALL) || currentState.equals(State.SUPER) ) {
              this.currentState = State.CAPE;
              this.score += 200;
            }
          }
        
          public void obtainFireFlower() {
            if (currentState.equals(State.SMALL) || currentState.equals(State.SUPER) ) {
              this.currentState = State.FIRE;
              this.score += 300;
            }
          }
        
          public void meetMonster() {
            if (currentState.equals(State.SUPER)) {
              this.currentState = State.SMALL;
              this.score -= 100;
              return;
            }
        
            if (currentState.equals(State.CAPE)) {
              this.currentState = State.SMALL;
              this.score -= 200;
              return;
            }
        
            if (currentState.equals(State.FIRE)) {
              this.currentState = State.SMALL;
              this.score -= 300;
              return;
            }
          }
        
          public int getScore() {
            return this.score;
          }
        
          public State getCurrentState() {
            return this.currentState;
          }
        }

状态机实现方式二：查表法:
    
    实际上，除了用状态转移图来表示之外，状态机还可以用二维表来表示，如下所示。
    在这个二维表中，第一维表示当前状态，第二维表示事件，值表示当前状态经过事件之后，转移到的新状态及其执行的动作。
    
    相对于分支逻辑的实现方式，查表法的代码实现更加清晰，可读性和可维护性更好。
    当修改状态机时，我们只需要修改 transitionTable 和 actionTable 两个二维数组即可。
    实际上，如果我们把这两个二维数组存储在配置文件中，当需要修改状态机时，我们甚至可以不修改任何代码，
    只需要修改配置文件就可以了。具体的代码如下所示：
    
    
        public enum Event {
          GOT_MUSHROOM(0),
          GOT_CAPE(1),
          GOT_FIRE(2),
          MET_MONSTER(3);
        
          private int value;
        
          private Event(int value) {
            this.value = value;
          }
        
          public int getValue() {
            return this.value;
          }
        }
        
        public class MarioStateMachine {
          private int score;
          private State currentState;
        
          private static final State[][] transitionTable = {
                  {SUPER, CAPE, FIRE, SMALL},
                  {SUPER, CAPE, FIRE, SMALL},
                  {CAPE, CAPE, CAPE, SMALL},
                  {FIRE, FIRE, FIRE, SMALL}
          };
        
          private static final int[][] actionTable = {
                  {+100, +200, +300, +0},
                  {+0, +200, +300, -100},
                  {+0, +0, +0, -200},
                  {+0, +0, +0, -300}
          };
        
          public MarioStateMachine() {
            this.score = 0;
            this.currentState = State.SMALL;
          }
        
          public void obtainMushRoom() {
            executeEvent(Event.GOT_MUSHROOM);
          }
        
          public void obtainCape() {
            executeEvent(Event.GOT_CAPE);
          }
        
          public void obtainFireFlower() {
            executeEvent(Event.GOT_FIRE);
          }
        
          public void meetMonster() {
            executeEvent(Event.MET_MONSTER);
          }
        
          private void executeEvent(Event event) {
            int stateValue = currentState.getValue();
            int eventValue = event.getValue();
            this.currentState = transitionTable[stateValue][eventValue];
            this.score = actionTable[stateValue][eventValue];
          }
        
          public int getScore() {
            return this.score;
          }
        
          public State getCurrentState() {
            return this.currentState;
          }
        
        }

状态机实现方式三：状态模式:
    
    在查表法的代码实现中，事件触发的动作只是简单的积分加减，
    所以，我们用一个 int 类型的二维数组 actionTable 就能表示，二维数组中的值表示积分的加减值。
    但是，如果要执行的动作并非这么简单，而是一系列复杂的逻辑操作（比如加减积分、写数据库，还有可能发送消息通知等等），
    我们就没法用如此简单的二维数组来表示了。这也就是说，查表法的实现方式有一定局限性。
    
    虽然分支逻辑的实现方式不存在这个问题，但它又存在前面讲到的其他问题，比如分支判断逻辑较多，导致代码可读性和可维护性不好等。
    实际上，针对分支逻辑法存在的问题，我们可以使用状态模式来解决。
    
    状态模式通过将事件触发的状态转移和动作执行，拆分到不同的状态类中，来避免分支判断逻辑。我们还是结合代码来理解这句话。
    其中，IMario 是状态的接口，定义了所有的事件。SmallMario、SuperMario、CapeMario、FireMario 是 IMario 接口的实现类，
    分别对应状态机中的 4 个状态。原来所有的状态转移和动作执行的代码逻辑，
    都集中在 MarioStateMachine 类中，现在，这些代码逻辑被分散到了这 4 个状态类中。
    
    
        public interface IMario { //所有状态类的接口
          State getName();
          //以下是定义的事件
          void obtainMushRoom();
          void obtainCape();
          void obtainFireFlower();
          void meetMonster();
        }
        
        public class SmallMario implements IMario {
          private MarioStateMachine stateMachine;
        
          public SmallMario(MarioStateMachine stateMachine) {
            this.stateMachine = stateMachine;
          }
        
          @Override
          public State getName() {
            return State.SMALL;
          }
        
          @Override
          public void obtainMushRoom() {
            stateMachine.setCurrentState(new SuperMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() + 100);
          }
        
          @Override
          public void obtainCape() {
            stateMachine.setCurrentState(new CapeMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() + 200);
          }
        
          @Override
          public void obtainFireFlower() {
            stateMachine.setCurrentState(new FireMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() + 300);
          }
        
          @Override
          public void meetMonster() {
            // do nothing...
          }
        }
        
        public class SuperMario implements IMario {
          private MarioStateMachine stateMachine;
        
          public SuperMario(MarioStateMachine stateMachine) {
            this.stateMachine = stateMachine;
          }
        
          @Override
          public State getName() {
            return State.SUPER;
          }
        
          @Override
          public void obtainMushRoom() {
            // do nothing...
          }
        
          @Override
          public void obtainCape() {
            stateMachine.setCurrentState(new CapeMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() + 200);
          }
        
          @Override
          public void obtainFireFlower() {
            stateMachine.setCurrentState(new FireMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() + 300);
          }
        
          @Override
          public void meetMonster() {
            stateMachine.setCurrentState(new SmallMario(stateMachine));
            stateMachine.setScore(stateMachine.getScore() - 100);
          }
        }
        
        // 省略CapeMario、FireMario类...
        
        public class MarioStateMachine {
          private int score;
          private IMario currentState; // 不再使用枚举来表示状态
        
          public MarioStateMachine() {
            this.score = 0;
            this.currentState = new SmallMario(this);
          }
        
          public void obtainMushRoom() {
            this.currentState.obtainMushRoom();
          }
        
          public void obtainCape() {
            this.currentState.obtainCape();
          }
        
          public void obtainFireFlower() {
            this.currentState.obtainFireFlower();
          }
        
          public void meetMonster() {
            this.currentState.meetMonster();
          }
        
          public int getScore() {
            return this.score;
          }
        
          public State getCurrentState() {
            return this.currentState.getName();
          }
        
          public void setScore(int score) {
            this.score = score;
          }
        
          public void setCurrentState(IMario currentState) {
            this.currentState = currentState;
          }
        }

    实际上，像游戏这种比较复杂的状态机，包含的状态比较多，我优先推荐使用查表法，而状态模式会引入非常多的状态类，会导致代码比较难维护。
    相反，像电商下单、外卖下单这种类型的状态机，它们的状态并不多，状态转移也比较简单，
    但事件触发执行的动作包含的业务逻辑可能会比较复杂，所以，更加推荐使用状态模式来实现。











    
        
