package fr.dpo.baylist.model;

public class ParsedItem {
	private String label;
	private int quantity;
	
	// prepared data
	private String quantity2Show;
	
	public ParsedItem(String label, int quantity) {
		super();
		this.label = label;
		this.quantity = quantity;
		this.quantity2Show = "x" + quantity;
	}

	public String getLabel() {
		return label;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getQuantity2Show() {
		return quantity2Show;
	}

}
