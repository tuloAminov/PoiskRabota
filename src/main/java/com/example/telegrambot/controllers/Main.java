package com.example.telegrambot.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "https://api.superjob.ru/2.0/vacancies?count=10&town=4&keyword=Java+developer";
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

            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
