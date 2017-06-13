package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


public class AngelProgressBarVertical extends FrameLayout {
	static int default_width = 4;
	static int default_color = R.color.blue;

	View bar;
	int max = 100;
	int min = 0;
	int pending_progress = -1;
	int progress = 0;
	int delta = 1;
	//    int minHeight = 10;
	int width;
	int color;
	boolean is_animating = false;
	float fps;
	int duration = 350;

	public AngelProgressBarVertical(Context context) {
		this(context, null);
	}

	public AngelProgressBarVertical(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public AngelProgressBarVertical(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);

		bar = new View(getContext());
		LayoutParams params = new LayoutParams(1, 1);
		params.gravity = Gravity.BOTTOM;
		bar.setLayoutParams(params);
		bar.setBackgroundResource(R.drawable.layer_angel_material_progress_bar);
		addView(bar);
		setBackgroundResource(R.color.trans);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AngelProgressBarVertical);
		color = a.getColor(R.styleable.AngelProgressBarVertical_apbv_color, getResources().getColor(default_color));
		width = a.getDimensionPixelSize(R.styleable.AngelProgressBarVertical_apbv_width, UIUtil.getPX(context, default_width));
		max = a.getInt(R.styleable.AngelProgressBarVertical_apbv_max, 100);
		min = a.getInt(R.styleable.AngelProgressBarVertical_apbv_min, 0);
		progress = a.getInt(R.styleable.AngelProgressBarVertical_apbv_value, 0);
		if (progress > max) {
			progress = max;
		} else if (progress < min) {
			progress = min;
		}
		a.recycle();
		setBackgroundColor(color);

		pending_progress = progress;
//        minHeight = Util.getPX(4);
		setMinimumHeight(width);

		post(new Runnable() {

			@Override
			public void run() {
				LayoutParams params = (LayoutParams) bar.getLayoutParams();
				params.width = getWidth();
				bar.setLayoutParams(params);
			}
		});
	}

	public static void setDefaultHeight(int default_height) {
		AngelProgressBarVertical.default_width = default_height;
	}

	public static void setDefaultColor(int default_color) {
		AngelProgressBarVertical.default_color = default_color;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		fps = display.getRefreshRate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (pending_progress != progress) {
			if (pending_progress > progress) {
				updateBarProgress(true);
			} else if (pending_progress < progress) {
				updateBarProgress(false);
			}
			invalidate();
		}
		is_animating = pending_progress != progress;
	}

	public int getMinWidth() {
		return width;
	}

	public void setMinWidth(int width) {
		this.width = width;
		setMinimumHeight(width);
	}

	public boolean isAnimating() {
		return is_animating;
	}

	void updateBarProgress(boolean increase) {
		LayoutParams params = (LayoutParams) bar.getLayoutParams();

		params.height = increase ? (params.height + delta) : (params.height - delta);
		params.width = getWidth();

		if (params.height > getHeight()) {
			params.height = getHeight();
		} else if (params.height < 0) {
			params.height = 0;
		}
		bar.setLayoutParams(params);
		progress = (max - min) * params.height / getHeight();
		if ((increase && progress > pending_progress) || (!increase && progress < pending_progress)) {
			progress = pending_progress;
		}
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public void setProgress(int progress) {
		if (getHeight() == 0) {
			pending_progress = progress;
		} else {
			pending_progress = progress;
			if (pending_progress > max)
				pending_progress = max;
			if (pending_progress < min)
				pending_progress = min;
			delta = (int) (Math.abs(pending_progress - this.progress) * 1.0 / max * getHeight() * fps / duration);
			invalidate();
		}
	}

	public int getProgress() {
		return progress;
	}

	// Set color of background
	public void setBackgroundColor(int color) {
		this.color = color;
		LayerDrawable layer = (LayerDrawable) bar.getBackground();
		GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.layer_angel_material_progress_bar_back);

		shape.setColor(color);
	}

}
