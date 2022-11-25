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

public class ItemCoreBlockWand implements Listener {

    public ItemStack getItem(String gameName) {
        return ItemBuilder.createItem(Material.STICK, "&cCore Block Wand - " + gameName, " ",
                "&cLeft Click &7a block to select it.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getItem().getType() == Material.STICK && event.getItem().getItemMeta() != null) {
                String gameName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().split("-")[1].substring(1));
                if (event.getItem().getItemMeta().getDisplayName().contains("Core Block Wand")) {
                    int offsetAmount = Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4;
                    if (Arun.getGameAPI().getTeamsWithCoreBlocks(gameName).size() - offsetAmount == 0) {
                        event.getPlayer().sendMessage(Chat.color("&c[Arun] &7The core block locations are already all set!"));
                        event.setCancelled(true);
                        return;
                    }
                    startConversation(event.getPlayer(), gameName, event.getClickedBlock().getLocation());
                    event.setCancelled(true);
                }
            }
        }
    }

    private void startConversation(Player p, String gameName, Location blockLoc) {
        ConversationFactory conversationFactory = new ConversationFactory(Arun.getPlugin(Arun.class));
        Conversation conversation = conversationFactory.withFirstPrompt(new CoreBlockConversation(gameName, blockLoc)).withLocalEcho(false).buildConversation(p);
        conversation.begin();
    }

}

class CoreBlockConversation extends StringPrompt {

    private String gameName;
    private Location blockLoc;

    public CoreBlockConversation(String gameName, Location blockLoc) {
        this.gameName = gameName;
        this.blockLoc = blockLoc;
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
        if (teamsLeft.equals("")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7The core blocks have already been set for each team!"));
        }
        return Chat.color("&c[Arun] &7Type the name of one of the following teams that need a core block or type &c'exit' &7to leave: " + teamsLeft);
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("exit")) {
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] Block un-selected and conversation exited."));
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
            context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Failed to add core block location! Invalid team: " + input));
            return null;
        }
        Arun.getGameAPI().addCoreBlockLocation(gameName, blockLoc, selectedTeam.substring(2));
        context.getForWhom().sendRawMessage(Chat.color("&c[Arun] &7Added a core block location for team " + selectedTeam));
        return null;
    }

    private boolean containsTeamName(String name, String gameName) {
        for (String s : Arun.getGameAPI().getTeamsWithCoreBlocks(gameName)) {
            if (s.toUpperCase().contains(name.toUpperCase()) || s.toUpperCase().equals(name.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

}
