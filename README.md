# 快捷多表查询框架
### 1.基础(70%)单库查询都可以简单做到 (不用强行手写xxxMapper.xml)
####
    List<UserStaff> userStaffsSimple = MultiExecutor.list(
            new MultiWrapper<>(MultiWrapperMain.lambda(UserStaff.class), User.class, UserAddress.class)
    );
####
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
####
    "sex": 0,