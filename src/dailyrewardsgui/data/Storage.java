package dailyrewardsgui.data;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import dailyrewardsgui.utils.Builder;
import dailyrewardsgui.utils.Tuple;

public class Storage extends FileConfig {

	public Storage(File file) {
		super(file);
	}

	private final HashMap<UUID, Tuple<Calendar, Integer>> storage = new HashMap<>();

	public void forceSetCurrentStreak(Player player, int streak) {
		storage.put(player.getUniqueId(), Tuple.of(getCurrentDate(), streak));
	}

	public void resetStreak(Player player) {
		storage.remove(player.getUniqueId());
	}

	public Tuple<Integer, Boolean> getStreak(Player player) {
		Tuple<Calendar, Integer> existing = storage.get(player.getUniqueId());
		//if entry didn't exist - it's first day
		if (existing == null) {
			return Tuple.of(updateStreak(player, 1).getObj2(), Boolean.TRUE);
		} else
		//if current time equals next streak trigger time - add 1 and return it (also update next streak trigger time)
		if (existing.getObj1().getTime().equals(getCurrentDate().getTime())) {
			return Tuple.of(updateStreak(player, existing.getObj2() + 1).getObj2(), Boolean.TRUE);
		} else
		//current time before streak trigger time - streak already received
		if (getCurrentDate().getTime().before(existing.getObj1().getTime())) {
			return Tuple.of(existing.getObj2(), Boolean.FALSE);
		} else
		//time if after streak trigger time - reset it
		{
			return Tuple.of(updateStreak(player, 1).getObj2(), Boolean.TRUE);
		}
	}

	private Tuple<Calendar, Integer> updateStreak(Player player, int streak) {
		Tuple<Calendar, Integer> streakTuple = Tuple.of(Builder.create(getCurrentDate()).invoke(cal -> cal.add(Calendar.DATE, 1)).build(), streak);
		storage.put(player.getUniqueId(), streakTuple);
		return streakTuple;
	}

	private final Calendar getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		Calendar datecal = Calendar.getInstance();
		datecal.clear();
		datecal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		return datecal;
	}

	public void load(MemorySection section) {
		storage.clear();
		for (String key : section.getKeys(false)) {
			ConfigurationSection uuidsect = section.getConfigurationSection(key);
			Calendar datecal = Calendar.getInstance();
			datecal.clear();
			datecal.set(uuidsect.getInt("year"), uuidsect.getInt("month"), uuidsect.getInt("date"));
			storage.put(UUID.fromString(key), Tuple.of(datecal, uuidsect.getInt("streak")));
		}
	}

	public void save(MemorySection section) {
		for (Entry<UUID, Tuple<Calendar, Integer>> entry : storage.entrySet()) {
			ConfigurationSection uuidsect = section.createSection(entry.getKey().toString());
			Calendar datecal = entry.getValue().getObj1();
			uuidsect.set("year", datecal.get(Calendar.YEAR));
			uuidsect.set("month", datecal.get(Calendar.MONTH));
			uuidsect.set("date", datecal.get(Calendar.DATE));
			uuidsect.set("streak", entry.getValue().getObj2());
		}
	}

}
