package studio.archangel.toolkit3.views.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

import studio.archangel.toolkit3.utils.Logger;


public class AngelHintDialog extends android.app.Dialog {

	int layout_id;
	int background_view_id;
	View v_back;
	boolean touch_outside_to_cancel = true;
	OnHintDialogDismissListener listener;
	WeakReference<Context> caller_context;

	AngelHintDialog(Context context, int layout_id, int background_view_id, int style_id) {
		this(context, style_id);
		this.layout_id = layout_id;
		this.background_view_id = background_view_id;
		caller_context = new WeakReference<>(context);
	}

	AngelHintDialog(Context context, int layout_id, int style_id) {
		this(context, style_id);
		this.layout_id = layout_id;
		caller_context = new WeakReference<>(context);
	}

	AngelHintDialog(Context context, int style_id) {
		super(context, style_id);
//		Locale locale = context.getResources().getConfiguration().locale;
//		if (locale != null) {
//			Logger.out("locale=" + locale.getCountry() + "," + locale.getLanguage());
//		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(layout_id);
		if (background_view_id != 0) {
			v_back = findViewById(background_view_id);
			v_back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (touch_outside_to_cancel) {
						dismiss();
					}
				}
			});
		}
		super.setCanceledOnTouchOutside(false);
	}

	@Override
	public void dismiss() {
		dismiss(-1);
	}

	public void dismiss(int clicked_view_id) {
		super.dismiss();
		if (listener != null) {
			listener.onDismiss(clicked_view_id);
		}
	}

	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		Logger.err(new Exception("use setOnHintDialogDismissListener() instead."));
	}

	public void setOnHintDialogDismissListener(OnHintDialogDismissListener listener) {
		this.listener = listener;
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(false);
		touch_outside_to_cancel = cancel;
	}

	@Override
	public void show() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.show();
	}

	public void showImmersive() {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		super.show();

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
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

	public interface OnHintDialogDismissListener {
		void onDismiss(int clicked_view_id);
	}
}
