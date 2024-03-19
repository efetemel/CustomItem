package io.efetemel.commands;

import io.efetemel.menu.ManageMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemRecipesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Sadece oyuncular kullanabilir!");
            return true;
        }

        ManageMenu menu = new ManageMenu(((Player) commandSender).getPlayer());
        menu.IRMenuOpen();
        return true;
    }
}
