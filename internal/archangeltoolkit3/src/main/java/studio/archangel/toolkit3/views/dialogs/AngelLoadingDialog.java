package studio.archangel.toolkit3.views.dialogs;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.views.AngelProgressBarHorizontal;
import studio.archangel.toolkit3.views.AngelProgressCircle;


public class AngelLoadingDialog extends Dialog {
	AngelProgressCircle circle;
	AngelProgressBarHorizontal bar;
	LinearLayout ll;
	View v_back;
	TextView tv_content;
	boolean is_indeterminate;
	int max = 0;
	boolean touch_outside_to_cancel = false;

	public AngelLoadingDialog(Context a, int res_color) {
		this(a, res_color, true);
	}

	public AngelLoadingDialog(Context a, int res_color, boolean e) {
		super(a, e ? R.style.AngelProgressDialogEternal : R.style.AngelProgressDialogEternal);
		init(res_color, e);
	}

	void init(int res_color, boolean e) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		is_indeterminate = e;
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0.25f;
		getWindow().setAttributes(lp);
		setTitle(null);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setOnCancelListener(null);
		setContentView(R.layout.view_dialog_loading);
		v_back = findViewById(R.id.view_dialog_back);
		v_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (touch_outside_to_cancel) {
					dismiss();
				}
			}
		});
		bar = (AngelProgressBarHorizontal) findViewById(R.id.dialog_progress_pb);
		ll = (LinearLayout) findViewById(R.id.dialog_progress_pb_layout);
		ll.setVisibility(e ? View.GONE : View.VISIBLE);
		circle = (AngelProgressCircle) findViewById(R.id.dialog_progress_pb_eternal);
		circle.setVisibility(e ? View.VISIBLE : View.GONE);
		tv_content = (TextView) findViewById(R.id.dialog_progress_content);
		bar.setBackgroundColor(getContext().getResources().getColor(res_color));
		circle.setBarColor(getContext().getResources().getColor(res_color));
		show();
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(false);
		touch_outside_to_cancel = cancel;
	}

	public void setMainColor(int color) {
		bar.setBackgroundColor(color);
		circle.setBarColor(color);
	}

	public TextView getContentTextView() {
		return tv_content;
	}

	public void setMax(int m) {
		if (is_indeterminate) {
			Logger.out("类型错误，无法设置最大值");
			return;
		}
		max = m;
		bar.setMax(m);
	}

	public int getMax() {
		return max;
	}

	public void setProgress(int p) {
		if (is_indeterminate) {
			Logger.out("类型错误，无法设置进度");
			return;
		}
		bar.setProgress(p);
	}

	public int getProgress() {
		return bar.getProgress();
	}

	public void forceDismiss() {
		if (AngelLoadingDialog.this != null && AngelLoadingDialog.this.isShowing()) {
			super.dismiss();
		}
	}

	@Override
	public void dismiss() {

		if (is_indeterminate) {//circle
			ObjectAnimator anim_width = ObjectAnimator.ofInt(circle, "BarWidthImmediately", circle.getBarWidth(), 0);
			ObjectAnimator anim_alpha = ObjectAnimator.ofFloat(circle, "Alpha", circle.getAlpha(), 0);
			AnimatorSet set = new AnimatorSet();
			set.setDuration(350);
			set.playTogether(anim_width, anim_alpha);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (AngelLoadingDialog.this != null && AngelLoadingDialog.this.isShowing()) {
						try {
							AngelLoadingDialog.super.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			set.start();
		} else {//progressbar
			int height = ll.getHeight();

			height = (int) ((height * 0.4f));
			AnimatorSet set = new AnimatorSet();
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(ll, "translationY", 0, -height);
			ObjectAnimator anim2 = ObjectAnimator.ofFloat(ll, "alpha", 1.0f, 0.0f);
			set.playTogether(anim1, anim2);
			set.setInterpolator(new AccelerateInterpolator());
			set.setDuration(400);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (AngelLoadingDialog.this != null && AngelLoadingDialog.this.isShowing()) {
						try {
							AngelLoadingDialog.super.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			set.start();
		}

	}

}
