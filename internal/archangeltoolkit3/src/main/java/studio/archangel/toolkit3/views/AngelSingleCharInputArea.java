package studio.archangel.toolkit3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import studio.archangel.toolkit3.R;


/**
 * Created by Michael on 2015/7/1.
 */
public abstract class AngelSingleCharInputArea extends LinearLayout {
	static final int default_count = 6;
	static final int default_divider_width = 1;
	static final int default_color_divider = R.color.grey_light;
	int count = 6;
	Drawable divider_drawable;
	int divider_width;
	ArrayList<View> char_views;
	StringBuilder builder;
	OnCharAmountChangeListener listener;
	int char_layout_id;

	public AngelSingleCharInputArea(Context context) {
		this(context, null);
	}

	public AngelSingleCharInputArea(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelSingleCharInputArea(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AngelSingleCharInputArea);
		divider_drawable = a.getDrawable(R.styleable.AngelSingleCharInputArea_ascia_divider_drawable);
		divider_width = a.getDimensionPixelSize(R.styleable.AngelSingleCharInputArea_ascia_divider_width, default_divider_width);
		count = a.getInteger(R.styleable.AngelSingleCharInputArea_ascia_count, default_count);
		char_layout_id = a.getResourceId(R.styleable.AngelSingleCharInputArea_ascia_char_layout, -1);
		char_views = new ArrayList<>();
		a.recycle();
		initCharViews();
		builder = new StringBuilder(count);

	}

	public OnCharAmountChangeListener getOnDotAmountChangedListener() {
		return listener;
	}

	public void setOnCharAmountChangeListener(OnCharAmountChangeListener listener) {
		this.listener = listener;
	}

	void initCharViews() {
		removeAllViews();
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				ImageView divider = new ImageView(getContext());
				addView(divider);
				if (divider_drawable != null) {
					divider.setBackground(divider_drawable);
				} else {
					divider.setBackgroundResource(default_color_divider);
					LayoutParams p = (LayoutParams) divider.getLayoutParams();
					p.width = divider_width;
					p.height = ViewGroup.LayoutParams.MATCH_PARENT;
				}
			}
			RelativeLayout layout = new RelativeLayout(getContext());
//			ConstrainedImageView iv = new ConstrainedImageView(getContext());
//			iv.setAspectRatio(1, 1);
//			iv.setImageResource(R.drawable.shape_oval_black);
//			if (isInEditMode()) {
//				iv.setPadding(grid_size / 3, grid_size / 3, grid_size / 3, grid_size / 3);
//			} else {
//				iv.setPadding(UIUtil.getPX(getContext(), grid_size / 3), UIUtil.getPX(getContext(), grid_size / 3), UIUtil.getPX(getContext(), grid_size / 3), UIUtil.getPX(getContext(), grid_size / 3));
//			}
////            iv.setAlpha(0);
//			iv.setScaleX(0);
//			iv.setScaleY(0);
//            iv.setVisibility(View.INVISIBLE);
			View view = getSingleCharView(layout, char_layout_id);
			char_views.add(view);
			addView(layout);
			LinearLayout.LayoutParams params = (LayoutParams) layout.getLayoutParams();
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = ViewGroup.LayoutParams.MATCH_PARENT;
			params.weight = 1;
//			LayoutParams p = (LayoutParams) view.getLayoutParams();
//			if (isInEditMode()) {
//				p.width = grid_size;
//				p.height = grid_size;
//			} else {
//				p.width = UIUtil.getPX(getContext(), grid_size);
//				p.height = UIUtil.getPX(getContext(), grid_size);
//			}
		}
	}

	/**
	 * You should generate your own single char input view, add it to parent and set layout parameters.
	 *
	 * @param parent AngelSingleCharInputArea which you should add your views to
	 * @return
	 */
	protected abstract View getSingleCharView(RelativeLayout parent, int layout_id);

	public String getText() {
		return builder.toString();
	}

	public void addChar(String character) {
		if (builder.length() >= count) {
			return;
		}
		builder = builder.append(character);
		animateAddChar(character);
		if (listener != null) {
			listener.onCharAmountChanged(count, builder.length());
		}
//        setText(builder.toString());
	}

	public boolean deleteLastChar() {
		if (builder.length() < 1) {
			return false;
		}
		animateRemoveLastChar(String.valueOf(builder.charAt(builder.length() - 1)));
		builder = builder.delete(builder.length() - 1, builder.length());
		if (listener != null) {
			listener.onCharAmountChanged(count, builder.length());
		}
		return true;
//        setText(builder.toString());
	}

	public void removeAllChars() {
		animateRemoveAllChar(builder.toString());
		builder = builder.delete(0, builder.length());
		if (listener != null) {
			listener.onCharAmountChanged(count, builder.length());
		}
//        setText(builder.toString());
	}

	public void setText(String t) {
		if (builder.length() >= count) {
			return;
		}
		if (builder.length() + t.length() >= count) {
			t = t.substring(0, count - builder.length() - 1);
		}
		builder = builder.append(t);
		animateAddAllChar(builder.toString());
		if (listener != null) {
			listener.onCharAmountChanged(count, builder.length());
		}
	}
//    void setText(String t) {
//        int length = t.length();
//        setDotAmount(length);
//        if (listener != null) {
//            listener.onDotAmountChanged(count, length);
//        }
//    }

	void animateAddChar(String character) {
		animateCharView(char_views.get(builder.length() - 1), character, true);
	}

	void animateAddAllChar(String text) {
		for (int i = 0; i < count; i++) {
			animateCharView(char_views.get(i), text.substring(i, i + 1), true);
		}
	}

	void animateRemoveLastChar(String character) {
		animateCharView(char_views.get(builder.length() - 1), character, false);

	}

	void animateRemoveAllChar(String text) {
		for (int i = 0; i < count; i++) {
			animateCharView(char_views.get(i), text.substring(i, i + 1), false);
		}
	}

//	void animateCharView(View dot, float scale, Interpolator interpolator) {
//		dot.animate().scaleX(scale).scaleY(scale).setDuration(200).setInterpolator(interpolator).start();
//	}

	protected abstract void animateCharView(View char_view, String character, boolean show);
//    public void setDotAmount(int amount) {
//        for (int i = 0; i < count; i++) {
//            View dot = char_views.GET(i);
//            boolean show = i < amount;
//            float scale = show ? 1 : 0;
//            Interpolator interpolator = null;
//            if (show) {
//                interpolator = new OvershootInterpolator();
//            } else {
//                interpolator = new AnticipateInterpolator();
//            }
//
//            ViewPropertyAnimator animator = dot.animate();
//
//            animator.scaleX(scale).scaleY(scale).setDuration(200).setInterpolator(interpolator).start();
//
////            if (show) {
////                if (dot.getScaleX() != 1) {
////                    dot.animate().scaleX(1).scaleY(1).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
////                }
////            } else {
////                if (dot.getScaleX() != 0) {
////                    dot.animate().scaleX(0).scaleY(0).setDuration(200).setInterpolator(new AnticipateOvershootInterpolator()).start();
////                }
////
////            }
////            dot.setVisibility(i < amount ? View.VISIBLE : View.INVISIBLE);
//        }
//    }

	public interface OnCharAmountChangeListener {
		void onCharAmountChanged(int max, int amount);
	}
}
