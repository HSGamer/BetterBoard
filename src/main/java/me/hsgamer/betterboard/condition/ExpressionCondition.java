package me.hsgamer.betterboard.condition;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCondition implements ConfigurableCondition {
    private final ExpressionConfiguration expressionConfiguration = ExpressionUtils.getDefaultExpressionConfiguration();
    private final List<String> list = new ArrayList<>();

    @Override
    public boolean check(Player player) {
        return list.parallelStream()
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .noneMatch(s -> {
                    try {
                        return !new Expression(s, expressionConfiguration).evaluate().getBooleanValue();
                    } catch (Exception e) {
                        return true;
                    }
                });
    }

    @Override
    public void loadFromObject(Object object) {
        this.list.addAll(CollectionUtils.createStringListFromObject(object, true));
    }
}
