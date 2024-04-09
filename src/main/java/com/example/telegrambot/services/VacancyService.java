package com.example.telegrambot.services;

import com.example.telegrambot.entities.Vacancy;
import com.example.telegrambot.repositories.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    public void saveVacancy(Vacancy vacancy) {
        if (!existsVacancy(vacancy)) {
            vacancyRepository.save(vacancy);
        }
    }

    public boolean existsVacancy(Vacancy vacancy) {
        boolean res = false;
        for (Vacancy vacancy1 : vacancies()) {
            if (vacancy1.getUrl().equals(vacancy.getUrl())) {
                res = true;
                break;
            }
        }

        return res;
    }

    public List<Vacancy> vacancies() {
        return vacancyRepository.findAll();
    }

    public long getIdByUrl(String url) {
        return vacancyRepository.getVacanciesByUrl(url).getId();
    }
}