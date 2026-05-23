package com.lasse.osrsstats;

import java.util.List;

// Holder alt for én spiller: brukernavn, om henting lyktes, og lista med ferdigheter.
// En "record" er en kompakt måte å lage en ren dataklasse på – Java lager
// automatisk konstruktør og tilgangsmetoder (username(), found(), skills()).
public record PlayerStats(String username, boolean found, List<Skill> skills) {
}