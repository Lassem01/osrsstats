package com.lasse.osrsstats;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class HiscoreService {

    // Det offisielle OSRS-endepunktet. %s byttes ut med spillernavnet.
    private static final String URL =
            "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    // Rekkefølgen på ferdighetene i CSV-en fra Jagex er fast.
    // Linje 0 er totalnivå (Overall), deretter ferdighetene i denne rekkefølgen.
    private static final List<String> SKILL_NAMES = List.of(
            "Overall", "Attack", "Defence", "Strength", "Hitpoints", "Ranged",
            "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing",
            "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility",
            "Thieving", "Slayer", "Farming", "Runecraft", "Hunter", "Construction, Sailing"
    );

    // RestClient er Spring sitt verktøy for å gjøre HTTP-kall til andre tjenester.
    private final RestClient restClient = RestClient.create();

    // Repository for å lagre snapshots, og service for å oppdage level-ups.
    private final SnapshotRepository snapshotRepository;
    private final LevelUpService levelUpService;

    public HiscoreService(SnapshotRepository snapshotRepository,
                          LevelUpService levelUpService) {
        this.snapshotRepository = snapshotRepository;
        this.levelUpService = levelUpService;
    }

    // Henter stats fra Jagex. Resultatet caches per spillernavn, så gjentatte
    // forespørsler innen cachens levetid (10 min) ikke treffer Jagex på nytt.
    @Cacheable("playerStats")
    public PlayerStats getStats(String username) {
        try {
            String body = restClient.get()
                    .uri(String.format(URL, username))
                    .retrieve()
                    .body(String.class);

            return parse(username, body);
        } catch (Exception e) {
            // Hvis spilleren ikke finnes (eller noe annet feiler),
            // returnerer vi et "tomt" resultat med found=false.
            return new PlayerStats(username, false, List.of());
        }
    }

    // oppdaterer tall, lagrer et snapshot, og oppdager eventuelle level-ups.
    // @Cacheable her gjør at gjentatte kall innen 10 min ikke lager nye snapshots
    // eller treffer Jagex – det fungerer som en spam-sperre for "Oppdater"-knappen.
    @Cacheable("statsAndSave")
    public PlayerStats getStatsAndSave(String username) {
        PlayerStats stats = getStats(username);

        if (stats.found()) {
            // "Overall" (første ferdighet) inneholder total-XP
            Skill overall = stats.skills().stream()
                    .filter(s -> s.name().equals("Overall"))
                    .findFirst()
                    .orElse(null);

            if (overall != null) {
                // Bygger en tekststreng med alle ferdighetsnivåer (unntatt Overall),
                // f.eks. "Attack:80,Defence:75,Strength:85,..."
                String skillLevels = stats.skills().stream()
                        .filter(s -> !s.name().equals("Overall"))
                        .map(s -> s.name() + ":" + s.level())
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

                // Lagre snapshotet med nåtidspunktet
                snapshotRepository.save(
                        new Snapshot(username, overall.xp(), Instant.now(), skillLevels)
                );

                // Oppdag og lagre eventuelle level-ups (sammenligner mot forrige snapshot)
                levelUpService.detectLevelUps(username, skillLevels);
            }
        }
        return stats;
    }

    // Gjør CSV-teksten fra Jagex om til en liste av Skill-objekter.
    private PlayerStats parse(String username, String body) {
        String[] lines = body.split("\n");
        List<Skill> skills = new ArrayList<>();


        for (int i = 0; i < SKILL_NAMES.size() && i < lines.length; i++) {
            // Hver linje ser ut som "rank,level,xp"
            String[] parts = lines[i].trim().split(",");
            if (parts.length >= 3) {
                long rank = Long.parseLong(parts[0]);
                int level = Integer.parseInt(parts[1]);
                long xp = Long.parseLong(parts[2]);
                skills.add(new Skill(SKILL_NAMES.get(i), level, xp, rank));
            }
        }
        return new PlayerStats(username, true, skills);
    }
}