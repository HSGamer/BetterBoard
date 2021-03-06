package me.hsgamer.betterboard.manager;

import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.builder.BoardProviderBuilder;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardProviderManager {
    private final List<BoardProvider> providers = new CopyOnWriteArrayList<>();
    private final Plugin plugin;

    public BoardProviderManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadProviders() {
        File folder = new File(plugin.getDataFolder(), "provider");
        if (!folder.exists() && folder.mkdirs()) {
            plugin.getLogger().info("Created provider folder");
            plugin.saveResource("provider" + File.separator + "default.yml", false);
        }
        List<Config> providerConfigs = getProviderConfigs(folder);
        providerConfigs.forEach(Config::setup);

        // PRIORITY
        for (String providerName : MainConfig.PRIORITY_PROVIDERS.getValue()) {
            providerConfigs
                    .stream()
                    .filter(config -> config.getName().equals(providerName))
                    .findFirst()
                    .ifPresent(config -> {
                        providerConfigs.remove(config);
                        BoardProviderBuilder.INSTANCE.build(config).ifPresent(providers::add);
                    });
        }

        // REMAINING
        for (Config config : providerConfigs) {
            BoardProviderBuilder.INSTANCE.build(config).ifPresent(providers::add);
        }
    }

    public void clearAll() {
        providers.forEach(BoardProvider::clear);
        providers.clear();
    }

    private List<Config> getProviderConfigs(File file) {
        List<Config> list = new ArrayList<>();
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                list.addAll(getProviderConfigs(subFile));
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".yml")) {
            list.add(new BukkitConfig(file));
        }
        return list;
    }

    public Optional<BoardProvider> getProvider(Player player) {
        return providers.stream().filter(boardProvider -> boardProvider.canFetch(player)).findFirst();
    }
}
