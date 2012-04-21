package fr.dpo.baylist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ImportActivity extends Activity {

	public static final String IMPORTED_DATA_KEY = "DATA";
	public static final String IMPORT_MODE_KEY = "MODE";
	public static final String IMPORT_MODE_APPEND = "APPEND";
	public static final String IMPORT_MODE_REPLACE = "REPLACE";

	private EditText editImport = null;
	private ClipboardManager clipboard = null;
	private Button buttonImportPast = null;
	private Button buttonImportProcess = null;

	private void fetchViewReferences() {
		this.editImport = (EditText) findViewById(R.id.e_import);
		this.clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		this.buttonImportPast = (Button) findViewById(R.id.b_import_past);
		this.buttonImportProcess = (Button) findViewById(R.id.b_import_process);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Toast.makeText(getApplicationContext(), splitedText[0], Toast.LENGTH_SHORT).show();
		setContentView(R.layout.import_act);

		fetchViewReferences();

		Button buttonImportInsert = (Button) findViewById(R.id.b_import_insert);
		buttonImportInsert.setOnClickListener(new ImportInsertClickListener());

		Button buttonImportReplace = (Button) findViewById(R.id.b_import_replace);
		buttonImportReplace.setOnClickListener(new ImportInsertClickListener());
		
		buttonImportPast.setOnClickListener(new ImportPastClickListener());
		buttonImportProcess.setOnClickListener(new ImportProcessClickListener());
	}

	private void processText(boolean fullProcess) {
		String text = editImport.getText().toString();

		// Split
		String[] splitedText = text.split("\\n");

		StringBuilder sb = new StringBuilder();
		boolean firstLine = true;
		for (String lineCandidate : splitedText) {
			// Trim
			lineCandidate = lineCandidate.trim();

			// Delete empty strings
			if (lineCandidate.isEmpty()) {
				continue;
			}

			if (fullProcess) {
				// Delete all characters before first letter
				Pattern pattern = Pattern.compile("\\w+");
				Matcher matcher = pattern.matcher(lineCandidate);
				boolean found = matcher.find();
				if (found) {
					lineCandidate = lineCandidate.substring(matcher.start());
				} else {
					continue;
				}
			}
			
			// Concat result
			if (!firstLine) {
				sb.append("\n");
			} else {
				firstLine = false;
			}
			sb.append(lineCandidate);
		}

		editImport.setText(sb.toString());
	}

	public class ImportInsertClickListener implements OnClickListener {
		public void onClick(View v) {
			processText(false);

			Intent intent = new Intent();
			intent.putExtra(IMPORTED_DATA_KEY, editImport.getText().toString());
			if (v.getId() == R.id.b_import_insert) {
				intent.putExtra(IMPORT_MODE_KEY, IMPORT_MODE_APPEND);
			} else {
				intent.putExtra(IMPORT_MODE_KEY, IMPORT_MODE_REPLACE);
			}

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	public class ImportProcessClickListener implements OnClickListener {
		public void onClick(View v) {
			processText(true);
		}
	}

	public class ImportPastClickListener implements OnClickListener {
		public void onClick(View v) {
			editImport.setText(clipboard.getText());
		}
	}
}
