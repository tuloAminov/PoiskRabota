package com.example.telegrambot.services;

import com.example.telegrambot.entities.UserParams;
import com.example.telegrambot.repositories.UserParamsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserParamsService {
    private final UserParamsRepo userParamsRepo;

    @Autowired
    public UserParamsService(UserParamsRepo userParamsRepo) {
        this.userParamsRepo = userParamsRepo;
    }

    public void addUserParams(UserParams userParams) {
        userParamsRepo.save(userParams);
    }

    public UserParams findById(long id) {
        return userParamsRepo.getUserParamsById(id);
    }

    public void setName(Long id, String name) {
        UserParams userParams = findById(id);
        userParamsRepo.deleteById(id);
        UserParams userParams2 = new UserParams();
        userParams2.setId(id);
        userParams2.setVacancyName(name);
        userParams2.setCity(userParams.getCity());
        userParams2.setSalary(userParams.getSalary());
        userParams2.setSchedule(userParams.getSchedule());

        addUserParams(userParams2);
    }

    public void setCity(Long id, String city) {
        UserParams userParams = findById(id);
        UserParams userParams2 = new UserParams();
        userParams2.setId(id);
        userParams2.setCity(city);
        userParams2.setVacancyName(userParams.getVacancyName());
        userParams2.setSalary(userParams.getSalary());
        userParams2.setSchedule(userParams.getSchedule());

        userParamsRepo.deleteById(id);
        addUserParams(userParams2);
    }

    public void setSchedule(Long id, String schedule) {
        UserParams userParams = findById(id);
        UserParams userParams2 = new UserParams();
        userParams2.setId(id);
        userParams2.setSchedule(schedule);
        userParams2.setCity(userParams.getCity());
        userParams2.setSalary(userParams.getSalary());
        userParams2.setVacancyName(userParams.getVacancyName());

        userParamsRepo.deleteById(id);
        addUserParams(userParams2);
    }

    public void setSalary(Long id, String salary) {
        UserParams userParams = findById(id);
        UserParams userParams2 = new UserParams();
        userParams2.setId(id);
        userParams2.setSalary(salary);
        userParams2.setCity(userParams.getCity());
        userParams2.setVacancyName(userParams.getVacancyName());
        userParams2.setSchedule(userParams.getSchedule());

        userParamsRepo.deleteById(id);
        addUserParams(userParams2);
    }
}
