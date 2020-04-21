package com.example.mybatisdemo.dao;

import com.example.mybatisdemo.pojo.User;

public interface UserDao {

    // 根据 id 查询用户信息
    public User findUserById(int id) throws Exception;
}