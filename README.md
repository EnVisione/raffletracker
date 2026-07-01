# RaffleTracker

A client-side Fabric mod for Hypixel SkyBlock's **Century Celebration** event. It shows a
SkyHanni-styled, always-visible HUD overlay that tracks your incomplete Raffle Tasks and the live
countdowns to each raffle draw.

It is **standalone** — it only mirrors SkyHanni's look and feel (movable overlay, `/sh`-style config
screen). SkyHanni does not need to be installed, and this mod does not hook into it.

## Features

- **Raffle Tracker HUD** — a bold "Raffle Tracker" title with a transparent background, always
  visible while in-world.
- **Task tracking** — completed tasks are hidden; each incomplete task is shown with the
  description of how to complete it (e.g. `Scribe Slayer: Kill a Scribe.`), grouped by the three
  tiers and their reset windows:
  - Easy (paper) — resets every **2h**
  - Medium (map) — resets every **24h**
  - Hard (filled map) — resets at the **event** end
- **Raffle countdowns** — a live "time to next raffle" countdown for each draw (Speed / Daily /
  The Big One), with the soonest one highlighted.
- **Auto task reset** — Raffle Tasks reset every time the Speed Raffle is drawn, so when its
  countdown hits zero the tracker drops the stale task list and prompts you to re-run
  `/centurytasks`. The Speed Raffle countdown automatically rolls forward to its next 2-hour cycle.
- **Missing-data prompts** — if a chest hasn't been read yet, the overlay tells you exactly which
  command to run.
- **SkyHanni-style config** — categories down the left (`Hub`, `GUI`) with the tracker on/off toggle
  and a drag-to-move / scroll-to-scale edit screen, plus a readable-backdrop option.

## Usage

1. In game, run `/centurytasks` and open the **Raffle Tasks** chest — the mod reads it automatically.
2. Run `/centuryrafflebox` and open the **Raffle Box** — the mod captures the draw countdowns.
3. The HUD updates instantly. Data is re-read every time you reopen the chests.

### Config

- `/raffletracker` (or `/rt`) opens the config screen.
  - **Hub** → toggle the Raffle Tracker on/off.
  - **GUI** → *Edit Position* (drag to move, scroll to scale), scale, **Background** on/off and
    **Opacity** (fully transparent → fully opaque) for readability, **Descriptions** on/off (hide the
    "how to complete" text and show just the task name), and reset-position.

Settings are saved to `config/raffletracker.json`. Parsed raffle data is kept in memory for the whole
session, so it survives server hops.

## Building

Requires JDK 25.

```bash
JAVA_HOME=/path/to/jdk-25 ./gradlew build
```

The mod jar is produced at `build/libs/raffletracker-1.0.0.jar`.

## License

Available under the CC0 license.
