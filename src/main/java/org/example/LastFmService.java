package org.example;

import de.umass.lastfm.Artist;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LastFmService {
    private static final Logger logger = LoggerFactory.getLogger(LastFmService.class);
    private static final String API_KEY = "7c138c06a6dd58608596e36aaabdb887";

    /**
     * Отримати кількість недавніх треків для вказаного користувача.
     *
     * @param username ім'я користувача Last.fm
     * @return мапа, що містить пари "track" та кількість відтворень
     */
    public Map<String, Integer> getRecentTracksCount(String username) {
        Map<String, Integer> tracksCount = new HashMap<>();

        try {
            // Викликаємо метод User.getRecentTracks для отримання списку треків користувача
            PaginatedResult<Track> result = User.getRecentTracks(username, API_KEY);

            for (Track track : result.getPageResults()) { // Використовуємо getPageResults() для ітерації
                String trackName = track.getName();
                int playcount = track.getPlaycount();
                tracksCount.put(trackName, playcount);
            }
        } catch (Exception e) {
            logger.error("Error fetching recent tracks for user " + username, e);
        }

        return tracksCount;
    }

    /**
     * Отримати всі треки та кількість недавніх треків для вказаного користувача.
     *
     * @param artists  набір імен артистів
     * @param username ім'я користувача Last.fm
     * @return мапа, що містить пари "artist - track" та кількість відтворень
     */
    public Map<String, Integer> getAllTracksAndRecentTracksCount(Set<String> artists, String username) {
        Map<String, Integer> tracksCount = new HashMap<>();

        // Додаємо усі треки для артистів
        for (String artist : artists) {
            Collection<Track> artistTracks = Artist.getTopTracks(artist, API_KEY);
            for (Track track : artistTracks) {
                String trackName = track.getName();
                int playcount = track.getPlaycount();
                String key = artist + " - " + trackName;
                tracksCount.put(key, playcount);
            }
        }

        // Додаємо всі недавні треки користувача
        try {
            PaginatedResult<Track> result = User.getRecentTracks(username, API_KEY);
            for (Track track : result.getPageResults()) {
                String trackName = track.getName();
                int playcount = track.getPlaycount();
                tracksCount.merge(trackName, playcount, Integer::sum);
            }
        } catch (Exception e) {
            logger.error("Error fetching recent tracks for user " + username, e);
        }

        return tracksCount;
    }
}
