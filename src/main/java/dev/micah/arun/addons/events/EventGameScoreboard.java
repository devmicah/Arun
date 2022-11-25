package dev.micah.arun.addons.events;

import dev.micah.arun.Arun;
import dev.micah.arun.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Scoreboard;

public class EventGameScoreboard extends Event implements Cancellable {

    private Player toSend;
    private Game game;
    private Scoreboard scoreboard;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;

    public EventGameScoreboard(Player toSend, Scoreboard scoreboard) {
        this.toSend = toSend;
        this.scoreboard = scoreboard;
        this.game = Arun.getGameManager().getPlayersGame(toSend);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return Gets the player the scoreboard was sent to
     */
    public Player getToSend() {
        return toSend;
    }

    /**
     * @return Gets the scoreboard that was sent
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Game getGame() {
        return game;
    }

}
