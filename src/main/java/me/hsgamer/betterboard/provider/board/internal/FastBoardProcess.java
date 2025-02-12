package me.hsgamer.betterboard.provider.board.internal;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.hook.MiniPlaceholdersHook;
import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FastBoardProcess implements BoardProcess {
    private static final boolean ADVENTURE_SUPPORT;
    private static final boolean MINIMESSAGE_SUPPORT;

    static {
        boolean adventureSupport = false;
        try {
            Class.forName("net.kyori.adventure.text.Component");
            adventureSupport = true;
        } catch (ClassNotFoundException ignored) {
            // IGNORED
        }
        ADVENTURE_SUPPORT = adventureSupport;

        boolean minimeessageSupport = false;
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            minimeessageSupport = true;
        } catch (ClassNotFoundException ignored) {
            // IGNORED
        }
        MINIMESSAGE_SUPPORT = minimeessageSupport;
    }

    private final Player player;
    private final FastBoardProvider provider;
    private FastBoardOperator operator;

    public FastBoardProcess(Player player, FastBoardProvider provider) {
        this.player = player;
        this.provider = provider;
    }

    private FastBoardOperator createOperator(Player player) {
        if (ADVENTURE_SUPPORT) {
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
                private final fr.mrmicky.fastboard.adventure.FastBoard fastBoard = new fr.mrmicky.fastboard.adventure.FastBoard(player);

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
        } else {
            return new FastBoardOperator() {
                private final fr.mrmicky.fastboard.FastBoard fastBoard = new fr.mrmicky.fastboard.FastBoard(player) {
                    @Override
                    protected boolean hasLinesMaxLength() {
                        if (super.hasLinesMaxLength()) {
                            return true;
                        } else if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
                            // noinspection unchecked
                            return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(getPlayer()) < com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13.getVersion();
                        }
                        return false;
                    }
                };

                private String replace(String text) {
                    return ColorUtils.colorize(text);
                }

                @Override
                public void updateTitle(String title) {
                    fastBoard.updateTitle(replace(title));
                }

                @Override
                public void updateLines(List<String> lines) {
                    Pair<List<String>, List<String>> pair = provider.getTextAndScore(lines);
                    List<String> textLines = pair.getKey().stream().map(this::replace).collect(Collectors.toList());
                    List<String> scoreLines = pair.getValue().stream().map(score -> score == null ? null : this.replace(score)).collect(Collectors.toList());
                    fastBoard.updateLines(textLines, scoreLines);
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

    @Override
    public void stop() {
        if (operator != null && !operator.isDeleted()) {
            operator.delete();
            operator = null;
        }
    }

    @Override
    public BoardProvider getProvider() {
        return provider;
    }

    @Override
    public void update() {
        Optional<BoardFrame> optional = provider.fetch(player);
        try {
            if (optional.isPresent()) {
                BoardFrame frame = optional.get();
                if (operator == null || operator.isDeleted()) {
                    operator = createOperator(player);
                }
                operator.updateTitle(frame.getTitle());
                operator.updateLines(frame.getLines());
            } else if (operator != null) {
                if (!operator.isDeleted()) {
                    operator.delete();
                }
                operator = null;
            }
        } catch (RuntimeException ignored) {
            // IGNORED
        }
    }

    private interface FastBoardOperator {
        void updateTitle(String title);

        void updateLines(List<String> lines);

        boolean isDeleted();

        void delete();
    }
}