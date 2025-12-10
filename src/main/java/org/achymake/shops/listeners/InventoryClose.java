package org.achymake.shops.listeners;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;

public class InventoryClose implements Listener {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private Shop getShop() {
        return getInstance().getShop();
    }
    private PluginManager getPluginManager() {
        return getInstance().getPluginManager();
    }
    public InventoryClose() {
        getPluginManager().registerEvents(this, getInstance());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        var player = (Player) event.getPlayer();
        if (!getShop().hasInventoryOpen(player))return;
        if (getShop().getInventories().get(player) != event.getInventory())return;
        getShop().close(player);
        getShop().playClose(player);
    }
}