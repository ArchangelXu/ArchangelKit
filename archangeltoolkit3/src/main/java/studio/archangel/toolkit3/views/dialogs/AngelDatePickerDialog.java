package studio.archangel.toolkit3.views.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;

import net.simonvt.numberpicker.NumberPicker;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.text.DateProvider;


/**
 * Created by Michael on 2014/10/18.
 */
public class AngelDatePickerDialog extends DialogFragment {

	/**
	 * 年份和月份的NumberPicker
	 */
	NumberPicker np_year, np_month;
	/**
	 * 至今的CheckBox
	 */
	CheckBox cb_tillnow;
	/**
	 * 年份和月份选择器的父控件
	 */
	RelativeLayout rl_container;
	/**
	 * 至今的容器
	 */
	RelativeLayout rl_tillnow_container;
	/**
	 * 标题
	 */
	TextView tv_title;
	/**
	 * 用户点击确定后会调用此监听器
	 */
	OnDatePickedListener l1;
	OnPostInitListener l2;
	/**
	 * 主题
	 */
	int res_theme;
	/**
	 * 月份的字符串转化器
	 */
	NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
		@Override
		public String format(int value) {
			if (value < 10) {
				return "0" + value;
			} else {
				return String.valueOf(value);
			}
		}
	};

	/**
	 * 实例化，带“至今”选项
	 *
	 * @param context
	 * @param title
	 * @param res_divider
	 * @param res_theme
	 * @param listener
	 * @return 一个新实例
	 */
	public static AngelDatePickerDialog newInstance(Context context, String title, int res_divider, int res_theme, OnDatePickedListener listener) {
		return newInstance(context, title, false, DateProvider.getThisYear(), res_divider, res_theme, listener);
	}

	/**
	 * 实例化
	 *
	 * @param context     上下文
	 * @param title       标题
	 * @param tillnow     是否显示“至今”选项
	 * @param max_year    最大年份。如果小于等于今年，将自动检测月份以限定用户选择的日期不超过当前日期
	 * @param res_divider 资源ID，NumberPicker的分割线的颜色
	 * @param res_theme   NumberPicker的主题
	 * @param listener    监听器
	 * @return 一个新实例
	 */
	public static AngelDatePickerDialog newInstance(Context context, String title, boolean tillnow, int max_year, int res_divider, int res_theme, OnDatePickedListener listener) {
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putInt("res_divider", res_divider);
		args.putBoolean("show_tillnow", tillnow);
		args.putInt("max_year", max_year);

		AngelDatePickerDialog d = (AngelDatePickerDialog) Fragment.instantiate(context, AngelDatePickerDialog.class.getName(), args);
		d.setStyle(AngelDatePickerDialog.STYLE_NORMAL, res_theme);
		d.res_theme = res_theme;
		d.setOnDatePickedListener(listener);
		return d;
	}

	String getValue() {
		if (cb_tillnow.isChecked()) {
			return "至今";
		} else {
			return np_year.getValue() + "." + formatter.format(np_month.getValue());
		}
	}

	/**
	 * 设置默认日期
	 *
	 * @param tillnow 是否勾选“至今”，勾选至今的情况下，忽略{@code year}和{@code month}的设置
	 * @param year    要显示的年份
	 * @param month   要显示的月份
	 */
	public void setDefaultDate(boolean tillnow, int year, int month) {
		if (tillnow) {
			cb_tillnow.setChecked(true);
		} else {
			try {
				np_year.setValue(year, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				np_month.setValue(month);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setDefaultDate(boolean tillnow, String date_String) {
		if (tillnow) {
			cb_tillnow.setChecked(true);
		} else {
			try {
				if (date_String.contains(".")) {
					String[] date = date_String.split("\\.");
					setDefaultDate(false, Integer.parseInt(date[0]), Integer.parseInt(date[1]));
				} else {
					setDefaultDate(false, Integer.parseInt(date_String.substring(0, 4)), Integer.parseInt(date_String.substring(4, 6)));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				setDefaultDate(false, DateProvider.getThisYear(), DateProvider.getThisMonth());
			}
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


//        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		String title = getArguments().getString("title");
		int res_divider = getArguments().getInt("res_divider");
		boolean show_tillnow = getArguments().getBoolean("show_tillnow");
		int max_year = getArguments().getInt("max_year");
		View custom = getActivity().getLayoutInflater().inflate(R.layout.dialog_datepicker, null);
		np_year = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_year);
		np_month = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_month);
		cb_tillnow = (CheckBox) custom.findViewById(R.id.dialog_datepicker_tillnow);
		rl_container = (RelativeLayout) custom.findViewById(R.id.dialog_datepicker_container);
		rl_tillnow_container = (RelativeLayout) custom.findViewById(R.id.dialog_datepicker_tillnow_container);
		tv_title = (TextView) custom.findViewById(R.id.dialog_datepicker_title);
		tv_title.setText(title);
		np_year.setMinValue(1970);
		np_year.setMaxValue(DateProvider.getThisYear());
		np_year.setFocusable(true);
		np_year.setFocusableInTouchMode(true);
		np_year.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_year.setDivider(getResources().getDrawable(res_divider));
		if (max_year <= DateProvider.getThisYear()) {
			np_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					if (np_year.getMaxValue() == newVal) {
						np_month.setMaxValue(DateProvider.getThisMonth());
						np_month.invalidate();
					} else {
						np_month.setMaxValue(12);
						np_month.invalidate();
					}
				}
			});
		}
		np_month.setMinValue(1);
		np_month.setMaxValue(12);
		np_month.setValue(DateProvider.getThisMonth());
		np_month.setFocusable(true);
		np_month.setFocusableInTouchMode(true);
		np_month.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_month.setDivider(getResources().getDrawable(res_divider));


		np_month.setFormatter(formatter);
		np_year.setValue(DateProvider.getThisYear(), true);
		cb_tillnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				rl_container.setAlpha(isChecked ? 0.5f : 1.0f);
				np_year.setEnabled(!isChecked);
				np_month.setEnabled(!isChecked);
			}
		});
		rl_tillnow_container.setVisibility(show_tillnow ? View.VISIBLE : View.GONE);
		AngelDialog dialog = new AngelDialog(getActivity(), null, "");

