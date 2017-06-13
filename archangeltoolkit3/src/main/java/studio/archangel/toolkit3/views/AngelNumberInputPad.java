package studio.archangel.toolkit3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import studio.archangel.toolkit3.R;

/**
 * Created by Michael on 2015/7/1.
 */
public class AngelNumberInputPad extends FrameLayout {
	View pad_1, pad_2, pad_3, pad_4, pad_5, pad_6, pad_7, pad_8, pad_9, pad_0, pad_backspace, bar;
	OnPadClickListener listener;
	boolean hidden = false;
	boolean clicked = false;

	public AngelNumberInputPad(Context context) {
		this(context, null);
	}

	public AngelNumberInputPad(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AngelNumberInputPad(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
		inflate(context, R.layout.view_angel_number_input_pad, this);
		pad_1 = findViewById(R.id.view_angel_number_input_pad_1);
		pad_2 = findViewById(R.id.view_angel_number_input_pad_2);
		pad_3 = findViewById(R.id.view_angel_number_input_pad_3);
		pad_4 = findViewById(R.id.view_angel_number_input_pad_4);
		pad_5 = findViewById(R.id.view_angel_number_input_pad_5);
		pad_6 = findViewById(R.id.view_angel_number_input_pad_6);
		pad_7 = findViewById(R.id.view_angel_number_input_pad_7);
		pad_8 = findViewById(R.id.view_angel_number_input_pad_8);
		pad_9 = findViewById(R.id.view_angel_number_input_pad_9);
		pad_0 = findViewById(R.id.view_angel_number_input_pad_0);
		pad_backspace = findViewById(R.id.view_angel_number_input_pad_backspace);
		bar = findViewById(R.id.view_angel_number_input_pad_top);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AngelNumberInputPad);
		int number_color = a.getColor(R.styleable.AngelNumberInputPad_anip_number_color, getResources().getColor(R.color.text_black));
		int grey_color = a.getColor(R.styleable.AngelNumberInputPad_anip_grey_color, getResources().getColor(R.color.grey));
		View[] views = new View[]{
				pad_1,
				pad_2,
				pad_3,
				pad_4,
				pad_5,
				pad_6,
				pad_7,
				pad_8,
				pad_9,
				pad_0
		};
		for (View view : views) {
			((TextView) ((RelativeLayout) view).getChildAt(0)).setTextColor(number_color);
		}
		findViewById(R.id.view_angel_number_input_pad_space).setBackgroundColor(grey_color);
		((ImageView) ((RelativeLayout) findViewById(R.id.view_angel_number_input_pad_backspace)).getChildAt(0)).getDrawable().mutate().setColorFilter(grey_color, PorterDuff.Mode.SRC_IN);

		a.recycle();
		pad_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("1");
				}
				clicked = false;
			}
		});
		pad_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("2");
				}
				clicked = false;
			}
		});
		pad_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("3");
				}
				clicked = false;
			}
		});
		pad_4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("4");
				}
				clicked = false;
			}
		});
		pad_5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("5");
				}
				clicked = false;
			}
		});
		pad_6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("6");
				}
				clicked = false;
			}
		});
		pad_7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("7");
				}
				clicked = false;
			}
		});
		pad_8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("8");
				}
				clicked = false;
			}
		});
		pad_9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("9");
				}
				clicked = false;
			}
		});
		pad_0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onPadClicked("0");
				}
				clicked = false;
			}
		});
		pad_backspace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clicked = true;
				if (listener != null) {
					listener.onBackspaceClicked();
				}
				clicked = false;
			}
		});
		bar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onTopBarClicked();
				}
			}
		});
	}

	public boolean isHidden() {
		return hidden;
	}

	public void hide() {
		if (hidden) {
			return;
		}
		animate().translationY(getHeight()).setStartDelay(0).setDuration(200).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				hidden = true;
			}
		}).start();
	}

	public void show() {
		if (!hidden) {
			return;
		}
		animate().translationY(0).setDuration(200).setStartDelay(0).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				hidden = false;
			}
		}).start();
	}

	public OnPadClickListener getOnPadClickListener() {
		return listener;
	}

	public void setOnPadClickListener(OnPadClickListener listener) {
		this.listener = listener;
	}

	public interface OnPadClickListener {
		void onPadClicked(String number);

		void onBackspaceClicked();

		void onTopBarClicked();

	}
}
