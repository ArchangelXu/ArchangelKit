package studio.archangel.toolkit3.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import studio.archangel.toolkit3.R;


public class AngelMenuDialog extends android.app.Dialog {

	String message;
	TextView tv_msg;
	String title;
	TextView titleTextView;
	ListView list;
	View v_back;
	OnMenuItemClickListener listener;
	int res_list_item_layout, res_list_item_layout_text;
	String[] items;
	boolean touch_outside_to_cancel = true;

	public AngelMenuDialog(Context context, String title, String message, String[] items, OnMenuItemClickListener listener) {
		this(context, title, message, items, R.layout.item_menudialog, R.id.item_menu_tv, listener);
	}

	public AngelMenuDialog(Context context, String title, String message, String[] items, int res_list_item_layout, int res_list_item_layout_text, OnMenuItemClickListener listener) {
		super(context, R.style.AnimDialog);
		this.title = title;
		this.message = message;
		this.items = items;
		this.res_list_item_layout = res_list_item_layout;
		this.res_list_item_layout_text = res_list_item_layout_text;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		setContentView(R.layout.view_dialog_menu);
		this.titleTextView = (TextView) findViewById(R.id.view_dialog_menu_title);
		setTitle(title);
		this.tv_msg = (TextView) findViewById(R.id.view_dialog_menu_message);
		setMessage(message);
		v_back = findViewById(R.id.view_dialog_back);
		v_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (touch_outside_to_cancel) {
					dismiss();
				}
			}
		});
		list = (ListView) findViewById(R.id.items);
		if (items != null) {
			list.setAdapter(new ItemAdapter(this, getContext(), res_list_item_layout, res_list_item_layout_text, items, listener));
			list.setVisibility(View.VISIBLE);
			tv_msg.setVisibility(View.GONE);
		}
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(false);
		touch_outside_to_cancel = cancel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		if (message == null || message.isEmpty())
			tv_msg.setVisibility(View.GONE);
		else {
			tv_msg.setVisibility(View.VISIBLE);
		}
		tv_msg.setText(message);
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
		if (title == null || title.isEmpty())
			titleTextView.setVisibility(View.GONE);
		else {
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
	}

	class ItemAdapter extends ArrayAdapter<String> {
		int res_layout, res_button;
		LayoutInflater inflater;
		OnMenuItemClickListener listener;
		AngelMenuDialog owner;
		String[] items;

		public ItemAdapter(AngelMenuDialog dialog, Context context, int resource, int textViewResourceId, String[] objects, OnMenuItemClickListener onItemClickListener) {
			super(context, resource, textViewResourceId, objects);
			items = objects;
			res_layout = resource;
			res_button = textViewResourceId;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listener = onItemClickListener;
			owner = dialog;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return createViewFromResource(position, convertView, parent, res_layout);
		}

		private View createViewFromResource(final int position, View convertView, ViewGroup parent, int resource) {
			View view;
			TextView button;

			if (convertView == null) {
				view = inflater.inflate(resource, parent, false);
			} else {
				view = convertView;
			}

			try {
				button = (TextView) view.findViewById(res_button);
			} catch (ClassCastException e) {
				Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
				throw new IllegalStateException(
						"ArrayAdapter requires the resource ID to be a TextView", e);
			}

			final String item = getItem(position);
			button.setText(item);
			button.setTextColor(view.getContext().getResources().getColor(R.color.black));
			button.setGravity(Gravity.CENTER_VERTICAL);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					owner.dismiss();
					listener.onClick(item, position);
				}
			});


			return view;
		}
	}

	public interface OnMenuItemClickListener {
		void onClick(String text, int position);
	}
}
