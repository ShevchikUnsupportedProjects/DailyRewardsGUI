package dailyrewardsgui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class Config extends FileConfig {

	public Config(File file) {
		super(file);
	}

	public final List<DailyReward> rewards = new ArrayList<>();
	public String prevPageName = "&2To previous page";
	public String nextPageName = "&2To next page";
	public String inventoryName = "&1Daily rewards";

	public static class DailyReward {

		public final String name;
		public final List<String> lore;
		public final List<String> commands;

		public DailyReward(String name, List<String> lore, List<String> commands) {
			this.name = name;
			this.lore = lore;
			this.commands = commands;
		}

	}

	protected void load(MemorySection section) {
		rewards.clear();
		for (String key : section.getKeys(false)) {
			ConfigurationSection rsection = section.getConfigurationSection(key);
			rewards.add(new DailyReward(rsection.getString("name"), rsection.getStringList("lore"), rsection.getStringList("cmds")));
		}
		if (rewards.isEmpty()) {
			rewards.add(new DailyReward("test", Arrays.asList("test"), Arrays.asList("doesntexist")));
		}
		prevPageName = section.getString("locale.prevPageName", prevPageName);
		nextPageName = section.getString("locale.nextPageName", nextPageName);
		inventoryName = section.getString("locale.inventoryName", inventoryName);
	}

	protected void save(MemorySection section) {
		int count = 1;
		for (DailyReward reward : rewards) {
			ConfigurationSection rsection = section.createSection(String.valueOf(count++));
			rsection.set("name", reward.name);
			rsection.set("lore", reward.lore);
			rsection.set("cmds", reward.commands);
		}
		section.set("locale.prevPageName", prevPageName);
		section.set("locale.nextPageName", nextPageName);
		section.set("locale.inventoryName", inventoryName);
	}

}
