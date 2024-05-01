package me.hsgamer.betterboard;

import com.ezylang.evalex.data.EvaluationValue;
import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import io.github.projectunified.minelib.plugin.postenable.PostEnableComponent;
import me.hsgamer.betterboard.command.ReloadCommand;
import me.hsgamer.betterboard.command.ToggleCommand;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.betterboard.listener.PlayerListener;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.betterboard.util.ExpressionUtil;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.variable.CommonVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.List;

public final class BetterBoard extends BasePlugin {
    private static final VariableBundle globalVariableBundle = new VariableBundle();

    @Override
    protected List<Object> getComponents() {
        return List.of(
                new PostEnableComponent(this),

                new BoardProviderManager(this),
                new PlayerBoardManager(this),

                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),
                new CommandComponent(this, new ReloadCommand(this), new ToggleCommand(this)),
                new PlayerListener(this),
                new Permissions(this)
        );
    }

    @Override
    public void load() {
        MessageUtils.setPrefix("&f[&6BetterBoard&f] ");
    }

    @Override
    public void enable() {
        if (PlaceholderAPIHook.isAvailable()) {
            VariableManager.GLOBAL.addExternalReplacer(StringReplacer.of((original, uuid) -> PlaceholderAPIHook.setPlaceholders(original, Bukkit.getOfflinePlayer(uuid))));
            getLogger().info("Hooked into PlaceholderAPI");
        }

        CommonVariableBundle.registerVariables(globalVariableBundle);
        BukkitVariableBundle.registerVariables(globalVariableBundle);
        globalVariableBundle.register("condition_", StringReplacer.of(original -> ExpressionUtil.getResult(original).map(EvaluationValue::getNumberValue).map(BigDecimal::toString).orElse(null)));

        new Metrics(this, 12861);
    }

    @Override
    public void disable() {
        globalVariableBundle.unregisterAll();
        VariableManager.GLOBAL.clearExternalReplacers();
    }
}
