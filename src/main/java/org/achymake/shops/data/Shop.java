package org.achymake.shops.data;

import org.achymake.shops.Shops;
import org.achymake.shops.handlers.InventoryHandler;
import org.achymake.shops.handlers.MaterialHandler;
import org.achymake.shops.handlers.RandomHandler;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private FileConfiguration getConfig() {
        return getInstance().getConfig();
    }
    private InventoryHandler getInventoryHandler() {
        return getInstance().getInventoryHandler();
    }
    private MaterialHandler getMaterialHandler() {
        return getInstance().getMaterialHandler();
    }
    private RandomHandler getRandomHandler() {
        return getInstance().getRandomHandler();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    private File getFile(String shop) {
        return new File(getInstance().getDataFolder(), "shop/" + shop + ".yml");
    }
    public boolean exists(String shop) {
        return getFile(shop).exists();
    }
    public FileConfiguration getConfig(String shop) {
        return YamlConfiguration.loadConfiguration(getFile(shop));
    }
    public String getMainShop() {
        return getConfig().getString("shop.main");
    }
    public long getDelay() {
        return getConfig().getLong("shop.open-delay");
    }
    public void playOpen(Player player) {
        var type = getConfig().getString("sounds.open.type");
        if (type == null)return;
        if (type.isEmpty())return;
        var volume = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.open.volume"));
        var pitch = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.open.pitch"));
        player.playSound(player, Sound.valueOf(type), volume, pitch);
    }
    public void playPurchase(Player player) {
        var type = getConfig().getString("sounds.purchase.type");
        if (type == null)return;
        if (type.isEmpty())return;
        var volume = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.purchase.volume"));
        var pitch = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.purchase.pitch"));
        player.playSound(player, Sound.valueOf(type), volume, pitch);
    }
    public void playClick(Player player) {
        var type = getConfig().getString("sounds.click.type");
        if (type == null)return;
        if (type.isEmpty())return;
        var volume = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.click.volume"));
        var pitch = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.click.pitch"));
        player.playSound(player, Sound.valueOf(type), volume, pitch);
    }
    public void playInsufficient(Player player) {
        var type = getConfig().getString("sounds.insufficient.type");
        if (type == null)return;
        if (type.isEmpty())return;
        var volume = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.insufficient.volume"));
        var pitch = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.insufficient.pitch"));
        player.playSound(player, Sound.valueOf(type), volume, pitch);
    }
    public void playClose(Player player) {
        var type = getConfig().getString("sounds.close.type");
        if (type == null)return;
        if (type.isEmpty())return;
        var volume = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.close.volume"));
        var pitch = getRandomHandler().makeRandom((float) getConfig().getDouble("sounds.close.pitch"));
        player.playSound(player, Sound.valueOf(type), volume, pitch);
    }
    public Inventory open(Player player, String shop) {
        if (exists(shop)) {
            var config = getConfig(shop);
            if (config.isString("title")) {
                var size = config.getInt("size");
                if (size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54) {
                    if (config.isConfigurationSection("items")) {
                        getInventoryHandler().getInventories().remove(player);
                        var inventory = getInventoryHandler().createInventory(player, size, getMessage().addColor(config.getString("title")));
                        for(var key : config.getConfigurationSection("items").getKeys(false)) {
                            var section = "items." + key;
                            var materialName = config.getString(section + ".display.type");
                            var amount = config.getInt(section + ".display.amount");
                            var item = getMaterialHandler().getItemStack(getMaterialHandler().get("stone"), amount);
                            var material = getMaterialHandler().get(materialName);
                            if (material != null) {
                                item.setType(material);
                            }
                            var meta = item.getItemMeta();
                            if (item.getType() == getMaterialHandler().get("spawner")) {
                                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                            }
                            if (config.isString(section + ".permission") && !player.hasPermission(config.getString(section + ".permission"))) {
                                item.setAmount(0);
                            }
                            if (item.getAmount() > 0) {
                                var index = config.getInt(section + ".slot") - 1;
                                var name = config.getString(section + ".display.name");
                                getMaterialHandler().setString(meta, "shop", shop);
                                if (name != null) {
                                    var displayName = getMessage().addPlaceholder(player, name);
                                    meta.setDisplayName(displayName);
                                    getMaterialHandler().setString(meta, "display-name", displayName);
                                }
                                if (materialName.equalsIgnoreCase("player_head")) {
                                    var skullKey = config.getString(section + ".display.custom");
                                    if (skullKey != null) {
                                        getMaterialHandler().getCustomHead(player, meta, name, skullKey);
                                    }
                                }
                                var list = config.getStringList(section + ".display.lore");
                                if (!list.isEmpty()) {
                                    var lore = new ArrayList<String>();
                                    for(var line : list) {
                                        lore.add(getMessage().addPlaceholder(player, line));
                                    }
                                    meta.setLore(lore);
                                }
                                if (config.isConfigurationSection(section + ".display.enchantments")) {
                                    for(var enchantmentName : config.getConfigurationSection(section + ".display.enchantments").getKeys(false)) {
                                        meta.addEnchant(Enchantment.getByName(enchantmentName.toUpperCase()), config.getInt(section + ".display.enchantments." + enchantmentName), true);
                                    }
                                }
                                for (var clickType : Shops.ClickType.values()) {
                                    var message = config.getString(section + "." + clickType + ".message");
                                    if (message != null) {
                                        getMaterialHandler().setString(meta, clickType + ".message", message);
                                    }
                                    var rewardType = config.getString(section + "." + clickType + ".reward-type");
                                    if (rewardType != null) {
                                        if (rewardType.equalsIgnoreCase("shop")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "shop");
                                            getMaterialHandler().setString(meta, clickType + ".reward", config.getString(section + "." + clickType + ".reward"));
                                        } else if (rewardType.equalsIgnoreCase("money")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "money");
                                            getMaterialHandler().setDouble(meta, clickType + ".reward", config.getDouble(section + "." + clickType + ".reward"));
                                        } else if (rewardType.equalsIgnoreCase("item")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "item");
                                            getMaterialHandler().setString(meta, clickType + ".reward.type", config.getString(section + "." + clickType + ".reward.type"));
                                            getMaterialHandler().setInt(meta, clickType + ".reward.amount", config.getInt(section + "." + clickType + ".reward.amount"));
                                            if (config.isString(section + "." + clickType + ".reward.custom")) {
                                                getMaterialHandler().setString(meta, clickType + ".reward.skull", config.getString(section + "." + clickType + ".reward.custom"));
                                            }
                                            if (config.isConfigurationSection(section + "." + clickType + ".reward.enchantments")) {
                                                var listed = new HashMap<>();
                                                config.getConfigurationSection(section + "." + clickType + ".reward.enchantments").getKeys(false).forEach((enchantmentName) -> listed.put(enchantmentName, config.getInt(section + "." + clickType + ".reward.enchantments." + enchantmentName)));
                                                getMaterialHandler().setString(meta, clickType + ".reward.enchantments", listed.toString());
                                            }
                                        } else if (rewardType.equalsIgnoreCase("itemall")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "itemall");
                                            getMaterialHandler().setString(meta, clickType + ".reward.type", config.getString(section + "." + clickType + ".reward.type"));
                                        } else if (rewardType.equalsIgnoreCase("command")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "command");
                                            getMaterialHandler().setString(meta, clickType + ".reward", config.getStringList(section + "." + clickType + ".reward").toString());
                                        } else if (rewardType.equalsIgnoreCase("nothing")) {
                                            getMaterialHandler().setString(meta, clickType + ".reward-type", "nothing");
                                        }
                                    } else getMaterialHandler().setString(meta, clickType + ".reward-type", "nothing");
                                    var priceType = config.getString(section + "." + clickType + ".price-type");
                                    if (priceType != null) {
                                        if (priceType.equalsIgnoreCase("nothing")) {
                                            getMaterialHandler().setString(meta, clickType + ".price-type", "nothing");
                                        } else if (priceType.equalsIgnoreCase("money")) {
                                            getMaterialHandler().setString(meta, clickType + ".price-type", "money");
                                            getMaterialHandler().setDouble(meta, clickType + ".price", config.getDouble(section + "." + clickType + ".price"));
                                        } else if (priceType.equalsIgnoreCase("item")) {
                                            getMaterialHandler().setString(meta, clickType + ".price-type", "item");
                                            getMaterialHandler().setString(meta, clickType + ".price.type", config.getString(section + "." + clickType + ".price.type"));
                                            getMaterialHandler().setInt(meta, clickType + ".price.amount", config.getInt(section + "." + clickType + ".price.amount"));
                                        } else if (priceType.equalsIgnoreCase("itemall")) {
                                            getMaterialHandler().setString(meta, clickType + ".price-type", "itemall");
                                            getMaterialHandler().setString(meta, clickType + ".price.type", config.getString(section + "." + clickType + ".price.type"));
                                        } else if (priceType.equalsIgnoreCase("nothing")) {
                                            getMaterialHandler().setString(meta, clickType + ".price-type", "nothing");
                                        }
                                    } else getMaterialHandler().setString(meta, clickType + ".price-type", "nothing");
                                }
                                item.setItemMeta(meta);
                                inventory.setItem(index, item);
                            }
                        }
                        player.openInventory(inventory);
                        getInventoryHandler().getInventories().put(player, inventory);
                        return inventory;
                    } else return null;
                } else return null;
            } else return null;
        } else return null;
    }
    public void close(Player player) {
        getInventoryHandler().getInventories().remove(player);
    }
    public void reload() {
        var folder = new File(getInstance().getDataFolder(), "shop");
        if (folder.exists() && folder.isDirectory()) {
            var files = folder.listFiles();
            if (files != null) {
                for(var file : files) {
                    if (!file.exists())return;
                    if (!file.isFile())return;
                    var config = YamlConfiguration.loadConfiguration(file);
                    try {
                        config.load(file);
                    } catch (InvalidConfigurationException | IOException e) {
                        getInstance().sendWarning(e.getMessage());
                    }
                }
            }
        } else {
            getInstance().saveResource("shop/butcher.yml", false);
            getInstance().saveResource("shop/farmer.yml", false);
            getInstance().saveResource("shop/fisherman.yml", false);
            getInstance().saveResource("shop/hunter.yml", false);
            getInstance().saveResource("shop/lumberjack.yml", false);
            getInstance().saveResource("shop/menu.yml", false);
            getInstance().saveResource("shop/miner.yml", false);
            getInstance().saveResource("shop/shepherd.yml", false);
            getInstance().saveResource("shop/test.yml", false);
        }
    }
    public boolean hasInventoryOpen(Player player) {
        return getInventoryHandler().getInventories().containsKey(player);
    }
    public List<String> getListed() {
        var listed = new ArrayList<String>();
        var folder = new File(getInstance().getDataFolder(), "shop");
        if (folder.exists() && folder.isDirectory()) {
            var files = folder.listFiles();
            if (files != null) {
                for(var file : files) {
                    if (file.exists() && file.isFile()) {
                        listed.add(file.getName().replace(".yml", ""));
                    }
                }
            }
        }
        return listed;
    }
}