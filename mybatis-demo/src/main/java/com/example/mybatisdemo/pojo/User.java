package com.example.mybatisdemo.pojo;

import java.io.Serializable;

// 实现 Serializable 接口是为之后使用 Mapper 动态代理做准备
public class User implements Serializable {

    private static final long serialVersionUID = 7542033210412936278L;
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

}