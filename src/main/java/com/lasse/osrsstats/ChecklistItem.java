package com.lasse.osrsstats;

import jakarta.persistence.*;
import java.time.Instant;

// Én oppgave i en spillers sjekkliste, f.eks. "firecape" for LaksenGI.
@Entity
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;    // hvilken spiller oppgaven tilhører
    private String task;        // selve oppgaven, f.eks. "firecape"
    private boolean completed;  // fullført eller ikke
    private Instant createdAt;

    protected ChecklistItem() {}

    public ChecklistItem(String username, String task) {
        this.username = username;
        this.task = task;
        this.completed = false;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getTask() { return task; }
    public boolean isCompleted() { return completed; }
    public Instant getCreatedAt() { return createdAt; }

    // Setter for completed, siden den endres når en oppgave fullføres
    public void setCompleted(boolean completed) { this.completed = completed; }
}