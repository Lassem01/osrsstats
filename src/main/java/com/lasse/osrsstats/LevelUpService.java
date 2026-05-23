package com.lasse.osrsstats;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LevelUpService {

    private final SnapshotRepository snapshotRepository;
    private final LevelUpRepository levelUpRepository;

    public LevelUpService(SnapshotRepository snapshotRepository,
                          LevelUpRepository levelUpRepository) {
        this.snapshotRepository = snapshotRepository;
        this.levelUpRepository = levelUpRepository;
    }

    // Sammenligner de nye ferdighetsnivåene mot forrige snapshot for spilleren.
    // For hver ferdighet som har gått opp, lagres en LevelUpEvent.
    // Kalles ETTER at det nye snapshotet er lagret.
    public void detectLevelUps(String username, String newSkillLevels) {
        // Hent de to siste snapshotene (det nyeste er det vi nettopp lagret)
        List<Snapshot> recent =
                snapshotRepository.findTop2ByUsernameOrderByTakenAtDesc(username);

        // Trenger minst to for å sammenligne (et "før" og et "nå")
        if (recent.size() < 2) return;

        Map<String, Integer> oldLevels = parse(recent.get(1).getSkillLevels());
        Map<String, Integer> newLevels = parse(newSkillLevels);

        for (Map.Entry<String, Integer> entry : newLevels.entrySet()) {
            String skill = entry.getKey();
            int newLevel = entry.getValue();
            int oldLevel = oldLevels.getOrDefault(skill, newLevel);

            // Har nivået gått opp? Lagre én hendelse per nytt nivå-tall.
            if (newLevel > oldLevel) {
                // Hvis noen gikk fra 80 til 82, logger vi 81 OG 82
                for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {
                    levelUpRepository.save(
                            new LevelUpEvent(username, skill, lvl, Instant.now())
                    );
                }
            }
        }
    }

    // Gjør "Attack:80,Defence:75" om til et kart {Attack=80, Defence=75}
    private Map<String, Integer> parse(String skillLevels) {
        Map<String, Integer> map = new HashMap<>();
        if (skillLevels == null || skillLevels.isBlank()) return map;

        for (String part : skillLevels.split(",")) {
            String[] kv = part.split(":");
            if (kv.length == 2) {
                map.put(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return map;
    }
}