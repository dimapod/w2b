package fr.dpo.baylist;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import fr.dpo.baylist.adapters.ShowListAdapter;
import fr.dpo.baylist.model.Item;
import fr.dpo.baylist.model.ItemContainer;
import fr.dpo.baylist.utils.Constantes;
import fr.dpo.baylist.utils.Utils;

public class BuyListActivity extends Activity {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH'h'mm");
	private static final int IMPORT_ITEMS = 1;

	private BaseAdapter showListAdapter;

	private static int mode = Constantes.Modes.NORMAL;
	private static int editPosition = -1;
	private static int currentListId = -1;
	private static boolean changed = false;

	// Views References
	private ListView listView = null;
	private Button buttonBackToList = null;
	private Button buttonAddEdit = null;
	private Button buttonOkEdit = null;
	private EditText editTitle = null;
	private ViewGroup statusPanel = null;
	private ViewGroup editPanel = null;
	private RadioGroup radioList = null;

	private TextView statusToBay = null;
	private TextView statusAll = null;
	private TextView statusLastChanged = null;
	private ViewGroup toolEditPanel = null;
	private ViewGroup toolEditMainPanel = null;

	private void fetchViewReferences() {
		listView = (ListView) findViewById(R.id.list);
		buttonBackToList = (Button) findViewById(R.id.b_back_to_list);
		buttonAddEdit = (Button) findViewById(R.id.b_edit_add);
		buttonOkEdit = (Button) findViewById(R.id.b_edit_ok);
		editTitle = (EditText) findViewById(R.id.e_edit);
		statusToBay = (TextView) findViewById(R.id.status_to_buy);
		statusAll = (TextView) findViewById(R.id.status_nb_items);
		statusLastChanged = (TextView) findViewById(R.id.status_last_changed);

		toolEditPanel = (ViewGroup) findViewById(R.id.edit_tools);
		toolEditMainPanel = (ViewGroup) findViewById(R.id.edit_tools_main);
		editPanel = (ViewGroup) findViewById(R.id.panel_edit);
		statusPanel = (ViewGroup) findViewById(R.id.status_bar);
		
		radioList = (RadioGroup) findViewById(R.id.rg_list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Toast.makeText(getApplicationContext(), "visi", Toast.LENGTH_SHORT).show();
		setContentView(R.layout.main);

		fetchViewReferences();

		// Show list adapter
		showListAdapter = new ShowListAdapter(this);

		// ListView
		listView.setAdapter(showListAdapter);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new ViewItemClickListener());
		// Register a context menu (long tap)
		registerForContextMenu(listView);

		// Button Edit
		Button buttonEdit = (Button) findViewById(R.id.b_edit);
		buttonEdit.setOnClickListener(new EditOnClickListener());
		buttonBackToList.setOnClickListener(new EditOnClickListener());

		// Button Clear List
		Button buttonClearList = (Button) findViewById(R.id.b_clear);
		buttonClearList.setOnClickListener(new ClearOnClickListener());
		// Register a context menu
		registerForContextMenu(buttonClearList);

		// Button Import List
		Button buttonImportList = (Button) findViewById(R.id.b_import_list);
		buttonImportList.setOnClickListener(new ImportListOnClickListener());

		// Button Add
		buttonAddEdit.setOnClickListener(new AddOnClickListener());

		// Button OK (EditMode)
		buttonOkEdit.setOnClickListener(new AddOnClickListener());

		editTitle.setOnKeyListener(new EditKeyListener());
		
		// RadioGroupe for list selection
		radioList.setOnCheckedChangeListener(new RedioListListener());

		// // == RESUME =========================
		// // If an instance of this activity had previously stopped, we can
		// // get the original edit text it started with.
		// if (savedInstanceState != null) {
		// editTitle.setText(savedInstanceState.getString(Constantes.StateData.EDIT_CONTENT));
		// }

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			// Resume data from file
			if (ItemContainer.size() == 0) {
				ItemContainer.restoreItems(openFileInput(Constantes.SaveConstantes.FILENAME));
			}
		} catch (Exception e) {
			// TODO ?
		}

