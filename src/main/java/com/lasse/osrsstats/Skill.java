package com.lasse.osrsstats;

// En "record" er en kort måte å lage en enkel dataklasse på i Java.
// Den lagrer bare data: navnet på ferdigheten, nivå, xp og rangering.
public record Skill(String name, int level, long xp, long rank) {
}