package me.northcraft.fishslap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FishListener implements Listener {

    private final Fishslap plugin;
    private final GameManager gameManager;

    public FishListener(Fishslap plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Eigene Join-Nachricht
        event.setJoinMessage("§2>>> §a" + player.getName());

        // Scoreboard setzen und Lobby-Prüfung starten
        ScoreboardManager.setLobbyScoreboard(player, 0);
        gameManager.checkLobby();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Eigene Leave-Nachricht
        event.setQuitMessage("§4<<< §c" + player.getName());

        // Abbruch-Logik bei Server-Verlassen ausführen
        gameManager.handleQuit(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!gameManager.isIngame() || !gameManager.isPlayerInGame(player)) {
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getY() < 50) {
            gameManager.handleFall(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (gameManager.isIngame() && gameManager.isPlayerInGame(player)) {
            event.getDrops().clear();
            gameManager.handleFall(player);
        }
    }
}