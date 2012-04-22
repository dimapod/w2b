package fr.dpo.baylist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.dpo.baylist.BuyListActivity;
import fr.dpo.baylist.R;
import fr.dpo.baylist.model.Item;
import fr.dpo.baylist.model.ItemContainer;
import fr.dpo.baylist.utils.Constantes;

public class ShowListAdapter extends BaseAdapter {
	// private static final String TAG = "BuyList";
	private LayoutInflater inflater;
	private static final int LAYOUT = R.layout.show_item;

	public interface ShowListColors {
		int TEXT_LIST1 = Color.BLUE;
		int TEXT_LIST2 = Color.GREEN;
		int TEXT_CHECKED = Color.GRAY;
		int TEXT_QUANTITY = Color.parseColor("#50A5FF");
	}

	public ShowListAdapter(Context context) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return ItemContainer.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup layout;
		if (convertView == null) {
			layout = (ViewGroup) inflater.inflate(LAYOUT, null);
		} else {
			layout = (ViewGroup) convertView;
		}

		// Item to Show
		Item item = ItemContainer.getItemAt(position);

		// Title
		TextView titleView = ((TextView) layout.findViewById(R.id.title));
		titleView.setText(item.getLabel());

		// Importent Item
		if (item.isImportent()) {
			titleView.setTypeface(Typeface.DEFAULT_BOLD);
		} else {
			titleView.setTypeface(Typeface.DEFAULT);
		}

		// Quantity
		TextView quantityView = ((TextView) layout.findViewById(R.id.quantity));
		if (item.getParsedItem().getQuantity() != -1) {
			quantityView.setText(item.getParsedItem().getQuantity2Show());
			quantityView.setVisibility(View.VISIBLE);
		} else {
			quantityView.setVisibility(View.INVISIBLE);
		}

		ViewGroup itemLayout = ((ViewGroup) layout.findViewById(R.id.item_layout));
		if (BuyListActivity.getMode() != Constantes.Modes.EDIT) {
			if (item.isChecked()) {
				titleView.getPaint().setStrikeThruText(true);
				titleView.setTextColor(ShowListColors.TEXT_CHECKED);
				quantityView.getPaint().setStrikeThruText(true);
				quantityView.setTextColor(ShowListColors.TEXT_CHECKED);
			} else {
				titleView.getPaint().setStrikeThruText(false);
				quantityView.getPaint().setStrikeThruText(false);
				if (item.getListId() == 1) {
					titleView.setTextColor(ShowListColors.TEXT_LIST1);
					quantityView.setTextColor(ShowListColors.TEXT_LIST1);
				} else {
					titleView.setTextColor(ShowListColors.TEXT_LIST2);
					quantityView.setTextColor(ShowListColors.TEXT_LIST2);
				}
			}
		} else {
			if (position == BuyListActivity.getToEditPosition()) {
				itemLayout.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.choice));
			} else {
				itemLayout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.transparent));
			}

			if (item.getListId() == 1) {
				titleView.setTextColor(ShowListColors.TEXT_LIST1);
				quantityView.setTextColor(ShowListColors.TEXT_LIST1);
			} else {
				titleView.setTextColor(ShowListColors.TEXT_LIST2);
				quantityView.setTextColor(ShowListColors.TEXT_LIST2);
			}

			titleView.getPaint().setStrikeThruText(false);
			quantityView.getPaint().setStrikeThruText(false);
		}

		return layout;
	}

	public class DeleteClickListener implements OnClickListener {
		private BaseAdapter adapter;
		private int position;

		public DeleteClickListener(final BaseAdapter adapter, final int position) {
			this.adapter = adapter;
			this.position = position;
		}

		public void onClick(View paramView) {
			ItemContainer.removeItemAt(position);
			// Modifications has been made
			BuyListActivity.setChanged(true);
			// Update
			adapter.notifyDataSetChanged();
		}
	}

}
