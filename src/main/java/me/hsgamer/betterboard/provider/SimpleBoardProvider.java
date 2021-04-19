package me.hsgamer.betterboard.provider;

import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleBoardProvider implements ConfigurableBoardProvider {
    private final List<String> lines = new ArrayList<>();
    private String title = "";

    @Override
    public void loadFromConfig(Config config) {
        this.title = Optional.ofNullable(config.get("title")).map(String::valueOf).orElse("");
        Optional.ofNullable(config.get("lines")).map(o -> CollectionUtils.createStringListFromObject(o, false)).ifPresent(this.lines::addAll);
    }

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        return Optional.of(new BoardFrame(
                MessageUtils.colorize(PlaceholderAPIHook.setPlaceholders(title, player)),
                lines.stream()
                        .map(s -> PlaceholderAPIHook.setPlaceholders(s, player))
                        .map(MessageUtils::colorize)
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public void clear() {
        this.lines.clear();
        this.title = "";
    }
}
