package dailyrewardsgui.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class FileConfig {

	private final File file;
	public FileConfig(File file) {
		this.file = file;
	}

	public void load() {
		load(YamlConfiguration.loadConfiguration(file));
	}

	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		save(config);
		try {
			config.save(file);
		} catch (IOException e) {
		}
	}

	protected abstract void load(MemorySection section);

	protected abstract void save(MemorySection section);

}
