package com.lzb.mpmt.test;

import cn.hutool.json.JSONUtil;
import com.lzb.mpmt.test.model.UserStaff;
import org.junit.Test;

public class SimpleTest {
    @Test
    public void test1(){
        UserStaff userStaff = JSONUtil.toBean("{\"adminUserId\":1,\"sex\":\"man\",\"updateTime\":1636387200000,\"createTime\":1636387200000,\"staffName\":\"staff1\",\"user_userStaff\":{\"sex\":\"man\",\"updateTime\":1636387200000,\"user_userAddress\":[{\"updateTime\":1636387200000,\"userId\":\"1\",\"province\":\"province\",\"createTime\":1636387200000,\"street\":\"street\",\"id\":111}],\"createTime\":1636387200000,\"id\":1,\"username\":\"username1\"},\"id\":11}", UserStaff.class);
        System.out.println(JSONUtil.toJsonStr(userStaff));
    }
}
