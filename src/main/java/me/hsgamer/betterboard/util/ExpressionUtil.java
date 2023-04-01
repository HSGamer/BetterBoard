package me.hsgamer.betterboard.util;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import me.hsgamer.hscore.expression.ExpressionUtils;

import java.util.Optional;

public final class ExpressionUtil {
    private static final ExpressionConfiguration expressionConfiguration = ExpressionUtils.getDefaultExpressionConfiguration();

    private ExpressionUtil() {
        // EMPTY
    }

    public static Optional<EvaluationValue> getResult(String expression) {
        try {
            return Optional.of(new Expression(expression, expressionConfiguration).evaluate());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
