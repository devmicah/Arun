package dev.micah.arun.game;

import dev.micah.arun.Arun;
import dev.micah.arun.addons.events.EventGameScoreboard;
import dev.micah.arun.team.Team;
import dev.micah.arun.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScoreboard {

    public GameScoreboard(Game game, Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = sb.registerNewObjective("arun", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(Chat.color("&c&lARUN"));

        List<String> configLines = Arun.getPlugin(Arun.class).getScoreboardFile().getConfig().getStringList("scoreboard");
        List<String> lines = new ArrayList<>();
        for (String line : configLines) {
            lines.add(line.replaceAll("%game_name%", game.getGameInfo().getGameName())
                    .replaceAll("%kill_points%", String.valueOf(game.getKillPoints().getOrDefault(player, 0)))
                    .replaceAll("%core_block%", hasCoreBlock(game, player) ? "&aALIVE" : "&cDEAD"));
        }
        Collections.reverse(lines);
        for (int i = 0; i < lines.size(); i++) {
            Score score = obj.getScore(Chat.color(lines.get(i)));
            score.setScore(i);
        }
        EventGameScoreboard event = new EventGameScoreboard(player, sb);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        player.setScoreboard(sb);
    }

    private boolean hasCoreBlock(Game game, Player player) {
        if (game.getTeamsWithoutCoreBlocks().isEmpty()) return true;
        for (Team t : game.getTeamsWithoutCoreBlocks()) {
            if (t.getTeamPlayers().contains(player)) {
                return false;
            }
        }
        return true;
    }

}
