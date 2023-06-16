package me.hsgamer.betterboard.condition;

import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.betterboard.util.ExpressionUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCondition implements ConfigurableCondition {
    private final List<String> list = new ArrayList<>();

    @Override
    public boolean check(Player player) {
        return list.parallelStream()
                .map(s -> VariableManager.GLOBAL.setVariables(s, player.getUniqueId()))
                .noneMatch(s -> ExpressionUtil.getResult(s).map(evaluationValue -> !evaluationValue.getBooleanValue()).orElse(true));
    }

    @Override
    public void loadFromObject(Object object) {
        this.list.addAll(CollectionUtils.createStringListFromObject(object, true));
    }
}
