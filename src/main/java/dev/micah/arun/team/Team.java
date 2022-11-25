package dev.micah.arun.team;

import org.bukkit.entity.Player;

import java.util.List;

public class Team {

    private List<Player> teamPlayers;
    private String teamName;

    public Team(List<Player> teamPlayers, String teamName) {
        this.teamPlayers = teamPlayers;
        this.teamName = teamName;
    }

    public List<Player> getTeamPlayers() {
        return teamPlayers;
    }

    public String getTeamName() {
        return teamName;
    }

}
