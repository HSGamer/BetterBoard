package me.hsgamer.betterboard;

import me.hsgamer.betterboard.command.ReloadCommand;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.betterboard.listener.PlayerListener;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterBoard extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);

    private final BoardProviderManager boardProviderManager = new BoardProviderManager(this);
    private final PlayerBoardManager playerBoardManager = new PlayerBoardManager(this);

    public static BetterBoard getInstance() {
        return JavaPlugin.getPlugin(BetterBoard.class);
    }

    @Override
    public void load() {
        MessageUtils.setPrefix("&f[&6BetterBoard&f] ");
        mainConfig.setup();
    }

    @Override
    public void enable() {
        if (PlaceholderAPIHook.setupPlugin()) {
            getLogger().info("Hooked into PlaceholderAPI");
        }

        registerListener(new PlayerListener(this));

        registerCommand(new ReloadCommand(this));
    }

    @Override
    public void postEnable() {
        boardProviderManager.loadProviders();
    }

    @Override
    public void disable() {
        playerBoardManager.clearAll();
        boardProviderManager.clearAll();
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
