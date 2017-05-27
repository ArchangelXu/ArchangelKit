package studio.archangel.toolkit3.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;

import java.util.ArrayList;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


/**
 * Created by Michael on 2015/3/4.
 */
public class AngelPageIndicator extends LinearLayout {
	ArrayList<AngelPageIndicatorUnit> units;
	int last_index = -1;
	int res_unit_top = R.color.blue;
	int res_unit_middle = R.color.white;
	int res_unit_bottom = R.color.trans_white_50;
	int unit_radius;

	public AngelPageIndicator(Context context) {
		this(context, null);
	}

	public AngelPageIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	void init(Context context, AttributeSet attrs, int defStyleAttr) {
		setOrientation(HORIZONTAL);
		setPadding(UIUtil.getPX(context, 1), UIUtil.getPX(context, 1), UIUtil.getPX(context, 1), UIUtil.getPX(context, 1));
		units = new ArrayList<>();
		unit_radius = UIUtil.getPX(context, 10);
	}

	public void setUnitColorRes(int top, int middle, int bottom) {
		res_unit_top = top;
		res_unit_middle = middle;
		res_unit_bottom = bottom;
	}

	public void setCount(int count) {
		removeAllViews();
		units.clear();
		for (int i = 0; i < count; i++) {
			AngelPageIndicatorUnit unit = new AngelPageIndicatorUnit(getContext());
			unit.cd_top.setColor(getContext().getResources().getColor(res_unit_top));
			unit.cd_middle.setColor(getContext().getResources().getColor(res_unit_middle));
			unit.cd_bottom.setColor(getContext().getResources().getColor(res_unit_bottom));
			unit.setSelected(false, false);
			units.add(unit);
			addView(unit);
		}
		if (count <= 1) {
			setVisibility(View.GONE);
		} else {
			AngelPageIndicatorUnit unit = units.get(units.size() - 1);
			LayoutParams params = (LayoutParams) unit.getLayoutParams();
			params.rightMargin = 0;
		}
		setClipChildren(false);
	}

	public void setUnitRadius(int radius) {
		unit_radius = radius;
	}

	public void setSelection(int index) {
		if (last_index >= 0 && last_index < units.size()) {
			AngelPageIndicatorUnit last_unit = units.get(last_index);
			last_unit.setSelected(false);
		}
		if (index >= 0 && index < units.size()) {
			AngelPageIndicatorUnit unit = units.get(index);
			unit.setSelected(true);
			last_index = index;
		}
	}
}

class AngelPageIndicatorUnit extends View {
	boolean is_selected = true;
	GradientDrawable cd_top, cd_middle, cd_bottom;

	public AngelPageIndicatorUnit(Context context) {
		super(context);
		init(context);
	}

	public AngelPageIndicatorUnit(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	void init(Context context) {
		setBackgroundResource(R.drawable.layer_indicator);
		LayerDrawable layer = (LayerDrawable) getBackground();
		cd_top = (GradientDrawable) layer.findDrawableByLayerId(R.id.layer_indicator_top);
		cd_middle = (GradientDrawable) layer.findDrawableByLayerId(R.id.layer_indicator_middle);
		cd_bottom = (GradientDrawable) layer.findDrawableByLayerId(R.id.layer_indicator_bottom);
//        cd_middle.setAlpha(255/8);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtil.getPX(context, 12), UIUtil.getPX(context, 12));
		params.weight = 1;
		params.rightMargin = UIUtil.getPX(context, 2);
		setLayoutParams(params);

	}

	public void setSelected(boolean selected) {
		setSelected(selected, true);
	}

	public void setSelected(boolean selected, boolean need_animation) {
		if (is_selected == selected) {//如果状态不变
			return;
		}
		is_selected = selected;
		float target_scale = is_selected ? 1f : 0.75f;
		long duration = need_animation ? 350 : 0;
		ViewPropertyAnimator animator_scale = animate().scaleX(target_scale).scaleY(target_scale).setDuration(duration);
		ObjectAnimator animator_alpha = null;
		if (is_selected) {
			animator_alpha = ObjectAnimator.ofInt(cd_top, "alpha", 0, 255);
		} else {
			animator_alpha = ObjectAnimator.ofInt(cd_top, "alpha", 255, 0);
		}
		animator_alpha.setDuration(duration);
		animator_scale.start();
		animator_alpha.start();
	}
}
