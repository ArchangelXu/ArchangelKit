package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.UIUtil;


/**
 * Created by Administrator on 2015/10/26.
 */


public class AngelRatingBar extends LinearLayout {
	static int default_star_empty = R.drawable.icon_star_empty;
	static int default_star_fill = R.drawable.icon_star_fill;
	static int default_star_half = R.drawable.icon_star_half;
	private boolean enabled;
	private int max;
	private double count;
	private OnRatingChangeListener listener;
	private float image_size, spacing, padding;
	private int res_empty;
	private int res_fill;
	private int res_half;


	public AngelRatingBar(Context context) {
		this(context, null);
	}

	public AngelRatingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelRatingBar(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
		setOrientation(LinearLayout.HORIZONTAL);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AngelRatingBar);
		image_size = mTypedArray.getDimension(R.styleable.AngelRatingBar_arab_star_size, UIUtil.getPX(context, 24));
		spacing = mTypedArray.getDimension(R.styleable.AngelRatingBar_arab_star_spacing, 0);
		padding = mTypedArray.getDimension(R.styleable.AngelRatingBar_arab_star_padding, image_size / 4);
		max = mTypedArray.getInteger(R.styleable.AngelRatingBar_arab_star_max, 5);
		count = mTypedArray.getInteger(R.styleable.AngelRatingBar_arab_star_count, 0);
		res_empty = mTypedArray.getResourceId(R.styleable.AngelRatingBar_arab_star_empty, default_star_empty);
		res_fill = mTypedArray.getResourceId(R.styleable.AngelRatingBar_arab_star_fill, default_star_fill);
		res_half = mTypedArray.getResourceId(R.styleable.AngelRatingBar_arab_star_half, default_star_half);
		enabled = mTypedArray.getBoolean(R.styleable.AngelRatingBar_arab_enabled, true);
		for (int i = 0; i < max; ++i) {
			ImageView imageView = new ImageView(context);
			LayoutParams para = new LayoutParams(
					Math.round(image_size + 2 * padding),
					Math.round(image_size + 2 * padding)
			);
			if (i != 0) {
				para.leftMargin = (int) spacing;
			}
			imageView.setLayoutParams(para);
			imageView.setPadding((int) (padding), (int) (padding), (int) (padding), (int) (padding));
			imageView.setImageResource(res_empty);
			if (i < count) {
				imageView.setImageResource(res_fill);
			}
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (enabled) {
						setStarCount(indexOfChild(v) + 1);
						if (listener != null) {
							listener.onRatingChange(indexOfChild(v) + 1);
						}
					}
				}
			});
			addView(imageView);
		}
		mTypedArray.recycle();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (enabled) {
			return super.dispatchTouchEvent(ev);
		} else {
			return false;
		}
	}

	public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
		this.listener = onRatingChangeListener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setStarCount(int c) {
		count = c;
		if (count > max) {
			count = max;
		}
		if (count < 0) {
			count = 0;
		}

		for (int i = 0; i < max; ++i) {
			((ImageView) getChildAt(i)).setImageResource(i < count ? res_fill : res_empty);
		}

//        for (int i = this.max - 1; i >= count; --i) {
//            ((ImageView) getChildAt(i)).setImageResource(res_empty);
//        }
//        for (int i = 0; i < count; ++i) {
//            ((ImageView) getChildAt(i)).setImageResource(res_fill);
//        }
//
//        for (int i = this.max - 1; i >= count; --i) {
//            ((ImageView) getChildAt(i)).setImageResource(res_empty);
//        }
	}

	public void setStarCount(double c) {
		count = c;
		if (count > max) {
			count = max;
		}
		if (count < 0) {
			count = 0;
		}

		for (int i = 0; i < max; ++i) {
			ImageView iv = (ImageView) getChildAt(i);
			if (count < i + 0.5) {
				iv.setImageResource(res_empty);
			} else if (count >= i + 0.5 && count < i + 1) {
				iv.setImageResource(res_half);
			} else if (count >= i + 1) {
				iv.setImageResource(res_fill);
			}


		}
	}

	public double getStarCount() {
		return count;
	}

	public interface OnRatingChangeListener {

		void onRatingChange(int RatingCount);

	}

}
