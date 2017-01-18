package dailyrewardsgui.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {

	public static <T> List<T> getSomeElements(Iterator<T> iterator, int limit) {
		ArrayList<T> list = new ArrayList<>();
		int currentAmount = 0;
		while (iterator.hasNext() && (currentAmount++ < limit)) {
			list.add(iterator.next());
		}
		return list;
	}

	public static String replacePlaceholders(Player player, String text) {
		return PlaceholderAPI.setPlaceholders(player, replaceColors(text));
	}

	public static String replaceColors(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

}
