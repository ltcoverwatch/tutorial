package com.example.zoom01.service;

import com.example.zoom01.dao.UserDao;
import com.example.zoom01.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public List<User> list(){
        return userDao.findAll();
    }
}
