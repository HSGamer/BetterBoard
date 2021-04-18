package me.hsgamer.betterboard.builder;

import me.hsgamer.betterboard.api.condition.Condition;
import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.betterboard.condition.ExpressionCondition;
import me.hsgamer.betterboard.condition.FirstTimeCondition;
import me.hsgamer.betterboard.condition.LevelCondition;
import me.hsgamer.betterboard.condition.PermissionCondition;
import me.hsgamer.hscore.builder.Builder;

public class ConditionBuilder extends Builder<Object, Condition> {
    public static final ConditionBuilder INSTANCE = new ConditionBuilder();

    public ConditionBuilder() {
        register(ExpressionCondition.class, "expression");
        register(o -> new FirstTimeCondition(), "first-time", "first-played", "not-played-before");
        register(LevelCondition.class, "level");
        register(PermissionCondition.class, "permission", "perms", "perm", "permissions");
    }

    public void register(Class<? extends ConfigurableCondition> clazz, String name, String... aliases) {
        register(object -> {
            try {
                ConfigurableCondition condition = clazz.newInstance();
                condition.loadFromObject(object);
                return condition;
            } catch (Exception e) {
                return null;
            }
        }, name, aliases);
    }
}
