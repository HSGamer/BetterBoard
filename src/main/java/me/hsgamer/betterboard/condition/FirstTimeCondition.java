package me.hsgamer.betterboard.condition;

import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import org.bukkit.entity.Player;

public class FirstTimeCondition implements ConfigurableCondition {
    private boolean checkValue = true;

    @Override
    public boolean check(Player player) {
        return (!player.hasPlayedBefore()) == checkValue;
    }

    @Override
    public void loadFromObject(Object object) {
        this.checkValue = Boolean.parseBoolean(String.valueOf(object));
    }
}
