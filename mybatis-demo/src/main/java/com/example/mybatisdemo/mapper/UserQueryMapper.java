package com.example.mybatisdemo.mapper;

import com.example.mybatisdemo.pojo.User;

import org.apache.ibatis.annotations.Select;

/**
 * 代理类
 * 
 * 在默认情况下，该 bean 的名字为 userQueryMapper（即首字母小写）
 */
public interface UserQueryMapper {

    @Select("SELECT * FROM USER WHERE id = #{id}")
    public User findUserById(int id) throws Exception;

}