package me.hsgamer.betterboard.provider.condition;

import me.hsgamer.betterboard.api.condition.Condition;
import me.hsgamer.betterboard.builder.ConditionBuilder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConditionProvider {
    private final List<Condition> conditionList = new CopyOnWriteArrayList<>();

    public void loadFromMap(Map<String, Object> map) {
        this.conditionList.addAll(ConditionBuilder.INSTANCE.build(map).values());
    }

    public boolean check(Player player) {
        return this.conditionList.parallelStream().allMatch(condition -> condition.check(player));
    }

    public void clear() {
        this.conditionList.forEach(Condition::clear);
        this.conditionList.clear();
    }
}
