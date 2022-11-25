package dev.micah.arun.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    public List<Team> generateTeams(List<Player> lobbyPlayers, TeamMode teamMode, boolean twoPlatforms) {
        String[] teamNames = new String[] {"RED", "BLUE", "GREEN", "YELLOW"};
        int playersNeededTotal = twoPlatforms ? teamMode.getTotalLobbySize() : teamMode.getTotalLobbySize()*2;
        List<Player> allPlayers = new ArrayList<>(lobbyPlayers);
        if (lobbyPlayers.size() == playersNeededTotal) {
            List<Team> teams = new ArrayList<>();
            for (String s : teamNames) {
                if (twoPlatforms && s.equals("GREEN")) {
                    break;
                }
                List<Player> players = new ArrayList<>();
                while (players.size() != teamMode.getTotalLobbySize() / 2) {
                    players.add(allPlayers.get(0));
                    allPlayers.remove(allPlayers.get(0));
                }
                teams.add(new Team(players, s));
            }
            return teams;
        } else {
            Bukkit.getLogger().severe("Could not generate teams! Invalid number of players given.");
        }
        return null;
    }

}
