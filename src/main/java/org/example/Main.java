package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting bot...");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            System.out.println("Registering bot...");
            botsApi.registerBot(new EurovisionBot());
            System.out.println("Bot registered successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error registering bot:");
            e.printStackTrace();

        }
    }
}
