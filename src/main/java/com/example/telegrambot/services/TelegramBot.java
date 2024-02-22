package com.example.telegrambot.services;

import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.entities.User;
import com.example.telegrambot.entities.Vacancy;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserService userService;
    private final VacancyService vacancyService;

    @Autowired
    public TelegramBot(BotConfig config, UserService userService, VacancyService vacancyService) {
        this.config = config;
        this.userService = userService;
        this.vacancyService = vacancyService;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "start"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.info("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.contains("/start")) {
                getVacancies("https://api.hh.ru/vacancies?text=java&experience=noExperience&area=2");
                getVacancies("https://api.zarplata.ru/vacancies?text=java");
                sendMessage(chatId, vacancyService.vacancies().get(0).toString());
                sendMessage(chatId, vacancyService.vacancies().get(1).toString());
                sendMessage(chatId, vacancyService.vacancies().get(2).toString());

                Vacancy vacancy2 = vacancyService.vacancies().get(0);
                ArrayList<Vacancy> vacancies2 = new ArrayList<>();
                vacancies2.add(vacancy2);
                sendMessage(chatId, String.valueOf(vacancies2.contains(vacancy2)));
                sendMessage(chatId, vacancyService.existsVacancy(vacancyService.vacancies().get(0)));
            }
            else {
                sendMessage(chatId, "errorrrrr");
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Hi, " + firstName + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + firstName);
        User newUser = new User();
        newUser.setId(chatId);
        userService.addUser(newUser);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        }

        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public String putInString(List list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < list.size() + 1; i++)
            stringBuilder.append(i).append(". ").append(list.get(i-1).toString()).append("\n");

        return stringBuilder.toString();
    }

    public void getVacancies(String url) throws IOException {
        JSONArray jsonArray =  new JSONObject(getStringBuilder(url)
                .toString()).getJSONArray("items");

        for (Object jsonObject : jsonArray) {
            JSONObject moJson = (JSONObject) jsonObject;
            Vacancy vacancy = new Vacancy();
            vacancy.setName(moJson.get("name").toString());
            vacancy.setArea(new JSONObject(moJson.get("area").toString()).get("name").toString());
            vacancy.setCompany(new JSONObject(moJson.get("employer").toString()).get("name").toString());
            vacancy.setSalary(moJson.get("salary").toString());
            vacancy.setSchedule(moJson.get("schedule").toString());
            vacancy.setExperience(moJson.get("experience").toString());
            vacancy.setUrl(moJson.get("url").toString());

            vacancyService.saveVacancy(vacancy);
        }

    }

    private static StringBuilder getStringBuilder(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        int j;
        while ((j = bReader.read()) != -1) {
            stringBuilder.append((char) j);
        }

        return stringBuilder;
    }
}