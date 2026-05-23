package com.lasse.osrsstats;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class HiscoreService {

    // Det offisielle OSRS-endepunktet. %s byttes ut med spillernavnet.
    private static final String URL =
            "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    // Rekkefølgen på ferdighetene i CSV-en fra Jagex er fast.
    // Linje 0 er totalnivå, deretter kommer ferdighetene i denne rekkefølgen.
    private static final List<String> SKILL_NAMES = List.of(
            "Overall", "Attack", "Defence", "Strength", "Hitpoints", "Ranged",
            "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing",
            "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility",
            "Thieving", "Slayer", "Farming", "Runecraft", "Hunter", "Construction"
    );

    // RestClient er Spring sitt verktøy for å gjøre HTTP-kall til andre tjenester.
    private final RestClient restClient = RestClient.create();

    public PlayerStats getStats(String username) {
        try {
            // Hent rå CSV-tekst fra Jagex
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

    // Gjør CSV-teksten om til en liste av Skill-objekter
    private PlayerStats parse(String username, String body) {
        String[] lines = body.split("\n");
        List<Skill> skills = new ArrayList<>();

        // Vi tar bare de første 24 linjene (totalnivå + 23 ferdigheter)
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