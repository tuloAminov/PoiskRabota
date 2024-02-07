package com.example.telegrambot.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Long.class)
public class User {
    @Id
    private Long id;

    @ManyToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_Vacancies",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_id", referencedColumnName = "id"))
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vacancy> userVacancies = new ArrayList<>();

    @ManyToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_favoriteVacancies",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_id", referencedColumnName = "id"))
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vacancy> favoriteVacancies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Vacancy> getUserVacancies() {
        return userVacancies;
    }

    public void setUserVacancies(List<Vacancy> userVacancies) {
        this.userVacancies = userVacancies;
    }

    public List<Vacancy> getFavoriteVacancies() {
        return favoriteVacancies;
    }

    public void setFavoriteVacancies(List<Vacancy> favoriteVacancies) {
        this.favoriteVacancies = favoriteVacancies;
    }
}