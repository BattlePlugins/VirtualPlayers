package org.battleplugins.virtualplayers.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a virtual player.
 * <p>
 * A virtual represents a non-existent, in-memory player that
 * can take actions via commands and print out the events that are
 * happening to them on the console or a player's chat. These players
 * don't show up in game, but are just used for performing actions
 * as if a player was there. Very useful if you don't even want to
 * load up Minecraft, you just boot the server and debug as if you
 * were players without even running the game.
 */
public interface VirtualPlayer extends Player {

    /**
     * Gets the observers of this virtual player.
     *
     * @return the observers of this virtual player
     */
    List<Observer> getObservers();

    /**
     * Adds an observer to this virtual player.
     *
     * @param observer the observer to add
     */
    void addObserver(CommandSender observer);

    /**
     * Adds an observer to this virtual player.
     *
     * @param observer the observer to add
     */
    void addObserver(Observer observer);

    /**
     * Removes an observer from this virtual player.
     *
     * @param observer the observer to remove
     */
    void removeObserver(Observer observer);
}
