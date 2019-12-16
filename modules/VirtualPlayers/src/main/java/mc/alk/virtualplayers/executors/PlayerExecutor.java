package mc.alk.virtualplayers.executors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.api.Vps;
import mc.alk.virtualplayers.util.DamageUtil;
import mc.alk.virtualplayers.util.InventoryUtil;
import mc.alk.virtualplayers.util.Util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author alkarin
 */
public class PlayerExecutor extends VPBaseExecutor {

    public PlayerExecutor() {
        super(Bukkit.getPluginManager().getPlugin("VirtualPlayers"), 1);
        super.useAlias = 0;
    }

    public PlayerExecutor(Plugin p) {
        super(p, 1);
        super.useAlias = 0;
    }

    @MCCommand(op = true)
    public boolean runCommand(CommandSender sender, VirtualPlayer vp, String... args) {
        String command = StringUtils.join(args, " ");
        sendMessage(sender, "&6 " + vp.getName() + "&2 executing '&6" + command + "&2'");
        PlayerCommandPreprocessEvent pcpe = new PlayerCommandPreprocessEvent(vp, "/" + command);
        Bukkit.getPluginManager().callEvent(pcpe);
        if (pcpe.isCancelled()) {
            return sendMessage(sender, "&6 " + vp.getName() + "&c command cancelled : &6" + command);
        }

        vp.performCommand(command);
        // Bukkit.getServer().dispatchCommand(vp, command);
        return true;
    }

    @MCCommand(cmds = {"showScoreboard"}, op = true)
    public boolean showScoreboard(CommandSender sender, VirtualPlayer vp) {
        Scoreboard sc = vp.getScoreboard();
        if (sc == null) {
            return sendMessage(sender, "&4Scoreboard for " + vp.getName() + " is null");
        }
        sendMessage(sender, "&4Scoreboard &f" + sc.hashCode());
        sendMessage(sender, "&e -- Teams -- ");
        Collection<OfflinePlayer> ops = sc.getPlayers();
        for (Team t : sc.getTeams()) {
            sendMessage(sender, t.getName() + " - " + t.getDisplayName());
        }
        for (Objective o : sc.getObjectives()) {
            sendMessage(sender, "&2 -- Objective &e" + o.getName() + " - " + o.getDisplayName());
            for (OfflinePlayer op : ops) {
                Score score = o.getScore(op);
                if (score == null) {
                    continue;
                }
                sendMessage(sender, op.getName() + " : " + score.getScore());
            }
        }
        return true;
    }

    @MCCommand(cmds = {"status"}, op = true)
    public boolean constructStatusMessages(CommandSender sender, VirtualPlayer vp) {
        return sendMessage(sender, "&4" + vp);
    }

    @MCCommand(cmds = {"op"}, op = true)
    public boolean opPlayer(CommandSender sender, VirtualPlayer vp) {
        return opPlayer(sender, vp, true);
    }

    @MCCommand(cmds = {"deop"}, op = true)
    public boolean deopPlayer(CommandSender sender, VirtualPlayer vp) {
        return opPlayer(sender, vp, false);
    }

    private boolean opPlayer(CommandSender sender, VirtualPlayer vp, boolean op) {
        vp.setOp(op);
        String opped = op ? "opped" : "deopped";
        return sendMessage(sender, "&6" + vp.getName() + "&2 " + opped + "!");
    }

    @MCCommand(cmds = {"click", "interact"}, op = true, usage = "/dc <vp> click <left|right> <location>")
    public boolean interactEvent(CommandSender sender, VirtualPlayer vp, String lr, Location l) {
        boolean left = lr.equalsIgnoreCase("left");
        Action action;
        if (l.getBlock().getType() == Material.AIR) {
            action = left ? Action.LEFT_CLICK_AIR : Action.RIGHT_CLICK_AIR;
        } else {
            action = left ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK;
        }
        ItemStack inhand = vp.getItemInHand();
        // Block b = w.getBlockAt(l);
        Block b = l.getWorld().getBlockAt(l);
        // PlayerInteractEvent(final Player who, final Action action, final
        // ItemStack item,
        // final Block clickedBlock, final BlockFace clickedFace) {
        PlayerInteractEvent ede = new PlayerInteractEvent(vp, action, inhand, b, BlockFace.EAST);
        Bukkit.getPluginManager().callEvent(ede);
        sendMessage(sender, "&6" + vp.getName() + "&e " + action.name() + " &4"
                + b.getType() + "&2  with a &a" + inhand.getType().name());
        if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST) {
            Sign s = (Sign) b.getState();
            String[] lines = s.getLines();
            for (int i = 1; i <= lines.length; i++) {
                sendMessage(sender, "&6Line: " + i + "=&f'" + lines[i - 1] + "&f'");
            }
        }

