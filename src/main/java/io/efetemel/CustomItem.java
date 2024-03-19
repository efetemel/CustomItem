package io.efetemel;

import io.efetemel.commands.ConfigReloadCommand;
import io.efetemel.commands.ItemRecipesCommand;
import io.efetemel.handlers.PlayerHandler;
import io.efetemel.menu.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItem extends JavaPlugin {

    public static CustomItem instance;
    @Override
    public void onLoad() {
        super.onLoad();
        //ItemManager.init();
        new CustomItemManager(this);
        getLogger().info("Plugin loading!");
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Plugin started!");
        getServer().getPluginManager().registerEvents(new MenuListener(),this);
        getCommand("ir").setExecutor(new ItemRecipesCommand());
        getCommand("customreload").setExecutor(new ConfigReloadCommand());
        new PlayerHandler(this);
        super.onEnable();
    }


}
