package me.hsgamer.betterboard.provider.board;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.betterboard.provider.board.internal.FastBoardProcess;
import me.hsgamer.betterboard.provider.condition.ConditionProvider;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class FastBoardProvider implements ConfigurableBoardProvider {
    public static final String TITLE_PATH = "title";
    public static final String LINES_PATH = "lines";
    private static final String USE_MINIMESSAGE_PATH = "use-minimessage";

    private final ConditionProvider conditionProvider = new ConditionProvider();
    private boolean useMiniMessage = false;

    public abstract Optional<BoardFrame> fetch(Player player);

    @Override
    public boolean canFetch(Player player) {
        return this.conditionProvider.check(player);
    }

    @Override
    public void clear() {
        this.conditionProvider.clear();
    }

    @Override
    public void loadFromConfig(Config config) {
        this.conditionProvider.loadFromObject(config.getNormalized("", ConditionProvider.PATH));
        this.useMiniMessage = Optional.ofNullable(config.getNormalized(USE_MINIMESSAGE_PATH))
                .map(Object::toString)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    public BoardProcess createProcess(Player player) {
        return new FastBoardProcess(player, this);
    }

    public boolean isUseMiniMessage() {
        return useMiniMessage;
    }
}
