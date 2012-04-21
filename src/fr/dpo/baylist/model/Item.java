package fr.dpo.baylist.model;

import fr.dpo.baylist.utils.ItemHelper;

public class Item {
	private int id;
	private String title;
	private boolean importent;
	private boolean checked;
	private int listId;

	// parsed data
	private ParsedItem parsedItem;

	public Item(String title, int listId) {
		super();
		setId(0);
		setTitle(title);
		setChecked(false);
		setListId(listId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.trim();

		// Parse title
		this.parsedItem = ItemHelper.extractFromTitle(title);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getLabel() {
		return parsedItem.getLabel();
	}

	public boolean isImportent() {
		return importent;
	}

	public void setImportent(boolean importent) {
		this.importent = importent;
	}

	public ParsedItem getParsedItem() {
		return parsedItem;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

}
