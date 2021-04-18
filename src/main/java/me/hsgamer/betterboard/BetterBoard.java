package me.hsgamer.betterboard;

import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.betterboard.listener.PlayerListener;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;

public final class BetterBoard extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);

    private final BoardProviderManager boardProviderManager = new BoardProviderManager(this);
    private final PlayerBoardManager playerBoardManager = new PlayerBoardManager(this);

    @Override
    public void load() {
        mainConfig.setup();
    }

    @Override
    public void enable() {
        if (PlaceholderAPIHook.setupPlugin()) {
            getLogger().info("Hooked into PlaceholderAPI");
        }

        registerListener(new PlayerListener(this));
    }

    @Override
    public void postEnable() {
        boardProviderManager.loadProviders();
    }

    @Override
    public void disable() {
        playerBoardManager.clearAll();
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public BoardProviderManager getBoardProviderManager() {
        return boardProviderManager;
    }

    public PlayerBoardManager getPlayerBoardManager() {
        return playerBoardManager;
    }
}