//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (l2 != null) {
					l2.onPostInit();
				}
			}
		});

		dialog.show();
		dialog.setCustomView(custom);
//        dialog.hide();
		dialog.setOnOkClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (l1 != null) {
					l1.onDatePicked(getValue());
				}
			}
		});
		TextView ab = dialog.getOkButton();
		TypedArray a = getActivity().getTheme().obtainStyledAttributes(res_theme, new int[]{R.attr.selectionDivider});
		int attributeResourceId = a.getResourceId(0, 0);
		a.recycle();
//        ColorDrawable drawable = (ColorDrawable) np_year.getDividerDrawable();

		ab.setTextColor(getResources().getColor(attributeResourceId));
		ab.setText("确定");
		TextView cb = dialog.getCancelButton();
		cb.setText("取消");
//        b.setView(custom);
//        b.setNegativeButton("取消", null);
//        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (l1 != null) {
//                    l1.onDatePicked(getValue());
//                }
//            }
//        });
//
//        AlertDialog d = b.create();
//        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        d.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                if (l2 != null) {
//                    l2.onPostInit();
//                }
//            }
//        });

		return dialog;
	}

	/**
	 * 设置监听器
	 *
	 * @param lis
	 */
	public void setOnDatePickedListener(OnDatePickedListener lis) {
		l1 = lis;
	}

	/**
	 * 设置监听器
	 *
	 * @param lis
	 */
	public void setOnPostInitListener(OnPostInitListener lis) {
		l2 = lis;
	}

	/**
	 * 日期选择监听器
	 */
	public interface OnDatePickedListener {
		/**
		 * 当用户按下对话框确定键时，调用此方法
		 *
		 * @param result 用户选择的日期
		 */
		public void onDatePicked(String result);
	}

	/**
	 * 初始化结束监听器
	 */
	public interface OnPostInitListener {
		/**
		 * 初始化完毕后，调用此方法
		 */
		public void onPostInit();
	}
}
