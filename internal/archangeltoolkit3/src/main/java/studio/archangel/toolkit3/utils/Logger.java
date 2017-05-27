/**
 *
 */
package studio.archangel.toolkit3.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 输出调试信息
 *
 * @author Michael
 */
public class Logger {
	/**
	 * 调试开关
	 */
	static boolean enabled = false;
	static String tag = "Logger";

	/**
	 * 在Logcat输出对象的值
	 *
	 * @param o 目标对象
	 */
	public static void out(Object o) {
		outSimple(o);
//		if (!enabled) {
//			return;
//		}
//		com.orhanobut.logger.Logger.i(o == null ? null : o.toString());
	}

	public static void outSimple(Object o) {
		if (!enabled) {
			return;
		}
		String[] gen = gen(o, 1);
		if (gen != null) {
			Log.i(tag, gen[0]);
			Log.i(tag, gen[1]);
		}
	}

	/**
	 * 在Logcat输出对象的值，链接定位向上n层
	 *
	 * @param o        目标对象
	 * @param up_level 向上层数
	 */
	public static void outUpper(Object o, int up_level) throws Exception {
		if (!enabled) {
			return;
		}
		String[] gen = gen(o, up_level);
		if (gen != null) {
			Log.i(tag, gen[0]);
			Log.i(tag, gen[1]);
		}
	}

	/**
	 * 在Logcat输出对象的值，链接定位向上一层
	 *
	 * @param o 目标对象
	 */
	public static void outUpper(Object o) throws Exception {
		outUpper(o, 1);
	}

	/**
	 * 在Logcat输出对象的值，使用ERROR Level
	 *
	 * @param o 目标对象
	 */
	public static void err(Object o) {
		if (!enabled) {
			return;
		}
		if (o instanceof Exception) {
			com.orhanobut.logger.Logger.e(((Exception) o), "", "obj");
		} else {
			com.orhanobut.logger.Logger.e(new Exception(o == null ? "" : o.toString()), "", "obj");
		}
//		String[] gen = gen(o);
//		if (gen != null) {
//			Log.e(tag, gen[0]);
//			Log.e(tag, gen[1]);
//		}
	}

	/**
	 * 在Logcat输出对象的值，使用ERROR Level
	 *
	 * @param o 目标对象
	 */
	public static void errSimple(Object o) {
		if (!enabled) {
			return;
		}
		String[] gen = gen(o, 1);
		if (gen != null) {
			Log.e(tag, gen[0]);
			Log.e(tag, gen[1]);
		}
	}

	/**
	 * 在Logcat输出JSON对象的值，使用ERROR Level
	 *
	 * @param o 目标对象
	 */
	public static void json(Object o) {
		if (!enabled) {
			return;
		}
		if (o == null) {
			outSimple("json is null");
		} else if (o instanceof JSONObject) {
			com.orhanobut.logger.Logger.json(o.toString());
		} else if (o instanceof JSONArray) {
			com.orhanobut.logger.Logger.json(o.toString());
		} else if (o instanceof String) {
			com.orhanobut.logger.Logger.json((String) o);
		}
	}

	/**
	 * 生成输出数据
	 *
	 * @param o 目标对象
	 * @return 长度为2的String数组。内容为{调用位置，对象值}
	 */
	static String[] gen(Object o) {
		return gen(o, 0);
	}

	/**
	 * 生成输出数据。
	 *
	 * @param o     目标对象
	 * @param level 调用栈的偏移值。必须是正数，例如：“1”即链接到调用位置的上一层调用位置
	 * @return 长度为2的String数组。内容为{调用位置，对象值}
	 */
	static String[] gen(Object o, int level) {
		String[] s = new String[2];
		try {
			StackTraceElement[] stackTraceElement = Thread.currentThread()
					.getStackTrace();
			int currentIndex = -1;
			for (int i = 0; i < stackTraceElement.length; i++) {
				String name = stackTraceElement[i].getMethodName();
				if (name.equalsIgnoreCase("out") || name.equalsIgnoreCase("err") || name.equalsIgnoreCase("outUpper") || name.equalsIgnoreCase("outSimple") || name.equalsIgnoreCase("errSimple")) {
					currentIndex = i + 1 + level;
					break;
				}
			}
			String fullClassName = stackTraceElement[currentIndex].getClassName();
			String className = stackTraceElement[currentIndex].getFileName();
			String lineNumber = String
					.valueOf(stackTraceElement[currentIndex].getLineNumber());
			s[0] = fullClassName + "『(" + className + ":" + lineNumber + ")』";
			if (o != null) {
				s[1] = o.toString();
			} else {
				s[1] = "null";
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 设置是否开启输出
	 *
	 * @param b 开启
	 */
	public static void setEnable(boolean b) {
		enabled = b;
	}

	/**
	 * 获得开启状态
	 *
	 * @return 是否开启输出
	 */
	public static boolean isEnabled() {
		return enabled;
	}
}