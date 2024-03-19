package io.efetemel.commands;

import io.efetemel.CustomItemManager;
import io.efetemel.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConfigReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        new CustomItemManager(CustomItem.instance);
        commandSender.sendMessage("Custom Items Reloaded...");
        return true;
    }
}
