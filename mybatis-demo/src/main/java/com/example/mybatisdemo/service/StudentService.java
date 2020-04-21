package com.example.mybatisdemo.service;

import com.example.mybatisdemo.pojo.Student;

public interface StudentService {
    int add(Student student);

    int update(Student student);

    int deleteBySno(String sno);

    Student queryStudentBySno(String sno);
}