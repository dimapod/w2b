package fr.dpo.baylist.model;

import fr.dpo.baylist.utils.ItemHelper;

public class Item {
	private int id;
	private String title;
	private boolean importent;
	private boolean checked;

	// parsed data
	private ParsedItem parsedItem;

	public Item(int id, String title) {
		super();
		setId(id);
		setTitle(title);
		setChecked(false);
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

}
