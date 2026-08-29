package me.northcraft.fishslap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final Fishslap plugin;
    private Player player1;
    private Player player2;
    private int killsP1 = 0;
    private int killsP2 = 0;
    private final int TARGET_KILLS = 10;

    private boolean isIngame = false;
    private boolean isCountingDown = false;
    private boolean isLobbyCountingDown = false;

    private BukkitTask countdownTask;
    private BukkitTask lobbyTask;

    public GameManager(Fishslap plugin) {
        this.plugin = plugin;
    }

    public void checkLobby() {
        if (isIngame || isCountingDown || isLobbyCountingDown) return;

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() >= 2) {
            startLobbyCountdown();
        }
    }

    public void startLobbyCountdown() {
        if (isLobbyCountingDown) return;
        isLobbyCountingDown = true;

        lobbyTask = new BukkitRunnable() {
            int secondsLeft = 20;

            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

                if (players.size() < 2) {
                    cancelLobbyCountdown("§cEin Spieler hat den Server verlassen! Countdown abgebrochen.");
                    cancel();
                    return;
                }

                if (secondsLeft == 20 || secondsLeft == 10 || (secondsLeft <= 5 && secondsLeft > 0)) {
                    broadcast("§7Das Spiel startet in §e" + secondsLeft + " §7Sekunden!");
                    for (Player p : players) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                }

                if (secondsLeft <= 0) {
                    isLobbyCountingDown = false;
                    cancel();
                    startMatch(players.get(0), players.get(1));
                    return;
                }

                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelLobbyCountdown(String reason) {
        if (lobbyTask != null) {
            lobbyTask.cancel();
            lobbyTask = null;
        }
        if (isLobbyCountingDown) {
            isLobbyCountingDown = false;
            if (reason != null && !reason.isEmpty()) {
                broadcast(reason);
            }
        }
    }

    public void startMatch(Player p1, Player p2) {
        cancelLobbyCountdown(null);

        if (plugin.getLoc("spawn1") == null || plugin.getLoc("spawn2") == null) {
            broadcast("§cFehler: Spawns wurden noch nicht gesetzt!");
            return;
        }

        this.player1 = p1;
        this.player2 = p2;
        this.killsP1 = 0;
        this.killsP2 = 0;
        this.isCountingDown = true;

        resetPlayerToSpawn(player1, "spawn1");
        resetPlayerToSpawn(player2, "spawn2");
        updateScoreboards();

        broadcast("§7Match startet! Wer zuerst §e" + TARGET_KILLS + " Kills §7erzielt, gewinnt!");

        countdownTask = new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {
                if (secondsLeft > 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§e" + secondsLeft, "§7Das Match startet gleich...", 0, 20, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                    secondsLeft--;
                } else {
                    isCountingDown = false;
                    isIngame = true;

                    giveFishKit(player1);
                    giveFishKit(player2);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§a§lLOS!", "§7Klatscht euch von der Plattform!", 0, 20, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void handleFall(Player fallen) {
        if (!isIngame || player1 == null || player2 == null) return;

        Player killer;

        if (fallen.equals(player1)) {
            killsP2++;
            killer = player2;
        } else if (fallen.equals(player2)) {
            killsP1++;
            killer = player1;
        } else {
            return;
        }

        String killerName = killer.equals(player1) ? "§c" + killer.getName() : "§9" + killer.getName();
        String fallenName = fallen.equals(player1) ? "§c" + fallen.getName() : "§9" + fallen.getName();

        broadcast(killerName + " §7hat " + fallenName + " §7geklatscht! §8(§e" + killsP1 + " §7: §e" + killsP2 + "§8)");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }

        updateScoreboards();

        if (killsP1 >= TARGET_KILLS) {
            broadcast("§c" + player1.getName() + " §ahat das Match gewonnen!");
            endMatch();
        } else if (killsP2 >= TARGET_KILLS) {
            broadcast("§9" + player2.getName() + " §ahat das Match gewonnen!");
            endMatch();
        } else {
            resetPlayerToSpawn(player1, "spawn1");
            resetPlayerToSpawn(player2, "spawn2");
        }
    }

    public void handleQuit(Player leavingPlayer) {
        if (isIngame || isCountingDown) {
            if (isPlayerInGame(leavingPlayer)) {
                broadcast("§c" + leavingPlayer.getName() + " hat den Server verlassen. Match wird abgebrochen!");
                endMatch();
                return;
            }
        }

        if (isLobbyCountingDown && Bukkit.getOnlinePlayers().size() - 1 < 2) {
            cancelLobbyCountdown("§cEin Spieler hat den Server verlassen! Countdown abgebrochen.");
        }
    }

    private void resetPlayerToSpawn(Player player, String spawnKey) {
        if (player != null && player.isOnline()) {
            player.teleport(plugin.getLoc(spawnKey));
            player.setHealth(20.0);
            player.setFireTicks(0);
            player.setFallDistance(0);
            giveFishKit(player);
        }
    }

    public void stopMatch() {
        cancelLobbyCountdown(null);
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        broadcast("§cDas Spiel wurde manuell abgebrochen!");
        endMatch();
    }

    public void endMatch() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }

        isIngame = false;
        isCountingDown = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.getLoc("lobby") != null) {
                p.teleport(plugin.getLoc("lobby"));
            }
            giveLobbyItems(p);
            ScoreboardManager.setLobbyScoreboard(p, 0);
        }

        player1 = null;
        player2 = null;
        killsP1 = 0;
        killsP2 = 0;

        Bukkit.getScheduler().runTaskLater(plugin, this::checkLobby, 40L);
    }

    private void updateScoreboards() {
        if (player1 != null && player1.isOnline()) {
            ScoreboardManager.setGameScoreboard(player1, "§cROT", killsP1, killsP2);
        }
        if (player2 != null && player2.isOnline()) {
            ScoreboardManager.setGameScoreboard(player2, "§9BLAU", killsP2, killsP1);
        }
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();

        ItemStack lobbyBed = new ItemStack(Material.RED_BED);
        ItemMeta meta = lobbyBed.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lZurück zur Lobby §7(Rechtsklick)");
            lobbyBed.setItemMeta(meta);
        }

        player.getInventory().setItem(8, lobbyBed);
    }

    private void giveFishKit(Player player) {
        player.getInventory().clear();

        ItemStack cod = new ItemStack(Material.COD);
        ItemMeta meta = cod.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§bKabeljau der Macht");
            meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
            cod.setItemMeta(meta);
        }

        player.getInventory().setItem(0, cod);
    }

    public boolean isIngame() {
        return isIngame;
    }

    public boolean isCountingDown() {
        return isCountingDown;
    }

    public boolean isLobbyCountingDown() {
        return isLobbyCountingDown;
    }

    public boolean isPlayerInGame(Player p) {
        return (isIngame || isCountingDown) && (p != null && (p.equals(player1) || p.equals(player2)));
    }

    private void broadcast(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }
}