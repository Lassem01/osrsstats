package com.lasse.osrsstats;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LevelUpRepository extends JpaRepository<LevelUpEvent, Long> {

    // Henter de nyeste hendelsene først. Vi bruker Spring sin innebygde
    // måte å begrense antall på: "findTop5By..." gir kun de 5 nyeste.
    List<LevelUpEvent> findTop5ByOrderByHappenedAtDesc();
}