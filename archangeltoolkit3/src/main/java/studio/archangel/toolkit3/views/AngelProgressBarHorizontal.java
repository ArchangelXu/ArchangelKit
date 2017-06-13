package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


public class AngelProgressBarHorizontal extends FrameLayout {
	static int default_height = 4;
	static int default_color = R.color.blue;

	View bar;
	int max = 100;
	int min = 0;
	int pending_progress = -1;
	int progress = 0;
	int delta = 1;
	//    int minHeight = 10;
	int height;
	int color;
	boolean is_animating = false;
	float fps = 30f;
	int duration = 350;

	public AngelProgressBarHorizontal(Context context) {
		this(context, null);
	}

	public AngelProgressBarHorizontal(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public AngelProgressBarHorizontal(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);

		bar = new View(getContext());
		LayoutParams params = new LayoutParams(1, 1);
		bar.setLayoutParams(params);
		bar.setBackgroundResource(R.drawable.layer_angel_material_progress_bar);
		addView(bar);
		setBackgroundResource(R.color.trans);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AngelProgressBarHorizontal);
		color = a.getColor(R.styleable.AngelProgressBarHorizontal_apbh_color, getResources().getColor(default_color));
		height = a.getDimensionPixelSize(R.styleable.AngelProgressBarHorizontal_apbh_height, isInEditMode() ? default_height : UIUtil.getPX(default_height));
		max = a.getInt(R.styleable.AngelProgressBarHorizontal_apbh_max, 100);
		min = a.getInt(R.styleable.AngelProgressBarHorizontal_apbh_min, 0);
		progress = a.getInt(R.styleable.AngelProgressBarHorizontal_apbh_value, 0);
		if (progress > max) {
			progress = max;
		} else if (progress < min) {
			progress = min;
		}
		a.recycle();
		setBackgroundColor(color);

		pending_progress = progress;
//        minHeight = Util.getPX(4);
		setMinimumHeight(height);

		post(new Runnable() {

			@Override
			public void run() {
				LayoutParams params = (LayoutParams) bar.getLayoutParams();
				params.height = getHeight();
				bar.setLayoutParams(params);
			}
		});
	}

	public static void setDefaultHeight(int default_height) {
		AngelProgressBarHorizontal.default_height = default_height;
	}

	public static void setDefaultColor(int default_color) {
		AngelProgressBarHorizontal.default_color = default_color;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!isInEditMode()) {
			Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			fps = display.getRefreshRate();
		}
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

	public int getMinHeight() {
		return height;
	}

	public void setMinHeight(int height) {
		this.height = height;
		setMinimumHeight(height);
	}

	public boolean isAnimating() {
		return is_animating;
	}

	void updateBarProgress(boolean increase) {
		LayoutParams params = (LayoutParams) bar.getLayoutParams();

		params.width = increase ? (params.width + delta) : (params.width - delta);
		params.height = getHeight();

		if (params.width > getWidth()) {
			params.width = getWidth();
		} else if (params.width < 0) {
			params.width = 0;
		}
		bar.setLayoutParams(params);
		progress = (max - min) * params.width / getWidth();
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
		if (getWidth() == 0) {
			pending_progress = progress;
		} else {
			pending_progress = progress;
			if (pending_progress > max)
				pending_progress = max;
			if (pending_progress < min)
				pending_progress = min;
			delta = (int) (Math.abs(pending_progress - this.progress) * 1.0 / max * getWidth() * fps / duration);
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