        return true;
    }

    @MCCommand(cmds = {"sneak"}, op = true)
    public boolean sneak(CommandSender sender, VirtualPlayer vp, boolean sneak) {
        vp.setSneaking(sneak);
        if (sneak) {
            return sendMessage(sender, "&6" + vp.getName() + "&e is now &8sneaking");
        } else {
            return sendMessage(sender, "&6" + vp.getName() + "&e is now &fnot sneaking");
        }
    }

    @MCCommand(cmds = {"examine"}, op = true)
    public boolean examine(CommandSender sender, VirtualPlayer vp, Location l) {
        Block b = l.getWorld().getBlockAt(l);

        sendMessage(sender, "&6" + vp.getName() + "&e examining location " + l + " &4" + b.getType());
        if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST) {
            Sign s = (Sign) b.getState();
            String[] lines = s.getLines();
            for (int i = 1; i <= lines.length; i++) {
                sendMessage(sender, "&6Line: " + i + "=&f'" + lines[i - 1] + "&f'");
            }
        }
        return true;
    }
    
    @MCCommand(cmds = {"movetome", "movehere"}, op = true)
    public boolean moveVirtualPlayerToMe(CommandSender sender, VirtualPlayer vp) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "You must be a player to use this!");
            return false;
        }
        Player player = (Player) sender;
        sendMessage(player, "&6" + vp.getName() + " is now trying to come to you!");
        vp.teleport(player.getLocation());
        return true;
    }

    @MCCommand(cmds = {"tphere"}, op = true)
    public boolean teleportPlayer(Player sender, VirtualPlayer vp) {
        return teleportPlayer(sender, vp, sender.getLocation());
    }

    @MCCommand(cmds = {"tp", "teleport"}, op = true)
    public boolean teleportPlayer(CommandSender sender, VirtualPlayer vp, Location l) {
        PlayerTeleportEvent pte = new PlayerTeleportEvent(vp, vp.getLocation(),
                l, TeleportCause.COMMAND);
        Bukkit.getPluginManager().callEvent(pte);
        if (pte.isCancelled()) {
            sendMessage(sender, "&cTeleport of " + vp.getDisplayName() + " was cancelled by some plugin");
        } else {
            vp.teleport(l);
        }
        return true;
    }

    @MCCommand(cmds = {"gm", "gamemode"}, op = true)
    public boolean setGameMode(CommandSender sender, VirtualPlayer vp, GameMode gm) {
        vp.setGameMode(gm);
        sendMessage(sender, "&6" + vp.getName() + "&2 gamemode &6" + gm);
        return true;
    }

    @MCCommand(cmds = {"chat"}, op = true)
    public boolean chatEvent(CommandSender sender, final VirtualPlayer vp, String... args) {
        final String msg = Util.colorChat(StringUtils.join(args, " "));
        final Set<Player> players = new HashSet<>(Vps.getApi().getOnlinePlayers());

        System.out.println("sender = " + sender.getName());
        System.out.println("vp = " + vp.getName());
        System.out.println("args = " + Arrays.toString(args));
        System.out.println("players = " + players.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                AsyncPlayerChatEvent apce = new AsyncPlayerChatEvent(true, vp, msg, players);
                Bukkit.getPluginManager().callEvent(apce);
            }
        };
        new Thread(r).start();

        String message = "" + ChatColor.GOLD + "" + vp.getName() + "" + ChatColor.GREEN + " said " + msg;
        sendMessage(sender, message);
        return true;
    }

    @MCCommand(cmds = {"respawn"}, op = true)
    public boolean playerRespawn(CommandSender sender, VirtualPlayer vp) {
        World w = vp.getWorld();
        vp.respawn(w.getSpawnLocation());
        return sendMessage(sender, "&6" + vp.getName() + "&2 respawned!");
    }
    
    /**
     * Used to test vp.getNearbyEntities()
     */
    @MCCommand(cmds = {"getNearby", "getNearbyEntities"}, op = true)
    public boolean getNearbyEntities(CommandSender sender, VirtualPlayer vp) {
        return getNearbyEntities(sender, vp, 15);
    }
    
    /**
     * This is a preview of the PIE cmd (PlayerPickupItemEvent).
     * 
     * vp.getNearbyEntities() does not give the same results as 
     * sender.getNearbyEntities() when they both have the same location.
     * 
     * sender.getNearbyEntities() works.
     * vp.getNearbyEntities() does NOT work.
     * vp.getNearbyEntities() always returns an empty List.
     */
    @MCCommand(cmds = {"getNearby", "getNearbyEntities"}, op = true)
    public boolean getNearbyEntities(CommandSender sender, VirtualPlayer vp, Integer radius) {
        List<Entity> eList = vp.getNearbyEntities(radius, radius, radius); // broken
        Collection<Entity> entities = Util.getNearbyEntities(vp, radius);
        sender.sendMessage("--------------------------");
        sender.sendMessage("Util.getNearbyEntities(vp)");
        sender.sendMessage("VirtualPlayer " + vp.getName() + " has found " + entities.size() + " nearby entities (Util).");
        for (Entity e : entities) {
            sender.sendMessage(" - " + e.getType().name() );
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            entities = player.getNearbyEntities(radius, radius, radius);
            sender.sendMessage("There are " + entities.size() + " entities near the CommandSender.");
            for (Entity e : entities) {
                sender.sendMessage(" - " + e.getType().name() );
            }
        }
        sender.sendMessage("--------------------------");
        sender.sendMessage("vp.getNearbyEntities()");
        sender.sendMessage("VirtualPlayer " + vp.getName() + " has found " + eList.size() + " nearby entities (vp).");
        for (Entity e : entities) {
            sender.sendMessage(" - " + e.getType().name() );
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            entities = player.getNearbyEntities(radius, radius, radius);
            sender.sendMessage("There are " + entities.size() + " entities near the CommandSender.");
            for (Entity e : entities) {
                sender.sendMessage(" - " + e.getType().name() );
            }
        }
        sender.sendMessage("--------------------------");
        return true;
    }
    
    /**
     * These methods do not return the same results:.
     * <pre>
     * - vp.getLocation().getChunk().getEntities()
     * - vp.getNearbyEntities()
     * 
     * When conditions are the same, and when the same results are expected.
     * </pre>
     * 
     * vp.getLocation().getChunk().getEntities() works.
     * vp.getNearbyEntities() does NOT work.
     * vp.getNearbyEntities() always returns an empty List.
     */
    @MCCommand(cmds = {"getChunk", "getChunkEntities"}, op = true)
    public boolean getChunkEntities(CommandSender sender, VirtualPlayer vp) {
        Entity[] entities = vp.getLocation().getChunk().getEntities();
        sender.sendMessage("VirtualPlayer " + vp.getName() + " has found " + entities.length + " entities in the chunk.");
        for (int x = 0; x < entities.length; x++) {
            Entity e = entities[x];
            if (e instanceof Player) {
                Player p = (Player) e;
                sender.sendMessage(" - " + p.getName() );
            } else {
                sender.sendMessage(" - " + entities[x].getType().name() );
            }
        }
        return true;
    }
    
    @MCCommand(cmds = {"pie", "pickupItems", "PickupItemEvent"}, op = true)
    public boolean pickupItemEvent(CommandSender sender, VirtualPlayer vp) {
        return pickupItemEvent(sender, vp, 16);
    }

    @MCCommand(cmds = {"pie", "pickupItems", "PickupItemEvent"}, op = true)
    public boolean pickupItemEvent(CommandSender sender, VirtualPlayer vp, Integer radius) {
        PlayerPickupItemEvent pie;
        Location loc = vp.getLocation();
        Collection<Entity> entities = Util.getNearbyEntities(vp, radius);
        List<Item> items = new ArrayList<Item>();
        for (Entity entity : entities) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                Item item = null;
                try {
                    item = (Item) entity;
                } catch (ClassCastException ex) {
                    continue;
                }
                items.add(item);
                sender.sendMessage("new PlayerPickupItemEvent queued for " + item.getItemStack().getType().name());
            }
        }
        int total = 0;
        for (int i = 0; i < items.size(); i = i + 1) {
            int numItemsOnGround = items.size() - (i + 1);
            pie = new PlayerPickupItemEvent(vp, items.get(i), numItemsOnGround);
            Bukkit.getPluginManager().callEvent(pie);
            if (pie.isCancelled()) {
                sendMessage(sender, "&cPlayerPickupItemEvent cancelled for &a" + items.get(i).getItemStack().getType().name());
            } else {
                Map<Integer, ItemStack> excess = vp.getInventory().addItem(items.get(i).getItemStack());
                if (excess.isEmpty()) {
                    items.get(i).remove(); // remove the item from the ground
                    total = total + 1;
                }
            }
        }
        if (items.isEmpty()) {
            String locationXYZ = "x=" + loc.getBlockX() + " y=" + loc.getBlockY() + " z=" + loc.getBlockZ();
            sendMessage(sender, "&cThere were no items to pickup at that location: &e" + locationXYZ);
            return false;
        }
        sendMessage(sender, "&6" + vp.getName() + " &2picked up a total of " + total + " item stacks.");
        return true;
    }

    @MCCommand(cmds = {"bpe", "BlockPlaceEvent"}, op = true)
    public boolean blockPlaceEvent(CommandSender sender, VirtualPlayer vp, Material mat, Location loc) {

        Block replaced = loc.getBlock();
        Material old = replaced.getType();
        replaced.setType(mat);
        BlockPlaceEvent bpe = new BlockPlaceEvent(replaced,
                replaced.getState(), replaced.getRelative(BlockFace.NORTH),
                vp.getItemInHand(), vp, true);
        Bukkit.getPluginManager().callEvent(bpe);
        if (bpe.isCancelled()) {
            replaced.setType(old);
            return sendMessage(sender, "&cBlockPlaceEvent was cancelled for &6"
                    + vp.getName());
        } else {
            return sendMessage(sender,
                    "&6" + vp.getName() + "&e placed " + mat + " on "
                    + old + "  at &4" + Util.getLocString(loc)
            );
        }
    }

    @MCCommand(cmds = {"bbe", "BlockBreakEvent"}, op = true)
    public boolean blockBreakEvent(CommandSender sender, VirtualPlayer vp, Location loc) {
        Block replaced = loc.getBlock();
        Material old = replaced.getType();
        BlockBreakEvent bpe = new BlockBreakEvent(replaced, vp);
        Bukkit.getPluginManager().callEvent(bpe);
        if (bpe.isCancelled()) {
            return sendMessage(sender, "&cBlockPlaceEvent was cancelled for &6" + vp.getName());
        } else {
            replaced.setType(Material.AIR);
            return sendMessage(sender, "&6" + vp.getName() + "&e broke the "
                    + old + " at &4" + Util.getLocString(loc));
        }
    }

    @MCCommand(cmds = {"giveInv"}, op = true)
    public boolean giveInv(CommandSender sender, VirtualPlayer vp, ItemStack is) {
        int amount = is.getAmount() > 0 ? is.getAmount() : 1;
        InventoryUtil.addItemToInventory(vp.getInventory(), is, amount);
        return sendMessage(sender, "&2Added &6" + is + "&2 to inventory");
    }

    @MCCommand(cmds = {"giveHelm"}, op = true)
    public boolean giveHelm(CommandSender sender, VirtualPlayer vp, ItemStack is) {
        vp.getInventory().setHelmet(is);
        return sendMessage(sender, "&2Added &6" + is + "&2 as the helm");
    }

    @MCCommand(cmds = {"inv", "printInv"}, op = true)
    public boolean printInv(CommandSender sender, VirtualPlayer vp) {
        PlayerInventory inv = vp.getInventory();
        if (inv != null) {
            sendMessage(sender, "&bHelm:&6" + InventoryUtil.getItemString(inv.getHelmet()));
            sendMessage(sender, "&bChest:&6" + InventoryUtil.getItemString(inv.getChestplate()));
            sendMessage(sender, "&bLegs:&6" + InventoryUtil.getItemString(inv.getLeggings()));
            sendMessage(sender, "&bBoot:&6" + InventoryUtil.getItemString(inv.getBoots()));
            sendMessage(sender, "&4InHand:&6" + InventoryUtil.getItemString(inv.getItemInHand()));
            ItemStack[] contents = inv.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack is = contents[i];
                if (is != null && is.getType() != Material.AIR) {
                    sendMessage(sender, "&2" + i + ":&e" + InventoryUtil.getItemString(is));
                }
            }
        }
        return true;
    }

    @MCCommand(cmds = {"health"}, op = true)
    public boolean health(CommandSender sender, VirtualPlayer damagee, double h, String... args) {
        boolean force = (args.length > 0 && args[0].equalsIgnoreCase("force"));
        EntityDamageEvent event = new EntityDamageEvent(
                damagee,
                DamageCause.ENTITY_ATTACK,
                damagee.getHealth() - h);
        /*
         EntityDamageEvent ede = PlayerExecutor.callEntityDamageEvent(
         null,
         damagee.getHandle(),
         EntityDamageEvent.DamageCause.ENTITY_ATTACK, // damagee.getHealth() - h,
         null,
         null); */
        if (!event.isCancelled() || force) {
            damagee.setLastDamageCause(event);
            damagee.setHealth(damagee.getHealth() - event.getDamage());
            return sendMessage(sender, damagee, "&6" + damagee.getName() + "&2, life=&4" + damagee.getHealth());

        } else {
            return sendMessage(sender, damagee, "&cHealth set was cancelled for &6" + damagee.getName());
        }
    }

    @MCCommand(cmds = {"hit"}, op = true)
    public boolean damageEvent(CommandSender sender, Player damager, Player damagee, int damage) {
        if (damagee.getHealth() <= 0) {
            return sendMessage(sender, "&6" + damagee.getName() + "&c is already dead!");
        }
        EntityDamageEvent ede = new EntityDamageByEntityEvent(
                damager, damagee,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                damage);
        /*
         EntityDamageEvent ede = CraftEventFactory.callEntityDamageEvent(
         ((Entity) ((CraftPlayer) damager).getHandle()),
         ((CraftPlayer) damagee).getHandle(),
         EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
         */
        if (!ede.isCancelled()) {
            damagee.setLastDamageCause(ede);
            if (damagee instanceof VirtualPlayer) {
                damagee.setHealth(damagee.getHealth() - ede.getDamage());
            } else {
                DamageUtil.damageEntity(damagee, ede.getDamage());
            }
            return sendMessage(sender, "&6" + damager.getName() + "&2 hit &6"
                    + damagee.getName() + "&2 for &6" + damage + "&2, life=&4"
                    + damagee.getHealth());
        } else {
            return sendMessage(sender, "&cDamage Event was cancelled for &6"
                    + damager.getName() + "&c hitting &6" + damagee.getName());
        }
    }

    @MCCommand(cmds = {"kill"}, op = true)
    public boolean playerKill(VirtualPlayer vp) {
        Server cserver = Bukkit.getServer();
        List<ItemStack> is = new LinkedList<ItemStack>();
        vp.setHealth(0.0);
        vp.damage(100000.0);
        PlayerDeathEvent ede = new PlayerDeathEvent(vp, is, 0, "");
        cserver.getPluginManager().callEvent(ede);
        return true;
    }

    @MCCommand(cmds = {"connect"}, op = true)
    public boolean playerConnect(CommandSender sender, final VirtualPlayer vp) {
        return playerConnection(sender, vp, true);
    }

    @MCCommand(cmds = {"dc", "disconnect"}, op = true)
    public boolean playerDisconnect(CommandSender sender, final VirtualPlayer vp) {
        return playerConnection(sender, vp, false);
    }

    private boolean playerConnection(CommandSender sender, final VirtualPlayer vp, boolean connecting) {
        vp.setOnline(connecting);
        if (connecting) {
            // PreLogin Event has to be called from a thread other than the main thread.
            final String playerName = vp.getName();
            Runnable r = () -> {
                try {
                    AsyncPlayerPreLoginEvent playerPreLoginEvent = new AsyncPlayerPreLoginEvent(
                            playerName, InetAddress.getLocalHost());
                    Bukkit.getPluginManager().callEvent(playerPreLoginEvent);
                } catch (UnknownHostException ex) {/* say nothing */

                }
                /// After we are done Asynchronously handling the preloginevent
                /// Sync back up and call the Login and Join events
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            // Then the Login Event.
                            Server cserver = Bukkit.getServer();
                            try {
                                PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(
                                        vp, "localhost", InetAddress
                                        .getLocalHost());
                                cserver.getPluginManager().callEvent(
                                        playerLoginEvent);
                            } catch (UnknownHostException ex) {/* do nothing */

                            }

                            // Finally, the player join event.
                            PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(
                                    vp, "\u00A7e" + vp.getName()
                                    + " joined the game.");
                            cserver.getPluginManager().callEvent(playerJoinEvent);
                        });

            };
            new Thread(r).start();
        } else { /// Disconnecting
            Server cserver = Bukkit.getServer();
            PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(vp, "\u00A7e"
                    + vp.getName() + " left the game.");
            cserver.getPluginManager().callEvent(playerQuitEvent);
        }
        String msg = "&6" + vp.getName() + "&2 "
                + (connecting ? "connecting. Details:&6" + vp : "&cdisconnecting");
        sendMessage(sender, vp, msg);
        return true;
    }
}
