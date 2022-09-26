# 快速使用：

### 1.创建数据库表

### 2.创建按springboot项目

### 3.引入相关依赖

```java
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--数据库相关 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
<!--        <dependency>-->
<!--            <groupId>com.baomidou</groupId>-->
<!--            <artifactId>mybatis-plus</artifactId>-->
<!--            <version>3.0.5</version>-->
<!--        </dependency>-->
        <!-- mybatis-plus相关:苞米豆 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.5</version>
        </dependency>

        <!-- lombok相关 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

### 4.application.properties中配置信息

```perporties
#配置数据库连接
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/表名?serverTimezone=GMT%2B8
spring.datasource.username=用户名
spring.datasource.password=密码
```

### 5.编写代码

```java
//创建对象类
@Data
public class Student {
    private String studentno;

    private String sname;

    private String sex;

    private Date birthdate;

    private int entrance;

    private String phone;

    private String Email;

}
```

```java
//Mapper类接口，只需要实现BaseMapper接口即可（简单sql都不用打了）
@mapper
@Repository		//使自动注入Autowird可以正常使用
public interface studentMapper extends BaseMapper<Student> {
}
```

```java
@SpringBootApplication
//启动类添加MapperScan注解,java包下的路径
@MapperScan("com.atguigu.mybatis_plus.mapper")
public class MybatisPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }

}

```

### 6.测试

```java
//test文件夹
@SpringBootTest
public class MybatisPlusApplicationTests {

    @Autowired
    private StudentMapper studentMapper;

    //查询student表所有数据
    @Test
    public void findAll() {
//        System.out.println("hello");
        List<Student> students = studentMapper.selectList(null);
        System.out.println(students);
    }

}

```





### 问题一：启动失败

注意，我们所需的jar包是springboot整合包<mybatis-plus-boot-starter>，而非mybatis_plus自身的包<mybatis-plus>，否则会没有Mapper扫描

```maven
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```



# 日志打印

```perporties
#mybatis日志打印
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```



# 新增信息

```java
//Test文件
	@Test
    public void addStudent() {
        Student student = new Student();
        student.setPhone("13567489");
        student.setSex("男");
        student.setStudentno("EW465654");
        student.setSname("蔡徐坤");
        int insert = studentMapper.insert(student);
        System.out.println(insert);
    }
//返回 1
```



# 主键生产策略

### 自动增长

每次值比上次多1				使用： **mysql关键字 AUTO INCREMENT**

缺点：分库分表时，还需要得到上张表最后一位的值



### UUID

每次生成唯一的值				使用： 有方法（最后12个字符串是时间序）

缺点：不可排序



### Redis生成ID

利用单线程的Redis				使用：原子操作(INCR和INCRBY)

缺点：如果系统本没使用Redis需要额外添加Redis，编码和配置工作量大



### mp自带实现

Twitter的snowflake算法		使用：snowflake（41bit作为毫秒数，10bit作为机器ID，12bit作为毫秒内流水号，最后一个符号位是0） 

TP：mp是mybatis-plus缩写



### 实际使用

```java
//在主键上添加 
@TableId(type = IdType.___)
//后面的横线选择：
    AUTO: 自动增长
    INPUT: 设置id值
    NONE: 输入
    UUID: 随机唯一值
    ID_WORKER: mp自带策略，生成19位值，数字类型使用这种策略，比如long
    ID_WORKER_STR: mp自带策略，生成19位值，字符串类型使用这种策略，比如String
```

注意：似乎主键字段一定是 **id**

问题：Mybatis-plus主键设置自增存在问题，需要数据库设置自增



# 自动填充功能

- 注解填充字段 **@TableField(..fill = FieldFill.INSERT)**

> 注意：FieldFill后接INSERT意味着在添加时自动填充
>
> 接UPDATE是在修改时自动填充
>
> INSERT_UPDATE是在添加和修改时都填充

```java
//Student类
	@TableField(fill = FieldFill.INSERT)
    private Date birthdate;

```

- 自定义实现类继承**MetaObjectHandle**接口

```java
...
import java.util.Date;
//将类交给springboot管理
@Component
public class StudentMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("birthdate",new Date(),metaObject);
    	//其他的继续写
    }

    @Override
    public void updateFill(MetaObject metaObject) {
		//修改写这
    }
}

