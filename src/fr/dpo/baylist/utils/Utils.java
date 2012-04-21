package fr.dpo.baylist.utils;

import android.view.View;

public class Utils {
	
	public static int getVisibility(boolean fl) {
		if (fl) {
			return View.VISIBLE;
		} else {
			return View.GONE;
		}
	}
	
}
