package studio.archangel.toolkit3.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import net.simonvt.numberpicker.NumberPicker;

import java.lang.reflect.Field;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.views.dialogs.AngelDialog;

/**
 * Created by Michael on 2015/8/7.
 */
public class AngelTimePickerDialog extends AngelDialog {

	NumberPicker np_hour, np_min;
	OnTimePickedListener listener;
	NumberPicker.Formatter formatter_hour, formatter_min;

	public AngelTimePickerDialog(Context context, String title, int color) {
		super(context, title, "", color);
		formatter_hour = new NumberPicker.Formatter() {
			@Override
			public String format(int value) {
				return value + "";
			}
		};
		formatter_min = new NumberPicker.Formatter() {
			@Override
			public String format(int value) {
				return value + "";
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View custom = getLayoutInflater().inflate(R.layout.dialog_timepicker, null);
		np_hour = (NumberPicker) custom.findViewById(R.id.dialog_timepicker_hour);
		np_min = (NumberPicker) custom.findViewById(R.id.dialog_timepicker_min);
		setCustomView(custom);
		setButtonStyle(DialogStyle.OK_CANCEL);
		setOnOkClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onTimePicked(formatter_hour.format(np_hour.getValue()), formatter_min.format(np_min.getValue()));
				}
			}
		});
		np_hour.setMaxValue(23);
		np_hour.setMinValue(0);
		np_min.setMaxValue(59);
		np_min.setMinValue(0);
		np_hour.setFocusable(true);
		np_hour.setFocusableInTouchMode(true);
		np_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_hour.setDivider(new ColorDrawable(main_color));
		np_min.setFocusable(true);
		np_min.setFocusableInTouchMode(true);
		np_min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_min.setDivider(new ColorDrawable(main_color));
		try {
			Field f = NumberPicker.class.getDeclaredField("mInputText");
			f.setAccessible(true);
			EditText inputText = (EditText) f.get(np_hour);
			inputText.setFilters(new InputFilter[0]);
			inputText = (EditText) f.get(np_min);
			inputText.setFilters(new InputFilter[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
//        String[] values = new String[24];
//        for (int i = 0; i < 24; i++) {
//            values[i] = (i < 10 ? "0" : "") + i;
//        }
//        np_hour.setDisplayedValues(values);
//        values = new String[60];
//        for (int i = 0; i < 60; i++) {
//            values[i] = (i < 10 ? "0" : "") + i;
//        }
//        np_min.setDisplayedValues(values);

	}

	public void configHourPicker(int max, int min, int def, NumberPicker.Formatter formatter) {
		np_hour.setMaxValue(max);
		np_hour.setMinValue(min);
		np_hour.setValue(def);
		np_hour.setFormatter(formatter);
		formatter_hour = formatter;
	}

	public void configMinutePicker(int max, int min, int def, NumberPicker.Formatter formatter) {
		np_min.setMaxValue(max);
		np_min.setMinValue(min);
		np_min.setValue(def);
		np_min.setFormatter(formatter);
		formatter_min = formatter;
	}

	public NumberPicker.Formatter getHourFormatter() {
		return formatter_hour;
	}

	public void setHourFormatter(NumberPicker.Formatter formatter_hour) {
		this.formatter_hour = formatter_hour;
	}

	public NumberPicker.Formatter getMinFormatter() {
		return formatter_min;
	}

	public void setMinFormatter(NumberPicker.Formatter formatter_min) {
		this.formatter_min = formatter_min;
	}

	public NumberPicker getNumberPickerHour() {
		return np_hour;
	}

	public NumberPicker getNumberPickerMinute() {
		return np_min;
	}

	public OnTimePickedListener getOnTimePickedListener() {
		return listener;
	}

	public void setOnTimePickedListener(OnTimePickedListener listener) {
		this.listener = listener;
	}

	public interface OnTimePickedListener {
		void onTimePicked(String hour, String min);
	}
}
