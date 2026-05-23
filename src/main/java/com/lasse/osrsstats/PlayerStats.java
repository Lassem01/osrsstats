package com.lasse.osrsstats;

import java.util.List;

// Holder alt for én spiller: brukernavn, om henting lyktes, og lista med ferdigheter.
public record PlayerStats(String username, boolean found, List<Skill> skills) {
}