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
        commandList.add(new BotCommand("/start", "Начать"));
        commandList.add(new BotCommand("/params", "Параметры"));
        commandList.add(new BotCommand("/vacancies", "Вакансии"));
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

    private void setInlineButton(long chatId, String textToSend, String[] buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        ButtonClass buttonClass = new ButtonClass();
        buttonClass.setInlineButton(sendMessage, buttons);
        executeMessage(sendMessage);
    }

    private void setReplyButtons(long chatId, String text, String[] buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        ButtonClass buttonClass = new ButtonClass();
        buttonClass.setReplyButtons(sendMessage, buttons);
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
        String answer = EmojiParser.parseToUnicode("Добро пожаловать, " + firstName + "!" + " :blush:");
        log.info("Replied to user " + firstName);
        User newUser = new User();
        newUser.setId(chatId);
        if (userService.idContains(newUser)) {
            userService.addUser(newUser);
            UserParams userParams = new UserParams();
            userParams.setId(chatId);
            userParamsService.addUserParams(userParams);
        }

        setReplyButtons(chatId, answer, new String[]{"Указать параметры", "Помощь"});
    }

    private Params param = Params.name;
    private int counter = 0;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            Info info = new Info();
            if (messageText.contains("/start")) {
                startCommandReceived(chatId, name);
            }

            else if (messageText.contains("/vacancies") || messageText.contains("Еще") || messageText.contains("Показать вакансии")) {
                getVacancies(counter, chatId, info);
                counter += 3;
                setReplyButtons(chatId, "Нажимайте на 'Еще', если хотите больше вакансий", new String[]{"Еще", "Указать параметры", "Помощь"});
            }

            else if (messageText.contains("Помощь")) {
                String text = "Укажите параметры по которым необходимо искать вакансии и бот ищет вакансий на сайтах hh, zr и sj";
                setReplyButtons(chatId, text, new String[]{"Указать параметры", "Помощь"});
            }

            else if(messageText.contains("/params") || messageText.contains("Указать параметры")) {
                param = Params.name;
                sendMessage(chatId, "Напишите название вакансии");
            }

            else {
                switch (param) {
                    case name:
                        userParamsService.setName(chatId, messageText);
                        param = Params.city;
                        ArrayList<String> cities = info.getCities();
                        String[] cityButtons = cities.toArray(new String[0]);
                        setInlineButton(chatId, "Укажите город", cityButtons);
                        break;
                    case city:
                        userParamsService.setCity(chatId, messageText);
                        param = Params.experience;
                        ArrayList<String> experiences = info.getExperiences();
                        String[] experienceButtons = experiences.toArray(new String[0]);
                        setInlineButton(chatId, "Какой у вас опыт работы?", experienceButtons);
                        break;
                    case experience:
                        userParamsService.setExperience(chatId, messageText);
                        param = Params.salary;
                        sendMessage(chatId, "Какую зарплату вы хотите получать?");
                        break;
                    case salary:
                        userParamsService.setSalary(chatId, messageText);
                        param = Params.start;
                        setReplyButtons(chatId, userParamsService.findById(chatId).toString(), new String[]{"Показать вакансии", "Указать параметры", "Помощь"});
                        break;
                }
            }

        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            Info info = new Info();
            if (info.getCities().contains(call_data)) {
                userParamsService.setCity(chatId, update.getCallbackQuery().getData());
                executeEditMessage("Вы выбрали: " + call_data, chatId, messageId);
                param = Params.experience;
                ArrayList<String> experiences = info.getExperiences();
                String[] experienceButtons = experiences.toArray(new String[0]);
                setInlineButton(chatId, "Какой у вас опыт работы?", experienceButtons);
            }

            else if (info.getExperiences().contains(call_data)) {
                userParamsService.setExperience(chatId, update.getCallbackQuery().getData());
                executeEditMessage("Вы выбрали: " + call_data, chatId, messageId);
                param = Params.salary;
                sendMessage(chatId, "Какую зарплату вы хотите получать?");
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

    private void executeEditMessage(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
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

    public void getVacancies(int i, long chatId, Info info) throws IOException {
        UserParams userParams = userParamsService.findById(chatId);
        String vacancyName = userParams.getVacancyName();
        String city = userParams.getCity();
        String schedule = userParams.getExperience();
        String salary = userParams.getSalary();
        saveVacancies(chatId, info.getHhUrl(vacancyName, city, schedule, salary));
        saveVacancies(chatId, info.getZrUrl(vacancyName, city, schedule, salary));
/*
        saveSuperjobVacancies(chatId, vacancyName, city, schedule, salary);
*/

        sendMessage(chatId, userService.getUserVacancies(chatId).get(i).toString());
        sendMessage(chatId, userService.getUserVacancies(chatId).get(i+1).toString());
        sendMessage(chatId, userService.getUserVacancies(chatId).get(i+2).toString());
    }

    public void saveVacancies(long chatId, String url) throws IOException {
        JSONArray jsonArray =  new JSONObject(getStringBuilder(url)
                .toString()).getJSONArray("items");

        for (Object jsonObject : jsonArray) {
            JSONObject moJson = (JSONObject) jsonObject;
            Vacancy vacancy = new Vacancy();
            vacancy.setName(moJson.get("name").toString());
            vacancy.setArea(new JSONObject(moJson.get("area").toString()).get("name").toString());
            vacancy.setCompany(new JSONObject(moJson.get("employer").toString()).get("name").toString());
            if (!moJson.get("salary").toString().contains("{")) {
                vacancy.setSalary("Зарплата не указана");

            }
            else {
                String salaryFrom = new JSONObject(moJson.get("salary").toString()).get("from").toString();
                if (salaryFrom == null)
                    salaryFrom = "";
                String salaryTo = new JSONObject(moJson.get("salary").toString()).get("to").toString();
                if (salaryTo == null)
                    salaryTo = "";
                vacancy.setSalary(salaryFrom + "-" + salaryTo + " Р");
            }
            vacancy.setSchedule(moJson.get("schedule").toString());
            vacancy.setExperience(moJson.get("experience").toString());
            vacancy.setUrl(moJson.get("alternate_url").toString());

            vacancyService.saveVacancy(vacancy);
            userService.addVacancy(chatId, vacancyService.getIdByUrl(vacancy.getUrl()));
        }
    }

    public void saveSuperjobVacancies(long chatId, String vacancyName, String city, String schedule, String salary) throws IOException {
        Info info = new Info();
        JSONArray jsonArray =  new JSONObject(info.getSuperjobVacancies(vacancyName, city, schedule, salary))
                .getJSONArray("objects");

        for (Object jsonObject : jsonArray) {
            JSONObject moJson = (JSONObject) jsonObject;
            Vacancy vacancy = new Vacancy();
            vacancy.setName(moJson.get("profession").toString());
            vacancy.setArea(new JSONObject(moJson.get("town").toString()).get("title").toString());
            vacancy.setCompany(new JSONObject(moJson.get("client").toString()).get("title").toString());
            String salaryFrom = (moJson.get("payment_from").toString());
            String salaryTo = (moJson.get("payment_to").toString());
            vacancy.setSalary(salaryFrom + "-" + salaryTo + " Р");
            vacancy.setExperience(moJson.get("experience").toString());
            vacancy.setUrl(moJson.get("link").toString());

            vacancyService.saveVacancy(vacancy);
            userService.addVacancy(chatId, vacancyService.getIdByUrl(vacancy.getUrl()));
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