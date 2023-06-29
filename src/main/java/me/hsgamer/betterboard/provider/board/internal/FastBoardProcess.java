package me.hsgamer.betterboard.provider.board.internal;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
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
            if (MINIMESSAGE_SUPPORT && provider.isUseMiniMessage()) {
                return new FastBoardOperator() {
                    private final fr.mrmicky.fastboard.adventure.FastBoard fastBoard = new fr.mrmicky.fastboard.adventure.FastBoard(player);

                    @Override
                    public void updateTitle(String title) {
                        fastBoard.updateTitle(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(title));
                    }

                    @Override
                    public void updateLines(List<String> lines) {
                        fastBoard.updateLines(lines.stream()
                                .map(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()::deserialize)
                                .collect(Collectors.toList()));
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
                    private final fr.mrmicky.fastboard.adventure.FastBoard fastBoard = new fr.mrmicky.fastboard.adventure.FastBoard(player);
                    private final net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer serializer = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder()
                            .character(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.SECTION_CHAR)
                            .hexColors()
                            .build();

                    private net.kyori.adventure.text.Component toComponent(String text) {
                        return serializer.deserialize(ColorUtils.colorize(text));
                    }

                    @Override
                    public void updateTitle(String title) {
                        fastBoard.updateTitle(toComponent(title));
                    }

                    @Override
                    public void updateLines(List<String> lines) {
                        fastBoard.updateLines(lines.stream().map(this::toComponent).collect(Collectors.toList()));
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

                @Override
                public void updateTitle(String title) {
                    fastBoard.updateTitle(ColorUtils.colorize(title));
                }

                @Override
                public void updateLines(List<String> lines) {
                    fastBoard.updateLines(lines.stream().map(ColorUtils::colorize).collect(Collectors.toList()));
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