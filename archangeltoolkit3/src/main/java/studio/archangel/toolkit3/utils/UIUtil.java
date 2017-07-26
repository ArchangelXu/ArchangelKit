package studio.archangel.toolkit3.utils;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.activities.AngelActivity;
import studio.archangel.toolkit3.utils.ui.ArgbEvaluator;
import studio.archangel.toolkit3.views.AngelActionBar;

/**
 * Created by xmk on 16/5/15.
 */
public class UIUtil {
	public static ValueAnimator getColorAnimator(int... values) {
		ValueAnimator anim = new ValueAnimator();
		anim.setIntValues(values);
		anim.setEvaluator(ArgbEvaluator.getInstance());
		return anim;
	}

	public static AnimatorSet getColorAnimator(int color0, int color1, long duration, final OnColorChangedListener listener) {
		ValueAnimator anim = getColorAnimator(color0, color1);
		anim.setDuration(duration);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				if (listener != null) {
					listener.onColorChanged((Integer) valueAnimator.getAnimatedValue());
				}
			}
		});
		AnimatorSet set = new AnimatorSet();
		set.playTogether(anim);
		return set;
	}

	public static void addImmersivePaddingTopForViews(View... views) {
		for (View v : views) {
			v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + AngelApplication.status_bar_height, v.getPaddingRight(), v.getPaddingBottom());
		}
	}

	public static void setupImmersiveActivity(final AngelActivity act, String title, String back) {
		setupImmersiveActivity(act, title, back, 0);
	}

	public static void setupImmersiveActivity(final AngelActivity act, String title, String back, int aab_back_res) {
		if (act == null) {
			return;
		}
		AngelActionBar aab = (AngelActionBar) act.findViewById(R.id.atk_actionbar);
		if (aab == null) {
			return;
		}
		act.setAngelActionBar(aab);
		View placeholder = act.findViewById(R.id.atk_status_bar_placeholder);
		if (placeholder != null) {
			placeholder.getLayoutParams().height = AngelApplication.status_bar_height;
		}
		if (aab_back_res != 0) {
			aab.setBackgroundResource(aab_back_res);
		} else {
			aab.setBackgroundResource(AngelApplication.getInstance().getActionbarBackgroundResource());
		}
		try {
			View container = aab.setDisplay(AngelActionBar.DisplayPosition.title, AngelActionBar.DisplayMode.title);
			TextView tv = (TextView) container.findViewById(R.id.view_actionbar_title);
			tv.setText(title);
			tv.setTextColor(AngelApplication.getInstance().getActionbarTextColor());
			container = aab.setDisplay(AngelActionBar.DisplayPosition.left, AngelActionBar.DisplayMode.arrow);
			tv = (TextView) container.findViewById(R.id.view_actionbar_left_arrow_text);
			ImageView iv = (ImageView) container.findViewById(R.id.view_actionbar_left_arrow_icon);
			iv.setImageResource(AngelActionBar.getDefaultArrowResource());
			if (back != null && !back.isEmpty()) {
				tv.setText(back);
			} else {
				tv.setText("");
				LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) tv.getLayoutParams();
				para.leftMargin = 0;
			}
			tv.setTextColor(AngelApplication.getInstance().getActionbarTextColor());

			com.rey.material.widget.LinearLayout layout = (com.rey.material.widget.LinearLayout) container.findViewById(R.id.view_actionbar_left_arrow_back);
			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					act.onBackPressed();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		addImmersivePaddingTopForViews(aab);
	}

	public static void setupImmersiveActionbar(AngelActionBar aab, View placeholder, String title) {
		setupImmersiveActionbar(aab, placeholder, title, 0);
	}

	public static void setupImmersiveActionbar(AngelActionBar aab, View placeholder, String title, int aab_back_res) {
		if (aab == null) {
			return;
		}
		if (placeholder != null) {
			placeholder.getLayoutParams().height = AngelApplication.status_bar_height;
		}
		if (aab_back_res != 0) {
			aab.setBackgroundResource(aab_back_res);
		} else {
			aab.setBackgroundResource(AngelApplication.getInstance().getActionbarBackgroundResource());
		}
		try {
			View container = aab.setDisplay(AngelActionBar.DisplayPosition.title, AngelActionBar.DisplayMode.title);
			TextView tv = (TextView) container.findViewById(R.id.view_actionbar_title);
			tv.setText(title);
			tv.setTextColor(AngelApplication.getInstance().getActionbarTextColor());
			aab.setDisplay(AngelActionBar.DisplayPosition.left, AngelActionBar.DisplayMode.none);
		} catch (Exception e) {
			e.printStackTrace();
		}
		aab.setPadding(0, AngelApplication.status_bar_height, 0, 0);
	}

	public static int getColorWithAlpha(int color, int alpha) {
		return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
	}

	public interface OnColorChangedListener {
		void onColorChanged(int color);
	}

	public static String getColorNote(int color, boolean with_alpha) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		StringBuilder sb = new StringBuilder("#");
		String s;
		int[] array = new int[]{a, r, g, b};
		for (int i = 0; i < array.length; i++) {
			int value = array[i];
			if (i == 0 && !with_alpha) {
				continue;
			}
			s = Integer.toHexString(value);
			if (s.length() == 1) {
				sb.append("0").append(s);
			} else {
				sb.append(s);
			}
		}

		return sb.toString();
	}

	/**
	 * 将sp转换为px
	 *
	 * @param spValue sp值
	 * @return 相应的px
	 */
	public static int getPXfromSP(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);

	}

	/**
	 * 将sp转换为px
	 *
	 * @param spValue sp值
	 * @return 相应的px
	 */
	public static int getPXfromSP(float spValue) {
		return getPXfromSP(AngelApplication.getInstance(), spValue);
	}

	/**
	 * 将dp转换为px
	 *
	 * @param dipValue dp值
	 * @return 相应的px
	 */
	public static int getPX(float dipValue) {
		return getPX(AngelApplication.getInstance(), dipValue);
	}

	/**
	 * 将dp转换为px
	 *
	 * @param dipValue dp值
	 * @return 相应的px
	 */
	public static int getPX(Context c, float dipValue) {
		final float scale = c.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px转换为dip
	 *
	 * @param pxValue px值
	 * @return 相应的dp
	 */
	public static int getDP(float pxValue) {
		final float scale = AngelApplication.getInstance().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 为指定的EditText设置输入限制，并把提示信息显示到指定的TextView
	 *
	 * @param et  要设置输入限制的EditText
	 * @param tv  用来显示提示信息的TextView
	 * @param max 最大输入长度
	 */
	public static void setInputLimit(final EditText et, final TextView tv, final int max) {
		int l = et.getText().toString().length();
		if (l <= max) {
			tv.setText("还可以输入" + (max - l) + "字");
			tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
		} else {
			tv.setText("已超出" + (l - max) + "字");
			tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
		}
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int l = s.length();
				if (l <= max) {
					tv.setText("还可以输入" + (max - l) + "字");
					tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
				} else {
					tv.setText("已超出" + (l - max) + "字");
					tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
				}
			}
		});
	}

	private static PowerManager.WakeLock wakeLock = null;

	/**
	 * 获得屏幕锁
	 *
	 * @param c 上下文
	 * @return 屏幕锁
	 */
	static PowerManager.WakeLock getWakeLock(Context c) {
		if (wakeLock != null) {
			return wakeLock;
		}
		try {
			PowerManager powerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
			int field = 0x00000020;

			// Yeah, this is hidden field.
			field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);

			wakeLock = powerManager.newWakeLock(field, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wakeLock;
	}

	/**
	 * 激活近物传感器。当被遮挡时关闭屏幕
	 *
	 * @param c 上下文
	 */
	public static void enableProximitySensor(Context c) {
		PowerManager.WakeLock wakeLock = getWakeLock(c);
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
		}
	}

	/**
	 * 关闭近物传感器
	 *
	 * @param c 上下文
	 */
	public static void disableProximitySensor(Context c) {
		PowerManager.WakeLock wakeLock = getWakeLock(c);
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	public static void removeOnGlobalLayoutListener(View target, ViewTreeObserver.OnGlobalLayoutListener listener) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
				target.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
			} else {
				target.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setBackgroundResourceWithOriginPadding(View v, int res) {
		setBackgroundDrawableWithOriginPadding(v, v.getResources().getDrawable(res));
	}

	public static void setBackgroundDrawableWithOriginPadding(View v, Drawable drawable) {
		int paddingLeft = v.getPaddingLeft();
		int paddingTop = v.getPaddingTop();
		int paddingRight = v.getPaddingRight();
		int paddingBottom = v.getPaddingBottom();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(drawable);
		} else {
			v.setBackgroundDrawable(drawable);
		}
		v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	public static void rewindAnimationDrawableToFirstFrame(AnimationDrawable d) {
		int current = -1;

		Drawable currentFrame;
		currentFrame = d.getCurrent();

		int total = d.getNumberOfFrames();
		for (int i = 0; i < total; i++) {
			if (d.getFrame(i) == currentFrame) {
				current = i;
				break;
			}
		}
		if (current != -1 && current != 0) {
			for (int i = 0; i < total - current; i++) {
				d.run();
			}
		}
	}

	public static boolean isWithinViewsBound(float x, float y, View v) {
		if (v == null) {
			return false;
		}
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		return x > location[0] && x < location[0] + v.getWidth()
				&& y > location[1] && y < location[1] + v.getHeight();
	}

	public static void lazyLoad(final AngelActivity act, final long delay, final Runnable r) {

		act.getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				act.handler.postDelayed(r, delay);
			}
		});
	}

	public interface OnImeOptionTriggerListener {
		void OnImeOptionTrigger(Editable text);
	}

	public static void setOnImeOptionTriggerListenerFor(final EditText et, final OnImeOptionTriggerListener listener) {
		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (listener != null) {
					listener.OnImeOptionTrigger(et.getText());
				}
				return true;
			}
		});
	}

	/**
	 * 隐藏软键盘
	 *
	 * @param v 正在输入内容（调用软键盘）的控件
	 * @param c 上下文
	 */
	public static void hideInputBoard(View v, Context c) {
		InputMethodManager imm = (InputMethodManager) c.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * 显示软键盘
	 *
	 * @param target 需要输入内容的控件
	 * @param a      正在显示软键盘的界面
	 */
	public static void showInputBoard(View target, Activity a) {
		InputMethodManager keyboard = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.showSoftInput(target, InputMethodManager.SHOW_FORCED);
	}


	public static void setupActionBar(AngelActivity act, String title) {
		setupActionBar(act, title, "");
	}


	public static void setupActionBar(final AngelActivity act, String title, String back) {
		android.support.v7.app.ActionBar bar = act.getSupportActionBar();
		if (bar == null) {
			return;
		}

		bar.setIcon(AngelApplication.getInstance().getResources().getDrawable(R.color.trans));
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setTitle("");
		AngelActionBar aab = act.getAngelActionBar();
		if (aab == null) {
			aab = new AngelActionBar(act);
			act.setAngelActionBar(aab);
		}
		bar.setCustomView(aab);
		Toolbar toolbar = (Toolbar) bar.getCustomView().getParent();
		toolbar.setContentInsetsAbsolute(0, 0);
//		boolean immersive = (act.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
//		aab.setImmersive(immersive);
//		if (immersive) {
//			View decor = act.getWindow().getDecorView();
//			final View rootView = decor.findViewById(android.R.id.content);
//			if (!act.getWindow().hasFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)) {
//				rootView.setPadding(0, getPX(48) + AngelApplication.status_bar_height, 0, 0);
//			}
//		}
		aab.setBackgroundResource(AngelApplication.getInstance().getActionbarBackgroundResource());
		try {
			View container = aab.setDisplay(AngelActionBar.DisplayPosition.title, AngelActionBar.DisplayMode.title);
			TextView tv = (TextView) container.findViewById(R.id.view_actionbar_title);
			tv.setText(title);
			tv.setTextColor(AngelApplication.getInstance().getActionbarTextColor());
			container = aab.setDisplay(AngelActionBar.DisplayPosition.left, AngelActionBar.DisplayMode.arrow);
			tv = (TextView) container.findViewById(R.id.view_actionbar_left_arrow_text);
			ImageView iv = (ImageView) container.findViewById(R.id.view_actionbar_left_arrow_icon);
			iv.setImageResource(AngelActionBar.getDefaultArrowResource());
			if (back != null && !back.isEmpty()) {
				tv.setText(back);
			} else {
				tv.setText("");
				LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) tv.getLayoutParams();
				para.leftMargin = 0;
			}
			tv.setTextColor(AngelApplication.getInstance().getActionbarTextColor());

			com.rey.material.widget.LinearLayout layout = (com.rey.material.widget.LinearLayout) container.findViewById(R.id.view_actionbar_left_arrow_back);
			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					act.onBackPressed();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		bar.setBackgroundDrawable(AngelApplication.getInstance().getResources().getDrawable(R.color.trans));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			bar.setElevation(0);
		}
//		if (status_bar_color != -1) {
//			StatusBarUtil.setTranslucentForImageView(act, 0, toolbar);
//		}
	}

	public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
		final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
		DrawableCompat.setTintList(wrappedDrawable, colors);
		return wrappedDrawable;
	}

	public static Drawable tintDrawable(Drawable drawable, int color_normal, int color_active) {
		return tintDrawable(drawable, generateColorState(color_normal, color_active));
	}

	public static Drawable tintDrawable(Drawable drawable, int color) {
		return tintDrawable(drawable, generateColorState(color));
	}

	public static ColorStateList generateColorState(int color_normal, int color_active) {
		int[][] states = new int[][]{
				new int[]{},
				new int[]{android.R.attr.state_checked,
						android.R.attr.state_pressed,
						android.R.attr.state_focused,
						android.R.attr.state_selected},
		};
		int[] colors = new int[]{
				color_normal,
				color_active,
		};
		return new ColorStateList(states, colors);
	}

	public static ColorStateList generateColorState(int color) {
		return generateColorState(color, color);
	}

	public static boolean hasNavigationBar(Context context) {
		Point size = getAppUsableScreenSize(context);
		return size.x == 0 && size.y == 0;
	}

	public static Point getNavigationBarSize(Context context) {
		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);

		// navigation bar on the right
		if (appUsableSize.x < realScreenSize.x) {
			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
		}

		// navigation bar at the bottom
		if (appUsableSize.y < realScreenSize.y) {
			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
		}

		// navigation bar is not present
		return new Point();
	}

	public static Point getAppUsableScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static Point getRealScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else if (Build.VERSION.SDK_INT >= 14) {
			try {
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Exception e) {
			}
		}

		return size;
	}

	public static int getTextViewRealHeight(TextView tv, boolean including_padding) {
		Layout layout = tv.getLayout();
		if (layout == null) {
			return 0;
		}
		int height = layout.getLineTop(tv.getLineCount());
		if (including_padding) {
			height += tv.getCompoundPaddingTop() + tv.getCompoundPaddingBottom();
		}
		return height;
	}
}
