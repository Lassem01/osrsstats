package com.lasse.osrsstats;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    // Et enkelt resultat per spiller: navn + hvor mye XP de har fått i perioden
    public record GainEntry(String username, long xpGained) {}

    // Regner ut XP-gevinst for hver spiller de siste X dagene.
    // 7 dager = uke, ~30 dager = måned.
    public List<GainEntry> getLeaderboard(List<String> usernames, int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        List<GainEntry> result = new ArrayList<>();

        for (String username : usernames) {
            // "Nå"-verdien: det aller nyeste snapshotet
            Optional<Snapshot> latest =
                    repository.findFirstByUsernameOrderByTakenAtDesc(username);

            // "Før"-verdien: det første snapshotet innenfor perioden
            Optional<Snapshot> earliest =
                    repository.findFirstByUsernameAndTakenAtAfterOrderByTakenAtAsc(username, since);

            if (latest.isPresent() && earliest.isPresent()) {
                long gained = latest.get().getTotalXp() - earliest.get().getTotalXp();
                result.add(new GainEntry(username, gained));
            } else {
                // Ikke nok data ennå – vis 0 så spilleren fortsatt dukker opp
                result.add(new GainEntry(username, 0));
            }
        }

        // Sorter slik at den med mest XP-gevinst kommer øverst
        result.sort(Comparator.comparingLong(GainEntry::xpGained).reversed());
        return result;
    }
}