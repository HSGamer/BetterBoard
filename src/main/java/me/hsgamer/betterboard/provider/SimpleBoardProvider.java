package me.hsgamer.betterboard.provider;

import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.api.provider.ConfigurableBoardProvider;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SimpleBoardProvider implements ConfigurableBoardProvider {
    private final List<String> lines = new CopyOnWriteArrayList<>();
    private final ConditionProvider conditionProvider = new ConditionProvider();
    private String title = "";

    @Override
    public boolean canFetch(Player player) {
        return this.conditionProvider.check(player);
    }

    @Override
    public void loadFromConfig(Config config) {
        this.title = Optional.ofNullable(config.get("title")).map(String::valueOf).orElse("");
        Optional.ofNullable(config.get("lines")).map(o -> CollectionUtils.createStringListFromObject(o, false)).ifPresent(this.lines::addAll);
        this.conditionProvider.loadFromMap(config.getNormalizedValues("condition", false));
    }

    @Override
    public Optional<BoardFrame> fetch(Player player) {
        return Optional.of(new BoardFrame(
                MessageUtils.colorize(VariableManager.setVariables(title, player.getUniqueId())),
                lines.stream()
                        .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                        .map(MessageUtils::colorize)
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public void clear() {
        this.lines.clear();
        this.title = "";
        this.conditionProvider.clear();
    }
}
