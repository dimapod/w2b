package fr.dpo.baylist.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.dpo.baylist.utils.Constantes;

public class ItemContainer {

	private interface SaveConstantes {
		// int INDEX_ID = 0;
		int INDEX_TITLE = 1;
		int INDEX_IS_CHECKED = 2;
		int INDEX_IS_IMPORTENT = 3;
		int INDEX_LIST_ID = 4;
	}

	private static List<Item> items = new ArrayList<Item>();
	private static Date lastChanged = new Date();

	public static Item getItemAt(int position) {
		return items.get(position);
	}

	public static void removeItemAt(int position) {
		lastChanged = new Date();
		items.remove(position);
	}

	public static void addItem(String title, int listId) {
		lastChanged = new Date();
		items.add(new Item(title, listId));
	}

	public static void changeItemAt(int position, String title) {
		lastChanged = new Date();
		Item item = items.get(position);
		item.setTitle(title);
	}

	public static void changeItemAt(int position, int listId) {
		lastChanged = new Date();
		Item item = items.get(position);
		item.setListId(listId);
	}

	public static void clear() {
		lastChanged = new Date();
		items.clear();
	}

	public static void clearDone() {
		lastChanged = new Date();
		for (int i = items.size() - 1; i >= 0; i--) {
			Item item = items.get(i);
			if (item.isChecked()) {
				items.remove(i);
			}
		}
	}

	public static int size() {
		return items.size();
	}

	public static int countChecked() {
		int res = 0;
		for (Item item : items) {
			if (item.isChecked()) {
				res++;
			}
		}
		return res;
	}

	public static void saveItems(FileOutputStream fos) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
		// Writing additional information
		bufferedWriter.write(String.valueOf(lastChanged.getTime()));
		bufferedWriter.newLine();
		// Writing items
		for (Item item : items) {
			bufferedWriter.write(serialize(item));
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		fos.close();
	}

	public static void restoreItems(FileInputStream fis) {
		items.clear();

		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
			// Reading additional information
			String line = bufferedReader.readLine();
			lastChanged = new Date(Long.parseLong(line));

			// Reading items
			while ((line = bufferedReader.readLine()) != null) {
				items.add(deserialize(line));
			}
		} catch (Exception e) {
			items.add(new Item(e.getMessage(), 0));
			lastChanged = new Date();
		}
	}

	private static String serialize(Item item) {
		StringBuilder sb = new StringBuilder();
		sb.append(item.getId());
		sb.append(Constantes.SaveConstantes.FIELD_SERAPATOR);
		sb.append(item.getTitle());
		sb.append(Constantes.SaveConstantes.FIELD_SERAPATOR);
		sb.append(Boolean.toString(item.isChecked()));
		sb.append(Constantes.SaveConstantes.FIELD_SERAPATOR);
		sb.append(Boolean.toString(item.isImportent()));
		sb.append(Constantes.SaveConstantes.FIELD_SERAPATOR);
		sb.append(Integer.toString(item.getListId()));

		return sb.toString();
	}

	private static Item deserialize(String str) {
		String[] array = str.split(Constantes.SaveConstantes.FIELD_SERAPATOR);
		try {
			Item item = new Item(array[SaveConstantes.INDEX_TITLE],
					Integer.parseInt(array[SaveConstantes.INDEX_LIST_ID]));
			item.setChecked(Boolean.parseBoolean(array[SaveConstantes.INDEX_IS_CHECKED]));
			item.setImportent(Boolean.parseBoolean(array[SaveConstantes.INDEX_IS_IMPORTENT]));
			return item;
		} catch (Exception e) {
			return new Item(e.getMessage(), 0);
		}
	}

	public static Date getLastChanged() {
		return lastChanged;
	}
}
