package org.battleplugins.virtualplayers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.battleplugins.virtualplayers.api.VirtualPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class Util {

    public static final Component HEADER = Component.text("------------------").color(NamedTextColor.GRAY)
            .append(Component.text("[").color(NamedTextColor.DARK_AQUA))
            .append(Component.text(" "))
            .append(Component.text("VirtualPlayers").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
            .append(Component.text(" "))
            .append(Component.text("]").color(NamedTextColor.DARK_AQUA))
            .append(Component.text("------------------")).color(NamedTextColor.GRAY);

    public static String toString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static Location fromString(String string) {
        String[] split = string.split(",");
        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            throw new IllegalArgumentException("World " + split[0] + " does not exist!");
        }

        return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    public static Component playerPrefix(VirtualPlayer player, @Nullable String context) {
        String formattedContext = context == null ? "" : " - " + context;
        return Component.text("[" + player.getName() + formattedContext + "] ", NamedTextColor.GOLD);
    }
}
