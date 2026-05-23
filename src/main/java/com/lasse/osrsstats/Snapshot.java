package com.lasse.osrsstats;

import jakarta.persistence.*;
import java.time.Instant;

// Hver rad er ett "øyeblikksbilde": hvor mye total-XP en spiller hadde
// på et bestemt tidspunkt. Ved å sammenligne to snapshots ser vi XP-gevinst.
@Entity
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private long totalXp;
    private Instant takenAt;   // når målingen ble gjort

    // Lagrer alle ferdighetsnivåer som tekst, f.eks. "Attack:80,Strength:85,..."
    // slik at vi senere kan sammenligne og oppdage level-ups.
    @Column(length = 1000)
    private String skillLevels;

    // JPA krever en tom konstruktør
    protected Snapshot() {}

    public Snapshot(String username, long totalXp, Instant takenAt, String skillLevels) {
        this.username = username;
        this.totalXp = totalXp;
        this.takenAt = takenAt;
        this.skillLevels = skillLevels;
    }

    public String getSkillLevels() { return skillLevels; }


    // Getters (settes ikke etter opprettelse, så ingen setters nødvendig)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public long getTotalXp() { return totalXp; }
    public Instant getTakenAt() { return takenAt; }
}