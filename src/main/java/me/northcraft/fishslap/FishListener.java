package me.northcraft.fishslap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

        event.setJoinMessage("§2>>> §a" + player.getName());

        ScoreboardManager.setLobbyScoreboard(player, 0);
        gameManager.giveLobbyItems(player);
        gameManager.checkLobby();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Wenn kein aktives Match läuft, führt ein Rechtsklick mit dem roten Bett zum Serverwechsel
        if (!gameManager.isIngame() && !gameManager.isCountingDown()) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getInventory().getItemInMainHand().getType() == Material.RED_BED) {
                    event.setCancelled(true);
                    sendToLobby(player, "lobby");
                }
            }
        }
    }

    private void sendToLobby(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage("§4<<< §c" + player.getName());

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