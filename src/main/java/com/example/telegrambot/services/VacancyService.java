package com.example.telegrambot.services;

import com.example.telegrambot.entities.Vacancy;
import com.example.telegrambot.repositories.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    public void saveVacancy(Vacancy vacancy) {
        if (!vacancies().contains(vacancy)) {
            vacancyRepository.save(vacancy);
        }
    }

    public String existsVacancy(Vacancy vacancy) {
        if (vacancies().contains(vacancy)) {
            return "yest";
        }
        else {
            return "nest";
        }
    }

    public List<Vacancy> vacancies() {
        return vacancyRepository.findAll();
    }
}