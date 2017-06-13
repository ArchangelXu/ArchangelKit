package studio.archangel.toolkit3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import studio.archangel.toolkit3.utils.Logger;


/**
 * Created by Michael on 2015/3/4.
 */
public class AngelPageIndicator2 extends LinearLayout {
	ArrayList<AngelPageIndicatorUnit2> units;
	int last_index = -1;
	Class<? extends AngelPageIndicatorUnit2> unit_clazz;

	public AngelPageIndicator2(Context context) {
		this(context, null);
	}

	public AngelPageIndicator2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelPageIndicator2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	void init(Context context, AttributeSet attrs, int defStyleAttr) {
		setOrientation(HORIZONTAL);
		units = new ArrayList<>();
	}

	public void setUnitClass(Class<? extends AngelPageIndicatorUnit2> c) {
		unit_clazz = c;
	}

	public void setCount(int count) {
		if (unit_clazz == null) {
			Logger.err("you must set unit class first");
			return;
		}
		removeAllViews();
		units.clear();
		for (int i = 0; i < count; i++) {
			AngelPageIndicatorUnit2 unit = null;
			try {
				Constructor constructor = unit_clazz.getDeclaredConstructor(Context.class);
				unit = (AngelPageIndicatorUnit2) constructor.newInstance(getContext());
				unit.setSelected(false, false);
			} catch (Exception e) {
				Logger.err(e);//e.printStackTrace();
			}
			units.add(unit);
			addView(unit);
		}
		if (count <= 1) {
			setVisibility(View.GONE);
		} else {
			AngelPageIndicatorUnit2 unit = units.get(units.size() - 1);
			LayoutParams params = (LayoutParams) unit.getLayoutParams();
			params.rightMargin = 0;
		}
		setClipChildren(false);
	}

	public void setSelection(int index) {
		if (last_index >= 0 && last_index < units.size()) {
			AngelPageIndicatorUnit2 last_unit = units.get(last_index);
			last_unit.setSelected(false);
		}
		if (index >= 0 && index < units.size()) {
			AngelPageIndicatorUnit2 unit = units.get(index);
			unit.setSelected(true);
			last_index = index;
		}
	}
}

