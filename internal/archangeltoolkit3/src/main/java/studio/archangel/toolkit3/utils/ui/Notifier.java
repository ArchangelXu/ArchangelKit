package studio.archangel.toolkit3.utils.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.utils.CommonUtil;
import studio.archangel.toolkit3.utils.UIUtil;

/**
 * Created by xmk on 16/7/7.
 */
public class Notifier {
	private static Handler handler = new Handler(Looper.getMainLooper());

	private static Toast toast = null;

	private static final Object sync_obj = new Object();

	public static void showNormalMsg(final Context context, final String msg) {
		showMessage(context, msg, Toast.LENGTH_LONG);
	}

	public static void showLongMsg(final Context context, final String msg) {
		showMessage(context, msg, Toast.LENGTH_LONG);
	}

	public static void showShortMsg(final Context context, final String msg) {
		showMessage(context, msg, Toast.LENGTH_SHORT);
	}

	public static void showNormalMsg(final String msg) {
		showMessage(null, msg, Toast.LENGTH_LONG);
	}

	public static void showLongMsg(final String msg) {
		showMessage(null, msg, Toast.LENGTH_LONG);
	}

	public static void showShortMsg(final String msg) {
		showMessage(null, msg, Toast.LENGTH_SHORT);
	}

	private static void showMessage(Context context, final String msg, final int len) {
		if (CommonUtil.isEmptyString(msg)) {
			return;
		}
//		final WeakReference<Context> ref = new WeakReference<>(context);
		new Thread(new Runnable() {
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						synchronized (sync_obj) {
							Context context = AngelApplication.getInstance().getApplicationContext();
//							Context context = ref.get();
							if (context != null) {
								if (toast != null) {
									TextView tv = (TextView) toast.getView().findViewById(R.id.view_toast_text);
									if (tv != null) {
										tv.setText(msg);
										toast.setDuration(len);
									}
								} else {
									toast = new Toast(context);
									View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_toast, null);
									toast.setView(view);
									TextView tv = (TextView) toast.getView().findViewById(R.id.view_toast_text);
									tv.setText(msg);
									toast.setDuration(len);
									int margin = UIUtil.getPX(context, 96);
									toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, margin);
								}
								toast.show();
							}

						}
					}
				});
			}
		}).start();
	}

}
