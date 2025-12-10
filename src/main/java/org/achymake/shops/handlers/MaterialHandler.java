package org.achymake.shops.handlers;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Message;
import org.achymake.shops.data.Shop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class MaterialHandler {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private Shop getShop() {
        return getInstance().getShop();
    }
    private PaperHandler getPaperHandler() {
        return getInstance().getPaperHandler();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public String getDisplayName(ItemMeta itemMeta) {
        return getData(itemMeta).get(getInstance().getKey("display-name"), PersistentDataType.STRING);
    }
    public Enchantment getEnchantment(String enchantmentName) {
        return Enchantment.getByName(enchantmentName.toUpperCase());
    }
    public void addEnchantments(ItemStack itemStack, Map<String, Integer> enchantmentsMap) {
        if (!enchantmentsMap.isEmpty()) {
            if (itemStack.getType().equals(this.get("enchanted_book"))) {
                var enchantedBook = (EnchantmentStorageMeta) itemStack.getItemMeta();
                for(var enchantmentName : enchantmentsMap.keySet()) {
                    enchantedBook.addStoredEnchant(getEnchantment(enchantmentName), enchantmentsMap.get(enchantmentName), true);
                }
                itemStack.setItemMeta(enchantedBook);
            } else {
                var meta = itemStack.getItemMeta();
                for(var enchantmentName : enchantmentsMap.keySet()) {
                    meta.addEnchant(getEnchantment(enchantmentName), enchantmentsMap.get(enchantmentName), true);
                }
                itemStack.setItemMeta(meta);
            }
        }
    }
    public Material get(String materialName) {
        return Material.valueOf(materialName.toUpperCase());
    }
    public ItemStack getItemStack(String itemName, int amount) {
        return new ItemStack(get(itemName), amount);
    }
    public void giveItemStack(Player player, ItemStack itemStack) {
        if (itemStack != null) {
            if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                player.getInventory().addItem(itemStack);
            } else dropItem(player.getLocation(), itemStack);
        }
    }
    public Item dropItem(Location location, ItemStack itemStack) {
        return location.getWorld().dropItem(location, itemStack);
    }
    public PersistentDataContainer getData(ItemMeta itemMeta) {
        return itemMeta.getPersistentDataContainer();
    }
    public boolean hasData(ItemMeta itemMeta) {
        return getData(itemMeta).has(getInstance().getKey("left.reward-type")) || getData(itemMeta).has(getInstance().getKey("left.price-type")) || getData(itemMeta).has(getInstance().getKey("right.reward-type")) || getData(itemMeta).has(getInstance().getKey("right.price-type")) || getData(itemMeta).has(getInstance().getKey("shift_left.reward-type")) || getData(itemMeta).has(getInstance().getKey("shift_left.price-type")) || getData(itemMeta).has(getInstance().getKey("shift_right.reward-type")) || getData(itemMeta).has(getInstance().getKey("shift_right.price-type"));
    }
    public void setString(ItemMeta itemMeta, String key, String value) {
        getData(itemMeta).set(getInstance().getKey(key), PersistentDataType.STRING, value);
    }
    public void setInt(ItemMeta itemMeta, String key, int value) {
        getData(itemMeta).set(getInstance().getKey(key), PersistentDataType.INTEGER, value);
    }
    public void setDouble(ItemMeta itemMeta, String key, double value) {
        getData(itemMeta).set(getInstance().getKey(key), PersistentDataType.DOUBLE, value);
    }
    public String getShop(ItemMeta itemMeta) {
        return getData(itemMeta).get(getInstance().getKey("shop"), PersistentDataType.STRING);
    }
    public void getCustomHead(Player player, ItemMeta itemMeta, String skullName, String skullKey) {
        var skullMeta = (SkullMeta) itemMeta;
        if (skullKey.equalsIgnoreCase("%player%")) {
            skullMeta.setOwningPlayer(player);
        } else if (16 >= skullName.length()) {
            if (!getInstance().isBukkit()) {
                skullMeta.setPlayerProfile(getPaperHandler().createProfile(skullName, skullKey));
            }
        }
    }
    public void setOwningHead(ItemStack itemStack, String skullName, String skullKey) {
        var skullMeta = (SkullMeta) itemStack.getItemMeta();
        if (16 >= skullName.length()) {
            if (!getInstance().isBukkit()) {
                itemStack.setItemMeta(getPaperHandler().updateSkull(skullMeta, skullName, skullKey));
            }
        }
    }
    public void open(Player player, String shop) {
        getInstance().getScheduleHandler().runLater(new Runnable() {
            @Override
            public void run() {
                getShop().open(player, shop);
            }
        }, getShop().getDelay());
    }
    public String getMessage(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".message"), PersistentDataType.STRING);
    }
    public Shops.RewardType getRewardType(ItemMeta itemMeta, Shops.ClickType clickType) {
        var value = getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward-type"), PersistentDataType.STRING);
        if (value == null) {
            return Shops.RewardType.nothing;
        } else if (value.equalsIgnoreCase("shop")) {
            return Shops.RewardType.shop;
        } else if (value.equals("money")) {
            return Shops.RewardType.money;
        } else if (value.equalsIgnoreCase("item")) {
            return Shops.RewardType.item;
        } else if (value.equalsIgnoreCase("command")) {
            return Shops.RewardType.command;
        } else return Shops.RewardType.nothing;
    }
    public String getRewardShop(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward"), PersistentDataType.STRING);
    }
    public List<String> getRewardCommands(ItemMeta itemMeta, Shops.ClickType clickType) {
        var mapString = getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward"), PersistentDataType.STRING);
        return mapString != null ? getMessage().getStringList(mapString) : null;
    }
    public String getRewardMaterial(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward.type"), PersistentDataType.STRING);
    }
    public String getRewardSkull(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward.skull"), PersistentDataType.STRING);
    }
    public int getRewardMaterialAmount(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward.amount"), PersistentDataType.INTEGER);
    }
    public Map<String, Integer> getRewardMaterialEnchantments(ItemMeta itemMeta, Shops.ClickType clickType) {
        var mapString = getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward.enchantments"), PersistentDataType.STRING);
        return mapString != null ? getMessage().getEnchantments(mapString) : null;
    }
    public double getRewardDouble(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward"), PersistentDataType.DOUBLE);
    }
    public int getRewardInt(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".reward"), PersistentDataType.INTEGER);
    }
    public Shops.PriceType getPriceType(ItemMeta itemMeta, Shops.ClickType clickType) {
        var value = getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".price-type"), PersistentDataType.STRING);
        if (value == null) {
            return Shops.PriceType.nothing;
        } else if (value.equals("money")) {
            return Shops.PriceType.money;
        } else if (value.equalsIgnoreCase("item")) {
            return Shops.PriceType.item;
        } else if (value.equalsIgnoreCase("itemall")) {
            return Shops.PriceType.itemall;
        } else return Shops.PriceType.nothing;
    }
    public String getPriceMaterial(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".price.type"), PersistentDataType.STRING);
    }
    public int getPriceMaterialAmount(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".price.amount"), PersistentDataType.INTEGER);
    }
    public double getPriceDouble(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".price"), PersistentDataType.DOUBLE);
    }
    public int getPriceInt(ItemMeta itemMeta, Shops.ClickType clickType) {
        return getData(itemMeta).get(getInstance().getKey(clickType.toString() + ".price"), PersistentDataType.INTEGER);
    }
}