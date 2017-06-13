/**
 *
 */
package studio.archangel.toolkit3.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import studio.archangel.toolkit3.activities.AngelActivity;


/**
 * @author Michael
 */
public class AngelFragment extends Fragment {
	protected AngelActivity owner;
	protected View cache = null;
	//    Validator validator;
	protected String realname;
	/**
	 * this fragment's hashCode
	 */
	protected int hash = -1;
	/**
	 * this fragment's index in owner's view pager
	 */
	protected int index = -1;
	/**
	 * owner(Activity or Fragment)'s hashCode
	 */
	protected int page_id = -1;

	public AngelFragment() {
		super();
		this.realname = getClass().getName();
	}

	public AngelFragment(String realname) {
		super();
		this.realname = realname;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		owner = (AngelActivity) activity;
	}

	public AngelActivity getSelf() {
		return owner;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hash = hashCode();
		Bundle extra = getArguments();
		if (extra != null) {
			page_id = extra.getInt("page_id", page_id);
			index = extra.getInt("index", index);
		}
	}

	/**
	 * 初始化方法，可以使用缓存，使得此Fragment在ViewPager中不会因为划出屏幕而重新加载
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @param layout
	 * @return
	 */
	public boolean onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layout) {
		boolean use_cache = (cache != null);
		if (!use_cache) {
			cache = inflater.inflate(layout, null);
		}

		ViewGroup parent = (ViewGroup) cache.getParent();
		if (parent != null) {
			parent.removeView(cache);
		}
		return use_cache;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

//    protected void setValidator(Validator.ValidationListener l) {
//        validator = new Validator(this);
//        validator.setValidationListener(l);
//    }
//
//    protected void validate() {
//        if (validator != null) {
//            validator.validate();
//        }
//    }

}
