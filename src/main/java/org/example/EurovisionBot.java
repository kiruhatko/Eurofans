package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EurovisionBot extends TelegramLongPollingBot {
    private ChartService chartService;
    private LastFmService lastFmService;
    private Map<Long, String> usersLastFm; // Зберігаємо чат ID та нік Last.fm
    private final long adminUserId = 6758329407L; // Ваш user id

    public EurovisionBot() {
        this.chartService = new ChartService();
        this.lastFmService = new LastFmService();
        this.usersLastFm = new HashMap<>();
    }

    @Override
    public String getBotUsername() {
        return "eurofansbot";
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN_HERE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/join")) {
                handleJoinCommand(messageText, chatId);
            } else if (messageText.equals("/chart")) {
                handleChartCommand(chatId);
            } else if (messageText.equals("/clear") && isAdmin(chatId)) {
                handleClearCommand(chatId);
            } else {
                sendTextMessage(chatId, "Unknown command. Use /join, /chart or /clear (admin only).");
            }
        }
    }

    private void handleJoinCommand(String messageText, long chatId) {
        String[] tokens = messageText.split(" ");
        if (tokens.length == 2) {
            String username = tokens[1].trim();
            usersLastFm.put(chatId, username);
            sendTextMessage(chatId, "You have joined with Last.fm username: " + username);
        } else {
            sendTextMessage(chatId, "Invalid command. Use /join [Last.fm username]");
        }
    }

    private void handleChartCommand(long chatId) {
        if (isAdmin(chatId)) {
            Map<String, Integer> chart = chartService.getWeeklyChart();
            sendChart(chatId, chart);
        } else {
            sendTextMessage(chatId, "Access denied.");
        }
    }

    private void handleClearCommand(long chatId) {
        chartService.clearWeeklyChart();
        sendTextMessage(chatId, "Weekly chart cleared.");
    }

    private boolean isAdmin(long chatId) {
        return chatId == adminUserId;
    }

    private void sendChart(long chatId, Map<String, Integer> chart) {
        StringBuilder messageText = new StringBuilder("Top 20 Eurovision songs:\n");
        chart.forEach((track, count) -> messageText.append(track).append(": ").append(count).append("\n"));

        sendTextMessage(chatId, messageText.toString());
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
