package com.lasse.osrsstats;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HiscoreController {

    // De tre kontoene i gruppa.
    private static final List<String> ACCOUNTS = List.of(
            "GIM Jostein", "LaksenGI", "MagickinderG"
    );

    private final HiscoreService service;
    private final LeaderboardService leaderboardService;
    private final LevelUpRepository levelUpRepository;

    public HiscoreController(HiscoreService service,
                             LeaderboardService leaderboardService,
                             LevelUpRepository levelUpRepository) {
        this.service = service;
        this.leaderboardService = leaderboardService;
        this.levelUpRepository = levelUpRepository;
    }

    // GET /api/stats → henter ferske stats til visning (caches)
    @GetMapping("/stats")
    public List<PlayerStats> getAllStats() {
        return ACCOUNTS.stream()
                .map(service::getStats)
                .toList();
    }

    @PostMapping("/refresh")
    public List<PlayerStats> refresh() {
        return ACCOUNTS.stream()
                .map(service::getStatsAndSave)
                .toList();
    }

    // GET /api/leaderboard?days=7 → XP-gevinst per spiller de siste X dagene
    // GET /api/leaderboard?period=week  (eller period=month)
    @GetMapping("/leaderboard")
    public List<LeaderboardService.GainEntry> getLeaderboard(
            @RequestParam(defaultValue = "week") String period) {
        return leaderboardService.getLeaderboard(ACCOUNTS, period);
    }

    // GET /api/activity → de 10 siste level-up-hendelsene
    @GetMapping("/activity")
    public List<LevelUpEvent> getActivity() {
        return levelUpRepository.findTop10ByOrderByHappenedAtDesc();
    }
}