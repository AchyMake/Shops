package org.achymake.shops.listeners;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Shop;
import org.achymake.shops.handlers.InventoryHandler;
import org.achymake.shops.handlers.MaterialHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.PluginManager;

public class InventoryClick implements Listener {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private Shop getShop() {
        return getInstance().getShop();
    }
    private InventoryHandler getInventoryHandler() {
        return getInstance().getInventoryHandler();
    }
    private MaterialHandler getMaterialHandler() {
        return getInstance().getMaterialHandler();
    }
    private PluginManager getPluginManager() {
        return getInstance().getPluginManager();
    }
    public InventoryClick() {
        getPluginManager().registerEvents(this, getInstance());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null)return;
        var inventory = event.getInventory();
        var player = (Player) event.getWhoClicked();
        if (!getShop().hasInventoryOpen(player))return;
        var shop = getInventoryHandler().getInventories().get(player);
        if (inventory != shop)return;
        event.setCancelled(true);
        if (clickedInventory != inventory)return;
        var item = event.getClickedInventory().getItem(event.getSlot());
        if (item == null)return;
        var meta = item.getItemMeta();
        if (!getMaterialHandler().hasData(meta))return;
        var clickType = event.getClick();
        if (clickType.equals(ClickType.LEFT)) {
            getInventoryHandler().isClicked(player, meta, Shops.ClickType.left);
        } else if (clickType.equals(ClickType.SHIFT_LEFT)) {
            getInventoryHandler().isClicked(player, meta, Shops.ClickType.shift_left);
        } else if (clickType.equals(ClickType.RIGHT)) {
            getInventoryHandler().isClicked(player, meta, Shops.ClickType.right);
        } else if (clickType.equals(ClickType.SHIFT_RIGHT)) {
            getInventoryHandler().isClicked(player, meta, Shops.ClickType.shift_right);
        }
    }
}