package fr.dpo.baylist.utils;

public class Constantes {

	public interface SeparatorCharacters {
		String QUANTITY = " x";
	}

	public interface StateData {
		// This is our state data that is stored when freezing.
		String EDIT_CONTENT = "editContent";
	}

	public interface Modes {
		int NORMAL = 0;
		int EDIT = 1;
	}

	public interface SaveConstantes {
		String FILENAME = "buylist";
		String FIELD_SERAPATOR = ";";
	}

}
