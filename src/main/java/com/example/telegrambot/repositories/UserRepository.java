package com.example.telegrambot.repositories;

import com.example.telegrambot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query(value = "insert into user_Vacancies (user_id, vacancy_id) values (?, ?)", nativeQuery = true)
    @Transactional
    void addVacancy(@Param("user_id") Long user_id, @Param("vacancy_id") Long vacancy_id);

    @Query(value = "select vacancy_id from user_Vacancies where user_id = :user_id", nativeQuery = true)
    List<Long> getUserVacancies(@Param("user_id") Long user_id);
}