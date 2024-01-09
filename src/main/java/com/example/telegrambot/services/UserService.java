package com.example.telegrambot.services;

import com.example.telegrambot.entities.User;
import com.example.telegrambot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        List<Long> ids = new ArrayList<>();
        for (User u: userRepository.findAll()) {
            ids.add(u.getId());
        }

        if (!ids.contains(user.getId()))
            userRepository.save(user);
    }
}
