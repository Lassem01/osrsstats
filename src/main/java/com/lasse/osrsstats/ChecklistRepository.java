package com.lasse.osrsstats;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChecklistRepository extends JpaRepository<ChecklistItem, Long> {

    // Eldste først – rekkefølgen bestemmer oppgavenummer (1, 2, 3...)
    List<ChecklistItem> findByUsernameOrderByCreatedAtAsc(String username);
}