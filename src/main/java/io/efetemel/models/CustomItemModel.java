package io.efetemel.models;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItemModel {
    private boolean custom_craft_item;
    private boolean random_effects;
    private ConfigurationSection effects;
    private String short_name;
    private ItemStack itemStack;
    private ConfigurationSection craft_type;
    private ConfigurationSection craft_items;

    public CustomItemModel(boolean custom_craft_item, ConfigurationSection effects,ItemStack itemStack,ConfigurationSection craft_type,ConfigurationSection craft_items,String short_name,boolean random_effects) {
        this.custom_craft_item = custom_craft_item;
        this.effects = effects;
        this.itemStack = itemStack;
        this.craft_type = craft_type;
        this.craft_items = craft_items;
        this.short_name = short_name;
        this.random_effects = random_effects;
    }

    public boolean isRandom_effects() {
        return random_effects;
    }

    public void setRandom_effects(boolean random_effects) {
        this.random_effects = random_effects;
    }

    public boolean isCustom_craft_item() {
        return custom_craft_item;
    }

    public void setCustom_craft_item(boolean custom_craft_item) {
        this.custom_craft_item = custom_craft_item;
    }


    public ConfigurationSection getEffects() {
        return effects;
    }

    public void setEffects(ConfigurationSection effects) {
        this.effects = effects;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ConfigurationSection getCraft_type() {
        return craft_type;
    }

    public void setCraft_type(ConfigurationSection craft_type) {
        this.craft_type = craft_type;
    }

    public ConfigurationSection getCraft_items() {
        return craft_items;
    }

    public void setCraft_items(ConfigurationSection craft_items) {
        this.craft_items = craft_items;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
}
