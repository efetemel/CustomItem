package io.efetemel.handlers;

import io.efetemel.CustomItemManager;
import io.efetemel.CustomItem;
import io.efetemel.models.CustomItemModel;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerHandler implements Listener {


    private long cooldownTime = 7 * 1000;
    private Map<UUID, Long> cooldowns = new HashMap<>();

    public PlayerHandler(CustomItem plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        //event.getPlayer().setResourcePack("https://drive.usercontent.google.com/download?id=1Dg4OVfiRbfhP7zlAdx7npocBO47GD7u1&export=download");

    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();

        if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        CustomItemManager.customItemList.stream()
                .filter(customItemModel -> event.getItem().getItemMeta().getDisplayName().equals(customItemModel.getItemStack().getItemMeta().getDisplayName()))
                .forEach(customItemModel -> {
                    ConfigurationSection effectsSection = customItemModel.getEffects();
                    if (effectsSection != null) {
                        if (customItemModel.isRandom_effects()) {
                            Random random = new Random();
                            String randomEffectName = effectsSection.getKeys(false).toArray()[random.nextInt(effectsSection.getKeys(false).size())].toString();
                            int duration = effectsSection.getInt(randomEffectName);
                            PotionEffectType randomEffectType = PotionEffectType.getByName(randomEffectName);
                            if (randomEffectType != null) {
                                player.addPotionEffect(new PotionEffect(randomEffectType, duration * 20, 0));
                                //player.sendMessage(ChatColor.GREEN + "Rastgele bir efekt aldınız!");
                            }
                        } else {
                            for (String effectName : effectsSection.getKeys(false)) {
                                int effectDuration = effectsSection.getInt(effectName);
                                PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
                                if (potionEffectType != null) {
                                    player.addPotionEffect(new PotionEffect(potionEffectType, effectDuration * 20, 0));
                                }
                            }
                        }
                    } else {
                        // Hata işleme veya bildirim kodları eklenebilir
                    }
                });

    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!event.hasItem()|| !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if(event.getAction().name().contains("RIGHT_CLICK")){

            if(event.hasItem() && event.getItem().hasItemMeta()){
                CustomItemModel itemModel = CustomItemManager.customItemList.stream().filter(customItemModel -> customItemModel.getItemStack().getItemMeta().displayName().equals(event.getItem().getItemMeta().displayName())).findAny().get();

                if(itemModel.getShort_name().contains("sword")){
                    if (checkCooldown(player)) {
                        Location entityLocation = player.getLocation();
                        createDamageToEntities(entityLocation,event.getPlayer(),event.getItem());

                    } else {
                        long x = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(ChatColor.RED+String.valueOf(x)+" Saniye sonra kullanabilirsin.");
                    }
                }
                else if(itemModel.getEffects() != null&& !itemModel.getItemStack().getType().isEdible() && !itemModel.getShort_name().contains("undying")){
                    player.sendMessage(itemModel.getShort_name());
                    for (String effectName : itemModel.getEffects().getKeys(false)) {

                        int effectDuration = itemModel.getEffects().getInt(effectName);

                        PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);

                        if (potionEffectType != null) {
                            //player.sendMessage("Yediğin Item: "+customItemModel.getShort_name() + " Effect Adı: "+effectName + " Effect Süresi: "+effectDuration);
                            player.addPotionEffect(new PotionEffect(potionEffectType, effectDuration * 20, 0));
                        }
                    }

                    if(getRemainingUses(event.getItem()) > 0){

                        if(itemModel.getShort_name().contains("cigaratte")){
                            Location location = player.getLocation().add(0, 1, 0);
                            player.spawnParticle(Particle.SMOKE_NORMAL, location, 1000);

                        }

                        int x = getRemainingUses(event.getItem()) -1;

                        if(x > 0){
                            updateLore(event.getItem(),x);
                        }
                        else{
                            player.getInventory().removeItem(event.getItem());
                        }

                    }
                }
            }
        }

    }

    private void createDamageToEntities(Location location,Player player,ItemStack clickedItem) {
        // Belirli bir yarıçap içindeki varlıkları al
        double radius = 3; // Etkileşim yarıçapı
        boolean foundEntities = false;
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if(livingEntity != player){
                    foundEntities = true;
                    double previousHealth = livingEntity.getHealth();
                    livingEntity.damage(6); // 3 kalp hasar (1 kalp = 2 hasar)
                    double newHealth = livingEntity.getHealth();
                    if (newHealth < previousHealth) {
                        // Hasar alırken titreme efekti ekle
                        livingEntity.getWorld().strikeLightningEffect(livingEntity.getLocation()); // Hasar aldığı zaman ışık çakma efekti ekle
                        // Geriye savrulma efekti uygula
                        @NotNull Vector direction = livingEntity.getLocation().toVector().subtract(location.toVector()).normalize();
                        direction.setY(0.3); // Y ekseni için bir miktar yukarıya doğru kuvvet ekle (isteğe bağlı)
                        livingEntity.setVelocity(direction.multiply(1.5)); // Hedefe doğru bir yönde savur
                    }
                }

            }
        }
        if (clickedItem.getDurability() < clickedItem.getType().getMaxDurability() && foundEntities) {
            // Eşyanın kullanım süresini azalt
            setCooldown(player);
            if(clickedItem.getDurability() + 6 < clickedItem.getType().getMaxDurability()){
                clickedItem.setDurability((short) (clickedItem.getDurability() + 6));
            }
            else{
                player.getInventory().removeItem(clickedItem);
            }
        }
    }

    private boolean checkCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            long cooldownTimeLeft = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            if (cooldownTimeLeft > 0) {
                // Cooldown devam ediyor
                return false;
            }
        }
        // Cooldown tamamlandı veya ilk kullanım
        return true;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownTime);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta() || !itemInHand.getItemMeta().hasDisplayName()) {
            return;
        }

        Optional<CustomItemModel> cItem = CustomItemManager.customItemList.stream().filter(customItemModel -> customItemModel.getItemStack().getItemMeta().displayName().equals(itemInHand.getItemMeta().displayName())).findAny();

        if(cItem.isPresent() && cItem.get().getShort_name().contains("axe") &&  !player.isSneaking() && isTreeBlock(block.getType())){
            Location blockLocation = block.getLocation();
            breakTree(blockLocation,player);
        }
        else if(cItem.isPresent() && cItem.get().getShort_name().contains("pickaxe") && !player.isSneaking() && isBreakingBlock(block.getType())){
            if(cItem.get().getShort_name().contains("super")){
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Block surroundingBlock = block.getLocation().add(x, y, z).getBlock();
                            surroundingBlock.breakNaturally(itemInHand);
                        }
                    }
                }
                if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
                    if (itemInHand.getDurability() + 14 < itemInHand.getType().getMaxDurability()) {
                        itemInHand.setDurability((short) (itemInHand.getDurability() + 14));
                    } else {
                        player.getInventory().removeItem(itemInHand);
                    }
                }
            }
            else if(cItem.get().getShort_name().contains("medium")){
                for (int x = -1; x <= 0; x++) {
                    for (int z = -1; z <= 0; z++) {
                        for (int y = -1; y <= 0; y++) {
                            Block surroundingBlock = block.getLocation().add(x, y, z).getBlock();
                            surroundingBlock.breakNaturally(itemInHand);
                        }
                    }
                }
                if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
                    if (itemInHand.getDurability() + 7 < itemInHand.getType().getMaxDurability()) {
                        itemInHand.setDurability((short) (itemInHand.getDurability() + 7));
                    } else {
                        player.getInventory().removeItem(itemInHand);
                    }
                }
            }
        }
        else if(cItem.isPresent() && cItem.get().getShort_name().contains("shovel")  && !player.isSneaking() && isShovelBlock(block.getType())){
            if(cItem.get().getShort_name().contains("super")){
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Block surroundingBlock = block.getLocation().add(x, y, z).getBlock();
                            surroundingBlock.breakNaturally(player.getInventory().getItemInMainHand());
                        }
                    }
                }
                if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
                    // Eşyanın kullanım süresini azalt
                    if(itemInHand.getDurability() + 14 < itemInHand.getType().getMaxDurability()){
                        itemInHand.setDurability((short) (itemInHand.getDurability() + 14));
                    }
                    else{
                        player.getInventory().removeItem(itemInHand);
                    }
                }
            }
            else if(cItem.get().getShort_name().contains("medium")){
                for (int x = -1; x <= 0; x++) {
                    for (int z = -1; z <= 0; z++) {
                        for (int y = -1; y <= 0; y++) {
                            Block surroundingBlock = block.getLocation().add(x, y, z).getBlock();
                            surroundingBlock.breakNaturally(player.getInventory().getItemInMainHand());
                        }
                    }
                }
                if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
                    // Eşyanın kullanım süresini azalt
                    if(itemInHand.getDurability() + 7 < itemInHand.getType().getMaxDurability()){
                        itemInHand.setDurability((short) (itemInHand.getDurability() + 7));
                    }
                    else{
                        player.getInventory().removeItem(itemInHand);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (offHandItem.hasItemMeta()) {
            if (isUndyingItem(offHandItem)) {
                event.setCancelled(true);
                useUndyingItem(player, offHandItem,false);
            }
        }

        if(mainHandItem.hasItemMeta()){
            if (isUndyingItem(mainHandItem)) {
                event.setCancelled(true);
                useUndyingItem(player, mainHandItem,true);
            }
        }

    }

    private void useUndyingItem(Player player, ItemStack item,boolean hand) {
        if(getRemainingUses(item) > 0){
            player.setHealth(1.5);

            Location location = player.getLocation().add(0, 1, 0);
            player.spawnParticle(Particle.TOTEM, location, 2000);

            CustomItemModel x = CustomItemManager.customItemList.stream().filter(customItemModel -> item.getItemMeta().displayName().equals(customItemModel.getItemStack().getItemMeta().displayName())).findFirst().get();

            for (String effectName : x.getEffects().getKeys(false)) {
                int effectDuration = x.getEffects().getInt(effectName);

                PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
                if (potionEffectType != null) {
                    player.addPotionEffect(new PotionEffect(potionEffectType, effectDuration, 0));
                }
            }

            int remainingUses = getRemainingUses(item) - 1;
            if (remainingUses > 0) {
                updateLore(item, remainingUses);
            } else {
                if(!hand){
                    player.getInventory().setItemInOffHand(null);
                }
                else{
                    player.getInventory().setItemInMainHand(null);
                }
            }
        }
    }
    private boolean isUndyingItem(ItemStack item) {
        Optional<CustomItemModel> x = CustomItemManager.customItemList.stream().filter(customItemModel -> item.getItemMeta().displayName().equals(item.getItemMeta().displayName())).findAny();
        if(x.isPresent()){
            return true;
        }
        return false;

    }
    private boolean isShovelBlock(Material material){
        return material == Material.DIRT ||material == Material.GRASS_BLOCK || material == Material.SAND || material == Material.GRAVEL || material == Material.SOUL_SAND || material == Material.SNOW_BLOCK;
    }
    private boolean isBreakingBlock(Material material){
        return material ==  Material.COBBLESTONE
                || material == Material.IRON_ORE
                || material == Material.GRAVEL
                || material == Material.STONE
                || material == Material.COAL_ORE
                || material == Material.GOLD_ORE
                || material == Material.LAPIS_ORE
                || material == Material.REDSTONE_ORE
                || material == Material.DIAMOND_ORE
                || material == Material.ANCIENT_DEBRIS
                || material == Material.DIORITE
                || material == Material.ANDESITE
                || material == Material.COPPER_ORE
                || material == Material.DEEPSLATE
                || material == Material.DEEPSLATE_COAL_ORE
                || material == Material.DEEPSLATE_GOLD_ORE
                || material == Material.DEEPSLATE_IRON_ORE
                || material == Material.DEEPSLATE_DIAMOND_ORE
                || material == Material.DEEPSLATE_COPPER_ORE
                || material == Material.DEEPSLATE_REDSTONE_ORE
                || material == Material.DEEPSLATE_LAPIS_ORE
                || material == Material.TUFF
                || material == Material.NETHERRACK
                || material == Material.NETHER_QUARTZ_ORE
                || material == Material.NETHER_GOLD_ORE
                || material == Material.WARPED_NYLIUM
                || material == Material.CRIMSON_NYLIUM
                || material == Material.BASALT
                || material == Material.BLACKSTONE
                || material == Material.END_STONE
                || material == Material.MAGMA_BLOCK
                || material == Material.POLISHED_BLACKSTONE_BRICKS
                || material == Material.CRACKED_POLISHED_BLACKSTONE_BRICKS
                || material == Material.GRANITE;
    }
    private boolean isTreeBlock(Material material) {
        return material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.SPRUCE_LOG || material == Material.JUNGLE_LOG || material == Material.ACACIA_LOG || material == Material.DARK_OAK_LOG;
    }
    private void breakTree(Location startLocation, Player player) {
        // Verilen başlangıç konumundan itibaren ağacın tüm bloklarını kırarak ağacı kaldıran metot

        Material startBlockType = startLocation.getBlock().getType();

        // Eğer başlangıç bloğu bir ağaç bloğu değilse (yaprak gibi), işlemi sonlandır
        if (!isTreeBlock(startBlockType)) {
            return;
        }

        // Bloğu kır
        startLocation.getBlock().breakNaturally();

        // Kullanılan eşyanın dayanıklılığını azalt
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null) {
            short durability = itemInHand.getDurability();
            itemInHand.setDurability((short) (durability + 1));
            player.getInventory().setItemInMainHand(itemInHand);
        }

        // Ağacın çevresindeki blokları kontrol et
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location currentLocation = startLocation.clone().add(x, y, z);
                    // Başlangıç bloğuyla aynı türde olan blokları kontrol et
                    if (currentLocation.getBlock().getType() == startBlockType) {
                        // Aynı türdeki blokları tekrar kontrol etmek için metodu çağır
                        breakTree(currentLocation, player);
                    }
                }
            }
        }
    }
    private int getRemainingUses(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                for (String lore : meta.getLore()) {
                    if (lore.contains("Kalan Hak: ")) {
                        return Integer.parseInt(lore.split(": ")[1]);
                    }
                }
            }
        }
        return 0; // Lore bulunamadı veya hata oluştuğunda
    }
    private void updateLore(ItemStack item, int remainingUses) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            meta.setLore(java.util.Arrays.asList("Kalan Hak: " + remainingUses));
            item.setItemMeta(meta);
        }
    }
}
