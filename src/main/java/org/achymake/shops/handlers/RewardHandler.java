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
    private MaterialHandler getMaterials() {
        return getInstance().getMaterialHandler();
    }
    private Economy getEconomy() {
        return getInstance().getEconomy();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public void rewardShop(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var priceType = getMaterials().getPriceType(meta, clickType);
        var message = getMaterials().getMessage(meta, clickType);
        var shop = getMaterials().getRewardShop(meta, clickType);
        if (getShop().exists(shop)) {
            if (priceType.equals(Shops.PriceType.money)) {
                var price = getMaterials().getPriceDouble(meta, clickType);
                if (getEconomy().has(player, price)) {
                    if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                        return;
                    }
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message
                                .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                                .replace("{reward}", getMessage().toTitleCase(shop))));
                    }
                    getMaterials().open(player, shop);
                    getShop().playPurchase(player);
                } else {
                    player.sendMessage(getMessage().get("insufficient-funds.shop")
                            .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                            .replace("{reward}", getMessage().toTitleCase(shop)));
                    getShop().playInsufficient(player);
                }
            } else if (priceType.equals(Shops.PriceType.item)) {
                var material = getMaterials().getPriceMaterial(meta, clickType);
                var amount = getMaterials().getPriceMaterialAmount(meta, clickType);
                if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message));
                    }
                    getMaterials().open(player, shop);
                    getShop().playPurchase(player);
                } else {
                    player.sendMessage(getMessage().get("insufficient-items.shop")
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                            .replace("{reward}", getMessage().toTitleCase(shop)));
                    getShop().playInsufficient(player);
                }
            } else if (priceType.equals(Shops.PriceType.nothing)) {
                getMaterials().open(player, shop);
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
        var priceType = getMaterials().getPriceType(meta, clickType);
        var message = getMaterials().getMessage(meta, clickType);
        var reward = getMaterials().getRewardDouble(meta, clickType);
        var shop = getMaterials().getShop(meta);
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterials().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    getEconomy().depositPlayer(player, reward);
                    if (message != null) {
                        player.sendMessage(getMessage().addColor(message
                                .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                                .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
                        ));
                    }
                    getMaterials().open(player, shop);
                    getShop().playPurchase(player);
                }
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.money")
                        .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var material = getMaterials().getPriceMaterial(meta, clickType);
            var amount = getMaterials().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                if (!getEconomy().depositPlayer(player, reward).transactionSuccess()) {
                    return;
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                            .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
                    ));
                }
                getMaterials().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.money")
                        .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.itemall)) {
            var materialName = getMaterials().getPriceMaterial(meta, clickType);
            var material = getMaterials().get(materialName);
            if (player.getInventory().contains(material)) {
                for (var itemStack : player.getInventory()) {
                    if (itemStack != null && !itemStack.getItemMeta().hasEnchants() && itemStack.getType() == material) {
                        var result = (double) itemStack.getAmount() * reward;
                        getEconomy().depositPlayer(player, result);
                        if (message != null) {
                            player.sendMessage(getMessage().addColor(message
                                    .replace("{price}", itemStack.getAmount() + " " + getMessage().toTitleCase(materialName))
                                    .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(result))
                            ));
                        }
                        itemStack.setAmount(itemStack.getAmount() - itemStack.getAmount());
                    }
                }
                getMaterials().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.money")
                        .replace("{price}", getMessage().toTitleCase(materialName))
                        .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
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
                        .replace("{reward}", getEconomy().currencyNamePlural() + getEconomy().format(reward))
                ));
            }
            getMaterials().open(player, shop);
            getShop().playPurchase(player);
        }
    }
    public void rewardItem(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var type = getMaterials().getRewardMaterial(meta, clickType);
        var amount = getMaterials().getRewardMaterialAmount(meta, clickType);
        var itemStack = getMaterials().getItemStack(type, amount);
        var enchantments = getMaterials().getRewardMaterialEnchantments(meta, clickType);
        if (enchantments != null) {
            getMaterials().addEnchantments(itemStack, enchantments);
        }
        var priceType = getMaterials().getPriceType(meta, clickType);
        var message = getMaterials().getMessage(meta, clickType);
        var shop = getMaterials().getShop(meta);
        var skullKey = getMaterials().getRewardSkull(meta, clickType);
        if (skullKey != null) {
            var displayName = getMaterials().getDisplayName(meta);
            if (displayName != null) {
                getMaterials().setOwningHead(itemStack, displayName, skullKey);
            }
        }
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterials().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    return;
                }
                getMaterials().giveItemStack(player, itemStack);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                            .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                    ));
                }
                getMaterials().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.item")
                        .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                        .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var type2 = getMaterials().getPriceMaterial(meta, clickType);
            var amount2 = getMaterials().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), type2, amount2)) {
                getMaterials().giveItemStack(player, itemStack);
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount2 + " " + getMessage().toTitleCase(type2))
                            .replace("{reward}", amount + " " + getMessage().toTitleCase(type))
                    ));
                }
                getMaterials().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-items.item")
                        .replace("{price}", amount2 + " " + getMessage().toTitleCase(type2))
                        .replace("{reward}",  amount + " " + getMessage().toTitleCase(type))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.nothing)) {
            getMaterials().giveItemStack(player, itemStack);
            if (message != null) {
                player.sendMessage(getMessage().get("insufficient-items.item")
                        .replace("{price}", getMessage().toTitleCase("nothing"))
                        .replace("{reward}",  amount + " " + getMessage().toTitleCase(type))
                );
            }
            getMaterials().open(player, shop);
            getShop().playPurchase(player);
        }
    }
    public void rewardCommand(Player player, ItemMeta meta, Shops.ClickType clickType) {
        var priceType = getMaterials().getPriceType(meta, clickType);
        var commands = getMaterials().getRewardCommands(meta, clickType);
        var message = getMaterials().getMessage(meta, clickType);
        var shop = getMaterials().getShop(meta);
        if (priceType.equals(Shops.PriceType.money)) {
            var price = getMaterials().getPriceDouble(meta, clickType);
            if (getEconomy().has(player, price)) {
                if (!getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                    return;
                }
                for(var command : commands) {
                    getInstance().dispatchCommand(command.replace("%player%", player.getName()));
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                    ));
                }
                getMaterials().open(player, shop);
                getShop().playPurchase(player);
            } else {
                player.sendMessage(getMessage().get("insufficient-funds.command")
                        .replace("{price}", getEconomy().currencyNamePlural() + getEconomy().format(price))
                );
                getShop().playInsufficient(player);
            }
        } else if (priceType.equals(Shops.PriceType.item)) {
            var material = getMaterials().getPriceMaterial(meta, clickType);
            var amount = getMaterials().getPriceMaterialAmount(meta, clickType);
            if (getInventoryHandler().removeItemStack(player.getInventory(), material, amount)) {
                for(var command : commands) {
                    getInstance().dispatchCommand(command.replace("%player%", player.getName()));
                }
                if (message != null) {
                    player.sendMessage(getMessage().addColor(message
                            .replace("{price}", amount + " " + getMessage().toTitleCase(material))
                    ));
                }
                getMaterials().open(player, shop);
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
            getMaterials().open(player, shop);
            getShop().playPurchase(player);
        }
    }
}