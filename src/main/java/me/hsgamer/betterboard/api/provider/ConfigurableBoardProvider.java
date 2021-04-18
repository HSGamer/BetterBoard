package me.hsgamer.betterboard.api.provider;

import me.hsgamer.hscore.config.Config;

public interface ConfigurableBoardProvider extends BoardProvider {
    void loadFromConfig(Config config);
}
