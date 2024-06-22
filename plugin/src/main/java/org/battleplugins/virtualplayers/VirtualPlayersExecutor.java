package org.battleplugins.virtualplayers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.function.TriConsumer;
import org.battleplugins.virtualplayers.api.Observer;
import org.battleplugins.virtualplayers.api.VirtualPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class VirtualPlayersExecutor implements TabExecutor {
    private final VirtualPlayersPlugin plugin;
    private final Map<CommandSender, VirtualPlayer> selectedPlayers = new WeakHashMap<>();

    private final Map<String, SimpleExecutor> simpleCommands = Map.of(
            "create", new SimpleExecutor("Creates a new virtual player.", Arguments.of("name"), this::createVirtualPlayer),
            "remove", new SimpleExecutor("Removes a virtual player.", Arguments.of("name"), this::removeVirtualPlayer),
            "removeall", new SimpleExecutor("Removes all virtual players.", Arguments.empty(), (sender, ignored) -> this.removeAllVirtualPlayers(sender)),
            "select", new SimpleExecutor("Selects a virtual player.", Arguments.of("player"), this::selectVirtualPlayer),
            "observe", new SimpleExecutor("Observes a virtual player.", Arguments.of("player"), this::observeVirtualPlayer),
            "unobserve", new SimpleExecutor("Unobserves a virtual player.", Arguments.of("player"), this::unobserveVirtualPlayer),
            "list", new SimpleExecutor("Lists virtual players.", Arguments.empty(), (sender, ignored) -> this.listVirtualPlayers(sender))
    );

    private final Map<String, VpExecutor> virtualPlayerCommands = Map.of(
            "verbose", new VpExecutor("Toggles verbosity for the virtual player.", Arguments.empty().optional("true|false"), this::verbose),
            "teleport", new VpExecutor("Teleports the virtual player to a location.", Arguments.of("location"), this::teleportVirtualPlayer),
            "command", new VpExecutor("Runs a command as a virtual player.", Arguments.of("command..."), this::runCommand),
            "chat", new VpExecutor("Sends a chat message as a virtual player.", Arguments.of("message..."), this::chat),
            "op", new VpExecutor("Ops the virtual player.", Arguments.empty(), (sender, player, ignored) -> this.op(sender, player)),
            "deop", new VpExecutor("De-ops the virtual player.", Arguments.empty(), (sender, player, ignored) -> this.deop(sender, player)),
            "attack", new VpExecutor("Attacks another player as this virtual player.", Arguments.of("target").optional("damage"), this::attack)
    );

    private final Map<String, Executor> allExecutors;

    public VirtualPlayersExecutor(VirtualPlayersPlugin plugin) {
        this.plugin = plugin;

        this.allExecutors = new HashMap<>();
        this.allExecutors.putAll(this.simpleCommands);
        this.allExecutors.putAll(this.virtualPlayerCommands);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.sendHelp(sender, label);
            return true;
        }

        SimpleExecutor simpleCommand = this.simpleCommands.get(args[0]);
        if (simpleCommand != null && ((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("removeall")) || args.length == 2)) {
            if (!hasPermission(sender, args[0])) {
                sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
                return true;
            }

            simpleCommand.consumer().accept(sender, args.length == 1 ? "" : args[1]);
            return true;
        }

        boolean entered = false;
        VirtualPlayer virtualPlayer = this.selectedPlayers.get(sender);
        if (virtualPlayer == null) {
            if (args.length < 2) {
                Executor executor = this.allExecutors.get(args[0]);
                if (executor != null) {
                    this.sendHelp(sender, label, args[0], executor);
                } else {
                    if (!hasPermission(sender, "select")) {
                        sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
                        return true;
                    }

                    // Select virtual player
                    this.selectVirtualPlayer(sender, args[0]);
                }
                return true;

            }

            entered = true;
        }

        VpExecutor virtualPlayerCommand = this.virtualPlayerCommands.get(args[0]);
        if (virtualPlayerCommand != null) {
            if (!hasPermission(sender, args[0])) {
                sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
                return true;
            }

            String[] newArgs;
            if (entered) {
                virtualPlayer = this.plugin.getVirtualPlayer(args[1]);
                if (virtualPlayer == null) {
                    sender.sendMessage(Component.text("Virtual player with name " + args[1] + " not found!", NamedTextColor.RED));
                    return true;
                }

                newArgs = new String[args.length - 2];
                System.arraycopy(args, 2, newArgs, 0, newArgs.length);
            } else {
                newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            }

            virtualPlayerCommand.consumer().accept(sender, virtualPlayer, String.join(" ", newArgs));
            return true;
        }

        this.sendHelp(sender, label);
        return true;
    }

    public void sendHelp(CommandSender sender, String label) {
        if (!hasPermission(sender, "help")) {
            sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Util.HEADER);

        Map<String, Executor> executors = new HashMap<>();
        executors.putAll(this.simpleCommands);
        executors.putAll(this.virtualPlayerCommands);

        // Sort alphabetical
        executors.keySet().stream()
                .sorted()
                .forEach(command -> {
                    Executor executor = executors.get(command);
                    String args = executor.describeArgs();
                    sender.sendMessage(Component.text("/" + label + " " + command + (args.isEmpty() ? "" : " " + args), NamedTextColor.AQUA)
                            .append(Component.text(" " + executor.description(), NamedTextColor.DARK_AQUA)));
                });
    }

    public void sendHelp(CommandSender sender, String label, String cmd, @Nullable Executor executor) {
        if (executor == null) {
            this.sendHelp(sender, label);
            return;
        }

        sender.sendMessage(Component.text("Invalid syntax! " +
                "Usage: /" + label + " " + cmd + " " + executor.describeArgs(),
                NamedTextColor.RED
        ));
    }

    private void createVirtualPlayer(CommandSender sender, String name) {
        if (this.plugin.getVirtualPlayer(name) != null) {
            sender.sendMessage(Component.text("Virtual player with name " + name + " already exists!", NamedTextColor.RED));
            return;
        }

        VirtualPlayer virtualPlayer;
        try {
            virtualPlayer = this.plugin.createVirtualPlayer(name);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            return;
        }

        virtualPlayer.addObserver(sender);

        sender.sendMessage(Component.text("Virtual player with name " + name + " created!", NamedTextColor.GREEN));
    }

    private void removeVirtualPlayer(CommandSender sender, String name) {
        VirtualPlayer virtualPlayer = this.plugin.getVirtualPlayer(name);
        if (virtualPlayer == null) {
            sender.sendMessage(Component.text("Virtual player with name " + name + " not found!", NamedTextColor.RED));
            return;
        }

        this.plugin.removeVirtualPlayer(virtualPlayer);
        sender.sendMessage(Component.text("Virtual player with name " + name + " removed!", NamedTextColor.GREEN));
    }

    private void removeAllVirtualPlayers(CommandSender sender) {
        this.plugin.removeAllVirtualPlayers();
        sender.sendMessage(Component.text("All virtual players removed!", NamedTextColor.GREEN));
    }

    private void selectVirtualPlayer(CommandSender sender, String name) {
        VirtualPlayer virtualPlayer = this.plugin.getVirtualPlayer(name);
        if (virtualPlayer == null) {
            sender.sendMessage(Component.text("Virtual player with name " + name + " not found!", NamedTextColor.RED));
            return;
        }

        this.selectedPlayers.put(sender, virtualPlayer);
        sender.sendMessage(Component.text("Selected virtual player " + virtualPlayer.getName(), NamedTextColor.GREEN));
    }

    private void observeVirtualPlayer(CommandSender sender, String name) {
        VirtualPlayer virtualPlayer = this.plugin.getVirtualPlayer(name);
        if (virtualPlayer == null) {
            sender.sendMessage(Component.text("Virtual player with name " + name + " not found!", NamedTextColor.RED));
            return;
        }

        virtualPlayer.addObserver(sender);
        sender.sendMessage(Component.text("Now observing virtual player " + virtualPlayer.getName(), NamedTextColor.GREEN));
    }

    private void unobserveVirtualPlayer(CommandSender sender, String name) {
        VirtualPlayer virtualPlayer = this.plugin.getVirtualPlayer(name);
        if (virtualPlayer == null) {
            sender.sendMessage(Component.text("Virtual player with name " + name + " not found!", NamedTextColor.RED));
            return;
        }

        virtualPlayer.addObserver(sender);
        sender.sendMessage(Component.text("No longer observing virtual player " + virtualPlayer.getName(), NamedTextColor.GREEN));
    }

    private void listVirtualPlayers(CommandSender sender) {
        List<VirtualPlayer> virtualPlayers = this.plugin.getVirtualPlayers();
        if (virtualPlayers.isEmpty()) {
            sender.sendMessage(Component.text("There are no virtual players!", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Util.HEADER);
        for (VirtualPlayer virtualPlayer : virtualPlayers) {
            sender.sendMessage(Component.text("- ", NamedTextColor.GRAY).append(Component.text(" " + virtualPlayer.getName(), NamedTextColor.AQUA)));
        }
    }

    private void verbose(CommandSender sender, VirtualPlayer virtualPlayer, String verbose) {
        Boolean verboseValue = verbose.isEmpty() ? null : Boolean.parseBoolean(verbose);
        for (Observer observer : virtualPlayer.getObservers()) {
            if (sender.equals(observer.getSender())) {
                observer.setVerbose(verboseValue == null ? !observer.isVerbose() : verboseValue);

                sender.sendMessage(Component.text("Set verbosity for virtual player " + virtualPlayer.getName() + " to " + observer.isVerbose(), NamedTextColor.GREEN));
                return;
            }
        }

        sender.sendMessage(Component.text("You are not an observer of virtual player " + virtualPlayer.getName() + "! You must observe them before enabling verbosity.", NamedTextColor.RED));
    }

    private void teleportVirtualPlayer(CommandSender sender, VirtualPlayer virtualPlayer, String location) {
        Location loc;
        if (location.isBlank() && sender instanceof Player player) {
            loc = player.getLocation();
            location = "your location";
        } else {
            try {
                loc = Util.fromString(location);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                return;
            }
        }

        virtualPlayer.teleport(loc);
        sender.sendMessage(Component.text("Teleported virtual player " + virtualPlayer.getName() + " to " + location, NamedTextColor.GREEN));
    }

    private void runCommand(CommandSender sender, VirtualPlayer virtualPlayer, String command) {
        virtualPlayer.performCommand(command);
    }

    private void chat(CommandSender sender, VirtualPlayer virtualPlayer, String message) {
        virtualPlayer.chat(message);
    }

    private void op(CommandSender sender, VirtualPlayer virtualPlayer) {
        virtualPlayer.setOp(true);

        sender.sendMessage(Component.text("Opped virtual player " + virtualPlayer.getName(), NamedTextColor.GREEN));
    }

    private void deop(CommandSender sender, VirtualPlayer virtualPlayer) {
        virtualPlayer.setOp(false);

        sender.sendMessage(Component.text("Deopped virtual player " + virtualPlayer.getName(), NamedTextColor.GREEN));
    }

    private void attack(CommandSender sender, VirtualPlayer virtualPlayer, String arg) {
        // Attack the player
        String[] args = arg.split(" ");
        if (args.length == 0) {
            sender.sendMessage(Component.text("Please specify a player to attack!", NamedTextColor.RED));
            return;
        }

        Player target = this.plugin.getVirtualPlayer(args[0]);
        if (target == null) {
            // See if a real player exists
            target = this.plugin.getServer().getPlayer(args[0]);
        }

        if (target == null) {
            sender.sendMessage(Component.text("Player " + args[0] + " not found!", NamedTextColor.RED));
            return;
        }

        Double damage = args.length > 1 ? Double.parseDouble(args[1]) : null;
        if (damage != null) {
            target.damage(damage, virtualPlayer);
        } else {
            virtualPlayer.attack(target);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> results = new ArrayList<>();
            results.addAll(this.simpleCommands.keySet());
            results.addAll(this.virtualPlayerCommands.keySet());

            StringUtil.copyPartialMatches(args[0], results, completions);
        } else if (args.length == 2) {
            if (this.simpleCommands.containsKey(args[0])) {
                StringUtil.copyPartialMatches(args[1], this.plugin.getVirtualPlayers().stream().map(VirtualPlayer::getName).toList(), completions);
            } else if (this.virtualPlayerCommands.containsKey(args[0])) {
                StringUtil.copyPartialMatches(args[1], this.plugin.getVirtualPlayers().stream().map(VirtualPlayer::getName).toList(), completions);
            }
        }

        return completions;
    }

    Map<CommandSender, VirtualPlayer> getSelectedPlayers() {
        return this.selectedPlayers;
    }

    private static boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("virtualplayers.command." + node);
    }

    record SimpleExecutor(String description, Arguments args, BiConsumer<CommandSender, String> consumer) implements Executor {

        @Override
        public String describeArgs() {
            return this.args.describe();
        }
    }

    record VpExecutor(String description, Arguments args, TriConsumer<CommandSender, VirtualPlayer, String> consumer) implements Executor {

        @Override
        public String describeArgs() {
            String args = this.args.describe();
            return args.isEmpty() ? "[player]" : "[player] " + args;
        }
    }

    public interface Executor {
        String description();

        String describeArgs();
    }

    public static class Arguments {
        private final List<Argument> arguments = new ArrayList<>();

        private Arguments() {
        }

        public String describe() {
            if (this.arguments.isEmpty()) {
                return "";
            }

            return this.arguments.stream()
                    .map(argument -> argument.required() ? "<" + argument.name() + ">" : "[" + argument.name() + "]")
                    .reduce((a, b) -> a + " " + b)
                    .orElse("");
        }

        private Arguments(String... arguments) {
            for (String argument : arguments) {
                this.arguments.add(new Argument(argument, true));
            }
        }

        public Arguments required(String... arguments) {
            for (String argument : arguments) {
                this.arguments.add(new Argument(argument, true));
            }

            return this;
        }

        public Arguments optional(String... arguments) {
            for (String argument : arguments) {
                this.arguments.add(new Argument(argument, false));
            }

            return this;
        }

        private Arguments(Argument... arguments) {
            this.arguments.addAll(List.of(arguments));
        }

        public static Arguments of(String... arguments) {
            return new Arguments(arguments);
        }

        public static Arguments of(Argument... arguments) {
            return new Arguments(arguments);
        }

        public static Arguments empty() {
            return new Arguments();
        }

        public record Argument(String name, boolean required) {
        }
    }
}
