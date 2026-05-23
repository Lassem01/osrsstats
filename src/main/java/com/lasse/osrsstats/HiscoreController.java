package com.lasse.osrsstats;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HiscoreController {

    // De tre kontoene som er i MATTOX OSRS
    private static final List<String> ACCOUNTS = List.of(
            "GIM Jostein", "LaksenGI", "MagickinderG"
    );

    private final HiscoreService service;

    public HiscoreController(HiscoreService service) {
        this.service = service;
    }

    // GET /api/stats → henter stats for alle kontoene på én gang
    @GetMapping("/stats")
    public List<PlayerStats> getAllStats() {
        return ACCOUNTS.stream()
                .map(service::getStats)
                .toList();
    }
}