package com.lasse.osrsstats;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    // Finn det nyeste snapshotet for en spiller (til "nå"-verdien)
    Optional<Snapshot> findFirstByUsernameOrderByTakenAtDesc(String username);

    // Finn det eldste snapshotet for en spiller ETTER et gitt tidspunkt
    // (altså: den første målingen innenfor uka/måneden – vårt "før"-punkt)
    Optional<Snapshot> findFirstByUsernameAndTakenAtAfterOrderByTakenAtAsc(
            String username, Instant after);
}