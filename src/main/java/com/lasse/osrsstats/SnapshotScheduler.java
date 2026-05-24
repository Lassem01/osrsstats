package com.lasse.osrsstats;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SnapshotScheduler {

    // Samme kontoer som i controlleren
    private static final List<String> ACCOUNTS = List.of(
            "GIM Jostein", "LaksenGI", "MagickinderG"
    );

    private final HiscoreService service;

    public SnapshotScheduler(HiscoreService service) {
        this.service = service;
    }

    // Kjører hver 6. time: kl. 01:00, 07:00, 13:00 og 19:00.
    @Scheduled(cron = "0 0 1,7,13,19 * * *")
    public void takeDailySnapshot() {
        for (String account : ACCOUNTS) {
            service.getStatsAndSave(account);
        }
        System.out.println("Daglig snapshot lagret for " + ACCOUNTS.size() + " kontoer.");
    }
    @org.springframework.cache.annotation.CacheEvict(value = "playerStats", allEntries = true)
    @Scheduled(fixedRate = 600000)   // 600000 ms = 10 minutter
    public void clearStatsCache() {
        // Tom metode – annotasjonene gjør jobben.
        // Tømmer cachen så ferske tall hentes ved neste forespørsel.
    }
}