package me.hsgamer.betterboard.condition;

import me.hsgamer.betterboard.api.condition.Condition;
import org.bukkit.entity.Player;

public class FirstTimeCondition implements Condition {
    @Override
    public boolean check(Player player) {
        return !player.hasPlayedBefore();
    }
}
