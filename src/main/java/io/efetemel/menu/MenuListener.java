package io.efetemel.menu;

import io.efetemel.CustomItemManager;
import io.efetemel.models.CustomItemModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryTitle = ChatColor.stripColor(event.getView().getTitle());

        if (inventoryTitle.equals("Item Yönetimi V2")) {
            handleItemManagement(event);
        } else if(inventoryTitle.contains("Yapımı")) {
            handleCrafting(event);
        }

    }
    private void handleItemManagement(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        ItemMeta currentItemMeta = currentItem.getItemMeta();
        if(currentItemMeta.getDisplayName().equals("Kapat")){
            event.getWhoClicked().closeInventory();
        }
        else{
            CustomItemModel customItem = CustomItemManager.customItemList.stream().filter(customItemModel -> customItemModel.getItemStack().getItemMeta().displayName().equals(currentItemMeta.displayName())).findFirst().get();
            createCustomItemCraft(event,customItem);
        }
    }
    private void handleCrafting(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.REDSTONE) {
            return;
        }
        event.getWhoClicked().closeInventory();
        new ManageMenu((Player) event.getWhoClicked()).IRMenuOpen();
    }

    private void createCustomItemCraft(InventoryClickEvent event,CustomItemModel customItemModel){
        Inventory customCraftInventory = Bukkit.createInventory(null, 27, ChatColor.BLACK + customItemModel.getItemStack().getItemMeta().getDisplayName()+" Yapımı");

        ItemStack targetStack = new ItemStack(Material.REDSTONE,1);
        ItemMeta itemMeta = targetStack.getItemMeta();
        itemMeta.setDisplayName("Geri Dön");
        targetStack.setItemMeta(itemMeta);

        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1);
        ItemMeta itemMetaGlass = glass.getItemMeta();
        itemMetaGlass.setDisplayName(" ");
        glass.setItemMeta(itemMetaGlass);

        customCraftInventory.setItem(0, glass);
        customCraftInventory.setItem(1, glass);
        customCraftInventory.setItem(2, glass);
        customCraftInventory.setItem(6, glass);
        customCraftInventory.setItem(7, glass);
        customCraftInventory.setItem(8, glass);
        customCraftInventory.setItem(9, glass);
        customCraftInventory.setItem(10, glass);
        customCraftInventory.setItem(11, glass);
        customCraftInventory.setItem(15, glass);
        customCraftInventory.setItem(16, glass);
        customCraftInventory.setItem(17, glass);
        customCraftInventory.setItem(18, glass);
        customCraftInventory.setItem(19, glass);
        customCraftInventory.setItem(20, glass);
        customCraftInventory.setItem(24, glass);
        customCraftInventory.setItem(25, glass);



        // Craft type bilgisini al
        Map<String, String> craftType = new HashMap<>();
        for (String key : customItemModel.getCraft_type().getKeys(false)) {
            craftType.put(key, customItemModel.getCraft_type().getString(key));
        }

        // Craft items bilgisini al
        Map<String, String> craftItems = new HashMap<>();
        for (String key : customItemModel.getCraft_items().getKeys(false)) {
            craftItems.put(key, customItemModel.getCraft_items().getString(key));
        }

        // Craft type'a göre envantere ekleme yap
        for (Map.Entry<String, String> entry : craftType.entrySet()) {
            String slotKey = entry.getKey();
            String itemKey = entry.getValue();

            // Slotları belirleme
            int slot = getCraftingSlot(slotKey);

            // Eğer slot null değilse ve itemKey de null değilse
            if (slot != -1 && itemKey != null) {
                // "N" değeri için null kontrolü yap
                String itemID;
                if ("N".equals(itemKey)) {
                    itemID = null;
                } else {
                    itemID = craftItems.get(itemKey);
                }

                if (itemID != null) {
                    if(itemID.contains(".")){
                        CustomItemModel a = CustomItemManager.customItemList.stream().filter(customItemModel1 -> customItemModel1.getShort_name().equals(itemID.substring(1))).findFirst().get();
                        customCraftInventory.setItem(slot, a.getItemStack());

                    }
                    else{
                        Material material = Material.getMaterial(itemID);
                        if (material != null) {
                            ItemStack itemStack = new ItemStack(material, 1);
                            customCraftInventory.setItem(slot, itemStack);
                        } else {
                            getLogger().warning("Geçersiz malzeme belirtildi: " + itemID);
                        }
                    }

                } else {
                    getLogger().warning("Craft item bulunamadı: " + itemKey);
                }
            } else {
                getLogger().warning("Geçersiz slot veya item: " + slotKey + ", " + itemKey);
            }
        }



        // Set "Geri Dön" item to the last slot
        customCraftInventory.setItem(26, targetStack);

        // Close current inventory and open custom crafting inventory
        event.getWhoClicked().closeInventory();
        event.getWhoClicked().openInventory(customCraftInventory);

    }

    private int getCraftingSlot(String key) {
        return switch (key) {
            case "top_left" -> 3;
            case "top_center" -> 4;
            case "top_right" -> 5;
            case "center_left" -> 12;
            case "center_center" -> 13;
            case "center_right" -> 14;
            case "bottom_left" -> 21;
            case "bottom_center" -> 22;
            case "bottom_right" -> 23;
            default -> -1;
        };
    }
}
