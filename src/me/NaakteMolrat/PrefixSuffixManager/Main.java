package me.NaakteMolrat.PrefixSuffixManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements TabCompleter {
	static protected Main instance;
	FileConfiguration config;
	private File customConfigFile;
    private FileConfiguration customConfig;

    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	config = this.getConfig();
    	config.addDefault("prefixsuffix", null);
    	saveDefaultConfig();
    	createCustomConfig();
    	instance = this;
    	getCommand("presuf").setExecutor(new CommandPresuf());
    	getCommand("presuf").setTabCompleter(this);
    	
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	saveCustomConfig();
		saveConfig();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	//System.out.println("BOING");
    	if(cmd.getName().equalsIgnoreCase("presuf")) {
    		if(args.length == 1) {
    			ArrayList<String> options = new ArrayList<String>();
    			String[] preDefOptions = {"get", "add", "give", "listall", "switch"};
    			if(!args[0].equals("")) {
    				for(String option : preDefOptions) {
    					if(option.toLowerCase().startsWith(args[0].toLowerCase()))
    						options.add(option);
    				}
    			} else {
    				for(String option : preDefOptions) {
    					options.add(option);
    				}
    			}
    			Collections.sort(options);
    			
    			return options;
    		} else
    		if(args.length == 3 && args[0].equalsIgnoreCase("give") && Main.instance.isSenderAllowed(sender, "prefixsuffixmanager.add")) {
    			
    			ArrayList<String> optionws = new ArrayList<String>();
    			List<String> presufs = Main.instance.getConfig().getStringList("prefixsuffix");
    			if(!args[2].equals("")) {
    				for(String presuf : presufs) {
    					if(presuf.toLowerCase().startsWith(args[2].toLowerCase()))
    						optionws.add(presuf);
    				}
    			} else {
    				for(String presuf : presufs) {
    					optionws.add(presuf);
    				}
    			}
    			Collections.sort(optionws);
    			System.out.println(optionws);
    			return optionws;
    		}
    		if(args[0].equalsIgnoreCase("switch")) {
    			if(args.length == 2) {
    				ArrayList<String> options = new ArrayList<String>();
    				String[] preDefOptions = {"suffix", "prefix"};
    				if(!args[1].equals("")) {
    					for(String option : preDefOptions) {
    						if(option.toLowerCase().startsWith(args[0].toLowerCase()))
    							options.add(option);
    					}
    				} else {
        				for(String option : preDefOptions) {
        					options.add(option);
        				}
        			}
    				Collections.sort(options);
    				return options;
    			}
    			if(args.length == 3) {
    				ArrayList<String> options = new ArrayList<String>();
    				List<String> presufs = Main.instance.getCustomConfig().getStringList(Bukkit.getPlayer(sender.getName()).getUniqueId().toString());
    				presufs.add("None");
    				if(!args[2].equals("")) {
        				for(String presuf : presufs) {
        					if(presuf.toLowerCase().startsWith(args[2].toLowerCase()))
        						options.add(presuf);
        				}
        			} else {
        				for(String presuf : presufs) {
        					options.add(presuf);
        				}
        			}
    				Collections.sort(options);
    				return options;
    			}
    		}
    	}
		return null;
    }
    
    public void execCommand(String command) {
    	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    	
    	new BukkitRunnable() {
            @Override
            public void run() {
            	Bukkit.dispatchCommand(console, command);
            }
        }.runTask(this);
    	
    }
    
    public void saveCustomConfig() {
    	try {
			customConfig.save(customConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "players.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("players.yml", false);
         }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        /* User Edit:
            Instead of the above Try/Catch, you can also use
            YamlConfiguration.loadConfiguration(customConfigFile)
        */
    }
    
    public List<String> getPresufsByUUID(String UUID) {
    	if(customConfig.contains(UUID)) {
    		return customConfig.getStringList(UUID);
    	} else {
    	return null;
    	}
    }
    
    public List<String> getPresufsByUsername(String Username) {
    	String UUID = Bukkit.getOfflinePlayer(Username).getUniqueId().toString();
    	if(customConfig.contains(UUID)) {
    		return customConfig.getStringList(UUID);
    	} else {
    	return null;
    	}
    }
    
    public boolean isSenderAllowed(CommandSender sender, String perm) {
    	if(sender.isOp() || sender instanceof ConsoleCommandSender || sender.hasPermission(perm))
    		return true;
    	return false;
    }
}
