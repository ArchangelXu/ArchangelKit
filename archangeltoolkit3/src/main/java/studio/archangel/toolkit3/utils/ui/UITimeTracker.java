package studio.archangel.toolkit3.utils.ui;

import android.os.SystemClock;

import java.util.ArrayList;

import studio.archangel.toolkit3.utils.Logger;

/**
 * Created by xmk on 16/6/3.
 */
public class UITimeTracker {
	static UITimeTracker instance;
	long start_time = 0;
	long last_time = 0;
	ArrayList<Record> records;

	public static synchronized UITimeTracker getInstance() {
		if (instance == null) {
			instance = new UITimeTracker();
		}
		return instance;
	}

	private UITimeTracker() {
		records = new ArrayList<>();
	}

	public static synchronized void reset() {
		getInstance().start_time = 0;
		getInstance().last_time = 0;
		getInstance().records.clear();
	}

	public static synchronized void start() {
		getInstance().start_time = SystemClock.uptimeMillis();
		getInstance().last_time = getInstance().start_time;
	}

	public static synchronized void count(String msg) {
		long time = SystemClock.uptimeMillis();
		getInstance().records.add(new Record(time - getInstance().last_time, msg));
		getInstance().last_time = time;
	}

	public static synchronized void print() {
		for (int i = 0; i < getInstance().records.size(); i++) {
			Record r1 = getInstance().records.get(i);
			if (i == 0) {
				Logger.out("start->" + r1.msg + ":" + r1.time);
			} else {
				Record r0 = getInstance().records.get(i - 1);
				Logger.out(r0.msg + "->" + r1.msg + ":" + r1.time);
			}
		}
	}
}

class Record {
	long time;
	String msg;

	Record(long t, String m) {
		time = t;
		msg = m;
	}
}
