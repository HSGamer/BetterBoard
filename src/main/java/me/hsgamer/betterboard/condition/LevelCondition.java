package me.hsgamer.betterboard.condition;

import com.ezylang.evalex.data.EvaluationValue;
import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.betterboard.util.ExpressionUtil;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class LevelCondition implements ConfigurableCondition {
    private String value = "0";

    public Integer getParsedValue(Player player) {
        String parsed = VariableManager.setVariables(String.valueOf(value).trim(), player.getUniqueId());
        return ExpressionUtil.getResult(parsed).map(EvaluationValue::getNumberValue).map(BigDecimal::intValue).orElse(0);
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
