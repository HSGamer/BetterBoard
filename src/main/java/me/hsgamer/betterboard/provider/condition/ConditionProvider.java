package me.hsgamer.betterboard.provider.condition;

import me.hsgamer.betterboard.api.condition.Condition;
import me.hsgamer.betterboard.builder.ConditionBuilder;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.PathString;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConditionProvider {
    public static final PathString PATH = new PathString("condition");
    private final List<Condition> conditionList = new CopyOnWriteArrayList<>();

    public void loadFromObject(Object object) {
        Map<String, Object> map = new HashMap<>();
        if (object instanceof Map) {
            ((Map<?, ?>) object).forEach((key, value) -> map.put(Objects.toString(key, ""), value));
        } else {
            List<String> list = CollectionUtils.createStringListFromObject(object);
            list.forEach(s -> {
                String[] split = s.split(":", 2);
                String key = split[0].trim();
                String value = split.length > 1 ? split[1].trim() : "";
                map.put(key, value);
            });
        }
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
