package dailyrewardsgui.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import dailyrewardsgui.DailyRewardsGUI;
import dailyrewardsgui.utils.Builder;
import dailyrewardsgui.utils.Tuple;
import dailyrewardsgui.utils.Utils;

public class InventoryPage {

	protected static final int DESCRIPTIONS_PER_PAGE = 7;

	private InventoryPage prev;
	private InventoryPage next;

	private final ArrayList<Tuple<String, List<String>>> descriptions = new ArrayList<>();

	public InventoryPage(Iterator<Tuple<String, List<String>>> activeItems) {
		this.descriptions.addAll(Utils.getSomeElements(activeItems, DESCRIPTIONS_PER_PAGE));
	}

	public void linkNext(InventoryPage next) {
		this.next = next;
	}

	public void linkPrev(InventoryPage prev) {
		this.prev = prev;
	}

	public InventoryPage next() {
		return next;
	}

	public InventoryPage prev() {
		return prev;
	}

	public int getSize() {
		return descriptions.size();
	}

	@SuppressWarnings("deprecation")
	private static final ItemStack blackGlass = Builder.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
	.invoke(item -> item.setItemMeta(
		Builder.create(Bukkit.getItemFactory().getItemMeta(item.getType()))
		.invoke(meta -> meta.setDisplayName(ChatColor.AQUA.toString()))
		.build()
	))
	.build();
	@SuppressWarnings("deprecation")
	private static final ItemStack greenGlass = Builder.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData()))
	.invoke(item -> item.setItemMeta(
		Builder.create(Bukkit.getItemFactory().getItemMeta(item.getType()))
		.invoke(meta -> meta.setDisplayName(ChatColor.AQUA.toString()))
		.build()
	))
	.build();
	@SuppressWarnings("deprecation")
	private static final ItemStack yellowGlass = Builder.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.YELLOW.getWoolData()))
	.invoke(item -> item.setItemMeta(
		Builder.create(Bukkit.getItemFactory().getItemMeta(item.getType()))
		.invoke(meta -> meta.setDisplayName(ChatColor.AQUA.toString()))
		.build()
	))
	.invoke(item -> item.addUnsafeEnchantment(Enchantment.DURABILITY, 1))
	.build();
	private static final ItemStack arrowPrev = Builder.create(new ItemStack(Material.SKULL_ITEM, 1, (short) 3))
	.invoke(item -> item.setItemMeta(
		Builder.create((SkullMeta) Bukkit.getItemFactory().getItemMeta(item.getType()))
		.invoke(meta -> meta.setOwner("MHF_ArrowLeft"))
		.invoke(meta -> meta.setDisplayName(Utils.replaceColors(DailyRewardsGUI.getInstance().getMainConfig().prevPageName)))
		.build()
	))
	.build();
	private static final ItemStack arrowNext = Builder.create(new ItemStack(Material.SKULL_ITEM, 1, (short) 3))
	.invoke(item -> item.setItemMeta(
		Builder.create((SkullMeta) Bukkit.getItemFactory().getItemMeta(item.getType()))
		.invoke(meta -> meta.setOwner("MHF_ArrowRight"))
		.invoke(meta -> meta.setDisplayName(Utils.replaceColors(ChatColor.GREEN + DailyRewardsGUI.getInstance().getMainConfig().nextPageName)))
		.build()
	)).build();

	private static final int bottomStart = 0;
	private static final int bottomEnd = 8;
	private static final int topStart = 18;
	private static final int topEnd = 26;
	private static final int middleStart = 10;
	private static final int middleEnd = 16;
	private static final int prevPagePtr = 9;
	private static final int nextPagePtr = 17;

	private Tuple<String, List<String>> getDescription(int index) {
		if (index < descriptions.size()) {
			return descriptions.get(index);
		} else {
			return null;
		}
	}

	protected InventoryData createInventory(Player player, int activeItemIndex) {
		InventoryData inventory = new InventoryData();
		//fill corners
		inventory.setItem(bottomStart, blackGlass);
		inventory.setItem(bottomEnd, blackGlass);
		inventory.setItem(topStart, blackGlass);
		inventory.setItem(topEnd, blackGlass);
		//fill prev page ptr
		inventory.setItem(prevPagePtr, prev != null ? arrowPrev : blackGlass);
		//fill next page ptr
		inventory.setItem(nextPagePtr, next != null ? arrowNext : blackGlass);
		//fill middle
		for (int i = 0; i <= (middleEnd - middleStart); i++) {
			//past
			ItemStack glass = null;
			if (i < activeItemIndex) {
				glass = greenGlass;
				inventory.setItem(middleStart + i, createDisplayItemStack(player, Material.EMERALD_BLOCK, ChatColor.GREEN, getDescription(i), false));
			} else
			//future
			if (i > activeItemIndex) {
				glass = blackGlass;
				inventory.setItem(middleStart + i, createDisplayItemStack(player, Material.OBSIDIAN, ChatColor.RED, getDescription(i), false));
			}
			//current
			else {
				glass = yellowGlass;
				inventory.setItem(middleStart + activeItemIndex, createDisplayItemStack(player, Material.GOLD_BLOCK, ChatColor.YELLOW, getDescription(i), true));
			}
			inventory.setItem(bottomStart + i + 1, glass);
			inventory.setItem(topStart + i + 1, glass);
		}
		return inventory;
	}

	private static ItemStack createDisplayItemStack(Player player, Material material, ChatColor color, Tuple<String, List<String>> descr, boolean hasEnch) {
		if (descr == null) {
			return blackGlass;
		}
		return Builder.create(new ItemStack(material))
		.invoke(item -> item.setItemMeta(
			Builder.create(Bukkit.getItemFactory().getItemMeta(item.getType()))
			.invoke(meta -> meta.setDisplayName(Utils.replacePlaceholders(player, color + descr.getObj1())))
			.invoke(meta -> meta.setLore(
				descr.getObj2()
				.stream()
				.map(str -> Utils.replacePlaceholders(player, color + str))
				.collect(Collectors.toList()))
			)
			.invoke(meta -> {
				if (hasEnch) {
					meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
			})
			.build()
		))
		.build();
	}

	protected ClickType getClickType(int slot) {
		switch (slot) {
			case prevPagePtr: {
				return prev != null ? ClickType.PREV_PAGE : ClickType.OTHER;
			}
			case nextPagePtr: {
				return next != null ? ClickType.NEXT_PAGE : ClickType.OTHER;
			}
			default: {
				return ClickType.OTHER;
			}
		}
	}

	public static enum ClickType {
		PREV_PAGE, NEXT_PAGE, OTHER
	}

}
