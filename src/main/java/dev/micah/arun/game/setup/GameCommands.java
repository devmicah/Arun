package dev.micah.arun.game.setup;

import dev.micah.arun.Arun;
import dev.micah.arun.economy.EconomyManager;
import dev.micah.arun.game.setup.items.ItemShopEntityWand;
import dev.micah.arun.gui.game.GuiGameManager;
import dev.micah.arun.gui.join.GuiGameJoin;
import dev.micah.arun.gui.publish.GuiPublishManager;
import dev.micah.arun.gui.region.GuiRegionManager;
import dev.micah.arun.kits.Kit;
import dev.micah.arun.kits.gui.GuiKitShop;
import dev.micah.arun.kits.gui.GuiViewKit;
import dev.micah.arun.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(Chat.color("&c[Arun] &7Invalid command! Use &c/arun help &7for a list of commands!"));
                return false;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("kitshop")) {
                    new GuiKitShop().open(p, null);
                    return false;
                }
                if (args[0].equalsIgnoreCase("join")) {
                    new GuiGameJoin().open(p, new Object[]{});
                    return false;
                }
                if (args[0].equalsIgnoreCase("shop")) {
                    p.getInventory().addItem(new ItemShopEntityWand().getItem());
                    p.sendMessage(Chat.color("&c[Arun] &cRight Click &7on a villager to set it as a shop!"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                    p.sendMessage(Chat.color("&c[Arun] &7You currently have a balance of &6$" + EconomyManager.getMoney(p)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("help")) {
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&7➠ &cMain Command: /arun <arguments>"));
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&c➤ User Commands: "));
                    p.sendMessage(Chat.color("&7➠ &c/arun help ⥤ &7Shows this page"));
                    p.sendMessage(Chat.color("&7➠ &c/arun join ⥤ &7Opens the join GUI"));
                    p.sendMessage(Chat.color("&7➠ &c/arun kitshop ⥤ &7Opens the Kit Shop"));
                    p.sendMessage(Chat.color("&7➠ &c/arun balance | bal ⥤ &7Shows you your coin balance"));
                    p.sendMessage(" ");
                    if (p.hasPermission("arun.admin")) {
                        p.sendMessage(Chat.color("&c➤ Admin Commands:"));
                        p.sendMessage(Chat.color("&7➠ &c/arun create <name> ⥤ &7Creates a game"));
                        p.sendMessage(Chat.color("&7➠ &c/arun delete <name> ⥤ &7Deletes a game"));
                        p.sendMessage(Chat.color("&7➠ &c/arun shop ⥤ &7Gives you the villager shop wand"));
                        p.sendMessage(Chat.color("&7➠ &c/arun help kit ⥤ &7Shows the kit help page"));
                        p.sendMessage(Chat.color("&7➠ &c/arun help economy | eco ⥤ &7Shows the economy help page"));
                        p.sendMessage(Chat.color("&7➠ &c/arun regionmanager | rm <game> ⥤ &7Region Manager for a specific game"));
                        p.sendMessage(Chat.color("&7➠ &c/arun gamemanager | gm <game> ⥤ &7Game Manager for a specific game"));
                        p.sendMessage(Chat.color("&7➠ &c/arun publish | p ⥤ &7Options to publish a game"));
                        p.sendMessage(" ");
                    }
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    return false;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("list")) {
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&c➤ Kits: "));
                    int index = 0;
                    for (String kit : Kit.getKits()) {
                        Kit kitObj = new Kit(kit);
                        if (kitObj.getItems() != null) {
                            index++;
                            p.sendMessage(Chat.color("&c" + index + " ➤ &7" + kitObj.getName() + " &8[-] " + (kitObj.getCost() > 0 ? "&6" + kitObj.getCost() : "&aFREE")));
                        }
                    }
                    if (index == 0) {
                        p.sendMessage(Chat.color("&cNo valid kits found!"));
                    }
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("kit")) {
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&c➤ Kit Commands: "));
                    p.sendMessage(Chat.color("&7➠ &c/arun kit create <name> <price> ⥤ &7Creates a kit for a set price, if price is 0 it will be free"));
                    p.sendMessage(Chat.color("&7➠ &c/arun kit delete <name> ⥤ &7Deletes a kit"));
                    p.sendMessage(Chat.color("&7➠ &c/arun kit view <name> ⥤ &7Shows the inventory of a kit"));
                    p.sendMessage(Chat.color("&7➠ &c/arun kit list ⥤ &7Get a list of all the existing kits"));
                    p.sendMessage(" ");
                    p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("help")) {
                    if (args[1].equalsIgnoreCase("eco") || args[1].equalsIgnoreCase("economy")) {
                        p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                        p.sendMessage(" ");
                        p.sendMessage(Chat.color("&c➤ Economy Commands: "));
                        p.sendMessage(Chat.color("&7➠ &c/arun eco set <name> <amount> ⥤ &7Sets the amount of golden ingots someone has"));
                        p.sendMessage(Chat.color("&7➠ &c/arun eco add <name> <amount> ⥤ &7Adds their current eco to the amount"));
                        p.sendMessage(Chat.color("&7➠ &c/arun eco subtract <name> <amount> ⥤ &7Subtracts their current eco from the amount"));
                        p.sendMessage(Chat.color("&7➠ &c/arun eco get <name> ⥤ &7Gets the amount of coins a player has"));
                        p.sendMessage(" ");
                        p.sendMessage(Chat.color("&c&m--------&r&c===> &r&c&l[ Arun ] &r&c<===&r&c&m--------"));
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("create") && args[1] != null && !args[1].isEmpty()) {
                    String gameName = args[1];
                    Arun.getGameAPI().createGame(gameName);
                    p.sendMessage(Chat.color("&c[Arun] &7You created a game called &c" + gameName + "&7!"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("delete") && args[1] != null && !args[1].isEmpty()) {
                    String gameName = args[1];
                    if (Arun.getGameAPI().doesGameExist(gameName)) {
                        Arun.getGameAPI().createGame(gameName);
                        p.sendMessage(Chat.color("&c[Arun] &7You delete the game called &c" + gameName + "&7!"));
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("regionmanager")) {
                    if (args[1] != null && !args[1].isEmpty()) {
                        String gameName = args[1];
                        if (Arun.getGameAPI().doesGameExist(gameName)) {
                            new GuiRegionManager().open(p, new Object[]{gameName});
                        } else {
                            p.sendMessage(Chat.color("&c[Arun] &7A game with the name of &c" + gameName + " &7does not exist!"));
                        }
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("gm") || args[0].equalsIgnoreCase("gamemanager")) {
                    if (args[1] != null && !args[1].isEmpty()) {
                        String gameName = args[1];
                        if (Arun.getGameAPI().doesGameExist(gameName)) {
                            new GuiGameManager().open(p, new Object[]{gameName});
                        } else {
                            p.sendMessage(Chat.color("&c[Arun] &7A game with the name of &c" + gameName + " &7does not exist!"));
                        }
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("publish")) {
                    if (args[1] != null && !args[1].isEmpty()) {
                        String gameName = args[1];
                        if (Arun.getGameAPI().doesGameExist(gameName)) {
                            new GuiPublishManager().open(p, new Object[]{gameName});
                        } else {
                            p.sendMessage(Chat.color("&c[Arun] &7A game with the name of &c" + gameName + " &7does not exist!"));
                        }
                        return false;
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("eco") || args[0].equalsIgnoreCase("economy")) {
                    if (args[1] != null && !args[1].isEmpty() && args[2] != null && !args[2].isEmpty()) {
                        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(args[2]);
                        if (op == null) {
                            p.sendMessage(Chat.color("&c[Arun] &7Could not find that player!"));
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("get")) {
                            p.sendMessage(Chat.color("&c[Arun] &c" + op.getName() + " &7has a balance of &6$" + EconomyManager.getMoney(op.getPlayer())));
                            return false;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("view") && args[2] != null && !args[2].isEmpty()) {
                    Kit kit = new Kit(args[2]);
                    if (Kit.getKits().contains(args[2])) {
                        new GuiViewKit().open(p, new Object[]{kit});
                    } else {
                        p.sendMessage(Chat.color("&c[Arun] &7Could not find a kit with that name!"));
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete") && args[2] != null && !args[2].isEmpty()) {
                    Kit kit = new Kit(args[2]);
                    if (Kit.getKits().contains(args[2])) {
                        kit.delete();
                        p.sendMessage(Chat.color("&c[Arun] &7Deleted kit &c" + kit.getName() + "&7!"));
                    } else {
                        p.sendMessage(Chat.color("&c[Arun] &7Could not find a kit with that name!"));
                    }
                    return false;
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("eco") || args[0].equalsIgnoreCase("economy")) {
                    if (args[1] != null && !args[1].isEmpty() && args[2] != null && !args[2].isEmpty() && args[3] != null && !args[3].isEmpty()) {
                        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(args[2]);
                        int amount = Integer.parseInt(args[3]);
                        if (op == null) {
                            p.sendMessage(Chat.color("&c[Arun] &7Could not find that player!"));
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("set")) {
                            EconomyManager.setMoney(op.getPlayer(), amount);
                            p.sendMessage(Chat.color("&c[Arun] &7Updated the balance of &c" + op.getName() + "&7 to &6$" + EconomyManager.getMoney(op.getPlayer())));
                        }
                        if (args[1].equalsIgnoreCase("add")) {
                            EconomyManager.setMoney(op.getPlayer(), EconomyManager.getMoney(op.getPlayer()) + amount);
                            p.sendMessage(Chat.color("&c[Arun] &7Updated the balance of &c" + op.getName() + "&7 to &6$" + EconomyManager.getMoney(op.getPlayer())));
                        }
                        if (args[1].equalsIgnoreCase("subtract")) {
                            EconomyManager.setMoney(op.getPlayer(), EconomyManager.getMoney(op.getPlayer()) - amount);
                            p.sendMessage(Chat.color("&c[Arun] &7Updated the balance of &c" + op.getName() + "&7 to &6$" + EconomyManager.getMoney(op.getPlayer())));
                        }
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("create") && args[2] != null && !args[2].isEmpty()
                        && args[3] != null && !args[3].isEmpty()) {
                    String name = args[2];
                    int price = Integer.parseInt(args[3]);
                    Kit kit = new Kit(name, p.getInventory().getContents());
                    kit.saveItemsToConfig();
                    kit.setPrice(price);
                    p.sendMessage(Chat.color("&c[Arun] &7You created the kit &c" + name + " &7for a cost of " + (price == 0 ? "&aFREE" : "&c$" + price + "&7!")));
                    return false;
                }
            }
            p.sendMessage(Chat.color("&c[Arun] &7Invalid command! Use &c/arun help &7for a list of commands!"));
        }
        return false;
    }

}
