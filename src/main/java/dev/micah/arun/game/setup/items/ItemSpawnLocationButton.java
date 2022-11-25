package dev.micah.arun.game.setup.items;

import dev.micah.arun.Arun;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSpawnLocationButton implements Listener {

    public ItemStack getItem(String gameName) {
        return ItemBuilder.createItem(Material.STONE_BUTTON, "&cSpawn Button - " + gameName, " ",
                "&cLeft Click &7&lSet Spawn", "&cShift + Left Click &7&lDelete Spawn");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR && event.getItem().getType() == Material.STONE_BUTTON) {
            if (event.getItem().getItemMeta() != null) {
                String gameName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().split("-")[1].substring(1));
                if (event.getPlayer().isSneaking()) {
                    if (Arun.getGameAPI().getSpawnLocations(gameName).size() > 0) {
                        startConversationDeleteSpawn(event.getPlayer(), gameName);
                    } else {
                        event.getPlayer().sendMessage(Chat.color("&c[Arun] &7No spawns exist!"));
                    }
                } else {
                    startConversation(event.getPlayer(), gameName);
                }
            }
        }
    }

    private void startConversation(Player p, String gameName) {
        ConversationFactory conversationFactory = new ConversationFactory(Arun.getPlugin(Arun.class));
        Conversation conversation = conversationFactory.withFirstPrompt(new SpawnLocationConversation(gameName)).withLocalEcho(false).buildConversation(p);
        conversation.begin();
    }

    private void startConversationDeleteSpawn(Player p, String gameName) {
        ConversationFactory conversationFactory = new ConversationFactory(Arun.getPlugin(Arun.class));
        Conversation conversation = conversationFactory.withFirstPrompt(new SpawnLocationRemoveConversation(gameName)).withLocalEcho(false).buildConversation(p);
        conversation.begin();
    }

}

class SpawnLocationConversation extends StringPrompt {

    private String gameName;

    public SpawnLocationConversation(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        String[] teams = new String[] {"&cRED", "&bBLUE", "&aGREEN", "&eYELLOW"};
        int neededTeams = Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4;
        String teamsLeft = "";
        for (int i = 0; i < neededTeams; i++) {
            if (!containsTeamName(teams[i].substring(2), gameName)) {
                teamsLeft += teams[i] + " ";
            }
        }
        return Chat.color("&c[Arun] &7Type the name of one of the following teams that need a spawn location or type &c'exit' &7to leave: " + teamsLeft);
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("exit")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] Conversation exited."));
            return null;
        }
        String[] teams = new String[] {"&cRED", "&bBLUE", "&aGREEN", "&eYELLOW"};
        String selectedTeam = "";
        for (String s : teams) {
            if (input.toUpperCase().equals(s.substring(2))) {
                selectedTeam = s;
            }
        }
        if (selectedTeam.equals("")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Failed to add spawn location! Invalid team: " + input));
            return null;
        }
        Arun.getGameAPI().addSpawnLocation(gameName, selectedTeam.substring(2), ((Player)context.getForWhom()).getLocation());
        context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Added a spawn location for team " + selectedTeam));
        return null;
    }

    private boolean containsTeamName(String name, String gameName) {
        return Arun.getGameAPI().getSpawnLocation(gameName, name) != null;
    }

}

class SpawnLocationRemoveConversation extends StringPrompt {

    private String gameName;

    public SpawnLocationRemoveConversation(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        String[] teams = new String[] {"&cRED", "&bBLUE", "&aGREEN", "&eYELLOW"};
        String teamsLeft = "";
        for (String team : teams) {
            Location location = Arun.getGameAPI().getSpawnLocation(gameName, team.substring(2));
            if (location != null) {
                teamsLeft += team + " ";
            }
        }
        return Chat.color("&c[Arun] &7Type the name of one of the following teams that you want to delete the spawn of or type &c'exit' &7to leave: " + teamsLeft);
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("exit")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] Conversation exited."));
            return null;
        }
        String[] teams = new String[] {"&cRED", "&bBLUE", "&aGREEN", "&eYELLOW"};
        String selectedTeam = "";
        for (String s : teams) {
            if (input.toUpperCase().equals(s.substring(2))) {
                selectedTeam = s;
            }
        }
        if (selectedTeam.equals("")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Failed to remove spawn location! Invalid team: " + input));
            return null;
        }
        Arun.getGameAPI().deleteSpawn(gameName, selectedTeam.substring(2));
        context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Removed a spawn location for team " + selectedTeam));
        return null;
    }

}