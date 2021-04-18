package me.hsgamer.betterboard.condition;

import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.betterboard.hook.PlaceholderAPIHook;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;

public class LevelCondition implements ConfigurableCondition {
    private String value = "0";

    public Integer getParsedValue(Player player) {
        String parsed = PlaceholderAPIHook.setPlaceholders(String.valueOf(value).trim(), player);
        return Optional.ofNullable(ExpressionUtils.getResult(parsed)).map(BigDecimal::intValue).orElse(0);
    }

    @Override
    public boolean check(Player player) {
        int levels = getParsedValue(player);
        return levels <= 0 || player.getLevel() >= levels;
    }

    @Override
    public void loadFromObject(Object object) {
        this.value = String.valueOf(object);
    }
}
