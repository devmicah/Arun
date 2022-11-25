package dev.micah.arun.game.setup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GameTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("help");
            commands.add("join");
            commands.add("kitshop");
            commands.add("balance");
            commands.add("bal");
            if (sender.isOp()) {
                commands.add("economy");
                commands.add("eco");
                commands.add("create");
                commands.add("delete");
                commands.add("shop");
                commands.add("regionmanager");
                commands.add("rm");
                commands.add("gamemanager");
                commands.add("gm");
                commands.add("publish");
                commands.add("p");
                commands.add("kit");
            }
            StringUtil.copyPartialMatches(args[0], commands, result);
        } else if (args.length == 2) {
            if (sender.isOp()) {
                if (args[0].equalsIgnoreCase("help")) {
                    commands.add("kit");
                    commands.add("economy");
                }
                if (args[0].equalsIgnoreCase("kit")) {
                    commands.add("create");
                    commands.add("delete");
                    commands.add("view");
                    commands.add("list");
                }
                if (args[0].equalsIgnoreCase("eco") || args[0].equalsIgnoreCase("economy")) {
                    commands.add("set");
                    commands.add("add");
                    commands.add("subtract");
                    commands.add("get");
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, result);
        }
        return result;
    }

}
