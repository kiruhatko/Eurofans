package org.example;

import java.util.*;

public class ChartService {
    private LastFmService lastFmService;
    private Map<String, Integer> weeklyChart;

    public ChartService() {
        this.lastFmService = new LastFmService();
        this.weeklyChart = new HashMap<>();
    }

    /**
     * Очищає тижневий чарт і розпочинає збір скролінгів заново.
     */
    public void clearWeeklyChart() {
        weeklyChart.clear();
    }

    /**
     * Додає скролінги для пісень, які слухають користувачі зі списку.
     *
     * @param artists    список артистів для відстеження
     * @param usersLastFm мапа користувачів з їхніми ніками Last.fm
     */
    public void updateChart(Set<String> artists, Map<Long, String> usersLastFm) {
        Map<String, Integer> artistScrollings = new HashMap<>();

        for (String username : usersLastFm.values()) {
            Map<String, Integer> userScrollings = lastFmService.getAllTracksAndRecentTracksCount(artists, username);
            userScrollings.forEach((track, count) -> artistScrollings.merge(track, count, Integer::sum));
        }

        // Обновляємо тижневий чарт
        artistScrollings.forEach((track, count) -> weeklyChart.merge(track, count, Integer::sum));
    }

    /**
     * Повертає топ-20 пісень за тижневим скролінгом.
     *
     * @return мапа з топ-20 пісень та їх скролінгами
     */
    public Map<String, Integer> getWeeklyChart() {
        // Сортуємо та обмежуємо топ-20
        return weeklyChart.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(20)
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);
    }
}
