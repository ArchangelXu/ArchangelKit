package studio.archangel.toolkit3.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

import java.util.ArrayList;

import studio.archangel.toolkit3.utils.Logger;

/**
 * Created by Administrator on 2015/12/18.
 */
public class AngelTabHost extends FragmentTabHost {
	OnSpecialTabButtonClickListener listener;
	ArrayList<Boolean> is_special_button;
	private FragmentManager manager;

	public AngelTabHost(Context context) {
		this(context, null);
	}

	public AngelTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		is_special_button = new ArrayList<>();
	}

	@Override
	public void setCurrentTab(int index) {
		if (is_special_button.size() > index && is_special_button.get(index)) {
			if (listener != null) {
				listener.OnSpecialTabButtonClicked(index);
			}
		} else {
			super.setCurrentTab(index);
		}
	}

	@Override
	public void setup(Context context, FragmentManager manager, int containerId) {
		super.setup(context, manager, containerId);
		this.manager = manager;
	}

	public boolean isSpecialButtonTab(int index) {
		if (is_special_button == null) {
			return false;
		}
		if (index >= is_special_button.size()) {
			return false;
		}
		return is_special_button.get(index);
	}

	@Override
	public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args) {
		super.addTab(tabSpec, clss, args);
		is_special_button.add(false);
	}

	public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args, boolean is_special_button) {
		super.addTab(tabSpec, clss, args);
		this.is_special_button.add(is_special_button);
	}

	public interface OnSpecialTabButtonClickListener {
		void OnSpecialTabButtonClicked(int index);
	}

	/**
	 * 解决 java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
	 */
	@Override
	protected void onAttachedToWindow() {
		try {
			super.onAttachedToWindow();
		} catch (Exception e) {
			Logger.err(e);//e.printStackTrace();
			if (manager != null) {
				manager.executePendingTransactions();
			}
		}
	}
}
