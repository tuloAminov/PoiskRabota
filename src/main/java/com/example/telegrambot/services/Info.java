package com.example.telegrambot.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Info {
    public String hhUrl = "https://api.hh.ru/vacancies?";
    public String zrUrl = "https://api.zarplata.ru/vacancies?";

    public String getHhUrl(String name, String city, String experience, String salary) {
        String textTOUrl = name.replace(" ", "+");
        String cityNumber = getCityNumber(city);
        String experienceNumber = getExperienceNumber(experience);
        return hhUrl + "text=" + textTOUrl + "&area=" + cityNumber + "&experience=" + experienceNumber + "&salary=" + salary + "&only_with_salary=true";
    }

    public String getZrUrl(String name, String city, String experience, String salary) {
        String cityNumber = getCityNumber(city);
        String experienceNumber = getExperienceNumber(experience);
        String textTOUrl = name.replace(" ", "+");
        return zrUrl + "text=" + textTOUrl + "&area=" + cityNumber + "&experience=" + experienceNumber + "&salary=" + salary;
    }

    public String getSuperjobVacancies(String name, String city, String experience, String salary) {
        String textTOUrl = name.replace(" ", "+");
        String url = "https://api.superjob.ru/2.0/vacancies?count=10&" + "keyword=" + textTOUrl + "&town=" + city + "&experience=" + experience + "&payment_from=" + salary;
        String apiKey = "v3.r.138310961.59d1be99737a47f82c9b2a13c94be8d08cc749f9.ca16d343f3c8262577fd26559dd6ee55fa382208";

        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestProperty("X-Api-App-Id", apiKey);

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList<String> getCities() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Москва");
        cities.add("Санкт-Петербург");
        cities.add("Екатеринбург");
        cities.add("Новосибирск");
        cities.add("Казань");
        cities.add("Ростов");
        cities.add("Уфа");
        cities.add("Чита");

        return cities;
    }

    public String getCityNumber(String city) {
        Dictionary<String, Integer> cities = new Hashtable<>();
        cities.put("Москва", 1);
        cities.put("Санкт-Петербург", 2);
        cities.put("Екатеринбург", 3);

        return cities.get(city).toString();
    }

    public ArrayList<String> getExperiences() {
        ArrayList<String> experiences = new ArrayList<>();
        experiences.add("нет опыта");
        experiences.add("От 1 года до 3 лет");
        experiences.add("От 3 до 6 лет");
        experiences.add("Более 6 лет");

        return experiences;
    }

    public String getExperienceNumber(String experience) {
        Dictionary<String, String> experiences = new Hashtable<>();
        experiences.put("нет опыта", "noExperience");
        experiences.put("От 1 года до 3 лет", "between1And3");
        experiences.put("От 3 до 6 лет", "between3And6");
        experiences.put("Более 6 лет", "moreThan6");

        return experiences.get(experience);
    }
}