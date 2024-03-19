package io.efetemel;

import io.efetemel.models.CustomItemModel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class CustomItemManager {

    private FileConfiguration config;
    public static List<CustomItemModel> customItemList = new ArrayList<>();

    private CustomItem testPlugin;
    public CustomItemManager(CustomItem customItem) {
        this.testPlugin = customItem;
        File dataFolder = customItem.getDataFolder(); // Veri klasörünü al

        // config.yml dosyasının yolu
        File configFile = new File(dataFolder, "config.yml");

        // Dosyanın varlığını kontrol et ve gerekirse oluştur
        if (!configFile.exists()) {
            try {
                dataFolder.mkdirs(); // Eğer dosya yoksa, ebeveyn dizinleri oluşturulur
                configFile.createNewFile(); // Yeni dosya oluşturulur
                saveDefaultConfig(); // Dosyaya varsayılan yapıyı kaydet
            } catch (IOException e) {
                throw new RuntimeException("Error creating config file", e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        initializeItems(); // Eşyaları oku ve listeye ekle
    }

    public void saveDefaultConfig() {
        // Varsayılan yapıyı kaydetme işlemleri buraya gelecek
    }

    private void initializeItems() {
        Bukkit.resetRecipes();
        customItemList.clear();
        ConfigurationSection itemsSection = config.getConfigurationSection("new_items");

        if (itemsSection != null) {

            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);

                String itemName = itemSection.getString("display_name");
                String shortName = itemSection.getString("short_name");
                boolean effectsRandom = itemSection.getBoolean("effects_random");

                List<String> loreList = itemSection.getStringList("lore");
                ConfigurationSection craftTypeSection = itemSection.getConfigurationSection("craft_type");
                ConfigurationSection craftItemsSection = itemSection.getConfigurationSection("craft_items");
                ConfigurationSection effectsSection = itemSection.getConfigurationSection("effects");

                String itemId = itemSection.getString("item_id");
                //String effectName = itemSection.getString("effect_name");
                //int effectDuration = itemSection.getInt("effect_duration");
                boolean customCraftItem = itemSection.getBoolean("custom_craft_item");
                int customModelData = itemSection.getInt("custom_model_data");

                // ItemStack oluştur
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemId));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(itemName);

                // Lore ekleyin (birden fazla lore için)
                List<String> formattedLoreList = new ArrayList<>(loreList);
                itemMeta.setLore(formattedLoreList);

                // Diğer özellikler (Custom Model Data, efektler vs.) buraya ekleyebilirsiniz
                itemMeta.setCustomModelData(customModelData);
                itemStack.setItemMeta(itemMeta);

                // CustomItemModel oluştur ve listeye ekle
                CustomItemModel customItem = new CustomItemModel(
                        customCraftItem,
                        effectsSection,
                        itemStack,
                        craftTypeSection,
                        craftItemsSection,
                        shortName,
                        effectsRandom
                );

                // ShapedRecipe oluştur ve ekle


                if(!customCraftItem){
                    // Sembolleri eşleştir
                    ShapedRecipe customItemRecipe = new ShapedRecipe(new NamespacedKey(testPlugin, customItem.getShort_name()), itemStack);

                    String[] shape = new String[3];
                    shape[0] = craftTypeSection.getString("top_left") + craftTypeSection.getString("top_center") + craftTypeSection.getString("top_right");
                    shape[1] = craftTypeSection.getString("center_left") + craftTypeSection.getString("center_center") + craftTypeSection.getString("center_right");
                    shape[2] = craftTypeSection.getString("bottom_left") + craftTypeSection.getString("bottom_center") + craftTypeSection.getString("bottom_right");
                    customItemRecipe.shape(shape);

                    for (String symbol : craftItemsSection.getKeys(false)) {
                        char symbolChar = symbol.charAt(0);
                        Material material = Material.getMaterial(craftItemsSection.getString(symbol));
                        if (material != null) {
                            customItemRecipe.setIngredient(symbolChar, material);
                        } else {
                            getLogger().warning("Geçersiz malzeme belirtildi: " + symbol);
                        }
                    }

                    Bukkit.addRecipe(customItemRecipe);
                    Bukkit.updateRecipes();

                }
                customItemList.add(customItem);


            }

            // customCraftItem Initialize




        }

        CustomCraftInitialize();
    }

    //bura çalışmıyor hiç log a girmiyor
    private void CustomCraftInitialize(){
        CustomItemManager.customItemList.stream()
                .filter(CustomItemModel::isCustom_craft_item)
                .forEach(customItemModel -> {

                    ShapedRecipe customItemRecipe = new ShapedRecipe(new NamespacedKey(testPlugin, customItemModel.getShort_name()), customItemModel.getItemStack());

                    String[] shape = new String[3];
                    shape[0] = customItemModel.getCraft_type().getString("top_left") + customItemModel.getCraft_type().getString("top_center") + customItemModel.getCraft_type().getString("top_right");
                    shape[1] = customItemModel.getCraft_type().getString("center_left") + customItemModel.getCraft_type().getString("center_center") + customItemModel.getCraft_type().getString("center_right");
                    shape[2] = customItemModel.getCraft_type().getString("bottom_left") + customItemModel.getCraft_type().getString("bottom_center") + customItemModel.getCraft_type().getString("bottom_right");
                    customItemRecipe.shape(shape);

                    for (String symbol : customItemModel.getCraft_items().getKeys(false)) {
                        String x = customItemModel.getCraft_items().getString(symbol);
                        if (x.startsWith(".")) {
                            String processedStr = x.substring(1);

                            CustomItemModel m1 = CustomItemManager.customItemList.stream()
                                    .filter(customItemModel1 -> customItemModel1.getShort_name().equals(processedStr))
                                    .findFirst()
                                    .orElse(null);

                            if (m1 != null) {
                                char symbolChar = symbol.charAt(0);

                                ShapedRecipe aa = (ShapedRecipe) Bukkit.getRecipe(new NamespacedKey(testPlugin, m1.getShort_name()));

                                customItemRecipe.setIngredient(symbolChar,aa.getResult());
                            } else {
                                getLogger().warning("Geçersiz custom craft öğesi belirtildi: " + symbol);
                            }
                        } else {
                            char symbolChar = symbol.charAt(0);
                            Material material = Material.getMaterial(customItemModel.getCraft_items().getString(symbol));
                            if (material != null) {
                                customItemRecipe.setIngredient(symbolChar, material);
                            } else {
                                getLogger().warning("Geçersiz malzeme belirtildi: " + symbol);
                            }
                        }
                    }

                    Bukkit.addRecipe(customItemRecipe);
                    Bukkit.updateRecipes();
                });
    }

}