package com.example.telegrambot.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "userParams")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Long.class)
public class UserParams {
    @Id
    private Long id;
    private String vacancyName;
    private String city;
    private String experience;
    private String salary;

    @Override
    public String toString() {
        return "Вы выбрали: \n" +
                "название вакансии: " + vacancyName + "\n" +
                "город: " + city + "\n" +
                "опыт работы: " + experience + "\n" +
                "желаемая зп: " + salary;
    }
}