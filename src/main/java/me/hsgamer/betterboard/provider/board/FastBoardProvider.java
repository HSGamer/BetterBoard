package me.hsgamer.betterboard.provider.board;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.betterboard.provider.board.internal.FastBoardProcess;
import me.hsgamer.betterboard.provider.condition.ConditionProvider;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class FastBoardProvider implements ConfigurableBoardProvider {
    public static final String TITLE_PATH = "title";
    public static final String LINES_PATH = "lines";
    private static final String USE_MINIMESSAGE_PATH = "use-minimessage";
    private static final String SCORE_SEPARATOR = "score-separator";

    private final ConditionProvider conditionProvider = new ConditionProvider();
    private boolean useMiniMessage = false;
    private String scoreSeparator = "";

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
        this.scoreSeparator = Optional.ofNullable(config.getNormalized(SCORE_SEPARATOR))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public BoardProcess createProcess(Player player) {
        return new FastBoardProcess(player, this);
    }

    public boolean isUseMiniMessage() {
        return useMiniMessage;
    }

    public String getScoreSeparator() {
        return scoreSeparator;
    }

    public Pair<String, String> getTextAndScore(String value) {
        if (!scoreSeparator.isEmpty() && value.contains(scoreSeparator)) {
            String[] split = value.split(Pattern.quote(scoreSeparator), 2);
            return Pair.of(split[0], split.length > 1 ? split[1] : null);
        } else {
            return Pair.of(value, null);
        }
    }

    public Pair<List<String>, List<String>> getTextAndScore(List<String> values) {
        return values.stream()
                .map(this::getTextAndScore)
                .collect(
                        () -> Pair.of(new ArrayList<>(), new ArrayList<>()),
                        (pair, textPair) -> {
                            pair.getKey().add(textPair.getKey());
                            pair.getValue().add(textPair.getValue());
                        },
                        (pair1, pair2) -> {
                            pair1.getKey().addAll(pair2.getKey());
                            pair1.getValue().addAll(pair2.getValue());
                        }
                );
    }
}
