package me.hsgamer.betterboard.builder;

import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.provider.board.AnimatedBoardProvider;
import me.hsgamer.betterboard.provider.board.SimpleBoardProvider;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;

import java.util.Objects;
import java.util.Optional;

public class BoardProviderBuilder extends Builder<Config, BoardProvider> {
    public static final BoardProviderBuilder INSTANCE = new BoardProviderBuilder();
    private static final PathString TYPE_PATH = new PathString("type");

    private BoardProviderBuilder() {
        register(SimpleBoardProvider.class, "simple");
        register(AnimatedBoardProvider.class, "animated", "animate", "animation");
    }

    public void register(Class<? extends ConfigurableBoardProvider> clazz, String... name) {
        register(config -> {
            try {
                ConfigurableBoardProvider provider = clazz.getDeclaredConstructor().newInstance();
                provider.loadFromConfig(config);
                return provider;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, name);
    }

    public Optional<BoardProvider> build(Config config) {
        if (config.contains(TYPE_PATH)) {
            String type = Objects.toString(config.getNormalized(TYPE_PATH));
            return build(type, config);
        } else {
            return build("simple", config);
        }
    }
}
