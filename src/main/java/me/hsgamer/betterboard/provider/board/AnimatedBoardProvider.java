package me.hsgamer.betterboard.provider.board;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.scheduler.Scheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AnimatedBoardProvider extends FastBoardProvider {
    private final List<AnimatedString> lines = new CopyOnWriteArrayList<>();
    private AnimatedString title;

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        String fetchedTitle = Optional.ofNullable(title)
                .map(AnimatedString::getString)
                .map(s -> VariableManager.GLOBAL.setVariables(s, player.getUniqueId()))
                .orElse("");
        List<String> fetchedLines = lines.stream()
                .map(AnimatedString::getString)
                .map(s -> VariableManager.GLOBAL.setVariables(s, player.getUniqueId()))
                .collect(Collectors.toList());
        return Optional.of(new BoardFrame(fetchedTitle, fetchedLines));
    }

    @Override
    public void loadFromConfig(Config config) {
        super.loadFromConfig(config);
        this.title = loadAnimatedString(config.getNormalized(FastBoardProvider.TITLE_PATH, false)).orElse(null);
        List<?> list = config.getInstance(FastBoardProvider.LINES_PATH, Collections.emptyList(), List.class);
        this.lines.addAll(list.stream().flatMap(o -> loadAnimatedString(o).stream()).collect(Collectors.toList()));
    }

    @Override
    public void clear() {
        super.clear();
        this.lines.forEach(AnimatedString::cancel);
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

    private static class AnimatedString implements Runnable {
        private final List<String> list;
        private int index = 0;
        private Task task;

        private AnimatedString(List<String> list, long update, boolean async) {
            this.list = list;
            boolean isSchedule = update >= 0;
            if (isSchedule) {
                JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
                Scheduler scheduler = async ? AsyncScheduler.get(plugin) : GlobalScheduler.get(plugin);
                task = scheduler.runTimer(this, update, update);
            }
        }

        @Override
        public void run() {
            this.index = (index + 1) % list.size();
        }

        public void cancel() throws IllegalStateException {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }

        private String getString() {
            return list.get(index);
        }
    }
}
