package org.battleplugins.virtualplayers.api;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The main API class for VirtualPlayers.
 */
public interface VirtualPlayers {

    /**
     * Gets all {@link VirtualPlayer}s.
     *
     * @return all virtual players
     */
    List<VirtualPlayer> getVirtualPlayers();

    /**
     * Creates a new {@link VirtualPlayer} with the given name.
     *
     * @param name the name of the virtual player
     * @return the created virtual player
     * @throws IllegalArgumentException if a virtual player with the given name already exists
     *                                  or if the name is a real player on the server
     */
    VirtualPlayer createVirtualPlayer(String name) throws IllegalArgumentException;

    /**
     * Removes the given {@link VirtualPlayer}.
     *
     * @param virtualPlayer the virtual player to remove
     */
    void removeVirtualPlayer(VirtualPlayer virtualPlayer);

    /**
     * Removes all {@link VirtualPlayer}s.
     */
    void removeAllVirtualPlayers();

    /**
     * Gets the {@link VirtualPlayer} with the given name.
     *
     * @param name the name of the virtual player
     * @return the virtual player with the given name
     */
    @Nullable
    VirtualPlayer getVirtualPlayer(String name);

    /**
     * Gets the instance of the VirtualPlayers API.
     *
     * @return the instance of the VirtualPlayers API
     */
    static VirtualPlayers api() {
        return ApiHolder.virtualPlayers();
    }
}
