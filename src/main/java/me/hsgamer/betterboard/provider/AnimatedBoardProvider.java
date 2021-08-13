package me.hsgamer.betterboard.provider;

import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
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
    private final ConditionProvider conditionProvider = new ConditionProvider();
    private AnimatedString title;

    @Override
    public boolean canFetch(Player player) {
        return this.conditionProvider.check(player);
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
        this.conditionProvider.loadFromMap(config.getNormalizedValues("condition", false));
        this.title = loadAnimatedString(config.getNormalizedValues("title", false)).orElse(null);
        List<?> list = config.getInstance("lines", Collections.emptyList(), List.class);
        this.lines.addAll(
                list.stream().flatMap(o -> loadAnimatedString(o).map(Stream::of).orElse(Stream.empty())).collect(Collectors.toList())
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
        this.conditionProvider.clear();
    }

    private Optional<AnimatedString> loadAnimatedString(Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
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
        } else if (value instanceof String) {
            return Optional.of(new AnimatedString(Collections.singletonList((String) value), -1, false));
        } else {
            return Optional.empty();
        }
    }

    private static class AnimatedString extends BukkitRunnable {
        private final List<String> list;
        private int index = 0;

        private AnimatedString(List<String> list, long update, boolean async) {
            this.list = list;
            if (update >= 0) {
                if (async) {
                    runTaskTimerAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), update, update);
                } else {
                    runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), update, update);
                }
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
