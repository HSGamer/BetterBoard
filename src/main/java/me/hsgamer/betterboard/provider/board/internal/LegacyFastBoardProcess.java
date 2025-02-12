package me.hsgamer.betterboard.provider.board.internal;

import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LegacyFastBoardProcess extends FastBoardProcess {
    public LegacyFastBoardProcess(Player player, FastBoardProvider provider) {
        super(player, provider);
    }

    @Override
    protected FastBoardOperator createOperator(Player player) {
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
