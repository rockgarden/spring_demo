package com.example.service;

import com.example.bean.Student;

public interface StudentService {
	int add(Student student);
    int update(Student student);
    int deleteBySno(String sno);
    Student queryStudentBySno(String sno);
}
