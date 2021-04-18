package me.hsgamer.betterboard.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.BaseConfigPath;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class MainConfig extends PathableConfig {
    public static final IntegerConfigPath UPDATE_TICKS = new IntegerConfigPath("update.ticks", 0);
    public static final BooleanConfigPath UPDATE_ASYNC = new BooleanConfigPath("update.async", true);
    public static final ConfigPath<List<String>> PRIORITY_PROVIDERS = new BaseConfigPath<>("priority-providers", Collections.emptyList(), o -> CollectionUtils.createStringListFromObject(o, true));

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
