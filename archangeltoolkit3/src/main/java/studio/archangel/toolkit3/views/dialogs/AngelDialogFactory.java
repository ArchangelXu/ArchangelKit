package studio.archangel.toolkit3.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.widget.EditText;

import java.util.List;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.activities.AngelActivity;
import studio.archangel.toolkit3.interfaces.OnPermissionCheckListener;
import studio.archangel.toolkit3.models.MenuDialogItem;
import studio.archangel.toolkit3.utils.CommonUtil;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.UIUtil;

/**
 * Created by xmk on 2016/10/19.
 */

public class AngelDialogFactory {
	public static Dialog getDefaultDialog(Context c) {
//		int res = AngelApplication.getInstance().getDialogStyleRes();
		Dialog d;
//		if (res == 0) {
		d = new Dialog(c);
//		} else {
//			d = new Dialog(c, res);
//		}
		d.cornerRadius(UIUtil.getPX(8));
		d.elevation(0);
		d.contentMargin(0);
		d.dimAmount(0.5f);
		d.inAnimation(R.anim.dialog_fade_in);
		d.outAnimation(R.anim.dialog_fade_out);
		d.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).maxWidth(UIUtil.getPX(300));
		return d;
	}

	public static Dialog getSimpleDialog(Context c, String title, CharSequence content) {
		Dialog d = getDefaultDialog(c);
		d.title(title);
		View custom = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_simple_text, null);
		TextView tv = (TextView) custom.findViewById(R.id.dialog_simple_text_text);
		tv.setText(content);
		d.maxHeight(UIUtil.getPX(320));
		d.contentView(custom);
		return d;
	}

	public static Dialog getInputDialog(Context c, String title, CharSequence hint) {
		Dialog d = getDefaultDialog(c);
		d.title(title);
		View custom = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_input, null);
		EditText tv = (EditText) custom.findViewById(R.id.dialog_input_input);
		tv.setHint(hint);
		d.maxHeight(UIUtil.getPX(320));
		d.contentView(custom);
		return d;
	}

	public static Dialog getLoadingDialog(Context c, String title, CharSequence content) {
		Dialog d = getDefaultDialog(c);
		d.title(title);
		View custom = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_loading, null);
		TextView tv = (TextView) custom.findViewById(R.id.dialog_loading_text);
		tv.setText(content);
//		ProgressBar pb= (ProgressBar) custom.findViewById(R.id.dialog_loading_progress);
//		tv = (TextView) custom.findViewById(R.id.dialog_loading_progress_text);
		d.contentView(custom);
		return d;
	}

	public static Dialog getMenuDialog(Context c, String title, CharSequence content, List<MenuDialogItem> items, OnMenuDialogItemClickListener listener) {
		Dialog d = getDefaultDialog(c);
		d.title(title);
		View custom = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_menu, null);
		TextView tv = (TextView) custom.findViewById(R.id.dialog_menu_content);
		if (CommonUtil.isEmptyString(content)) {
			tv.setVisibility(View.GONE);
		} else {
			tv.setText(content);
		}
		LinearLayout layout = (LinearLayout) custom.findViewById(R.id.dialog_menu_items);
		if (items != null) {
			for (MenuDialogItem item : items) {
				View v_item = null;
				try {
					v_item = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_dialog_menu, null);
					ImageView iv = (ImageView) v_item.findViewById(R.id.item_dialog_menu_icon);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();
					params.width = params.height = item.icon_size_px;
					if (item.icon_res == 0) {
						if (item.icon_drawable != null) {
							iv.setImageDrawable(item.icon_drawable);
						} else {
							iv.setVisibility(View.GONE);
						}
					} else {
						iv.setImageResource(item.icon_res);
					}
					tv = (TextView) v_item.findViewById(R.id.item_dialog_menu_text);
					tv.setText(item.text);
					v_item.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (listener != null) {
								listener.onItemClick(item);
							}
							d.dismiss();
						}
					});
					layout.addView(v_item);
					params = (LinearLayout.LayoutParams) v_item.getLayoutParams();
					params.width = ViewGroup.LayoutParams.MATCH_PARENT;
					params.height = UIUtil.getPX(48);
				} catch (Exception e) {
					Logger.err(e);//e.printStackTrace();
				}
			}
		}
		d.contentView(custom);
		return d;
	}

	public static Dialog getPermissionDialog(AngelActivity act, String title, String content, int setting_button_text_color_res, OnPermissionCheckListener listener) {
		Dialog d = AngelDialogFactory.getSimpleDialog(act, title, content);
		TextView tv = (TextView) d.findViewById(R.id.dialog_simple_text_text);
		tv.setTextColor(act.getResources().getColor(R.color.text_grey));
		d.negativeActionRipple(R.style.MDRipple);
		d.negativeActionTextColor(act.getResources().getColor(R.color.text_black));
		d.negativeAction(R.string.permission_setting);
		d.negativeActionClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
				act.permission_helper.openSystemPermissionSettingActivity(listener);
			}
		});
		d.positiveActionRipple(R.style.MDRipple);
		d.positiveActionTextColor(act.getResources().getColor(setting_button_text_color_res));
		d.positiveAction(R.string.permission_try_again);
		d.positiveActionClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
				listener.onRetry();
			}
		});
		d.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				listener.onDeny();
			}
		});
		return d;
	}

	public interface OnMenuDialogItemClickListener {
		void onItemClick(MenuDialogItem item);
	}
}

