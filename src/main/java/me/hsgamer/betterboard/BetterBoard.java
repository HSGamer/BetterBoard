package me.hsgamer.betterboard;

import com.ezylang.evalex.data.EvaluationValue;
import me.hsgamer.betterboard.command.ReloadCommand;
import me.hsgamer.betterboard.command.ToggleCommand;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.betterboard.listener.PlayerListener;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.betterboard.util.ExpressionUtil;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.CommonVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.math.BigDecimal;

public final class BetterBoard extends BasePlugin {
    private static final VariableBundle globalVariableBundle = new VariableBundle();

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
        Permissions.register();

        if (PlaceholderAPIHook.isAvailable()) {
            VariableManager.GLOBAL.addExternalReplacer(StringReplacer.of((original, uuid) -> PlaceholderAPIHook.setPlaceholders(original, Bukkit.getOfflinePlayer(uuid))));
            getLogger().info("Hooked into PlaceholderAPI");
        }

        CommonVariableBundle.registerVariables(globalVariableBundle);
        BukkitVariableBundle.registerVariables(globalVariableBundle);
        globalVariableBundle.register("condition_", StringReplacer.of(original -> ExpressionUtil.getResult(original).map(EvaluationValue::getNumberValue).map(BigDecimal::toString).orElse(null)));

        registerListener(new PlayerListener(this));

        registerCommand(new ReloadCommand(this));
        registerCommand(new ToggleCommand(this));

        new Metrics(this, 12861);
    }

    @Override
    public void postEnable() {
        boardProviderManager.loadProviders();
    }

    @Override
    public void disable() {
        Permissions.unregister();
        playerBoardManager.clearAll();
        boardProviderManager.clearAll();
        globalVariableBundle.unregisterAll();
        VariableManager.GLOBAL.clearExternalReplacers();
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
