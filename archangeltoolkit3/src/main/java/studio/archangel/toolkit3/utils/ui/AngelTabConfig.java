package studio.archangel.toolkit3.utils.ui;

import android.graphics.drawable.Drawable;
import android.view.View;

import studio.archangel.toolkit3.R;

/**
 * Created by xmk on 16/5/17.
 */
public abstract class AngelTabConfig {
	public int getTabLayoutId() {
		return R.layout.view_tab;
	}

	public int getReddotId() {
		return R.id.view_tab_red_dot;
	}

	public abstract boolean hasReddot();

	public abstract String getText();

	public abstract Drawable getSelectedDrawable();

	public abstract Drawable getUnselectedDrawable();

	public abstract boolean isSpecialButton();

	public abstract View constructSpecialButton();
}
