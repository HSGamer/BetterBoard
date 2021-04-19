package me.hsgamer.betterboard.provider;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.api.condition.Condition;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.builder.ConditionBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimatedBoardProvider implements ConfigurableBoardProvider {
    private final List<AnimatedString> lines = new CopyOnWriteArrayList<>();
    private final List<Condition> conditions = new CopyOnWriteArrayList<>();
    private AnimatedString title;

    @Override
    public boolean canFetch(Player player) {
        return this.conditions.parallelStream().allMatch(condition -> condition.check(player));
    }

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        String fetchedTitle = Optional.ofNullable(title)
                .map(AnimatedString::getString)
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .map(MessageUtils::colorize)
                .orElse("");
        List<String> fetchedLines = lines.stream()
                .map(AnimatedString::getString)
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .map(MessageUtils::colorize)
                .collect(Collectors.toList());
        return Optional.of(new BoardFrame(fetchedTitle, fetchedLines));
    }

    @Override
    public void loadFromConfig(Config config) {
        this.conditions.addAll(ConditionBuilder.INSTANCE.build(config.getNormalizedValues("condition", false)).values());

        this.title = loadAnimatedString(config.getNormalizedValues("title", false)).orElse(null);
        this.lines.addAll(
                config.getNormalizedValues("lines", false).values().stream()
                        .filter(o -> o instanceof Map)
                        .map(o -> (Map<String, Object>) o)
                        .flatMap(map -> loadAnimatedString(map).map(Stream::of).orElse(Stream.empty()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void clear() {
        this.lines.forEach(line -> {
            if (!line.isCancelled()) {
                line.cancel();
            }
        });
        this.lines.clear();
        if (title != null && !title.isCancelled()) {
            title.cancel();
        }
        this.title = null;
        this.conditions.forEach(Condition::clear);
        this.conditions.clear();
    }

    private Optional<AnimatedString> loadAnimatedString(Map<String, Object> map) {
        List<String> list = Optional.ofNullable(map.get("list"))
                .map(o -> CollectionUtils.createStringListFromObject(o, false))
                .orElse(Collections.singletonList(""));
        if (list.isEmpty()) {
            return Optional.empty();
        }

        long update = Optional.ofNullable(map.get("update"))
                .map(String::valueOf)
                .map(Long::parseLong)
                .map(l -> Math.max(l, 0L))
                .orElse(0L);
        boolean async = Optional.ofNullable(map.get("async"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);

        return Optional.of(new AnimatedString(list, update, async));
    }

    private static class AnimatedString extends BukkitRunnable {
        private final List<String> list;
        private int index = 0;

        private AnimatedString(List<String> list, long update, boolean async) {
            this.list = list;
            update = Math.max(update, 0);
            if (async) {
                runTaskTimerAsynchronously(BetterBoard.getInstance(), update, update);
            } else {
                runTaskTimer(BetterBoard.getInstance(), update, update);
            }
        }

        @Override
        public void run() {
            this.index = (index + 1) % list.size();
        }

        private String getString() {
            return list.get(index);
        }
    }
}
