package org.battleplugins.virtualplayers.api;

import org.bukkit.command.CommandSender;

public class Observer {
    private final CommandSender sender;
    private boolean verbose;

    public Observer(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
