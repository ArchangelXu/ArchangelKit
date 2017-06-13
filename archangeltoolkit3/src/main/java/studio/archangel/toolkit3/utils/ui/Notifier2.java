/**
 *
 */
package studio.archangel.toolkit3.utils.ui;

//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.graphics.drawable.GradientDrawable;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.View;
//
//import com.github.johnpersano.supertoasts.SuperToast;
//import com.romainpiel.shimmer.Shimmer;
//import com.romainpiel.shimmer.ShimmerTextView;
//
//import java.lang.ref.WeakReference;
//
//import studio.archangel.toolkit3.R;
//import studio.archangel.toolkit3.utils.UIUtil;
//

/**
 * 处理弹出消息提示相关的事务
 *
 * @author Michael
 */
public class Notifier2 {
//
//	/**
//	 * 通知栏通知管理器
//	 */
//	static NotificationManager nm;
//	/**
//	 * 通知是聊天消息时的消息id
//	 */
//	public static final int noti_id_chat = 0;
//	static final int duration_short = 1000;
//	static final int duration_normal = 3000;
//	static final int duration_long = 7000;
//	/**
//	 * 默认背景
//	 */
//	static GradientDrawable default_shape;
//	/**
//	 * 当前正在显示的提示
//	 */
//	static SuperToast current;
//	/**
//	 *
//	 */
//	static Handler handler = new Handler(Looper.getMainLooper());
//	static final Object synObj = new Object();
//
//	/**
//	 * 弹出较长时间的消息提示。持续时间：7s
//	 *
//	 * @param c   上下文
//	 * @param msg 消息内容
//	 */
//	public static void showLongMsg(Context c, String msg) {
//		showMsg(c, msg, duration_long);
//
//	}
//
//	/**
//	 * 弹出消息提示。持续时间：3s
//	 *
//	 * @param c   上下文
//	 * @param msg 消息内容
//	 */
//	public static void showNormalMsg(Context c, String msg) {
//		showMsg(c, msg, duration_normal);
//
//	}
//
//	/**
//	 * 弹出较短时间的消息提示。持续时间：1s
//	 *
//	 * @param c   上下文
//	 * @param msg 消息内容
//	 */
//	public static void showShortMsg(Context c, String msg) {
//		showMsg(c, msg, duration_short);
//
//	}
//
//	/**
//	 * 弹出消息提示
//	 *
//	 * @param c        上下文
//	 * @param msg      消息内容
//	 * @param duration 持续时间，毫秒
//	 */
//	static void showMsg(final Context c, final String msg, final int duration) {
//		if (default_shape == null) {
//			default_shape = new GradientDrawable();
//			default_shape.setCornerRadius(UIUtil.getPX(25));
//			default_shape.setAlpha((int) (0.95 * 256));
//			default_shape.setColor(c.getResources().getColor(R.color.black));
//		}
//		handler.post(new Runnable() {
//
//			@Override
//			public void run() {
//				synchronized (synObj) {
//					if (current != null) {
//						SuperToast.cancelAllSuperToasts();
//					}
//					if (c != null) {
//						try {
//							current = cook(c, msg, duration);
//							current.show();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		});
//
//	}
//
//	/**
//	 * 生成一条消息提示
//	 *
//	 * @param c        上下文
//	 * @param msg      消息内容
//	 * @param duration 持续时间
//	 * @return 生成的消息提示。带闪光效果
//	 */
//	static SuperToast cook(Context c, String msg, int duration) {
//		SuperToast st = new SuperToast(c);
//		st.setAnimations(SuperToast.Animations.FADE);
//		st.setDuration(duration);
//		st.setText(msg);
//		st.setBackground(default_shape);
//		st.setTextColor(c.getResources().getColor(R.color.text_white));
//		st.setTextSize(SuperToast.TextSize.SMALL);
//		Shimmer shimmer = new Shimmer();
//		shimmer.setDuration(msg.length() <= 4 ? (int) (duration / 1.2) : duration / 2);
//		shimmer.setStartDelay(Math.min(duration / 10, 500));
//		ShimmerTextView tv = (ShimmerTextView) st.getTextView();
//		tv.setTextColor(c.getResources().getColor(R.color.text_grey));
//		shimmer.start(tv);
//		final WeakReference<Shimmer> ref = new WeakReference<>(shimmer);
//		st.setOnDismissListener(new SuperToast.OnDismissListener() {
//			@Override
//			public void onDismiss(View view) {
//				Shimmer shimmer = ref.get();
//				if (shimmer != null) {
//					shimmer.cancel();
//				}
//			}
//		});
//		return st;
//	}
//
//	/**
//	 * 在通知栏显示通知
//	 *
//	 * @param c  上下文
//	 * @param n  通知
//	 * @param id 前一个通知的id。
//	 * @see NotificationManager#notify
//	 */
//	public static void displayNotification(Context c, Notification n, int id) {
//		if (nm == null) {
//			nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
//		}
//		nm.notify(id, n);
//	}
}
