package org.achymake.shops.data;

import me.clip.placeholderapi.PlaceholderAPI;
import org.achymake.shops.Shops;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private final File file = new File(getInstance().getDataFolder(), "message.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    public String get(String path) {
        return config.isString(path) ? addColor(config.getString(path)) : path + ": is missing a value";
    }
    public String get(String path, String... format) {
        return config.isString(path) ? addColor(MessageFormat.format(config.getString(path), format)) : path + ": is missing a value";
    }
    private void setup() {
        config.options().copyDefaults(true);
        config.set("error.shop.invalid", "{shop}&c does not exist");
        config.set("commands.shop.exempt", "&cYou are not allowed to open shop for&f {target}");
        config.set("insufficient-funds.shop", "&cYou do not have&a {price}&c to open&f {reward}");
        config.set("insufficient-funds.command", "&cYou do not have&a {price}&c to receive commands");
        config.set("insufficient-funds.item", "&cYou do not have&a {price}&c to receive&f {reward}");
        config.set("insufficient-funds.money", "&cYou do not have&a {price}&c to receive&a {reward}");
        config.set("insufficient-items.shop", "&cYou do not have any&f {price}&c to open&f {reward}");
        config.set("insufficient-items.command", "&cYou do not have any&f {price}&c to receive commands");
        config.set("insufficient-items.item", "&cYou do not have any&f {price}&c to receive&f {reward}");
        config.set("insufficient-items.money", "&cYou do not have any&f {price}&c to receive&a {reward}");
        try {
            config.save(file);
        } catch (IOException e) {
            getInstance().sendWarning(e.getMessage());
        }
    }
    public void reload() {
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else setup();
    }
    public String addPlaceholder(Player player, String message) {
        return addColor(PlaceholderAPI.setPlaceholders(player, message));
    }
    public String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public List<String> getStringList(String lines) {
        var listed = new ArrayList<String>();
        var result = lines.replace("[", "").replace("]", "");
        for(var test : result.split(",")) {
            listed.add(test.strip());
        }
        return listed;
    }
    public Map<String, Integer> getEnchantments(String mapString) {
        var listed = new HashMap<String, Integer>();
        var result = mapString.replace("{", "").replace("}", "").replace(",", "");
        if (result.contains(" ")) {
            for(var test : result.split(" ")) {
                var args = test.split("=");
                listed.put(args[0], Integer.parseInt(args[1]));
            }
        } else {
            var args = result.split("=");
            listed.put(args[0], Integer.parseInt(args[1]));
        }
        return listed;
    }
    public String toTitleCase(String string) {
        if (string.contains(" ")) {
            var builder = getBuilder();
            for(var strings : string.split(" ")) {
                builder.append(strings.toUpperCase().charAt(0)).append(strings.substring(1).toLowerCase());
                builder.append(" ");
            }
            return builder.toString().strip();
        } else if (string.contains("_")) {
            var builder = getBuilder();
            for(var strings : string.split("_")) {
                builder.append(strings.toUpperCase().charAt(0)).append(strings.substring(1).toLowerCase());
                builder.append(" ");
            }
            return builder.toString().strip();
        } else return string.toUpperCase().charAt(0) + string.substring(1).toLowerCase();
    }
    public StringBuilder getBuilder() {
        return new StringBuilder();
    }
    public String format(double amount) {
        return new DecimalFormat(getInstance().getConfig().getString("format")).format(amount);
    }
}