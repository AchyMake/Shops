package org.achymake.shops.commands;

import org.achymake.shops.Shops;
import org.achymake.shops.data.Message;
import org.achymake.shops.data.Shop;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShopCommand implements CommandExecutor, TabCompleter {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private Shop getShop() {
        return getInstance().getShop();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public ShopCommand() {
        getInstance().getCommand("shop").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getShop().exists(getShop().getMainShop())) {
                    getShop().open(player, getShop().getMainShop());
                    getShop().playOpen(player);
                } else player.sendMessage(getMessage().get("error.shop.invalid")
                        .replace("{shop}", getShop().getMainShop()));
            } else if (args.length == 1) {
                var shop = args[0];
                if (getShop().exists(shop)) {
                    if (player.hasPermission("shops.command.shop." + shop)) {
                        getShop().open(player, shop);
                        getShop().playOpen(player);
                    }
                } else player.sendMessage(getMessage().get("error.shop.invalid")
                        .replace("{shop}", shop));
            } else if (args.length == 2 && player.hasPermission("shops.command.shop.other")) {
                var shop = args[0];
                var username = args[1];
                var target = getInstance().getPlayer(username);
                if (target != null) {
                    if (target == player) {
                        if (getShop().exists(shop)) {
                            getShop().open(target, shop);
                            getShop().playOpen(target);
                        } else player.sendMessage(getMessage().get("error.shop.invalid")
                                .replace("{shop}", shop));
                    } else if (!target.hasPermission("shops.command.shop.exempt")) {
                        if (getShop().exists(shop)) {
                            getShop().open(target, shop);
                            getShop().playOpen(target);
                        } else player.sendMessage(getMessage().get("error.shop.invalid")
                                .replace("{shop}", shop));
                    } else player.sendMessage(getMessage().get("commands.shop.exempt")
                            .replace("{shop}", username));
                } else player.sendMessage(getMessage().get("error.target.invalid")
                        .replace("{target}", username));
            }
        } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                var shop = args[0];
                var username = args[1];
                var target = getInstance().getPlayer(username);
                if (target != null) {
                    if (getShop().exists(shop)) {
                        getShop().open(target, shop);
                        getShop().playOpen(target);
                    } else consoleCommandSender.sendMessage(getMessage().get("error.shop.invalid")
                            .replace("{shop}", shop));
                } else consoleCommandSender.sendMessage(getMessage().get("error.target.invalid")
                        .replace("{target}", username));
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        var commands = new ArrayList<String>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                for(var shopName : getShop().getListed()) {
                    if (player.hasPermission("shops.command.shop." + shopName) && shopName.startsWith(args[0])) {
                        commands.add(shopName);
                    }
                }
            } else if (args.length == 2 && player.hasPermission("shops.command.shop.other")) {
                getInstance().getOnlinePlayers().forEach((target) -> {
                    if (!target.isSilent() && target.getName().startsWith(args[1])) {
                        commands.add(target.getName());
                    }
                });
            }
        }
        return commands;
    }
}