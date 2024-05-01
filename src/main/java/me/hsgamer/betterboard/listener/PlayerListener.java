package me.hsgamer.betterboard.listener;

import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements ListenerComponent {
    private final BetterBoard instance;

    public PlayerListener(BetterBoard instance) {
        this.instance = instance;
    }

    @Override
    public BetterBoard getPlugin() {
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.get(PlayerBoardManager.class).addBoard(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        instance.get(PlayerBoardManager.class).removeBoard(event.getPlayer());
    }
}
