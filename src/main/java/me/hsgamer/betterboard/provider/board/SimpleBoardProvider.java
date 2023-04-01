package me.hsgamer.betterboard.provider.board;

import me.hsgamer.betterboard.provider.board.internal.BoardFrame;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SimpleBoardProvider extends FastBoardProvider {
    private final List<String> lines = new CopyOnWriteArrayList<>();
    private String title = "";

    @Override
    public void loadFromConfig(Config config) {
        super.loadFromConfig(config);
        this.title = Optional.ofNullable(config.get("title")).map(String::valueOf).orElse("");
        Optional.ofNullable(config.get("lines")).map(o -> CollectionUtils.createStringListFromObject(o, false)).ifPresent(this.lines::addAll);
    }

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        return Optional.of(new BoardFrame(
                ColorUtils.colorize(VariableManager.setVariables(title, player.getUniqueId())),
                lines.stream()
                        .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                        .map(ColorUtils::colorize)
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public void clear() {
        super.clear();
        this.lines.clear();
        this.title = "";
    }
}
