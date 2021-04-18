package me.hsgamer.betterboard.listener;

import me.hsgamer.betterboard.BetterBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final BetterBoard instance;

    public PlayerListener(BetterBoard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.getPlayerBoardManager().addBoard(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        instance.getPlayerBoardManager().removeBoard(event.getPlayer());
    }
}
