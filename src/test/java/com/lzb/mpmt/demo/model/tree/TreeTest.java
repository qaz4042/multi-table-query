package com.lzb.mpmt.demo.model.tree;

import com.lzb.mpmt.demo.model.BaseModel;
import com.lzb.mpmt.demo.model.User;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TreeTest {
    public static List<User> users = Arrays.asList(
            User.builder()
                    .id(1L)
                    .parentId(null)
                    .build(),
            User.builder()
                    .id(3L)
                    .parentId(1L)
                    .build(),
            User.builder()
                    .id(2L)
                    .parentId(3L)
                    .build(),
            User.builder()
                    .id(4L)
                    .parentId(1L)
                    .build()
    );

    @Test
    public void testTree() {
        TreeNode<User> userTreeNode = TreeNode.buildTreeLong(users, BaseModel::getId, User::getParentId);
        int i = 0;
    }
}
