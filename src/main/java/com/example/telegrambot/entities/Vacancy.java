package com.example.telegrambot.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vacancy")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Long.class)
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String area;
    private String company;
    private String salary;
    private String schedule;
    private String experience;
    private String url;

    @ManyToMany(mappedBy = "userVacancies", fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<User> userVacancies = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteVacancies", fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<User> userFavoriteVacancies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<User> getUserVacancies() {
        return userVacancies;
    }

    public void setUserVacancies(List<User> userVacancies) {
        this.userVacancies = userVacancies;
    }

    public List<User> getUserFavoriteVacancies() {
        return userFavoriteVacancies;
    }

    public void setUserFavoriteVacancies(List<User> userFavoriteVacancies) {
        this.userFavoriteVacancies = userFavoriteVacancies;
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
