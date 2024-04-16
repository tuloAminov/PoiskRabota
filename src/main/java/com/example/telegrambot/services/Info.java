package com.example.telegrambot.services;

import java.util.Dictionary;
import java.util.Hashtable;

public class Info {
    public String hhUrl = "https://api.hh.ru/vacancies?";
    public String zrUrl = "https://api.zarplata.ru/vacancies?";

    public String getHhUrl(String name, String city, String experience, String salary) {
        return hhUrl + "text=" + name + "&area=" + city + "&experience=" + experience + "&salary=" + salary + "&only_with_salary=true";
    }

    public String getZrUrl(String name, String city, String experience, String salary) {
        return zrUrl + "text=" + name + "&area=" + city + "&experience=" + experience + "&salary=" + salary;
    }

    public String getCityNumber(String city) {
        Dictionary<String, Integer> cities = new Hashtable<>();
        cities.put("Москва", 1);
        cities.put("Санкт-Петербург", 2);
        cities.put("Екатеринбург", 3);

        return cities.get(city).toString();
    }

    public String getExperienceNumber(String experience) {
        Dictionary<String, String> experiences = new Hashtable<>();
        experiences.put("0", "noExperience");
        experiences.put("1", "between1And3");
        experiences.put("2", "between1And3");
        experiences.put("3", "between1And3");
        experiences.put("4", "between3And6");
        experiences.put("5", "between3And6");
        experiences.put("6", "between3And6");
        experiences.put("7", "moreThan6");
        experiences.put("8", "moreThan6");
        experiences.put("9", "moreThan6");
        experiences.put("10", "moreThan6");

        return experiences.get(experience);
    }
}