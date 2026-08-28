package me.northcraft.fishslap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FishslapCommand implements CommandExecutor {

    private final Fishslap plugin;

    public FishslapCommand(Fishslap plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("fishslap.admin")) return true;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setlobby")) {
                plugin.saveLoc("lobby", player.getLocation());
                player.sendMessage("§aLobby-Spawn gesetzt!");
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn1")) {
                plugin.saveLoc("spawn1", player.getLocation());
                player.sendMessage("§aSpawn 1 für Spieler 1 gesetzt!");
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn2")) {
                plugin.saveLoc("spawn2", player.getLocation());
                player.sendMessage("§aSpawn 2 für Spieler 2 gesetzt!");
                return true;
            } else if (args[0].equalsIgnoreCase("start")) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.size() < 2) {
                    player.sendMessage("§cEs müssen mindestens 2 Spieler online sein!");
                    return true;
                }
                plugin.getGameManager().startMatch(players.get(0), players.get(1));
                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!plugin.getGameManager().isIngame() && !plugin.getGameManager().isCountingDown() && !plugin.getGameManager().isLobbyCountingDown()) {
                    player.sendMessage("§cEs läuft aktuell kein Match oder Countdown!");
                    return true;
                }
                plugin.getGameManager().stopMatch();
                return true;
            }
        }
        player.sendMessage("§cNutzung: /fishslap <setlobby|setspawn1|setspawn2|start|stop>");
        return true;
    }
}