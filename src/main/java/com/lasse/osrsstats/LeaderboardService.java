package com.lasse.osrsstats;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardService {

    private final SnapshotRepository repository;

    public LeaderboardService(SnapshotRepository repository) {
        this.repository = repository;
    }

    public record GainEntry(String username, long xpGained) {}

    // Norsk tidssone, så "mandag" og "1. i måneden"
    private static final ZoneId ZONE = ZoneId.of("Europe/Oslo");

    // Startpunktet for "denne uka": siste mandag kl. 00:00
    private Instant startOfWeek() {
        LocalDate monday = LocalDate.now(ZONE)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.atStartOfDay(ZONE).toInstant();
    }

    // Startpunktet for "denne måneden": den 1. kl. 00:00
    private Instant startOfMonth() {
        LocalDate first = LocalDate.now(ZONE).withDayOfMonth(1);
        return first.atStartOfDay(ZONE).toInstant();
    }

    // period = "week" eller "month"
    public List<GainEntry> getLeaderboard(List<String> usernames, String period) {
        Instant since = period.equals("month") ? startOfMonth() : startOfWeek();
        List<GainEntry> result = new ArrayList<>();

        for (String username : usernames) {
            // Nyeste snapshot = "nå"-verdien
            Optional<Snapshot> latest =
                    repository.findFirstByUsernameOrderByTakenAtDesc(username);

            // Første snapshot etter periodestart = "før"-verdien
            Optional<Snapshot> earliest =
                    repository.findFirstByUsernameAndTakenAtAfterOrderByTakenAtAsc(username, since);

            if (latest.isPresent() && earliest.isPresent()) {
                long gained = latest.get().getTotalXp() - earliest.get().getTotalXp();
                result.add(new GainEntry(username, gained));
            } else {
                result.add(new GainEntry(username, 0));
            }
        }

        result.sort(Comparator.comparingLong(GainEntry::xpGained).reversed());
        return result;
    }
}