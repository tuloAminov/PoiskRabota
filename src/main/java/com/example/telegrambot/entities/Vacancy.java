package com.example.telegrambot.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vacancy")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Long.class)
@Getter
@Setter
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

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
