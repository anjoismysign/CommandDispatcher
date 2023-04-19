package us.mytheria.commanddispatcher;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandDispatcher extends JavaPlugin {
    @Override
    public void onEnable() {
        WorldDispatch worldDispatch = new WorldDispatch();
        PluginCommand worldDispatchCommand = Bukkit.getPluginCommand("worlddispatch");
        worldDispatchCommand.setExecutor(worldDispatch);
        worldDispatchCommand.setTabCompleter(worldDispatch);
    }

    protected class WorldDispatch implements CommandExecutor, TabCompleter {

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!commandSender.hasPermission("commanddispatcher.admin")) {
                commandSender.sendMessage("You do not have permission to use this command");
                return true;
            }
            if (strings.length == 0) {
                commandSender.sendMessage("Please specify a world");
                return true;
            }
            World world = Bukkit.getWorld(strings[0]);
            if (world == null) {
                commandSender.sendMessage("World not found");
                return true;
            }
            String toExecute = String.join(" ", strings);
            toExecute = toExecute.substring(strings[0].length() + 1);
            String finalToExecute = toExecute;
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName()
                            .compareTo(world.getName()) == 0)
                    .forEach(player -> player.performCommand(finalToExecute));
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
            if (!command.getName().equalsIgnoreCase("worlddispatch"))
                return null;
            if (args.length == 1) {
                return Bukkit.getWorlds().stream()
                        .map(World::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    }
}
