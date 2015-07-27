package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.getData;
import static com.predictry.fisher.domain.util.Helper.getDataName;
import static com.predictry.fisher.domain.util.Helper.getType;
import static com.predictry.fisher.domain.util.Helper.isRecommended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.predictry.fisher.domain.stat.Stat;

/**
 * Add <code>CartBoostAggregation<code> which is the number of recommended 
 * items divided by regular bought items.  This is done based on session basis.
 * 
 * @author jocki
 *
 */
public class CartBoostAggregation implements Aggregation{

	private Map<String, List<Item>> itemsPerSessions = new HashMap<>();
	
	private double total;
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			Map<String,Object> fields = getData(mapJson);
			String session = (String) fields.get("session");
			String item = (String) fields.get("item");
			
			// Add items if necessary
			if (!itemsPerSessions.containsKey(session)) {
				itemsPerSessions.put(session, new ArrayList<Item>());
			}
			List<Item> items = itemsPerSessions.get(session);
			if (!items.contains(item)) {
				items.add(new Item(item, isRecommended(mapJson)));
			} 
		}
	}
	
	@Override
	public void postProcessing(Stat stat) {
		total = 0.0;
		itemsPerSessions.forEach((session, items) -> {
			long recommended = items.stream().filter(i -> i.isRecommended).count();
			long overall = items.stream().filter(i -> !i.isRecommended).count();
			double boost = (overall == 0 || recommended == 0)? 0.0: ((double) recommended / overall);
			total += boost;
		});
		if ((total == 0) || itemsPerSessions.isEmpty()) {
			stat.setCartBoost(0.0);
		} else {
			stat.setCartBoost(total / itemsPerSessions.keySet().size());
		}
	}
	
	private class Item {
		
		private String item;
		private boolean isRecommended;
		
		public Item(String item, boolean isRecommended) {
			this.item = item;
			this.isRecommended = isRecommended;
		}

		public String getItem() {
			return item;
		}

		public boolean isRecommended() {
			return isRecommended;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isRecommended ? 1231 : 1237);
			result = prime * result + ((item == null) ? 0 : item.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Item)) return false;
			Item anotherItem = (Item) obj;
			if (!anotherItem.getItem().equals(getItem())) return false;
			if (!anotherItem.isRecommended() == isRecommended()) return false;
			return true;
		}
		
	}

}