```



# 锁

### 隔离级别 

有四种，分别是：读未提交、读已提交、可重复读、序列化。

　　**读未提交**： Read Uncommitted，顾名思义，就是一个事务可以读取另一个未提交事务的数据。最低级别，它存在4个常见问题（脏读、不可重复读、幻读、丢失更新）。
　　**读已提交**： Read Committed，顾名思义，就是一个事务要等另一个事务提交后才能读取数据。 它解决了脏读问题，存在3个常见问题（不可重复读、幻读、丢失更新）。
　　**可重复读**： Repeatable Read，就是在开始读取数据（事务开启）时，不再允许修改操作 。它解决了脏读和不可重复读，还存在2个常见问题（幻读、丢失更新）。
　　**序列化**： Serializable，序列化，或串行化。就是将每个事务按一定的顺序去执行，它将隔离问题全部解决，但是这种事务隔离级别效率低下，比较耗数据库性能，一般不使用。
　
大多数数据库默认的事务隔离级别是 Read Committed，比如 SQL Server , Oracle。但 MySQL 的默认隔离级别是 Repeatable Read。

### **不考虑事务隔离性会产生的读问题：**

#### 脏读

**无效数据的读出**，是指在数据库访问中， 事务 T1将某一值修改，然后事务T2读取该值，此后T1因为某种原因撤销对该值的修改，这就导致了T2所读取到的数据是无效的，值得注意的是，脏读一般是针对于update操作的。

#### 不可重复读

在一个事务内，多次读同一数据。在这个事务还没有结束时，另外一个事务也访问该同一数据。那么，在第一个事务中的两次读数据之间，由于第二个事务的修改，那么第一个事务两次读到的的数据可能是不一样的。

#### 幻读

事务不是独立执行时发生的一种现象，例如第一个事务对一个表中的数据进行了修改，这种修改涉及到表中的全部数据行。同时，第二个事务也修改这个表中的数据，这种修改是向表中插入一行新数据。那么，以后就会发生操作第一个事务的用户发现表中还有没有修改的数据行，就好象发生了幻觉一样。

### **不考虑事务隔离性会产生的写问题：**

#### 丢失更新

多个人同时修改一条数据，最后提交的数据把之前提交的数据覆盖

### 解决方式：

#### 悲观锁：

串形执行，绝对安全，但是效率相对非常慢

#### 乐观锁：

并行，设置版本号，提交数据前检查是否和数据库版本号一致，提交后将数据库版本号+1

### 实际使用

- 数据库添加version字段//手动换表

```mysql
ALTER TABLE 'student' ADD COLUMN 'version' INT
```

- 实体类添加version字段，并添加@version注解

```java
@Version
@TableField(fill = FieldFill.INSERT)
private Integer version;
```

- 元对象处理器接口添加version的insert默认值

```java
public void insertFill(MetaObject metaObject){
    ...
    this.setFieldValByName("version",1,metaObject);
}
```

- 在MybatisPlusConfig中注册Bean

可以把相关mybatisplus配置全部写到这里

```java
@EnableTransactionManagement
@Configuration
@MapperScan("//mapper路径")
public class MybatisPlusConfig{
	@Bean
	public OptimisticLockerInterceptor optimisticLockerInterceptor() {
    	return new OptimisticLockerInterceptor();
	}
}
```

- 测试使用乐观锁

# 分页查询

### mp简单查询

```java
    //查询单人id
	@Test
    public void testSelect(){
        Student student = studentMapper.selectById(5L);
        System.out.println(student);
    }
```

### 多id查询

```java
    //查询多人id
    @Test
    public void testSelectDemo(){
        List<Student> students = studentMapper.selectBatchIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(students);
    }
```

### 条件查询

```java
    //条件查询
    @Test
    public void testSelectMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("sname","张明");
        map.put("entrance",789);
        List<Student> students = studentMapper.selectByMap(map);
        System.out.println(students);
    }
```

### 分页查询

```java
//配置分页配置文件
@Bean
public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
}
//使用时直接new Page()获取相关数据
//分页查询
@Test
public void testPage(){
    //1. 创建Page对象
    //传入两个参数：当前页和每页记录数
    Page<Student> page = new Page<>(1,3);
    //调用分页查询的方法:（参数）Page对象和条件
    //所有分页数据会被封装到page对象中
    studentMapper.selectPage(page, null);

    //通过page对象获得分页数据
    System.out.println(page.getCurrent());  //当前页
    System.out.println(page.getRecords());  //每页数据list集合
    System.out.println(page.getPages());    //总页数
    System.out.println(page.getSize());     //每页显示记录数
    System.out.println(page.getTotal());    //总记录数
    System.out.println(page.hasNext());     //是否有下一页
    System.out.println(page.hasPrevious()); //是否有上一页

}
```



# 删除

### 物理删除

删除数据，直接从数据库中将其移除，不可挽回（清空回收站）

```java
    //删除操作
    @Test
    public void testDeleteById(){
        int i = studentMapper.deleteById(1L);
        System.out.println(i);
    }

    //批量删除
    @Test
    public void testDeleteMore(){
        int result = studentMapper.deleteBatchIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(result);
    }

	//条件删除类似条件查询
