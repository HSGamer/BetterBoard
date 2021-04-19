package me.hsgamer.betterboard.builder;

import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.provider.AnimatedBoardProvider;
import me.hsgamer.betterboard.provider.SimpleBoardProvider;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.config.Config;

import java.util.Optional;

public class BoardProviderBuilder extends Builder<Config, BoardProvider> {
    public static final BoardProviderBuilder INSTANCE = new BoardProviderBuilder();

    private BoardProviderBuilder() {
        register(SimpleBoardProvider.class, "simple");
        register(AnimatedBoardProvider.class, "animated", "animate", "animation");
    }

    public void register(Class<? extends ConfigurableBoardProvider> clazz, String name, String... aliases) {
        register(config -> {
            try {
                ConfigurableBoardProvider provider = clazz.newInstance();
                provider.loadFromConfig(config);
                return provider;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, name, aliases);
    }

    public Optional<BoardProvider> build(Config config) {
        if (config.contains("type")) {
            String type = config.getInstance("type", String.class);
            return build(type, config);
        } else {
            return build("simple", config);
        }
    }
}
