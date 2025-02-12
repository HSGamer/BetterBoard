package me.hsgamer.betterboard.provider.board.internal;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.hsgamer.betterboard.hook.MiniPlaceholdersHook;
import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdventureFastBoardProcess extends FastBoardProcess {
    private static final boolean MINIMESSAGE_SUPPORT;

    static {
        boolean minimeessageSupport = false;
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            minimeessageSupport = true;
        } catch (Exception ignored) {
            // IGNORED
        }
        MINIMESSAGE_SUPPORT = minimeessageSupport;
    }

    public AdventureFastBoardProcess(Player player, FastBoardProvider provider) {
        super(player, provider);
    }

    @Override
    protected FastBoardOperator createOperator(Player player) {
        Function<String, Component> componentFunction;
        if (MINIMESSAGE_SUPPORT && provider.isUseMiniMessage()) {
            componentFunction = text -> {
                if (MiniPlaceholdersHook.isAvailable()) {
                    return MiniPlaceholdersHook.toMiniComponent(player, text);
                }
                return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
            };
        } else {
            net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer serializer = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder()
                    .character(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.SECTION_CHAR)
                    .hexColors()
                    .build();
            componentFunction = text -> serializer.deserialize(ColorUtils.colorize(text));
        }

        return new FastBoardOperator() {
            private final FastBoard fastBoard = new FastBoard(player);

            @Override
            public void updateTitle(String title) {
                fastBoard.updateTitle(componentFunction.apply(title));
            }

            @Override
            public void updateLines(List<String> lines) {
                Pair<List<String>, List<String>> pair = provider.getTextAndScore(lines);
                List<Component> textComponents = pair.getKey().stream().map(componentFunction).collect(Collectors.toList());
                List<Component> scoreComponents = pair.getValue().stream().map(score -> score == null ? null : componentFunction.apply(score)).collect(Collectors.toList());
                fastBoard.updateLines(textComponents, scoreComponents);
            }

            @Override
            public boolean isDeleted() {
                return fastBoard.isDeleted();
            }

            @Override
            public void delete() {
                fastBoard.delete();
            }
        };
    }
}
