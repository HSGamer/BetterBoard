package me.hsgamer.betterboard.provider.board;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.betterboard.provider.board.internal.FastBoardProcess;
import me.hsgamer.betterboard.provider.condition.ConditionProvider;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class FastBoardProvider implements ConfigurableBoardProvider {
    public static final PathString TITLE_PATH = new PathString("title");
    public static final PathString LINES_PATH = new PathString("lines");

    private final ConditionProvider conditionProvider = new ConditionProvider();

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
        this.conditionProvider.loadFromObject(config.getNormalized(ConditionProvider.PATH, ""));
    }

    @Override
    public BoardProcess createProcess(Player player) {
        return new FastBoardProcess(player, this);
    }
}
