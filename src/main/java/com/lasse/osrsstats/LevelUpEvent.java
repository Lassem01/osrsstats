package com.lasse.osrsstats;

import jakarta.persistence.*;
import java.time.Instant;

// En registrert level-up: hvem, hvilken ferdighet, til hvilket nivå, og når.
@Entity
public class LevelUpEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String skill;
    private int newLevel;
    private Instant happenedAt;

    protected LevelUpEvent() {}

    public LevelUpEvent(String username, String skill, int newLevel, Instant happenedAt) {
        this.username = username;
        this.skill = skill;
        this.newLevel = newLevel;
        this.happenedAt = happenedAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getSkill() { return skill; }
    public int getNewLevel() { return newLevel; }
    public Instant getHappenedAt() { return happenedAt; }
}