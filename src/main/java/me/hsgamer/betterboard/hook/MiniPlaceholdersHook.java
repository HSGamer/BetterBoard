package me.hsgamer.betterboard.hook;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MiniPlaceholdersHook {
    private static Boolean available = null;

    private MiniPlaceholdersHook() {
        // EMPTY
    }

    public static boolean isAvailable() {
        if (available == null) {
            available = Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders");
        }
        return available;
    }

    public static Component toMiniComponent(Player player, String message) {
        TagResolver tagResolver = MiniPlaceholders.getGlobalPlaceholders();
        tagResolver = TagResolver.resolver(tagResolver, MiniPlaceholders.getAudiencePlaceholders(player));
        return MiniMessage.miniMessage().deserialize(message, tagResolver);
    }
}
