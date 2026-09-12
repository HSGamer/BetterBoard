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
            available = Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")
                    && hasMethod(MiniPlaceholders.class, "audienceGlobalPlaceholders")
                    && hasMethod(MiniMessage.class, "deserialize",
                    "java.lang.String",
                    "net.kyori.adventure.pointer.Pointered",
                    "net.kyori.adventure.text.minimessage.tag.resolver.TagResolver");
        }
        return available;
    }

    private static boolean hasMethod(Class<?> clazz, String name, String... parameterTypeNames) {
        try {
            Class<?>[] parameterTypes = new Class<?>[parameterTypeNames.length];
            for (int i = 0; i < parameterTypeNames.length; i++) {
                parameterTypes[i] = Class.forName(parameterTypeNames[i]);
            }
            clazz.getMethod(name, parameterTypes);
            return true;
        } catch (ReflectiveOperationException | SecurityException | LinkageError e) {
            return false;
        }
    }

    public static Component toMiniComponent(Player player, String message) {
        TagResolver tagResolver = MiniPlaceholders.audienceGlobalPlaceholders();
        return MiniMessage.miniMessage().deserialize(message, player, tagResolver);
    }
}
