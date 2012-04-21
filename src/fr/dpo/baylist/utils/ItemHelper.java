package fr.dpo.baylist.utils;

import fr.dpo.baylist.model.ParsedItem;

public class ItemHelper {

	public interface ResultIndexes {
		int LABEL_INDEX = 0;
		int QUANTITY_INDEX = 1;
	}

	public static ParsedItem extractFromTitle(String title) {
		String label;
		int quantity = -1;

		int indexX = title.lastIndexOf(Constantes.SeparatorCharacters.QUANTITY);
		if (indexX != -1) {
			String quantityStr = title.substring(indexX + Constantes.SeparatorCharacters.QUANTITY.length());
			try {
				label = title.substring(0, indexX);
				quantity = Integer.parseInt(quantityStr);
			} catch (NumberFormatException ex) {
				quantity = -1;
				label = title;
			}
		} else {
			label = title;
		}

		return new ParsedItem(label, quantity);
	}

}
