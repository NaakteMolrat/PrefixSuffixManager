package me.NaakteMolrat.PrefixSuffixManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandPresuf implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg3.length == 0) {
			arg0.sendMessage(ChatColor.GREEN + "Please select one of the 5 presuf options");
			return true;
		}
		switch(arg3[0].toLowerCase()) {
		case "get":
			if(arg3.length > 1) {
				if(Main.instance.isSenderAllowed(arg0, "manageprefixsuffix.getothers")) {
					String target = arg3[1];
					if(Bukkit.getOfflinePlayer(target) != null) {
					arg0.sendMessage("Player " + target + " has the presufs: " + Main.instance.getPresufsByUsername(target));
					return true;
					} else {
						arg0.sendMessage(ChatColor.GREEN + "Couldn't find player " + target + "!");
						return true;
					}
				} else {
					arg0.sendMessage(ChatColor.GREEN + "You are not allowed to see other players' presufs");
					return true;
				}
					
			} else {
				arg0.sendMessage("You have the presufs: " + Main.instance.getPresufsByUsername(arg0.getName()));
			}
			return true;
		case "add":
			if(arg0.isOp() || arg0 instanceof ConsoleCommandSender || arg0.hasPermission("prefixsuffixmanager.addtoconfig")) {
				FileConfiguration config = Main.instance.getConfig();
				String prefix = arg3[1];
				if(!(config.contains("prefixsuffix"))) {
					List<String> presuf = new ArrayList<String>();
					presuf.add(prefix);
					config.set("prefixsuffix", presuf);
					arg0.sendMessage("Successful");
					
				} else {
					List<String> idk = config.getStringList("prefixsuffix");
					if(idk.contains(prefix)) {
						arg0.sendMessage(ChatColor.GREEN + "That presuf already exists!");
						return true;
					}
					idk.add(prefix);
					config.set("prefixsuffix", idk);
					arg0.sendMessage("Successful");
					
				}
					
				Main.instance.saveCustomConfig();
				return true;
			} else {
				arg0.sendMessage(ChatColor.GREEN + "You are not allowed to use this command!");
				return true;
			}
		case "give":
			if(arg0.isOp() || arg0 instanceof ConsoleCommandSender || arg0.hasPermission("prefixsuffixmanager.give")) {
				FileConfiguration config = Main.instance.getCustomConfig();
				String prefixsuffix = arg3[2];
				
				if(Main.instance.getConfig().getStringList("prefixsuffix").contains(prefixsuffix)) {
					if(!(config.contains(Bukkit.getOfflinePlayer(arg3[1]).getUniqueId().toString()))) {
						List<String> prefixsuffixes = new ArrayList<String>();
						prefixsuffixes.add(prefixsuffix);
						config.set(Bukkit.getOfflinePlayer(arg3[1]).getUniqueId().toString(), prefixsuffixes);
						Main.instance.saveCustomConfig();
						arg0.sendMessage("Successful");
						return true;
					}
					
					List<String> idk =  config.getStringList(Bukkit.getOfflinePlayer(arg3[1]).getUniqueId().toString());
					System.out.println(idk);
					if(idk.contains(prefixsuffix)) {
						arg0.sendMessage(ChatColor.GREEN + "That player already has that presuf");
						return true;
					}
					idk.add(prefixsuffix);
					config.set(Bukkit.getOfflinePlayer(arg3[1]).getUniqueId().toString(), idk);
					Main.instance.saveCustomConfig();
					arg0.sendMessage("Successful");
					return true;
				} else {
					arg0.sendMessage(ChatColor.RED + "That is not a valid prefix/suffix");
					return true;
				}
			} else {
				arg0.sendMessage(ChatColor.GREEN + "You are not allowed to use this command!");
				return true;
			}
		case "listall":
			if(Main.instance.isSenderAllowed(arg0, "prefixsuffixmanager.listall")) {
				arg0.sendMessage("The current presufs are: " + Main.instance.getConfig().getStringList("prefixsuffix"));
				return true;
			}
			arg0.sendMessage(ChatColor.GREEN + "You are not allowed to use this command");
			return true;
		case "switch":
			if(arg3.length < 2) {
				arg0.sendMessage(ChatColor.GREEN + "Please specify if you want a suffix or a prefix");
				return true;
			}
			boolean suffix = false;
			if(arg3[1].equalsIgnoreCase("suffix") || arg3[1].equalsIgnoreCase("prefix")) {
				if(arg3[1].equalsIgnoreCase("suffix"))
					suffix = true;
			} else {
				arg0.sendMessage(ChatColor.GREEN + "Please specify if you want a suffix or a prefix");
				return true;
			}
			
			if(arg0 instanceof ConsoleCommandSender) {
				arg0.sendMessage(ChatColor.GREEN + "Console can't have prefixes or suffixes");
				return true;
			}
			
			if(arg3.length > 2) {
			List<String> presufs = Main.instance.getCustomConfig().getStringList(Bukkit.getPlayer(arg0.getName()).getUniqueId().toString());
			int weight = Main.instance.getConfig().getInt("lp-presuf-weight");
			if(presufs.contains(arg3[2])) {
				Main.instance.execCommand("lp user " + arg0.getName() + " meta remove" + (suffix ? "suffix" : "prefix") + " "+weight);
				Main.instance.execCommand("lp user "  + arg0.getName() + " meta add" + (suffix ? "suffix" : "prefix") + " " +weight + " " + arg3[2]);
				return true;
			} else if(arg3[2].equalsIgnoreCase("none")) {
				Main.instance.execCommand("lp user " + arg0.getName() + " meta remove" + (suffix ? "suffix" : "prefix") + " "+weight);
				return true;
			} else {
				arg0.sendMessage(ChatColor.GREEN + "You do not own that " + (suffix ? "suffix" : "prefix"));
				return true;
			}
			} else {
				arg0.sendMessage(ChatColor.GREEN + "Specify a presuf!");
				return true;
			}
			
		/*case "remove":
			if(Main.instance.isSenderAllowed(arg0, "prefixsuffixmanager.remove")) {
				if(Main.instance.getConfig().getStringList("prefixsuffix").contains(arg3[1])) {
				Main.instance.getConfig().getStringList("prefixsuffix").remove(arg3[1]);
				Main.instance.saveCustomConfig();
				arg0.sendMessage("Removed that presuf from the config.yml, but not from the players.yml, but don't worry, the players won't be able to see or use them.");
				return true;
				} else {
					arg0.sendMessage(ChatColor.RED + "That presuf didn't exist in the first place.");
					return true;
				}
			} else {
				arg0.sendMessage(ChatColor.GREEN + "You do not have the correct permissions to use this command.");
				return true;
			}*/
		//}
		//	}
		}
		arg0.sendMessage(ChatColor.GREEN + "Please select one of the 3 presuf options");
		return true;
	}

}
