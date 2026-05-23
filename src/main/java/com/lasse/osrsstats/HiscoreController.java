package com.lasse.osrsstats;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HiscoreController {

    // De tre kontoene i gruppa. Vil dere legge til/endre, gjør dere det her.
    private static final List<String> ACCOUNTS = List.of(
            "GIM Jostein", "LaksenGI", "MagickinderG"
    );

    private final HiscoreService service;
    private final LeaderboardService leaderboardService;

    // Spring gir oss begge tjenestene automatisk (dependency injection)
    public HiscoreController(HiscoreService service, LeaderboardService leaderboardService) {
        this.service = service;
        this.leaderboardService = leaderboardService;
    }

    // GET /api/stats → henter stats for alle kontoene OG lagrer et snapshot for hver
    @GetMapping("/stats")
    public List<PlayerStats> getAllStats() {
        return ACCOUNTS.stream()
                .map(service::getStatsAndSave)
                .toList();
    }

    // GET /api/leaderboard?days=7  → XP-gevinst per spiller de siste X dagene
    // days=7 gir uke, days=30 gir måned. Hopper du over parameteren, brukes 7.
    @GetMapping("/leaderboard")
    public List<LeaderboardService.GainEntry> getLeaderboard(
            @RequestParam(defaultValue = "7") int days) {
        return leaderboardService.getLeaderboard(ACCOUNTS, days);
    }
}