package studio.archangel.toolkit3.utils;

/**
 * Created by Michael on 2015/6/27.
 */

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.badoo.mobile.util.WeakHandler;

public abstract class AngelCountDownTimer {

	private static final int what = 1;
	private final long total_ms;
	private final long interval;
	private long next;
	private long future_stop_time;
	private boolean is_canceled = false;
	private WeakHandler handler;

	{

	}

	public AngelCountDownTimer(long total_ms, long tick_interval_ms) {
		this(null, total_ms, tick_interval_ms);
	}

	public AngelCountDownTimer(Looper looper, long total_ms, long tick_interval_ms) {
		this.total_ms = total_ms;
		interval = tick_interval_ms;
		Handler.Callback callback = new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				synchronized (AngelCountDownTimer.this) {
//				Logger.outSimple("AngelCountDownTimer handleMessage:is_canceled=" + is_canceled);
					long current = SystemClock.uptimeMillis();
					final long time_remain = future_stop_time - current;
					if (is_canceled) {
						return false;
					}
					if (time_remain <= 0) {
						onFinish();
					} else {
						onTick(time_remain);
						do {
							next += interval;
						} while (current > next);
						Message m = Message.obtain();
						m.what = what;
						if (next < future_stop_time) {
							handler.sendMessageAtTime(m, next);
						} else {
							handler.sendMessageAtTime(m, future_stop_time);
						}
					}
				}
				return false;
			}
		};
		if (looper != null) {
			handler = new WeakHandler(looper, callback);
		} else {
			handler = new WeakHandler(callback);
		}
	}

	public final void cancel() {
		is_canceled = true;
		handler.removeMessages(what);
	}

	public synchronized final AngelCountDownTimer start() {
		if (total_ms <= 0) {
			onFinish();
			return this;
		}
		is_canceled = false;
		next = SystemClock.uptimeMillis();
		future_stop_time = next + total_ms;
		next += interval;
		handler.sendMessageAtTime(new Message(), next);
		return this;
	}

	public boolean isCanceled() {
		return is_canceled;
	}

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();
}