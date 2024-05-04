package com.example.telegrambot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ButtonClass extends TelegramLongPollingBot {
    public synchronized void setReplyButtons(SendMessage sendMessage, String[] buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String button : buttons) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(new KeyboardButton(button));
            keyboard.add(keyboardFirstRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public void setInlineButton(SendMessage message, String[] buttons) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> allLines = new ArrayList<>();

        for (String i : buttons) {
            var button = new InlineKeyboardButton();
            button.setText(i);
            button.setCallbackData(i);
            allLines.add(button);
        }

        while (!allLines.isEmpty()) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                if (!allLines.isEmpty()) {
                    rowInLine.add(allLines.get(0));
                    allLines.remove(0);
                }
            }

            rowsInLine.add(rowInLine);
        }

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return null;
    }
}