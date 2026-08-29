# 🐟 Fishslap Plugin for Spigot / Paper

Ein benutzerdefiniertes Minigame-Plugin für das **Northcraft.me** Netzwerk. Spieler versuchen, sich gegenseitig mit dem *Kabeljau der Macht* von der Plattform zu klatschen. Wer zuerst **10 Kills** erreicht, gewinnt das Match!

---

## ✨ Features

* **First-to-10-Kills System:** Das Match endet automatisch, sobald ein Spieler 10 Kills erreicht.
* **Fair-Play Respawns:** Nach jedem Kill werden **beide** Spieler gleichzeitig auf ihre Spawns zurückgesetzt, um Spawncamping komplett zu verhindern.
* **Automatisches Lobby-System:** 
  * Ein 20-Sekunden-Lobby-Countdown startet automatisch, sobald 2 Spieler online sind.
  * Bricht automatisch ab, wenn ein Spieler die Lobby vorzeitig verlässt.
* **In-Game Countdown:** 5-Sekunden-Countdown vor Matchstart mit Title-Screens und Sound-Effekten.
* **Dynamische Scoreboards:**
  * **Wartelobby:** Zeigt Spielername, Gewonnen-Stats, Münzen und Server-Info.
  * **In-Game:** Zeigt aktuelles Team (ROT / BLAU), Kills und Tode in Echtzeit.
* **Custom Tablist:** Farbiger `NORTHCRAFT.ME` Header in der Spielerliste.
* **Clean Chat & Custom Messages:** Formatierte Join/Quit-Nachrichten (`>>>` / `<<<`) und sauberes Nachrichtendesign ohne störende Plugin-Präfixe.

---

## 🛠️ Befehle & Permissions

| Befehl | Beschreibung | Permission |
| :--- | :--- | :--- |
| `/fishslap setlobby` | Setzt den Spawnpunkt für die Wartelobby | `fishslap.admin` |
| `/fishslap setspawn1` | Setzt den Spawn für Spieler 1 (Team ROT) | `fishslap.admin` |
| `/fishslap setspawn2` | Setzt den Spawn für Spieler 2 (Team BLAU) | `fishslap.admin` |
| `/fishslap start` | Überspringt den Lobby-Timer und startet das Match sofort | `fishslap.admin` |
| `/fishslap stop` | Bricht das laufende Match oder den Countdown ab | `fishslap.admin` |

---

## 🚀 Installation & Einrichtung

1. Lade die kompilierte `.jar`-Datei herunter und platziere sie im `plugins/`-Ordner deines Spigot/Paper-Servers.
2. Starte den Server neu.
3. Betritt den Server als Admin und richte die Standorte ein:
   * Position in der Wartelobby einnehmen $\rightarrow$ `/fishslap setlobby`
   * Position für Arena-Spawn 1 einnehmen $\rightarrow$ `/fishslap setspawn1`
   * Position für Arena-Spawn 2 einnehmen $\rightarrow$ `/fishslap setspawn2`
4. Sobald zwei Spieler den Server betreten, startet das Minigame automatisch!
