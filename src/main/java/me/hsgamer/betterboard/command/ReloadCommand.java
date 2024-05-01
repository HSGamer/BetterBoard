package me.hsgamer.betterboard.command;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.Permissions;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.betterboard.manager.BoardProviderManager;
import me.hsgamer.betterboard.manager.PlayerBoardManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

        PlayerBoardManager playerBoardManager = instance.get(PlayerBoardManager.class);
        BoardProviderManager boardProviderManager = instance.get(BoardProviderManager.class);
        MainConfig mainConfig = instance.get(MainConfig.class);

        playerBoardManager.clearAll();
        boardProviderManager.clearAll();
        mainConfig.reloadConfig();
        boardProviderManager.loadProviders();
        Bukkit.getOnlinePlayers().forEach(playerBoardManager::addBoard);

        MessageUtils.sendMessage(sender, "&aSuccessfully reloaded");
        return true;
    }
}
