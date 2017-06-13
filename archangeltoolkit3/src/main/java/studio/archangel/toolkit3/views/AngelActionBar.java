package studio.archangel.toolkit3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


/**
 * Created by xmk on 16/5/14.
 */
public class AngelActionBar extends FrameLayout {
	static int default_arrow_resource = R.drawable.ic_back;
	static int arrow_size = 24;
	static int image_size = 24;
	private boolean immersive;

	public enum DisplayMode {
		arrow, title, text, image, custom, none
	}

	public enum DisplayPosition {
		left, right, title
	}

	LinearLayout layout;
	View v_status_bar;
	FrameLayout container_left;
	FrameLayout container_right;
	FrameLayout container_title;

	public AngelActionBar(Context context) {
		super(context);
		init();
	}

	public AngelActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AngelActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public static void setDefaultArrowResource(int res) {
		default_arrow_resource = res;
	}

	public static void setDefaultArrowSize(int size_in_dp) {
		arrow_size = size_in_dp;
	}

	public static void setDefaultImageSize(int size_in_dp) {
		image_size = size_in_dp;
	}

	public static int getDefaultArrowResource() {
		return default_arrow_resource;
	}

	public boolean isImmersive() {
		return immersive;
	}

	public void setImmersive(boolean immersive) {
		this.immersive = immersive;
	}

	void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_actionbar, this);
		layout = (LinearLayout) findViewById(R.id.view_actionbar_layout);
		v_status_bar = findViewById(R.id.view_actionbar_status_bar);
		container_left = (FrameLayout) findViewById(R.id.view_actionbar_left_container);
		container_right = (FrameLayout) findViewById(R.id.view_actionbar_right_container);
		container_title = (FrameLayout) findViewById(R.id.view_actionbar_title_container);
		if (isInEditMode()) {
			AngelApplication.status_bar_height = UIUtil.getPX(getContext(), 16);
			UIUtil.setupImmersiveActionbar(this, null, "Title", R.color.google_blue);
		}
	}

	public void setTitle(String title) {
		try {
			((TextView) setDisplay(DisplayPosition.title, DisplayMode.title).findViewById(R.id.view_actionbar_title)).setText(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TextView getTitleTextView() {
		return (TextView) getContainer(AngelActionBar.DisplayPosition.title);
	}

	public View getContainer(DisplayPosition position) {
		switch (position) {
			case left: {
				return container_left.getChildAt(0);
			}
			case right: {
				return container_right.getChildAt(0);
			}
			case title: {
				return container_title.getChildAt(0);
			}
		}
		return null;
	}

	public View setDisplay(DisplayPosition position, DisplayMode mode) throws Exception {
		View result = null;
		int layout_id = -1;
		FrameLayout parent = null;
		switch (position) {
			case left: {
				parent = container_left;
				break;
			}
			case right: {
				parent = container_right;
				break;
			}
			case title: {
				parent = container_title;
				break;
			}
		}
		switch (mode) {
			case arrow: {
				layout_id = R.layout.view_actionbar_arrow;
				break;
			}
			case text: {
				layout_id = R.layout.view_actionbar_text;
				break;
			}
			case title: {
				layout_id = R.layout.view_actionbar_title;
				break;
			}
			case image: {
				layout_id = R.layout.view_actionbar_image;
				break;
			}
			case custom: {
				layout_id = R.layout.view_actionbar_custom;
				break;
			}
			case none: {
				layout_id = -3;//not used
				break;
			}
		}
		if (parent == null || layout_id == -1) {
			throw new Exception("error");
		}
		parent.removeAllViews();
		parent.setOnClickListener(null);
		if (layout_id > 0) {
			result = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout_id, null);
			parent.addView(result);
		}
		if (mode == DisplayMode.arrow) {
			ViewGroup.LayoutParams lp = result.findViewById(R.id.view_actionbar_left_arrow_icon).getLayoutParams();
			lp.width = lp.height = UIUtil.getPX(arrow_size);
		} else if (mode == DisplayMode.image) {
			ViewGroup.LayoutParams lp = result.findViewById(R.id.view_actionbar_image).getLayoutParams();
			lp.width = lp.height = UIUtil.getPX(image_size);
		}
		return result;
	}
}
