# multi-table-query 多表查询Dao框架
### 功能&特点
#### ①.基础多表查询的简单&统一实现 (不需手写xxxMapper.xml) 
	使用习惯类似 MyBatis-Plus https://baomidou.com
#### 
```java
public class UserStaff {
    private String staffName;
    private TestConst.SexEnum sex;
    private Long adminUserId;
    //关联表属性(属性名为relationCode)
    private User userAndUserStaff;
}
```
```java
List<UserStaff> userStaffsSimple = MultiExecutor.list(
    new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
);

IMultiPage<UserStaff> page = MultiExecutor.page(new MultiPage<>(1, 10),
    new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class)
	    .desc(BaseModel::getCreateTime)
	    , User.class, UserAddress.class, Address.class));

MultiAggregateResult aggregateSumAll = MultiExecutor.aggregate(
    new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class).aggregateAll(MultiConstant.MultiAggregateTypeEnum.SUM),
    MultiWrapperSub.lambda(User.class)
));
//...包含有 list getOne page aggregate(聚合,例如sum) paramMap(统一参数传递)... 等查询功能
```
    
```json
//list查询结果:
[
  {
    "id": 11,
    "createTime": "2021-11-09",
    "updateTime": "2021-11-09",
    "staffName": "staff1",
    "sex": 0,
    "adminUserId": 1,
    "user_userStaff": {
      "id": 1,
      "createTime": "2021-11-09",
      "updateTime": "2021-11-09",
      "username": "username1",
      "sex": 1,
      "parentId": null,
      "user_userAddress": [
	{
	  "id": 111,
	  "createTime": "2021-11-09",
	  "updateTime": "2021-11-09",
	  "userId": "1",
	  "province": "province1",
	  "street": "street1"
	},
	{
	  "id": 112,
	  "createTime": "2021-11-09",
	  "updateTime": "2021-11-09",
	  "userId": "1",
	  "province": "province2",
	  "street": "street2"
	}
      ]
    }
  },
  {}
]
```

#### ②.实体类可以直接用枚举类做属性
数据库中储存IMultiEnum.getValue(),查询结果出来为具体的枚举属性 (spring-mvc下依赖jackson枚举自定义序列化)
 
###  2.快速入门
1.引入jar
```xml
<!-- https://mvnrepository.com/artifact/io.github.qaz4042/multi-table-query -->
<dependency>
    <groupId>io.github.qaz4042</groupId>
    <artifactId>multi-table-query</artifactId>
    <version>0.0.1</version>
</dependency>
```
2.配置表和表的关系,数据库查询引擎
```java
public class MybatisPlusMultiConfig {
    public static void build() {
        MultiConfig.build(
                new MultiProperties(),
		//表和表的关系
                new MultiTableRelationServiceImpl(),
//                new MultiDbJdbcAdaptor(
//                        "org.h2.Driver",
//                        "jdbc:h2:mem:multi_table_query_demo",
//                        "multi_table_query_demo",
//                        ""
//                )
		// 数据库查询引擎
                new MultiDbJdbcAdaptor( 
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/multi_table_query_demo?useUnicode=true&characterEncoding=utf-8",
                "root",
                "root"
                )
        );
    }
}
```
```java
//表和表的关系
public class MultiTableRelationServiceImpl implements MultiTableRelationService {

    @Override
    public List<MultiClassRelation> loadRelation() {
        //todo可以查询数据库/枚举信息
        return Arrays.asList(MultiClassRelation.builder()
                        .code("userAndUserStaff")
                        .className1("user")
                        .class1KeyProp("id")
                        .class1OneOrMany(MultiConstant.ClassRelationOneOrManyEnum.ONE)
                        .class1Require(true)
                        .className2("userStaff")
                        .class2KeyProp("adminUserId")
                        .class2OneOrMany(MultiConstant.ClassRelationOneOrManyEnum.MANY)
                        .class2Require(false)
                        .build()
        );
    }
}
```
3.使用
```java
List<UserStaff> userStaffsSimple = MultiExecutor.list(
    new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
);
```
### 3.DEMO项目
[multi-table-query-demo](https://github.com/qaz4042/multi-table-query-demo.git)

	multi-table-query-demo-jdk
        测试入口类 Main.java 中 main() 方法
	multi-table-query-demo-spring
        测试入口类 MultiTableQueryDemoApplicationTest.java 中 @Test 相关方法
	
### 4.调试环境
    jdk8 mysql5.6
	
    jdk8 spring-jdbc h2database(内存数据库模式)/mysql5.6
    
### 5.交流讨论
    QQ群    multi-table-query交流群    390137834
    群主QQ 404286846
![multi-table-query交流群群二维码](https://user-images.githubusercontent.com/29392228/146323468-05b3d0b8-d93b-49d1-aba2-b9ef3ba14b14.png)

### 6.license
    Apache Licence 2.0

### 7.赞助
	为更nice的代码加油
<img src="https://user-images.githubusercontent.com/29392228/146324082-c5af3414-3395-4c13-9ad8-e22ab924145b.jpg" width="205" height="319" alt="微信小程序"/><br/>
