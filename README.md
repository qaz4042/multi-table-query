# multi-table-query 多表查询Dao框架
### 1.基础(70%)单库查询都可以简单做到 (不用强行手写xxxMapper.xml)
####
    public class UserStaff {
        private String staffName;
        private TestConst.SexEnum sex;
        private Long adminUserId;
        //关联表属性(属性名为relationCode)
        private User userAndUserStaff;
    }
####
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

    ...包含有 list getOne page aggregate(聚合,例如sum) paramMap(统一参数传递)... 等查询功能
#### list查询结果:
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
	  {
	    "id": 13,
	    "createTime": "2021-11-09",
	    "updateTime": "2021-11-09",
	    "staffName": "staff3",
	    "sex": 0,
	    "adminUserId": 2,
	    "user_userStaff": {
	      "id": 2,
	      "createTime": "2021-11-09",
	      "updateTime": "2021-11-09",
	      "username": "username2",
	      "sex": 1,
	      "parentId": null,
	      "user_userAddress": [
	        {
	          "id": 114,
	          "createTime": "2021-11-09",
	          "updateTime": "2021-11-09",
	          "userId": "2",
	          "province": "province4",
	          "street": "street4"
	        }
	      ]
	    }
	  }
	]
### 2.实体类可以直接用枚举类做属性
####
    数据库中,可以储存是枚举的name(),也可以储存IMultiEnum.getValue()(Integer)

### 3.运用详见demo项目
[multi-table-query-demo](https://github.com/qaz4042/multi-table-query-demo.git)

	multi-table-query-demo-jdk
        测试入口类 Main.java 中 main() 方法
	multi-table-query-demo-spring
        测试入口类 MultiTableQueryDemoApplicationTest.java 中 @Test 相关方法
### 4.调试环境
    jdk8 mysql5.6
	
    jdk8 spring-jdbc h2database(内存数据库模式)/mysql5.6

### license
    Apache Licence 2.0
