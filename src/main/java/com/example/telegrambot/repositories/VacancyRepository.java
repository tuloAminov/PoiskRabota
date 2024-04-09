package com.example.telegrambot.repositories;

import com.example.telegrambot.entities.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Vacancy getVacanciesById(long id);

    Vacancy getVacanciesByUrl(String url);
}