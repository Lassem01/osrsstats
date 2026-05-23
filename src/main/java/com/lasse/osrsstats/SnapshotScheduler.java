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

    // Kjører automatisk hver dag kl. 04:00.
    // cron-formatet er: sekund minutt time dag måned ukedag
    // "0 0 4 * * *" = ved sekund 0, minutt 0, time 4, hver dag.
    @Scheduled(cron = "0 0 1 * * *")
    public void takeDailySnapshot() {
        for (String account : ACCOUNTS) {
            service.getStatsAndSave(account);
        }
        System.out.println("Daglig snapshot lagret for " + ACCOUNTS.size() + " kontoer.");
    }
}