package dailyrewardsgui.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dailyrewardsgui.DailyRewardsGUI;
import dailyrewardsgui.utils.Utils;

public class InventoryData {

	private final ItemStack[] contents = new ItemStack[27];

	protected void setItem(int index, ItemStack itemstack) {
		this.contents[index] = itemstack;
	}

	protected Inventory create() {
		Inventory inv = Bukkit.createInventory(null, contents.length, Utils.replaceColors(DailyRewardsGUI.getInstance().getMainConfig().inventoryName));
		inv.setContents(contents);
		return inv;
	}

	protected void fill(Inventory inv) {
		inv.setContents(contents);
	}

}