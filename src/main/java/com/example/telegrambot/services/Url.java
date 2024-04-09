package com.example.telegrambot.services;

public class Url {
    public String hhUrl = "https://api.hh.ru/vacancies?";
    public String zrUrl = "https://api.zarplata.ru/vacancies?";

    public String getHhUrl(String name, String city, String schedule, String salary) {
        return hhUrl + "text=" + name + "&area=" + city + "&experience" + schedule + "&salary" + salary;
    }

    public String getZrUrl(String name, String city, String schedule, String salary) {
        return zrUrl + "text=" + name + "&area=" + city + "&experience" + schedule + "&salary" + salary;
    }
}