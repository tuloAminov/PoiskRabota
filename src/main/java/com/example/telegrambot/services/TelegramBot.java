package com.example.telegrambot.services;

import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.entities.Params;
import com.example.telegrambot.entities.User;
import com.example.telegrambot.entities.UserParams;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
    private final UserParamsService userParamsService;

    @Autowired
    public TelegramBot(BotConfig config, UserService userService, VacancyService vacancyService, UserParamsService userParamsService) {
        this.config = config;
        this.userService = userService;
        this.vacancyService = vacancyService;
        this.userParamsService = userParamsService;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "start"));
        commandList.add(new BotCommand("/find", "find"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.info("Error setting bot's command list: " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        executeMessage(sendMessage);
    }

    private void sendReplyButton(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        ButtonClass buttonClass = new ButtonClass();
        buttonClass.setButtons(sendMessage);
        executeMessage(sendMessage);
    }

    private void sendInlineButton(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        ButtonClass buttonClass = new ButtonClass();
        buttonClass.findCity(sendMessage);
        executeMessage(sendMessage);
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Hi, " + firstName + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + firstName);
        User newUser = new User();
        newUser.setId(chatId);
        userService.addUser(newUser);
        UserParams userParams = new UserParams();
        userParams.setId(chatId);
        userParamsService.addUserParams(userParams);
        sendReplyButton(chatId, answer);
    }

    private Params param = Params.name;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getForwardSenderName();
            if (messageText.contains("/start")) {
                startCommandReceived(chatId, name);
            }

            else if(messageText.contains("/find")) {
                param = Params.name;
                sendMessage(chatId, "Напишите название вакансии");
            }

            else {
                switch (param) {
                    case name:
                        userParamsService.setName(chatId, messageText);
                        param = Params.city;
                        sendMessage(chatId, "В каком городе ты живешь?");
                        break;
                    case city:
                        userParamsService.setCity(chatId, messageText);
                        param = Params.schedule;
                        sendMessage(chatId, "Какой у вас опыт работы?");
                        break;
                    case schedule:
                        userParamsService.setSchedule(chatId, messageText);
                        param = Params.salary;
                        sendMessage(chatId, "Какую зарплату вы хотите получать?");
                        break;
                    case salary:
                        userParamsService.setSalary(chatId, messageText);
                        param = Params.start;
                        sendMessage(chatId, userParamsService.findById(chatId).toString());
                        break;
                }
            }
        }
    }

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void executeEditMessage(EditMessageText editMessage){
        try {
            execute(editMessage);
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