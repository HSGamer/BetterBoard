package me.hsgamer.betterboard.provider;

import me.hsgamer.betterboard.api.condition.Condition;
import me.hsgamer.betterboard.builder.ConditionBuilder;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConditionalBoardProvider extends SimpleBoardProvider {
    private final List<Condition> conditions = new CopyOnWriteArrayList<>();

    @Override
    public boolean canFetch(Player player) {
        return this.conditions.parallelStream().allMatch(condition -> condition.check(player));
    }

    @Override
    public void loadFromConfig(Config config) {
        super.loadFromConfig(config);
        this.conditions.addAll(ConditionBuilder.INSTANCE.build(config.getNormalizedValues("condition", false)).values());
    }

    @Override
    public void clear() {
        super.clear();
        this.conditions.forEach(Condition::clear);
        this.conditions.clear();
    }
}
