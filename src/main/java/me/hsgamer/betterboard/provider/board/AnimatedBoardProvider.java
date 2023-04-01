package me.hsgamer.betterboard.provider.board;

import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
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

public class AnimatedBoardProvider extends FastBoardProvider {
    private final List<AnimatedString> lines = new CopyOnWriteArrayList<>();
    private AnimatedString title;

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        String fetchedTitle = Optional.ofNullable(title)
                .map(AnimatedString::getString)
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .map(ColorUtils::colorize)
                .orElse("");
        List<String> fetchedLines = lines.stream()
                .map(AnimatedString::getString)
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .map(ColorUtils::colorize)
                .collect(Collectors.toList());
        return Optional.of(new BoardFrame(fetchedTitle, fetchedLines));
    }

    @Override
    public void loadFromConfig(Config config) {
        super.loadFromConfig(config);
        this.title = loadAnimatedString(config.getNormalizedValues("title", false)).orElse(null);
        List<?> list = config.getInstance("lines", Collections.emptyList(), List.class);
        this.lines.addAll(
                list.stream().flatMap(o -> loadAnimatedString(o).map(Stream::of).orElse(Stream.empty())).collect(Collectors.toList())
        );
    }

    @Override
    public void clear() {
        super.clear();
        this.lines.forEach(BukkitRunnable::cancel);
        this.lines.clear();
        if (title != null) {
            title.cancel();
        }
        this.title = null;
    }

    private Optional<AnimatedString> loadAnimatedString(Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            List<String> list = Optional.ofNullable(map.get("list"))
                    .map(o -> CollectionUtils.createStringListFromObject(o, false))
                    .orElse(Collections.emptyList());
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
        private final boolean isSchedule;
        private int index = 0;

        private AnimatedString(List<String> list, long update, boolean async) {
            this.list = list;
            this.isSchedule = update >= 0;
            if (isSchedule) {
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

        @Override
        public synchronized void cancel() throws IllegalStateException {
            if (isSchedule) {
                super.cancel();
            }
        }

        private String getString() {
            return list.get(index);
        }
    }
}
