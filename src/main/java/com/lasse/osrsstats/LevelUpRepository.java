package com.lasse.osrsstats;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LevelUpRepository extends JpaRepository<LevelUpEvent, Long> {

    // Henter de 10 nyeste hendelsene først.
    List<LevelUpEvent> findTop10ByOrderByHappenedAtDesc();
}