```

### 逻辑删除

设置标记位，当标记为不满足条件时无法查询到（将信息放入回收站）

- 数据库添加deleted字段，设为boolean类型
- 实体类添加deleted字段，加上注解**@TableLogin**注解和**@TableField(fill = FieldFill.INSERT)**注解

```java
    @TableLogic
    @TableField(fill=FieldFill.INSERT)
    private Integer deleted;
```

- 元对象处理器接口添加deleted的insert默认值

```java
public void insertFill(MetaObject metaObject){
    ...
    this.setFieldValByName("version",1,metaObject);
}
```

- application.properties加入配置（这是默认配置，如果一致则可以不用设置）

```properties
#逻辑删除默认配置
mybatis-plus.global-config.db-config.logic-not-delete-value=0
mybatis-plus.global-config.db-config.logic-delete-value=1
```

- 在MybatisPlusConfig中注册Bean

```java
    //逻辑删除插件
    @Bean
    public ISqlInjector sqlInjector(){
        return new LogicSqlInjector();
    }
```

- 测试使用逻辑删除

注意：如果想要查询已经逻辑删除的数据，需要像mybatis一样，自己写mapper和对于xml文件



# 性能分析

性能分析拦截器，用于输出每一条SQL语句以及其执行时间

SQL性能执行分析，开发环境使用，超过指定时间，停止运行，有助于发现问题

### 插件配置

##### （1）参数说明

maxTime : sql执行最大时长，超过自动停止运行，有助于发现问题

format : SQL是否格式化，默认false

##### （2）在MybatisPlusConfig中配置

```java
    //sql执行性能分析插件
    @Bean
    @Profile({"dev","test"})        //设置dev test环境开始
    public PerformanceInterceptor performanceInterceptor(){
        PerformanceInterceptor pi = new PerformanceInterceptor();
        pi.setMaxTime(500);     //500ms,执行时间超过此的不执行
        pi.setFormat(true);
        return pi;
    }
```

项目中的三种环境：	**dev 开发环境**					**test 测试环境**					**prod 生产环境**

##### （3）SpringBoot中设置dev环境

```properties
#环境设置 ： dev  test   prod
spring.profiles.active=dev
```





# 复杂查询

- Wrapper：条件构造类，最顶尖父类

  - AbstractWrapper：用于查询条件封装，生产sql的where条件

    - **QueryWrapper**：Entity对象的封装操作类，不是用lambda语法

    - UpdateWrapper：Update条件封装，用于Entity对象更新操作

    - AbstractLambdaWrapper：Lambda语法使用Wrapper统一处理解析lambda获取column

      - LambdaQueryWrapper：Lambda语法使用的查询wrapper

      - LambdaUpdateWrapper：Lambda更新封装Wrapper		

其中QueryWrapper最常使用。

```java
    //复杂查询
    @Test
    public void testMid(){
        //创建QueryWrapper对象
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        //通过querywrapper设置查询条件
        //ge 大于等于，gt 大于，le 小于等于，lt小于
//        wrapper.ge("entrance",100);
//        wrapper.lt("entrance",50);
        //eq等于，ne不等于
//        wrapper.eq("sname","蔡徐坤");
//        wrapper.ne("sname","蔡徐坤");
        //between位于之间，notBetween不位于之间
        wrapper.between("entrance",25,800);
//        wrapper.notBetween("entrance",25,800);
        //like模糊查询，notlike,likeLeft,likeRight
//        wrapper.like("sname","蔡");
//        wrapper.likeLeft("sname","明");
//        wrapper.likeRight("sname","许");
        //orderBy,orderByDesc升序排列,orderByAsc降序排列
        wrapper.orderByDesc("id");
//        wrapper.orderByAsc("id");
        //last拼接部分语句到sql最后
//        wrapper.last("limit 1"); //limit n : 查n条语句
        //查询指定列
        wrapper.select("id","sname");
        //输出结果
        List<Student> students = studentMapper.selectList(wrapper);
        System.out.println(students);
        //allEq
    }
```

















