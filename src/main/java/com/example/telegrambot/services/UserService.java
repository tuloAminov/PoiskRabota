package com.example.telegrambot.services;

import com.example.telegrambot.entities.User;
import com.example.telegrambot.entities.Vacancy;
import com.example.telegrambot.repositories.UserRepository;
import com.example.telegrambot.repositories.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VacancyRepository vacancyRepository;

    @Autowired
    public UserService(UserRepository userRepository, VacancyRepository vacancyRepository) {
        this.userRepository = userRepository;
        this.vacancyRepository = vacancyRepository;
    }

    public void addUser(User user) {
        if (idContains(user))
            userRepository.save(user);
    }

    public boolean idContains(User user) {
        List<Long> ids = new ArrayList<>();
        for (User u: userRepository.findAll()) {
            ids.add(u.getId());
        }

        return !ids.contains(user.getId());
    }

    public void addVacancy(long userId, long vacancyId) {
        if (!existsVacancy(userId, vacancyId))
            userRepository.addVacancy(userId, vacancyId);
    }

    public List<Vacancy> getUserVacancies(long userId) {
        List<Long> vacanciesIds = userRepository.getUserVacancies(userId);
        List<Vacancy> userVacancies = new ArrayList<>();
        for (long id : vacanciesIds) {
            userVacancies.add(vacancyRepository.getVacanciesById(id));
        }

        return userVacancies;
    }

    public boolean existsVacancy(long userId, long vacancyId) {
        boolean res = false;
        for (Vacancy vacancy1 : getUserVacancies(userId)) {
            if (vacancy1.getId() == vacancyId) {
                res = true;
                break;
            }
        }

        return res;
    }
}
