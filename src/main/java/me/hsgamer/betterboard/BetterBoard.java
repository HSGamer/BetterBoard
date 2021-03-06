package me.hsgamer.betterboard;

import me.hsgamer.betterboard.command.ReloadCommand;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.betterboard.listener.PlayerListener;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.betterboard.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.variable.ExternalStringReplacer;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.util.UUID;

public final class BetterBoard extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);

    private final BoardProviderManager boardProviderManager = new BoardProviderManager(this);
    private final PlayerBoardManager playerBoardManager = new PlayerBoardManager(this);

    @Override
    public void load() {
        MessageUtils.setPrefix("&f[&6BetterBoard&f] ");
        mainConfig.setup();
    }

    @Override
    public void enable() {
        if (PlaceholderAPIHook.setupPlugin()) {
            VariableManager.addExternalReplacer(new ExternalStringReplacer() {
                @Override
                public String replace(String original, UUID uuid) {
                    return PlaceholderAPIHook.setPlaceholders(original, Bukkit.getOfflinePlayer(uuid));
                }

                @Override
                public boolean canBeReplaced(String original) {
                    return PlaceholderAPIHook.hasPlaceholders(original);
                }
            });
            getLogger().info("Hooked into PlaceholderAPI");
        }
        PluginVariableManager.registerDefaultVariables();

        registerListener(new PlayerListener(this));

        registerCommand(new ReloadCommand(this));

        if (MainConfig.METRICS.getValue()) {
            new Metrics(this, 12861);
        }
    }

    @Override
    public void postEnable() {
        boardProviderManager.loadProviders();
    }

    @Override
    public void disable() {
        playerBoardManager.clearAll();
        boardProviderManager.clearAll();
        PluginVariableManager.unregisterAll();
        VariableManager.clearExternalReplacers();
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
