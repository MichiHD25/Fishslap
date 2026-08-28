package me.northcraft.fishslap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    // Setzt den NORTHCRAFT.ME-Header in der Tab-Liste
    public static void updateTablist(Player player) {
        player.setPlayerListHeader("\n§b§lNORTH§f§lCRAFT§c§l.ME\n");
    }

    // 1. Design für die Wartelobby
    public static void setLobbyScoreboard(Player player, int wins) {
        updateTablist(player);

        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("lobby", Criteria.DUMMY, "§b§lNORTH§f§lCRAFT§c§l.ME");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        setScore(obj, "§1", 15);
        setScore(obj, "§fSpieler: §b" + player.getName(), 14);
        setScore(obj, "§2", 13);
        setScore(obj, "§fGewonnen: §e" + wins, 12);
        setScore(obj, "§3", 11);
        setScore(obj, "§fMünzen: §e0", 10);
        setScore(obj, "§4", 9);
        setScore(obj, "§fServer: §bLobby-1", 8);
        setScore(obj, "§5", 7);
        setScore(obj, "§fnorthcraft.me", 6);

        player.setScoreboard(board);
    }

    // 2. Design während eines Fishslap-Matches
    public static void setGameScoreboard(Player player, String teamColorName, int kills, int deaths) {
        updateTablist(player);

        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("fishslap", Criteria.DUMMY, "§b§lNORTH§f§lCRAFT§c§l.ME");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        setScore(obj, "§1", 15);
        setScore(obj, "§fTeam: " + teamColorName, 14);
        setScore(obj, "§2", 13);
        setScore(obj, "§fKills: §e" + kills, 12);
        setScore(obj, "§3", 11);
        setScore(obj, "§fTode: §e" + deaths, 10);
        setScore(obj, "§4", 9);
        setScore(obj, "§fServer: §bFishslap", 8);
        setScore(obj, "§5", 7);
        setScore(obj, "§fnorthcraft.me", 6);

        player.setScoreboard(board);
    }

    private static void setScore(Objective obj, String text, int scoreValue) {
        obj.getScore(text).setScore(scoreValue);
    }
}