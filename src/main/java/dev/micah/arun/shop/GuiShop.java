package dev.micah.arun.shop;

import dev.micah.arun.Arun;
import dev.micah.arun.game.Game;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.team.Team;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class GuiShop implements Gui, Listener {

    public enum PickaxeUpgrade {

        WOOD_PICKAXE(Material.WOOD_PICKAXE, 0, false, Material.STONE_PICKAXE),
        STONE_PICKAXE(Material.STONE_PICKAXE, 1, false, Material.IRON_PICKAXE),
        IRON_PICKAXE(Material.IRON_PICKAXE, 3, false, Material.DIAMOND_PICKAXE),
        DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, 5, true, null);

        private Material material;
        private int cost;
        private boolean max;
        private Material next;

        PickaxeUpgrade(Material material, int cost, boolean max, Material next) {
            this.material = material;
            this.cost = cost;
            this.max = max;
            this.next = next;
        }

        public Material getMaterial() {
            return material;
        }

        public int getCost() {
            return cost;
        }

        public boolean isMax() {
            return max;
        }

        public Material getNext() {
            return next;
        }

    }

    public enum CoreBlockUpgrade {

        COBBLESTONE(Material.COBBLESTONE, 0, false, Material.ENDER_STONE),
        ENDSTONE(Material.ENDER_STONE, 1, false, Material.IRON_BLOCK),
        DIAMOND_BLOCK(Material.DIAMOND_BLOCK, 2, false, Material.OBSIDIAN),
        OBSIDIAN(Material.OBSIDIAN, 4, true, null);

        private Material material;
        private int cost;
        private boolean max;
        private Material next;

        CoreBlockUpgrade(Material material, int cost, boolean max, Material next) {
            this.material = material;
            this.cost = cost;
            this.max = max;
            this.next = next;
        }

        public Material getMaterial() {
            return material;
        }

        public int getCost() {
            return cost;
        }

        public boolean isMax() {
            return max;
        }

        public Material getNext() {
            return next;
        }

    }


    @Override
    public void open(Player player, Object[] input) {
        Game game = (Game) input[0];

        Inventory gui = Bukkit.createInventory(null, 9 * 3, Chat.color("&cKill Points Shop"));
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, ItemBuilder.createItem(Material.IRON_BLOCK, Chat.color("&7 ")));
        }

        gui.setItem(11, createPickaxeItem(player, game));
        gui.setItem(15, createCoreBlockItem(player, game));

        player.openInventory(gui);
    }

    public ItemStack createPickaxeItem(Player p, Game game) {
        PickaxeUpgrade upgrade = next(game.getPickaxeUpgrade().getOrDefault(p, PickaxeUpgrade.WOOD_PICKAXE));
        if (game.getPickaxeUpgrade().getOrDefault(p, PickaxeUpgrade.WOOD_PICKAXE).isMax()) return ItemBuilder.createItem(Material.BARRIER, "&c&lMAX UPGRADE REACHED");
        return ItemBuilder.createItem(upgrade.getMaterial(), "&cPickaxe Upgrade", " ",
                "&cCost: &f" + upgrade.getCost() + " Kill Points");
    }

    public ItemStack createCoreBlockItem(Player p, Game game) {
        AtomicReference<Team> team = new AtomicReference<>();
        game.getTeams().forEach(t -> {
            if (t.getTeamPlayers().contains(p)) {
                team.set(t);
            }
        });
        CoreBlockUpgrade coreBlockUpgrade = next(game.getCoreBlockUpgrade().getOrDefault(team.get().getTeamName(), CoreBlockUpgrade.COBBLESTONE));
        if (game.getCoreBlockUpgrade().getOrDefault(team.get().getTeamName(), CoreBlockUpgrade.COBBLESTONE).isMax()) return ItemBuilder.createItem(Material.BARRIER, "&c&lMAX UPGRADE REACHED");
        return ItemBuilder.createItem(coreBlockUpgrade.getMaterial(), "&cCore Block Upgrade", " ",
                "&cCost: &f" + coreBlockUpgrade.getCost() + " Kill Points");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Kill Points Shop")) {
            Player p = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            Game game = Arun.getGameManager().getPlayersGame(p);
            if (game != null && item != null) {
                if (item.getType().name().toUpperCase().contains("PICKAXE")) {
                    PickaxeUpgrade next = next(game.getPickaxeUpgrade().getOrDefault(p, PickaxeUpgrade.WOOD_PICKAXE));
                    if (game.getKillPoints().getOrDefault(p, 0) >= next.getCost()) {
                        game.getPickaxeUpgrade().put(p, next);
                        Arrays.asList(p.getInventory().getContents())
                                .forEach(itemStack -> {
                                    if (itemStack != null) {
                                        if (itemStack.getType().name().toLowerCase().contains("pickaxe")) {
                                            p.getInventory().remove(itemStack);
                                        }
                                    }
                                });
                        p.sendMessage(Chat.color("&c[Arun] &7You have upgraded your pickaxe to &c" + next.getMaterial().name().replaceAll("_", " ")));
                        p.getInventory().addItem(new ItemStack(next.getMaterial()));
                        game.getKillPoints().put(p, game.getKillPoints().getOrDefault(p, 0) - next.getCost());
                        p.closeInventory();
                    } else {
                        p.closeInventory();
                        p.sendMessage(Chat.color("&c[Arun] &7You do not have enough kill points!"));
                    }
                }
                if (item.getType() != Material.IRON_BLOCK && item.getItemMeta() != null) {
                    if (item.getItemMeta().getDisplayName().contains("Core Block")) {
                        AtomicReference<Team> team = new AtomicReference<>();
                        game.getTeams().forEach(t -> {
                            if (t.getTeamPlayers().contains(p)) {
                                team.set(t);
                            }
                        });
                        CoreBlockUpgrade next = next(game.getCoreBlockUpgrade().getOrDefault(team.get().getTeamName(), CoreBlockUpgrade.COBBLESTONE));
                        if (game.getKillPoints().getOrDefault(p, 0) >= next.getCost()) {
                            game.getCoreBlockUpgrade().put(team.get().getTeamName(), next);

                            Location loc = Arun.getGameAPI().getCoreBlockLocation(game.getGameInfo().getGameName(), team.get().getTeamName());
                            loc.getBlock().setType(next.getMaterial());

                            p.closeInventory();
                            p.sendMessage(Chat.color("&c[Arun] &7You have upgraded your core block to &c" + next.getMaterial().name().replaceAll("_", " ")));
                            game.getKillPoints().put(p, game.getKillPoints().getOrDefault(p, 0) - next.getCost());
                        } else {
                            p.closeInventory();
                            p.sendMessage(Chat.color("&c[Arun] &7You do not have enough kill points!"));
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public PickaxeUpgrade next(PickaxeUpgrade pickaxeUpgrade) {
        if (pickaxeUpgrade.isMax()) return null;
        for (int i = 0; i < PickaxeUpgrade.values().length; i++) {
            if (PickaxeUpgrade.values()[i] == pickaxeUpgrade) {
                return PickaxeUpgrade.values()[i + 1];
            }
        }
        return null;
    }

    public CoreBlockUpgrade next(CoreBlockUpgrade coreBlockUpgrade) {
        if (coreBlockUpgrade.isMax()) return null;
        for (int i = 0; i < CoreBlockUpgrade.values().length; i++) {
            if (CoreBlockUpgrade.values()[i] == coreBlockUpgrade) {
                return CoreBlockUpgrade.values()[i + 1];
            }
        }
        return null;
    }

}
