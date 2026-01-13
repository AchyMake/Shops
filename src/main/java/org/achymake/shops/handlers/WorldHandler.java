package org.achymake.shops.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class WorldHandler {
    public Item spawnItem(Location location, ItemStack itemStack) {
        var world = location.getWorld();
        if (world != null) {
            var item = world.createEntity(location, Item.class);
            item.setItemStack(itemStack);
            return world.addEntity(item);
        } else return null;
    }
}