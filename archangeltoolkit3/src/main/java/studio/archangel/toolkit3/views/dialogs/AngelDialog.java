package studio.archangel.toolkit3.views.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;

import java.lang.ref.WeakReference;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.Logger;


public class AngelDialog extends Dialog {

	String message;
	TextView tv_msg;
	String title;
	TextView tv_title;
	FrameLayout v_container;
	View v_button_container;
	View v_body;
	View v_back;
	TextView b_ok;
	TextView b_cancel;
	TextView b_neutral;
	//    View v_custom;
	View.OnClickListener listener_ok;
	View.OnClickListener listener_cancel;
	View.OnClickListener listener_neutral;
	OnPostShowListener listener;
	protected int main_color;
	boolean touch_outside_to_cancel = true;
	DialogStyle button_style = DialogStyle.OK_CANCEL;
	WeakReference<Context> caller_context;

	public AngelDialog(Context context, String title, String message) {
		super(context, R.style.AnimDialog);
		this.message = message;
		this.title = title;
	}

	public AngelDialog(Context context, String title, String message, int color) {
		this(context, title, message);
		main_color = context.getResources().getColor(color);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_dialog);
//		setContentView(AngelApplication.getInstance().getDialogLayoutRes());

		this.tv_title = (TextView) findViewById(R.id.view_dialog_title);
		setTitle(title);
		this.tv_msg = (TextView) findViewById(R.id.view_dialog_message);
		setMessage(message);
		v_container = (FrameLayout) findViewById(R.id.view_dialog_custom);
		v_body = findViewById(R.id.view_dialog_body);
		v_back = findViewById(R.id.view_dialog_back);
		v_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (touch_outside_to_cancel) {
					dismiss();
				}
			}
		});
		v_button_container = findViewById(R.id.view_dialog_button_container);
//        if (v_custom != null) {
//            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//            v_container.addView(v_custom, p);
//            v_container.setVisibility(View.VISIBLE);
//            tv_msg.setVisibility(View.GONE);
//        }

		this.b_ok = findViewById(R.id.button_accept);
		b_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener_ok != null)
					listener_ok.onClick(v);
			}
		});
		this.b_cancel = findViewById(R.id.button_cancel);

		b_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener_cancel != null)
					listener_cancel.onClick(v);
			}
		});
		b_neutral = findViewById(R.id.button_neutral);
		b_neutral.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener_neutral != null)
					listener_neutral.onClick(v);
			}
		});
		if (main_color != 0) {
			b_ok.setTextColor(main_color);
		}
		super.setCanceledOnTouchOutside(false);
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(false);
		touch_outside_to_cancel = cancel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		if (message == null || message.isEmpty())
			tv_msg.setVisibility(View.GONE);
		else {
			tv_msg.setVisibility(View.VISIBLE);
			tv_msg.setText(message);
		}
		tv_msg.setText(message);
	}

	public void setButtonStyle(DialogStyle button_style) {
		this.button_style = button_style;
	}

	public void setOnPostShowListener(OnPostShowListener listener) {
		this.listener = listener;
	}

	public String getTitle() {
		return title;
	}

	public View getDialogBody() {
		return v_body;
	}

	public void setCustomView(View c) {
//        v_custom = c;
		if (c != null) {
			FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			v_container.removeAllViews();
			v_container.addView(c, p);
			v_container.setVisibility(View.VISIBLE);
			tv_msg.setVisibility(View.GONE);
		}
	}

	public void setTitle(String title) {
		this.title = title;
		if (title == null || title.isEmpty())
			tv_title.setVisibility(View.GONE);
		else {
			tv_title.setVisibility(View.VISIBLE);
			tv_title.setText(title);
		}
	}

	@Override
	public void show() {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.show();
//		afterShow();
	}

	@Override
	protected void onStart() {
		super.onStart();
		afterShow();
	}

	private void afterShow() {
		if (listener != null) {
			listener.onPostShow(this);
		}
		setCancelable(false);
		switch (button_style) {
			case OK: {
				v_button_container.setVisibility(View.VISIBLE);
				b_ok.setVisibility(View.VISIBLE);
				b_cancel.setVisibility(View.GONE);
				b_neutral.setVisibility(View.GONE);
				break;
			}
			case CANCEL: {
				v_button_container.setVisibility(View.VISIBLE);
				b_ok.setVisibility(View.GONE);
				b_cancel.setVisibility(View.VISIBLE);
				b_neutral.setVisibility(View.GONE);
				setCancelable(true);
				break;
			}
			case OK_CANCEL: {
				v_button_container.setVisibility(View.VISIBLE);
				b_ok.setVisibility(View.VISIBLE);
				b_cancel.setVisibility(View.VISIBLE);
				b_neutral.setVisibility(View.GONE);
				setCancelable(true);
				break;
			}
			case OK_NEUTRAL_CANCEL: {
				v_button_container.setVisibility(View.VISIBLE);
				b_ok.setVisibility(View.VISIBLE);
				b_cancel.setVisibility(View.VISIBLE);
				b_neutral.setVisibility(View.VISIBLE);
				setCancelable(true);
				break;
			}
			case NONE: {
				v_button_container.setVisibility(View.GONE);
				setCancelable(true);
				break;
			}
			default:
				break;
		}

	}

	public TextView getOkButton() {
		return b_ok;
	}

	public TextView getCancelButton() {
		return b_cancel;
	}

	public TextView getNeutralButton() {
		return b_neutral;
	}

	public void setOnOkClickedListener(View.OnClickListener onAcceptButtonClickListener) {
		this.listener_ok = onAcceptButtonClickListener;
	}

	public void setOnCancelClickedListener(View.OnClickListener onCancelButtonClickListener) {
		this.listener_cancel = onCancelButtonClickListener;
	}

	public void setOnNeutralClickedListener(View.OnClickListener onNeutralButtonClickListener) {
		this.listener_neutral = onNeutralButtonClickListener;
	}

	/**
	 * Copy the visibility of the Activity that has started the dialog. If the
	 * activity is in Immersive mode the dialog will be in Immersive mode too and vice versa.
	 */
	@SuppressLint("NewApi")
	private void copySystemUiVisibility() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && caller_context != null && caller_context.get() instanceof Activity) {
			try {
				getWindow().getDecorView().setSystemUiVisibility(((Activity) caller_context.get()).getWindow().getDecorView().getSystemUiVisibility());
			} catch (Exception e) {
				Logger.err(e);//e.printStackTrace();
			}
		}
	}

	public enum DialogStyle {
		OK, CANCEL, OK_CANCEL, OK_NEUTRAL_CANCEL, NONE
	}

	public interface OnPostShowListener {
		void onPostShow(AngelDialog d);
	}
}
