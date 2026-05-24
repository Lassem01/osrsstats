# Mattox

Samlet statusside for Group Ironman-gruppen vår i Old School RuneScape. Henter ferdighetsnivåene til alle kontoene fra Jagex sine offisielle hiscores og samler dem på én side, med leaderboard over XP og en logg over de siste levlene noen har fått. Bygget for å slippe å slå opp hver konto for seg på den Jagex sin hiscore-side.

## Funksjoner

- **Stats per konto** alle 23 ferdigheter for hver konto, med markering av hvem som leder i hver enkelt
- **Leaderboard** XP per spiller for inneværende uke (fra mandag) og måned (fra den 1.)
- **Aktivitetslogg** de siste nivåene som er tatt i gruppen
- **Automatisk innsamling** stats hentes fra Jagex hver sjette time og lagres som tidsstemplede øyeblikksbilder

## Teknologi

Java 21 · Spring Boot (Web, Data JPA) · PostgreSQL · Caffeine · HTML/CSS/JS · Gradle · Docker · Railway

## Kjøre lokalt

Krever Java 21 og Docker.

```bash
# Start PostgreSQL
docker compose up -d db

# Bygg og kjør hele appen
docker compose up --build
```

Appen kjører på `http://localhost:8080`. Databasekobling og port leses fra miljøvariabler med standardverdier for lokal kjøring, så samme kode fungerer både lokalt og i produksjon.

## API

| Metode | Endepunkt | Beskrivelse |
|--------|-----------|-------------|
| `GET` | `/api/stats` | Gjeldende stats for alle kontoer |
| `POST` | `/api/refresh` | Henter ferske stats og lagrer et snapshot |
| `GET` | `/api/leaderboard?period=week` | XP per spiller (`week` eller `month`) |
| `GET` | `/api/activity` | De siste level-hendelsene |

## Hvordan det henger sammen

Jagex sender ikke CORS-headere, så nettleseren kan ikke hente hiscores direkte derfor henter serveren på vegne av frontend og mellomlagrer svaret.

En planlagt jobb henter stats hver sjette time og lagrer et snapshot per konto. Resten utledes fra disse: leaderboardet tar differansen mellom det nyeste snapshotet og det første i perioden, og aktivitetsloggen sammenligner levler mellom påfølgende snapshots for å finne nye levler. Periodene følger kalenderen i norsk tidssone, uavhengig av hvor serveren kjører.

To entiteter utgjør kjernen: `Snapshot` (total-XP og alle ferdighetsnivåer på et tidspunkt) og `LevelUpEvent` (en enkelt level-oppnåelse). Alt annet er avledet.

## Deploy

Kjører på Railway: app-tjenesten bygges fra `Dockerfile`, og PostgreSQL kjører som egen managed tjeneste. Databasekoblingen settes med miljøvariabler som refererer til Postgres-tjenesten. Serveren kjører i UTC, og den planlagte jobben er tilpasset dette for å treffe rett i forhold til Jagex sine oppdateringer.

## Videre arbeid

- Leaderboard per enkeltferdighet
- Samlet gruppestatistikk for hele Mattox
- Utregnet combat-nivå per konto
- Grafer over XP-utvikling
