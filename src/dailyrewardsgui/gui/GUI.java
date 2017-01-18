package dailyrewardsgui.gui;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import dailyrewardsgui.DailyRewardsGUI;
import dailyrewardsgui.utils.Tuple;

public class GUI implements Listener {

	private InventoryPages pages;
	public void create() {
		pages = InventoryPages.create(
			DailyRewardsGUI.getInstance().getMainConfig().rewards
			.stream()
			.map(reward -> Tuple.of(reward.name, reward.lore))
			.collect(Collectors.toList())
		);
	}

	/*
	 * key: player uuid
	 * value:
	 *   key: current open page
	 *   value: page to inventory contents cache
	 */
	private final HashMap<UUID, Tuple<InventoryPage, HashMap<InventoryPage, InventoryData>>> playerPages = new HashMap<>();

	public void destroy() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (playerPages.containsKey(player.getUniqueId())) {
				player.closeInventory();
			}
		}
	}

	public void openPage(Player player, int activeIndex) {
		// find page
		InventoryPage page = pages.findPage(activeIndex);
		// register player as viewing daily rewards inv
		playerPages.put(player.getUniqueId(), Tuple.of(page, new HashMap<>()));
		// open daily rewards inv
		player.openInventory(getOrCreateInventory(player, page, activeIndex % InventoryPage.DESCRIPTIONS_PER_PAGE).create());
	}

	@EventHandler(ignoreCancelled = true)
	protected void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Tuple<InventoryPage, HashMap<InventoryPage, InventoryData>> tuple = playerPages.get(player.getUniqueId());
		if (tuple != null) {
			event.setCancelled(true);
			// only handler clicks in top inventorys
			if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
				// get current open page
				InventoryPage cpage = tuple.getObj1();
				// handler click
				switch (cpage.getClickType(event.getSlot())) {
					case PREV_PAGE: {
						// get prev page
						InventoryPage prev = cpage.prev();
						// set it as current
						tuple.setObj1(prev);
						// open it
						getOrCreateInventory(player, prev, InventoryPage.DESCRIPTIONS_PER_PAGE + 1).fill(player.getOpenInventory().getTopInventory());
						break;
					}
					case NEXT_PAGE: {
						// get next page
						InventoryPage next = cpage.next();
						// set it as current
						tuple.setObj1(next);
						// open it
						getOrCreateInventory(player, next, -1).fill(player.getOpenInventory().getTopInventory());
						break;
					}
					default: {
						break;
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	protected void onClose(InventoryCloseEvent event) {
		playerPages.remove(event.getPlayer().getUniqueId());
	}

	private InventoryData getOrCreateInventory(Player player, InventoryPage page, int activeIndex) {
		HashMap<InventoryPage, InventoryData> cache = playerPages.get(player.getUniqueId()).getObj2();
		InventoryData inv = cache.get(page);
		if (inv == null) {
			inv = page.createInventory(player, activeIndex);
			cache.put(page, inv);
		}
		return inv;
	}

}
