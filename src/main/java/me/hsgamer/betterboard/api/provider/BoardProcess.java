package me.hsgamer.betterboard.api.provider;

public interface BoardProcess {
    default void init() {
        // EMPTY
    }

    default void update() {
        // EMPTY
    }

    default void stop() {
        // EMPTY
    }

    BoardProvider getProvider();
}
