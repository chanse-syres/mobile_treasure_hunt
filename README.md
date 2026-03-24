# Mobile Treasure Hunt (Android)

A location based Android game built with **Jetpack Compose** where players solve clues by physically traveling to real-world coordinates and there they verify their position with GPS.

## Repo at a glance

This repository contains a single Android app module (`:app`) for a prototype/educational treasure-hunt experience:

- **Flow:** Home -> Difficulty -> Clue -> Clue Solved -> Completed.
- **Game loop:** Read clues, optionally view hints, go to the suspected locations, tap **Found It!** button, and let the app verify proximity.
- **Data source:** Hunts and clues are loaded from a local JSON file (`res/raw/hunts.json`).
- **Current content:** `easy` mode is enabled with two clues; `medium` and `hard` are scaffolded but currently disabled. I plan to modify this app for the Corvallis campus and put it on the app store.

---

## Best one-line description

**A Jetpack Compose GPS treasure-hunt app that literally validates clue completion by checking the player’s real- orld location with configurable coordinates and radii.**

---

## Technical skills demonstrated

This project showcases practical, resume-relevant mobile engineering skills:

- **Android app architecture (MVVM):** Stateful UI powered by a `ViewModel` and reactive `StateFlow`.
- **Jetpack Compose UI development:** Composable screens, dialogs, state handling, and Material 3 components.
- **Navigation design:** Multi screen app flow with `navigation-compose`.
- **Mobile location/GPS integration:** Runtime permission handling, location service checks, and location retrieval.
- **Geospatial logic:** Distance verification using haversine calculations for proximity based game rules.
- **Data modeling and parsing:** JSON driven game content (`hunts.json`) parses into typed Kotlin models.
- **Feature gating/configuration:** Difficulty modes are enabled/disabled from data, not hardcoded per screen.
- **Kotlin and Gradle ecosystem proficiency:** Modern Android toolchain that uses Kotlin DSL build scripts.

---

## Features

- **Location permission and GPS gating** before play starts.
- **Difficulty selection** driven by JSON-configured hunt metadata.
- **Per-clue gameplay** with:
  - clue title and text,
  - optional hint dialog,
  - on-demand location validation,
  - distance feedback when the player is not yet close enough.
- **Timer tracking** from hunt start through completion.
- **Solved and completion screens** with elapsed time and contextual messages.
- **Simple MVVM state management** using `AndroidViewModel` and `StateFlow`.

---

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Navigation:** `androidx.navigation:navigation-compose`
- **Location services:** Google Play Services Fused Location Provider
- **Build:** Gradle Kotlin DSL
- **Min SDK:** 31
- **Target/Compile SDK:** 36

---

## Project structure

```text
app/src/main/java/com/chanse/cs492/treasurehunt
data/
   model/           # Hunt/Clue data classes
   HuntRepository   # Loads/parses hunts.json
ui/
   navigation/      # Nav routes and graphs
   screens/         # Home/Difficulty/Clue/Solved/Completed
   theme/
util/
   LocationUtils    # Haversine distance calculation
viewmodel/
   TreasureViewModel
```

---

## Setup

### Prerequisites

- Android Studio (recent stable)
- Android SDK for API 36
- Emulator or physical Android device with location services

### Run locally

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Build and run the `app` configuration.
4. Grant location permissions when prompted.
5. Ensure device location/GPS is enabled.

---

## Configure hunt locations for your area

By default, the sample clues point to locations around OSU Corvallis.

To localize gameplay:

1. Open `app/src/main/res/raw/hunts.json`.
2. Edit each clue’s:
   - `latitude`
   - `longitude`
   - `radiusMeters`
   - text fields (`clueText`, `hintText`, `solvedInfo`) as desired.
3. Rebuild and run.

Tip: start with a larger `radiusMeters` while testing (e.g., `75.0`–`150.0`), then tighten if needed.

---

## Gameplay data format (`hunts.json`)

Each hunt includes metadata and a clue list:

- `id` (string): unique key (e.g., `easy`)
- `order` (int): display ordering
- `title` (string): button label
- `intro` (string)
- `enabled` (bool): if false, button is disabled
- `finalMessage` (string): shown on completion
- `clues` (array): ordered list of clue objects

Each clue includes:

- `id` (int)
- `title` (string)
- `clueText` (string)
- `hintText` (string)
- `latitude` / `longitude` (double)
- `radiusMeters` (double)
- `solvedInfo` (string)

---

## Notes and current limitations

- `Settings`, `Stats`, and `Leaderboard` buttons are scaffold placeholders.
- Medium/Hard hunts are currently disabled in seed data.
- Location matching is based on straight-line (haversine) distance to clue coordinates.

---

## License

No license file is currently defined in this repository.
