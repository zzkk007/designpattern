package com.km.designpattern.geekbang.beautyDesign24;

/**
 * 如何实现一个遵从设计原则的积分兑换系统？
 *
 *  业务系统设计和开发，三方面的工作：接口设计、数据库设计、业务模型设计(业务逻辑)。
 *  大部分业务系统的开发都可以分为 Controller、Service、Repository 三层。
 *
 *  1、分层能起到代码复用的作用 DRY原则（Don't Repeat Yourself）。
 *
 *  2、分层能起到隔离变化的作用
 *      分层体现了一种抽象和封装的设计思想。比如，Repository 层封装了对数据库访问的操作，提供了抽象的数据访问接口。
 *      基于接口而非实现编程的设计思想，Service 层使用 Repository 层提供的接口，
 *      并不关心其底层依赖的是哪种具体的数据库。
 *
 *      Controller、Service、Repository
 *      三层代码的稳定程度不同、引起变化的原因不同，所以分成三层来组织代码，能有效地隔离变化。
 *
 *  3、分层能起到隔离关注点的作用。
 *      Controller 层关注与外界打交道、数据校验，封装，格式转换，并不关心业务层逻辑
 *      Service 层只关注业务逻辑，不关注数据来源
 *      Repository 层只关注数据的读写
 *      三层关注点不同，分层之后，职责分明。
 *
 *  4、分层能提高代码的可测试性
 *
 *  5、分层能应对系统的复杂性
 *     当一个类或一个函数的代码过多之后，可读性、可维护性就会变差。
 *     那我们就要想办法拆分。拆分有垂直和水平两个方向。
 *     水平方向基于业务来做拆分，就是模块化，垂直方向基于流程来做拆分，就是分层。
 *
 * 6、BO、VO、Entity 存在的意义是什么？
 *
 *  针对 Controller、Service、Repository 三层，每层都会定义相应的数据对象，
 *  它们分别是 VO（View Object）、BO（Business Object）、Entity，例如 UserVo、UserBo、UserEntity。
 *  在实际的开发中，VO、BO、Entity 可能存在大量的重复字段，甚至三者包含的字段完全一样。
 *  在开发的过程中，我们经常需要重复定义三个几乎一样的类，显然是一种重复劳动。
 *  我更加推荐每层都定义各自的数据对象这种设计思路，主要有以下 3 个方面的原因。
 *  (1) VO、BO、Entity 并非完全一样。比如，我们可以在 UserEntity、UserBo 中定义 Password 字段，
 *      但显然不能在 UserVo 中定义 Password 字段，否则就会将用户的密码暴露出去。
 *  (2) VO、BO、Entity 三个类虽然代码重复，但功能语义不重复，从职责上讲是不一样的。所以，也并不能算违背 DRY 原则。
 *
 *  (3) 为了尽量减少每层之间的耦合，把职责边界划分明确，每层都会维护自己的数据对象，层与层之间通过接口交互。
 *      数据从下一层传递到上一层的时候，将下一层的数据对象转化成上一层的数据对象，再继续处理。
 *      虽然这样的设计稍微有些繁琐，每层都需要定义各自的数据对象，需要做数据对象之间的转化，但是分层清晰。
 *      对于非常大的项目来说，结构清晰是第一位的！
 *
 * 7、既然 VO、BO、Entity 不能合并，那如何解决代码重复的问题呢？
 *    （1）继承可以解决代码重复问题。我们可以将公共的字段定义在父类中，让 VO、BO、Entity 都继承这个父类，各自只定义特有的字段。
 *    （2）多用组合，少用继承”设计思想的时候，我们提到，组合也可以解决代码重复的问题，
 *          所以，这里我们还可以将公共的字段抽取到公共的类中，VO、BO、Entity 通过组合关系来复用这个类的代码。
 *          VO、BO、Entity 通过组合关系来复用这个类的代码不是特别好，尤其是VO。因为用组合的方式会增加返回数据的层次，
 *          这对前端来说是不是不不太友好，主要是对象转json的格式问题。
 *
 * 8、代码重复问题解决了，那不同分层之间的数据对象该如何互相转化呢？
 *   当下一层的数据通过接口调用传递到上一层之后，我们需要将它转化成上一层对应的数据对象类型。
 *   比如，Service 层从 Repository 层获取的 Entity 之后，将其转化成 BO，再继续业务逻辑的处理。
 *   所以，整个开发的过程会涉及“Entity 到 BO”和“BO 到 VO”这两种转化。

 */
public class note {
}