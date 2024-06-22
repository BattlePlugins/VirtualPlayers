package org.battleplugins.virtualplayers.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

class ApiHolder {
    private static VirtualPlayers virtualPlayers;

    static VirtualPlayers virtualPlayers() {
        if (virtualPlayers == null) {
            RegisteredServiceProvider<VirtualPlayers> registration = Bukkit.getServicesManager().getRegistration(VirtualPlayers.class);
            if (registration == null) {
                throw new IllegalStateException("VirtualPlayers is not registered!");
            }

            virtualPlayers = registration.getProvider();
        }

        return virtualPlayers;
    }
}