		// Update views
		updateStatusbar();
		updateMode();
		updateEditPosition();
		showListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// If items changed - save to file
		if (isChanged()) {
			try {
				ItemContainer.saveItems(openFileOutput(Constantes.SaveConstantes.FILENAME, Context.MODE_PRIVATE));
			} catch (Exception e) {
				// TODO ?
			}
			setChanged(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save away the original text, so we still have it if the activity needs to be killed while paused.
		outState.putString(Constantes.StateData.EDIT_CONTENT, editTitle.getText().toString());
	}
	
	public void onRadioListSelectionClicked(View v) {
	    // Perform action on clicks
	    RadioButton rb = (RadioButton) v;
	    if (rb.getId() == R.id.radio_list1) {
	    	currentListId = 1;
	    } else {
	    	currentListId = 2;
	    }
	}

	// Listeners ///////////////////////////////////////////////////////////////////////////////////
	public class ViewItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Clicked item

			if (mode == Constantes.Modes.EDIT) {
				if (editPosition != position) {
					setEditPosition(position);
				} else {
					setEditPosition(-1);
				}
				updateEditPosition();
			} else {
				// Invert checked
				Item item = ItemContainer.getItemAt(position);
				item.setChecked(!item.isChecked());

				// Update Status bar
				updateStatusbar();
			}

			// Update
			showListAdapter.notifyDataSetChanged();

			// Modifications has been made
			setChanged(true);
		}
	}

	public class EditOnClickListener implements OnClickListener {
		public void onClick(View v) {
			// // save index and top position
			// int index = listView.getFirstVisiblePosition();
			// View view = listView.getChildAt(0);
			// int top = (view == null) ? 0 : view.getTop();

			if (mode == Constantes.Modes.EDIT) {
				// listView.setAdapter(showListAdapter);
				setMode(Constantes.Modes.NORMAL);
				// Update Status bar
				updateStatusbar();
			} else {
				// listView.setAdapter(editListAdapter);
				setMode(Constantes.Modes.EDIT);
			}
			updateMode();
			// Update
			showListAdapter.notifyDataSetChanged();
			// restore
			// listView.setSelectionFromTop(index, top);
		}
	}

	public class ClearOnClickListener implements OnClickListener {
		public void onClick(View v) {
			openContextMenu(v);
		}
	}

	public class ImportListOnClickListener implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ImportActivity.class);
			startActivityForResult(intent, IMPORT_ITEMS);
		}
	}

	/*
	 * This method is called back when the activity opened from this activity terminates 
	 * (it can optionnaly return some result) 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == IMPORT_ITEMS) {
			Bundle bundle = intent.getExtras();

			// Data returned from the importActivity
			String returnedData = bundle.getString(ImportActivity.IMPORTED_DATA_KEY);
			if (returnedData != null && !returnedData.isEmpty()) {
				String importMode = bundle.getString(ImportActivity.IMPORT_MODE_KEY);
				if (ImportActivity.IMPORT_MODE_REPLACE.equals(importMode)) {
					ItemContainer.clear();
				}

				String[] titlesToAdd = returnedData.split("\\n");
				for (String title : titlesToAdd) {
					ItemContainer.addItem(title, currentListId);
				}

				// Update
				showListAdapter.notifyDataSetChanged();
				// Modifications has been made
				setChanged(true);
			} else {
				Toast.makeText(getApplicationContext(), "No items to add", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class AddOnClickListener implements OnClickListener {
		public void onClick(View v) {
			applyTitleChanges();
		}
	}

	private class EditKeyListener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// If the event is a key-down event on the "enter" button
			if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
				// Perform action on key press
				applyTitleChanges();
				return true;
			}
			return false;
		}
	}
	
	private class RedioListListener implements OnCheckedChangeListener {

		public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static int getToEditPosition() {
		return editPosition;
	}

	private void applyTitleChanges() {
		String changedTitle = editTitle.getText().toString();
		if (editPosition == -1) {
			if (changedTitle.trim().length() != 0) {
				// Add Item
				ItemContainer.addItem(changedTitle, currentListId);
				editTitle.setText("");
				// Update
				showListAdapter.notifyDataSetChanged();
			}
		} else {
			if (changedTitle.trim().length() != 0) {
				// Change Item content
				ItemContainer.changeItemAt(editPosition, changedTitle);
			} else {
				// Delete Item if content erased
				ItemContainer.removeItemAt(editPosition);
			}
			setEditPosition(-1);
			updateEditPosition();
		}

		// Modifications has been made
		setChanged(true);
	}

	private void updateEditPosition() {
		if (editPosition == -1) {
			editTitle.setText("");
		} else {
			Item item = ItemContainer.getItemAt(editPosition);
			editTitle.setText(item.getTitle());
			editTitle.setSelection(item.getTitle().length());
		}

		showListAdapter.notifyDataSetChanged();

		// Show/Hide Views
		boolean itemSelectedForEdit = (editPosition != -1);
		buttonAddEdit.setVisibility(Utils.getVisibility(!itemSelectedForEdit));
		buttonOkEdit.setVisibility(Utils.getVisibility(itemSelectedForEdit));
	}

	private void updateMode() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mode == Constantes.Modes.EDIT) {
			// Show/Hide views
			editPanel.setVisibility(View.VISIBLE);
			statusPanel.setVisibility(View.GONE);
			toolEditPanel.setVisibility(View.VISIBLE);
			toolEditMainPanel.setVisibility(View.GONE);

			// Set Focus to Edit Text Bar
			editTitle.requestFocus();

			// Show keyboard
			imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
		} else {
			// Show/Hide views
			statusPanel.setVisibility(View.VISIBLE);
			editPanel.setVisibility(View.GONE);
			toolEditPanel.setVisibility(View.GONE);
			toolEditMainPanel.setVisibility(View.VISIBLE);

			// Hode keyboard
			imm.hideSoftInputFromWindow(editPanel.getWindowToken(), 0);

			// edit position
			setEditPosition(-1);
			updateEditPosition();
		}
	}

	private void updateStatusbar() {
		// Update Status Bar
		statusToBay.setText(Integer.toString(ItemContainer.size() - ItemContainer.countChecked()));
		statusAll.setText(Integer.toString(ItemContainer.size()));
		statusLastChanged.setText(dateFormat.format(ItemContainer.getLastChanged()));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();

		switch (v.getId()) {
		case R.id.list:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			Item item = ItemContainer.getItemAt(info.position);
			menu.setHeaderTitle(item.getTitle());

			inflater.inflate(R.menu.context_menu_list, menu);

			MenuItem menuItem = menu.getItem(1);
			if (item.isImportent()) {
				menuItem.setTitle(R.string.cxtm_unmark_importent);
			} else {
				menuItem.setTitle(R.string.cxtm_mark_importent);
			}
			break;

		case R.id.b_clear:
			inflater.inflate(R.menu.context_menu_confirm_clear, menu);
			menu.setHeaderTitle(R.string.cxtm_clear_title);
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.cxtm_delete_item:
			ItemContainer.removeItemAt(info.position);
			// Modifications has been made
			BuyListActivity.setChanged(true);
			// Update
			showListAdapter.notifyDataSetChanged();
			// Update Status bar
			updateStatusbar();
			return true;
		case R.id.cxtm_mark_importent:
			ItemContainer.getItemAt(info.position).setImportent(!ItemContainer.getItemAt(info.position).isImportent());
			// Update
			showListAdapter.notifyDataSetChanged();
			return true;
		case R.id.cxtm_clear_confirm:
			ItemContainer.clear();
			// Modifications has been made
			setChanged(true);
			// Update
			showListAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public static boolean isChanged() {
		return changed;
	}

	public static void setChanged(boolean changed) {
		BuyListActivity.changed = changed;
	}

	public static int getEditPosition() {
		return editPosition;
	}

	public static void setEditPosition(int editPosition) {
		BuyListActivity.editPosition = editPosition;
	}

	public static int getMode() {
		return mode;
	}

	public static void setMode(int mode) {
		BuyListActivity.mode = mode;
	}

}