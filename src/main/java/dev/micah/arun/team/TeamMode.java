package dev.micah.arun.team;

public enum TeamMode {

    SOLO(2, "1v1"), DUO(4, "2v2"), TRIO(6, "3v3"), SQUAD(8, "4v4");

    private int totalLobbySize;
    private String displayString;

    TeamMode(int totalLobbySize, String displayString) {
        this.totalLobbySize = totalLobbySize;
        this.displayString = displayString;
    }

    public int getTotalLobbySize() {
        return totalLobbySize;
    }

    public String getDisplayString() {
        return displayString;
    }

}
