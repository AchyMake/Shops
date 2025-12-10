package org.achymake.shops;

import net.milkbowl.vault.economy.Economy;
import org.achymake.shops.commands.*;
import org.achymake.shops.data.*;
import org.achymake.shops.handlers.*;
import org.achymake.shops.listeners.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.Collection;

public final class Shops extends JavaPlugin {
    private static Shops instance;
    private Message message;
    private Shop shop;
    private InventoryHandler inventoryHandler;
    private MaterialHandler materialHandler;
    private PaperHandler paperHandler;
    private RandomHandler randomHandler;
    private RewardHandler rewardHandler;
    private ScheduleHandler scheduleHandler;
    private UpdateChecker updateChecker;
    private BukkitScheduler bukkitScheduler;
    private PluginManager pluginManager;
    private Economy economy = null;
    @Override
    public void onEnable() {
        instance = this;
        message = new Message();
        shop = new Shop();
        inventoryHandler = new InventoryHandler();
        materialHandler = new MaterialHandler();
        if (!isBukkit()) {
            paperHandler = new PaperHandler();
        } else paperHandler = null;
        randomHandler = new RandomHandler();
        rewardHandler = new RewardHandler();
        scheduleHandler = new ScheduleHandler();
        updateChecker = new UpdateChecker();
        pluginManager = getServer().getPluginManager();
        bukkitScheduler = getServer().getScheduler();
        commands();
        events();
        reload();
        var economyServices = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyServices != null) {
            economy = economyServices.getProvider();
        } else getPluginManager().disablePlugin(this);
        sendInfo("Enabled for " + getMinecraftProvider() + " " + getMinecraftVersion());
        getUpdateChecker().getUpdate();
    }
    @Override
    public void onDisable() {
        getScheduleHandler().disable();
        sendInfo("Disabled for " + getMinecraftProvider() + " " + getMinecraftVersion());
    }
    private void commands() {
        new ShopCommand();
        new ShopsCommand();
    }
    private void events() {
        new InventoryClick();
        new InventoryClose();
        new PlayerJoin();
    }
    public void reload() {
        if (!new File(getInstance().getDataFolder(), "config.yml").exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else reloadConfig();
        getMessage().reload();
        getShop().reload();
    }
    public Collection<? extends Player> getOnlinePlayers() {
        return getServer().getOnlinePlayers();
    }
    public Player getPlayer(String username) {
        return getServer().getPlayerExact(username);
    }
    public void dispatchCommand(String command) {
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }
    public Economy getEconomy() {
        return economy;
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public BukkitScheduler getBukkitScheduler() {
        return bukkitScheduler;
    }
    public PluginManager getPluginManager() {
        return pluginManager;
    }
    public ScheduleHandler getScheduleHandler() {
        return scheduleHandler;
    }
    public RewardHandler getRewardHandler() {
        return rewardHandler;
    }
    public RandomHandler getRandomHandler() {
        return randomHandler;
    }
    public PaperHandler getPaperHandler() {
        return paperHandler;
    }
    public MaterialHandler getMaterialHandler() {
        return materialHandler;
    }
    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }
    public Shop getShop() {
        return shop;
    }
    public Message getMessage() {
        return message;
    }
    public static Shops getInstance() {
        return instance;
    }
    public boolean isBukkit() {
        return getMinecraftProvider().equals("Bukkit") || getMinecraftProvider().equals("CraftBukkit");
    }
    public void sendInfo(String message) {
        getLogger().info(message);
    }
    public void sendWarning(String message) {
        getLogger().warning(message);
    }
    public String name() {
        return getDescription().getName();
    }
    public String version() {
        return getDescription().getVersion();
    }
    public String getMinecraftVersion() {
        return getServer().getBukkitVersion();
    }
    public String getMinecraftProvider() {
        return getServer().getName();
    }
    public NamespacedKey getKey(String key) {
        return new NamespacedKey(this, key);
    }
    public enum ClickType {
        left,
        shift_left,
        right,
        shift_right
    }
    public enum PriceType {
        money,
        item,
        itemall,
        nothing
    }
    public enum RewardType {
        shop,
        money,
        item,
        command,
        nothing
    }
}