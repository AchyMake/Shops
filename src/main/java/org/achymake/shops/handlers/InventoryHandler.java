package org.achymake.shops.handlers;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class InventoryHandler {
    private final Map<Player, Inventory> inventories = new HashMap<>();
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private MaterialHandler getMaterialHandler() {
        return getInstance().getMaterialHandler();
    }
    private RewardHandler getRewardHandler() {
        return getInstance().getRewardHandler();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public Inventory createInventory(Player player, int size, String title) {
        return getInstance().getServer().createInventory(player, size, title);
    }
    public ItemStack hasItemStack(Inventory inventory, String materialName, int amount) {
        var material = getMaterialHandler().get(materialName);
        if (inventory.contains(material)) {
            var itemStack = inventory.getItem(inventory.first(material));
            if (itemStack != null && !itemStack.getItemMeta().hasEnchants()) {
                var result = itemStack.getAmount() - amount;
                return itemStack.getAmount() >= result ? itemStack : null;
            } else return null;
        } else return null;
    }
    public boolean removeItemStack(Inventory inventory, String materialName, int amount) {
        var itemStack = hasItemStack(inventory, materialName, amount);
        if (itemStack != null) {
            var result = itemStack.getAmount() - amount;
            itemStack.setAmount(result);
            return true;
        } else return false;
    }
    public int removeItemStack(Inventory inventory, String materialName) {
        var material = getMaterialHandler().get(materialName);
        var listed = new HashMap<Material, Integer>();
        if (inventory.contains(material)) {
            for (var itemStack : inventory.getStorageContents()) {
                if (itemStack != null && !itemStack.getItemMeta().hasEnchants()) {
                    if (itemStack.getType() == material) {
                        if (listed.containsKey(material)) {
                            var result = listed.get(material) + itemStack.getAmount();
                            listed.replace(material, result);
                        } else listed.put(material, itemStack.getAmount());
                        itemStack.setAmount(0);
                    }
                }
            }
            return listed.get(material);
        } else return 0;
    }
    public void isClicked(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var rewardType = getMaterialHandler().getRewardType(meta, clickType);
        var message = getMaterialHandler().getMessage(meta, clickType);
        if (rewardType.equals(Shops.RewardType.shop)) {
            getRewardHandler().rewardShop(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.money)) {
            getRewardHandler().rewardMoney(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.item)) {
            getRewardHandler().rewardItem(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.command)) {
            getRewardHandler().rewardCommand(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.nothing)) {
            getMaterialHandler().open(player, getMaterialHandler().getShop(meta));
            if (message != null) {
                player.sendMessage(getMessage().addColor(message));
            }
        }
    }
    public Map<Player, Inventory> getInventories() {
        return inventories;
    }
}