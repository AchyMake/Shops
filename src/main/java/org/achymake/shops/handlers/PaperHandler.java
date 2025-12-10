package org.achymake.shops.handlers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.achymake.shops.Shops;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PaperHandler {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    public PlayerProfile createProfile(String skullName, String skullKey) {
        var profile = getInstance().getServer().createProfile(skullName);
        profile.setProperty(new ProfileProperty("textures", skullKey));
        profile.update();
        return profile;
    }
    public ItemMeta updateSkull(ItemMeta itemMeta, String skullName, String skullKey) {
        var skullMeta = (SkullMeta) itemMeta;
        skullMeta.setPlayerProfile(createProfile(skullName, skullKey));
        return itemMeta;
    }
}