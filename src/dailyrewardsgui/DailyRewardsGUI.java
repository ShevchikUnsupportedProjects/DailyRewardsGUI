package dailyrewardsgui;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dailyrewardsgui.data.Config;
import dailyrewardsgui.data.Storage;
import dailyrewardsgui.gui.GUI;
import dailyrewardsgui.utils.Tuple;
import dailyrewardsgui.utils.Utils;

public class DailyRewardsGUI extends JavaPlugin {

	private static DailyRewardsGUI instance;

	public static DailyRewardsGUI getInstance() {
		return instance;
	}

	public DailyRewardsGUI() {
		instance = this;
	}

	private final Storage storage = new Storage(new File(getDataFolder(), "data.yml"));
	private final Config config = new Config(new File(getDataFolder(), "config.yml"));

	public Config getMainConfig() {
		return config;
	}

	private final GUI gui = new GUI();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(gui, this);
		loadGUI();
		storage.load();
	}

	private void loadGUI() {
		gui.destroy();
		config.load();
		config.save();
		gui.create();
	}

	@Override
	public void onDisable() {
		gui.destroy();
		config.save();
		storage.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName().toLowerCase()) {
			case "dailyrewards": {
				Player player = (Player) sender;
				//get streak
				Tuple<Integer, Boolean> streak = storage.getStreak(player);
				//reset it if streak is more than configured rewards
				if (streak.getObj1() > config.rewards.size()) {
					storage.resetStreak(player);
					streak = storage.getStreak(player);
				}
				//open gui
				gui.openPage(player, streak.getObj1() - 1);
				//reward player if didn't reward already
				if (streak.getObj2()) {
					config.rewards.get(streak.getObj1() - 1).commands
					.stream()
					.map(rcommand -> Utils.replacePlaceholders(player, rcommand))
					.forEach(rcommand -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rcommand));
				}
				return true;
			}
			case "dailyrewardsadmin": {
				if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.YELLOW + "/" + label + " reload - reload configuration");
					sender.sendMessage(ChatColor.YELLOW + "/" + label + " forcestreak {streak} - sets current streak");
					sender.sendMessage(ChatColor.YELLOW + "/" + label + " resetstreak - resets current streak");
				} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					loadGUI();
					sender.sendMessage(ChatColor.YELLOW + "Config reloaded");
				} else if (args.length == 2 && args[0].equalsIgnoreCase("forcestreak")) {
					Player player = (Player) sender;
					player.closeInventory();
					storage.forceSetCurrentStreak(player, Integer.parseInt(args[1]));
					sender.sendMessage(ChatColor.YELLOW + "Streak set");
				} else if (args.length == 1 && args[0].equalsIgnoreCase("resetstreak")) {
					Player player = (Player) sender;
					player.closeInventory();
					storage.resetStreak(player);
					sender.sendMessage(ChatColor.YELLOW + "Streak reset");
				}
				return true;
			}
			default: {
				return true;
			}
		}
	}

}
