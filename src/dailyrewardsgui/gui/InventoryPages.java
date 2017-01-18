package dailyrewardsgui.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dailyrewardsgui.utils.Tuple;

public class InventoryPages {

	private final ArrayList<InventoryPage> inventoryPages = new ArrayList<>();

	private InventoryPages() {
	}

	public static InventoryPages create(List<Tuple<String, List<String>>> descriptions) {
		InventoryPages pages = new InventoryPages();
		Iterator<Tuple<String, List<String>>> iterator = descriptions.iterator();
		InventoryPage prev = null;
		do {
			InventoryPage page = new InventoryPage(iterator);
			if (prev != null) {
				page.linkPrev(prev);
				prev.linkNext(page);
			}
			prev = page;
			pages.inventoryPages.add(page);
		} while (iterator.hasNext());
		return pages;
	}

	protected InventoryPage findPage(int activeIndex) {
		return inventoryPages.get(activeIndex / InventoryPage.DESCRIPTIONS_PER_PAGE);
	}

}
