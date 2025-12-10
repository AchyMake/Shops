package org.achymake.shops.handlers;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryHandler {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private MaterialHandler getMaterials() {
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
        var material = getMaterials().get(materialName);
        if (inventory.contains(material)) {
            var item = inventory.getItem(inventory.first(material));
            if (item != null) {
                if (!item.getItemMeta().hasEnchants()) {
                    var result = item.getAmount() - amount;
                    return item.getAmount() >= result ? item : null;
                } else return null;
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
    public void isClicked(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var rewardType = getMaterials().getRewardType(meta, clickType);
        var message = getMaterials().getMessage(meta, clickType);
        if (rewardType.equals(Shops.RewardType.shop)) {
            getRewardHandler().rewardShop(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.money)) {
            getRewardHandler().rewardMoney(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.item)) {
            getRewardHandler().rewardItem(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.command)) {
            getRewardHandler().rewardCommand(player, meta, clickType);
        } else if (rewardType.equals(Shops.RewardType.nothing)) {
            getMaterials().open(player, getMaterials().getShop(meta));
            if (message != null) {
                player.sendMessage(getMessage().addColor(message));
            }
        }
    }
}