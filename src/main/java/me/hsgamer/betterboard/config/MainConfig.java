package me.hsgamer.betterboard.config;

import me.hsgamer.betterboard.config.converter.StringListConverter;
import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.util.Collections;
import java.util.List;

public interface MainConfig {
    @ConfigPath({"update", "ticks"})
    default long getUpdateTicks() {
        return 0L;
    }

    @ConfigPath({"update", "async"})
    default boolean isUpdateAsync() {
        return true;
    }

    @ConfigPath(value = "priority-providers", converter = StringListConverter.class)
    default List<String> getPriorityProviders() {
        return Collections.emptyList();
    }

    void reloadConfig();
}
