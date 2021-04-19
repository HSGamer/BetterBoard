package me.hsgamer.betterboard.command;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.Permissions;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ReloadCommand extends Command {
    private final BetterBoard instance;

    public ReloadCommand(BetterBoard instance) {
        super("reloadboard", "Reload the plugin", "/reloadboard", Collections.singletonList("rlboard"));
        this.instance = instance;

        setPermission(Permissions.RELOAD.getName());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        instance.getPlayerBoardManager().clearAll();
        instance.getBoardProviderManager().clearAll();
        instance.getMainConfig().reload();
        instance.getBoardProviderManager().loadProviders();
        for (Player player : Bukkit.getOnlinePlayers()) {
            instance.getPlayerBoardManager().addBoard(player);
        }
        MessageUtils.sendMessage(sender, "&aSuccessfully reloaded");
        return true;
    }
}
