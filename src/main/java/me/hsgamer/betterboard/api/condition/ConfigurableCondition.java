package me.hsgamer.betterboard.api.condition;

public interface ConfigurableCondition extends Condition {
    void loadFromObject(Object object);
}
