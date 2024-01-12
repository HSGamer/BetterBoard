package me.hsgamer.betterboard.command;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.Permissions;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;

import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleCommand extends Command {
    private final BetterBoard instance;

    public ToggleCommand(BetterBoard instance) {
        super("toggleboard", "Enable/Disable the board", "/toggleboard", Collections.singletonList("tboard"));
        this.instance = instance;

        setPermission(Permissions.TOGGLE.getName());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cYou must be a player to do this");
            return false;
        }
        Player player = (Player) sender;
        if (instance.getPlayerBoardManager().hasBoard(player)) {
            instance.getPlayerBoardManager().removeBoard(player);
            MessageUtils.sendMessage(sender, "&aDisabled the board");
        } else {
            instance.getPlayerBoardManager().addBoard(player);
            MessageUtils.sendMessage(sender, "&aEnabled the board");
        }
        MessageUtils.sendMessage(sender, "&aSuccessfully reloaded");
        return true;
    }
}
