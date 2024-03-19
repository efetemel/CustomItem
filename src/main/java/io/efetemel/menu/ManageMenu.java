package io.efetemel.menu;

import io.efetemel.CustomItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.atomic.AtomicInteger;

public class ManageMenu {

    public Player player;

    public ManageMenu(Player player){
        this.player = player;
    }

    public void IRMenuOpen(){
        //Envanter Oluşturma

        Inventory inventory = Bukkit.createInventory(null,54, ChatColor.BLACK+"Item Yönetimi V2");

        //Kapatma
        ItemStack targetStack = new ItemStack(Material.REDSTONE,1);
        ItemMeta itemMeta = targetStack.getItemMeta();
        itemMeta.setDisplayName("Kapat");
        targetStack.setItemMeta(itemMeta);

        AtomicInteger startInt = new AtomicInteger(0);
        CustomItemManager.customItemList
                .forEach(customItemModel -> {
                    inventory.setItem(startInt.get(), customItemModel.getItemStack());
                    startInt.getAndIncrement();

                });


        inventory.setItem(53, targetStack);

        //Oyuncuya Envanteri Açtırma
        player.openInventory(inventory);
    }

}
