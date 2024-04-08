package com.example.telegrambot.repositories;

import com.example.telegrambot.entities.UserParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserParamsRepo extends JpaRepository<UserParams, Long> {
    public UserParams getUserParamsById(Long id);
}
