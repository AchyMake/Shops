package org.achymake.shops.handlers;

import net.milkbowl.vault.economy.Economy;
import org.achymake.shops.Shops;
import org.achymake.shops.data.Message;
import org.achymake.shops.data.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RewardHandler {
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
    private Economy getEconomy() {
        return getInstance().getEconomy();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public void rewardShop(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var priceType = getMaterialHandler().getPriceType(meta, clickType);
        var message = getMaterialHandler().getMessage(meta, clickType);
        var shop = getMaterialHandler().getRewardShop(meta, clickType);
        if (getShop().exists(shop)) {
            if (priceType.equals(Shops.PriceType.money)) {
                var price = getMaterialHandler().getPriceDouble(meta, clickType);
                if (getEconomy().has(player, price)) {
                    if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                        return;
                    }
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message
                                .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                                .replace("{reward}", getMessage().toTitleCase(shop))));
                    }
                    getMaterialHandler().open(player, shop);
                    getShop().playPurchase(player);
                } else {
                    player.sendMessage(getMessage().get("insufficient-funds.shop")
                            .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                            .replace("{reward}", getMessage().toTitleCase(shop)));
                    getShop().playInsufficient(player);
                }
            } else if (priceType.equals(Shops.PriceType.item)) {
                var material = getMaterialHandler().getPriceMaterial(meta, clickType);
                var amount = getMaterialHandler().getPriceMaterialAmount(meta, clickType);
                if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message));
                    }
                    getMaterialHandler().open(player, shop);
                    getShop().playPurchase(player);
                } else {
                    player.sendMessage(getMessage().get("insufficient-items.shop")
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                            .replace("{reward}", getMessage().toTitleCase(shop)));
                    getShop().playInsufficient(player);
                }
            } else if (priceType.equals(Shops.PriceType.nothing)) {
                getMaterialHandler().open(player, shop);
                getShop().playClick(player);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", getMessage().toTitleCase("nothing"))
                            .replace("{reward}", getMessage().toTitleCase(shop))
                    ));
                }
            }
        } else player.sendMessage(getMessage().get("error.shop.invalid").replace("{shop}", shop));
    }
    public void rewardMoney(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var priceType = getMaterialHandler().getPriceType(meta, clickType);
        var message = getMaterialHandler().getMessage(meta, clickType);
        var reward = getMaterialHandler().getRewardDouble(meta, clickType);
        var shop = getMaterialHandler().getShop(meta);
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterialHandler().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    getEconomy().depositPlayer(player, reward);
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message
                                .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                                .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                        ));
                    }
                    getMaterialHandler().open(player, shop);
                    getShop().playPurchase(player);
                }
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.money")
                        .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var material = getMaterialHandler().getPriceMaterial(meta, clickType);
            var amount = getMaterialHandler().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                if (!getEconomy().depositPlayer(player, reward).transactionSuccess()) {
                    return;
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                            .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.money")
                        .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.itemall)) {
            var materialName = getMaterialHandler().getPriceMaterial(meta, clickType);
            var material = getMaterialHandler().get(materialName);
            if (player.getInventory().contains(material)) {
                var amount = getInventoryHandler().removeItemStack(player.getInventory(), materialName);
                var result = (double) amount * reward;
                getEconomy().depositPlayer(player, result);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount + " " + getMessage().toTitleCase(materialName))
                            .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(result))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.money")
                        .replace("{price}", getMessage().toTitleCase(materialName))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.nothing)) {
            if (!getEconomy().depositPlayer(player, reward).transactionSuccess()) {
                return;
            }
            if (message != null) {
                player.sendMessage(getMessage().addColor(message
                        .replace("{price}", getMessage().toTitleCase("nothing"))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getMessage().format(reward))
                ));
            }
            getMaterialHandler().open(player, shop);
            getShop().playPurchase(player);
        }
    }
    public void rewardItem(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var type = getMaterialHandler().getRewardMaterial(meta, clickType);
        var amount = getMaterialHandler().getRewardMaterialAmount(meta, clickType);
        var itemStack = getMaterialHandler().getItemStack(getMaterialHandler().get("stone"), amount);
        var material = getMaterialHandler().get(type);
        if (material != null) {
            itemStack.setType(material);
        }
        var enchantments = getMaterialHandler().getRewardMaterialEnchantments(meta, clickType);
        if (enchantments != null) {
            getMaterialHandler().addEnchantments(itemStack, enchantments);
        }
        var priceType = getMaterialHandler().getPriceType(meta, clickType);
        var message = getMaterialHandler().getMessage(meta, clickType);
        var shop = getMaterialHandler().getShop(meta);
        var skullKey = getMaterialHandler().getRewardSkull(meta, clickType);
        if (skullKey != null) {
            var displayName = getMaterialHandler().getDisplayName(meta);
            if (displayName != null) {
                getMaterialHandler().setOwningHead(itemStack, displayName, skullKey);
            }
        }
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterialHandler().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    return;
                }
                getMaterialHandler().giveItemStack(player, itemStack);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                            .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.item")
                        .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                        .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var type2 = getMaterialHandler().getPriceMaterial(meta, clickType);
            var amount2 = getMaterialHandler().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), type2, amount2)) {
                getMaterialHandler().giveItemStack(player, itemStack);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount2 + " " + getMessage().toTitleCase(type2))
                            .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.item")
                        .replace("{price}", amount2 + " " + getMessage().toTitleCase(type2))
                        .replace("{reward}",  amount + " " + getMessage().toTitleCase(type))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.nothing)) {
            getMaterialHandler().giveItemStack(player, itemStack);
            if (message != null) {
                player.sendMessage(getMessage().get("insufficient-items.item")
                        .replace("{price}", getMessage().toTitleCase("nothing"))
                        .replace("{reward}",  amount + " " + getMessage().toTitleCase(type))
                );
            }
            getMaterialHandler().open(player, shop);
            getShop().playPurchase(player);
        }
    }
    public void rewardCommand(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var priceType = getMaterialHandler().getPriceType(meta, clickType);
        var commands = getMaterialHandler().getRewardCommands(meta, clickType);
        var message = getMaterialHandler().getMessage(meta, clickType);
        var shop = getMaterialHandler().getShop(meta);
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterialHandler().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    return;
                }
                for(var command : commands) {
                    getInstance().dispatchCommand(command.replace("%player%", player.getName()));
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.command")
                        .replace("{price}", getEconomy().currencyNamePlural() + getMessage().format(price))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var material = getMaterialHandler().getPriceMaterial(meta, clickType);
            var amount = getMaterialHandler().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                for(var command : commands) {
                    getInstance().dispatchCommand(command.replace("%player%", player.getName()));
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                    ));
                }
                getMaterialHandler().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.command")
                        .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.nothing)) {
            for(var command : commands) {
                getInstance().dispatchCommand(command.replace("%player%", player.getName()));
            }
            if (message != null) {
                player.sendMessage(getMessage().addColor(message
                        .replace("{price}", getMessage().toTitleCase("nothing"))
                ));
            }
            getMaterialHandler().open(player, shop);
            getShop().playPurchase(player);
        }
    }
}