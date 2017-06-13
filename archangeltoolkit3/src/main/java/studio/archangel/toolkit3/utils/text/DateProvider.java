/**
 *
 */
package studio.archangel.toolkit3.utils.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;

/**
 * 日期提供者
 *
 * @author Michael
 */
public class DateProvider {
	public static String format_date_chinese = "yyyy年MM月dd日";
	public static String format_date_noyear_chinese = "MM月dd日";
	public static String format_date = "yyyy-MM-dd";
	public static String format_datetime = "yyyy-MM-dd HH:mm:ss";
	public static String format_datetime_nosecond = "yyyy-MM-dd HH:mm";
	public static String format_clock = "HH:mm:ss:SSS";
	public static String format_time = "HH:mm:ss";
	public static String format_time_nosecond = "HH:mm";
	public static String format_raw = "yyyyMMddHHmmss";
	public static String format_timezone = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static String format_timezone_nosecond = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final long one_minute = 60 * 1000;
	private static final long one_hour = 60 * one_minute;
	private static final long one_day = 24 * one_hour;
	private static final long one_week = 7 * one_day;
	private static final long one_month = 4 * one_week;
	private static final long one_year = 365 * one_month;

	/**
	 * 获取用户友好的日期，如“1分钟前”
	 *
	 * @param offset 时间（毫秒）
	 * @return 日期
	 */
//	public static String getReadableDate(long t) {
//		try {
//			long cur = System.currentTimeMillis();
//			long offset = cur - t;
//			if (offset < 60 * 1000 * 2) {// 1分钟内
////                return (int) (offset / 1000) + "秒前";
//				return AngelApplication.getInstance().getString(R.string.date_just_now);
//			} else if (offset < 60 * 60 * 1000) {// 1小时内
//				return (int) (offset / 60 / 1000) + AngelApplication.getInstance().getString(R.string.date_minutes_ago);
//			} else if (offset < 24 * 60 * 60 * 1000) {// 1天内
//				return (int) (offset / 60 / 60 / 1000) + AngelApplication.getInstance().getString(R.string.date_hours_ago);
//			} else if (offset < 7 * 24 * 60 * 60 * 1000) {// 7天内
//				return (int) (offset / 24 / 60 / 60 / 1000) + AngelApplication.getInstance().getString(R.string.date_days_ago);
//			} else {
//				return getDate(format_date, t);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
	public static String getReadableDate(long offset) {
		offset *= 1000;
		try {
			if (offset < one_minute) {// 1分钟内
				return AngelApplication.getInstance().getString(R.string.date_just_now);
			} else if (offset < one_hour) {// 1小时内
				int i = (int) (offset / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_minutes_ago_plural, i, i);
			} else if (offset < one_day) {// 1天内
				int i = (int) (offset / 60 / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_hours_ago_plural, i, i);
			} else if (offset < one_week) {// 1周内
				int i = (int) (offset / 24 / 60 / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_days_ago_plural, i, i);
			} else if (offset < one_month) {// 1个月内
				int i = (int) (offset / 7 / 24 / 60 / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_weeks_ago_plural, i, i);
			} else if (offset < one_year) {// 1年内
				int i = (int) (offset / 4 / 7 / 24 / 60 / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_months_ago_plural, i, i);
			} else {// 更早
				int i = (int) (offset / 365 / 4 / 7 / 24 / 60 / 60 / 1000);
				return AngelApplication.getInstance().getResources().getQuantityString(R.plurals.date_years_ago_plural, i, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getReadableDateShort(long offset) {
		offset *= 1000;
		try {
			if (offset < one_minute) {// 1分钟内
				return AngelApplication.getInstance().getString(R.string.date_just_now);
			} else if (offset < one_hour) {// 1小时内
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_minutes_ago), (int) (offset / 60 / 1000));
			} else if (offset < one_day) {// 1天内
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_hours_ago), (int) (offset / 60 / 60 / 1000));
			} else if (offset < one_week) {// 1周内
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_days_ago), (int) (offset / 24 / 60 / 60 / 1000));
			} else if (offset < one_month) {// 1个月内
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_weeks_ago), (int) (offset / 7 / 24 / 60 / 60 / 1000));
			} else if (offset < one_year) {// 1年内
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_months_ago), (int) (offset / 4 / 7 / 24 / 60 / 60 / 1000));
			} else {// 更早
				return String.format(Locale.CHINA, AngelApplication.getInstance().getString(R.string.date_years_ago), (int) (offset / 365 / 4 / 7 / 24 / 60 / 60 / 1000));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取用户友好的日期，用于IM
	 *
	 * @param t 时间（毫秒）
	 * @return 日期
	 */
	public static String getIMReadableDate(long t) {
		try {
			long cur = System.currentTimeMillis();
			long offset = cur - t;
			if (offset < 24 * 60 * 60 * 1000) {// 1天内
				return getDate(format_time_nosecond, t);
			} else {
				return getDate(format_datetime_nosecond, t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取今年
	 *
	 * @return 今年
	 */
	public static int getThisYear() {
		int this_year = Calendar.getInstance().get(Calendar.YEAR);
		return this_year;
	}

	/**
	 * 获取本月
	 *
	 * @return 本月
	 */
	public static int getThisMonth() {
		int this_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		return this_month;
	}

	/**
	 * 获取今天（在本月中第几天）
	 *
	 * @return 今天
	 */
	public static int getToday() {
		int this_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		return this_day;
	}

	/**
	 * 获取某年某月有多少天
	 *
	 * @param year
	 * @param month 1~12
	 * @return
	 */
	public static int getMaxDay(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, 1);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param format 日期格式
	 * @param l      long型日期
	 * @return 日期字符串
	 */
	public static String getDate(String format, long l) {
		return getDate(format, l, false);
	}

	/**
	 * @param format      日期格式
	 * @param l           long型日期
	 * @param is_gmt_time 日期是否是GMT时间
	 * @return 日期字符串
	 */
	public static String getDate(String format, long l, boolean is_gmt_time) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		if (is_gmt_time) {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		Date date = new Date(l);
		return sdf.format(date);
//        SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
//        Date date = new Date(l);
//        return df.format(date);
	}

	/**
	 * @param format 日期格式
	 * @param s      {@link String }型日期
	 * @return 日期字符串
	 */
	public static long getDate(String format, String s) {
		return getDate(format, s, false);
	}

	/**
	 * @param format      日期格式
	 * @param s           {@link String }型日期
	 * @param is_gmt_time 日期是否是GMT时间
	 * @return 日期字符串
	 */
	public static long getDate(String format, String s, boolean is_gmt_time) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (is_gmt_time) {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		try {
			c.setTime(sdf.parse(s));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
//        SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
//        Date date = null;
//        try {
//            date = df.parse(s);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date.getTime();
	}

	/**
	 * 获得计时器格式时间
	 *
	 * @param l    时间，毫秒
	 * @param tick 是否显示分和秒之间的“：”
	 * @return 计时器格式时间
	 */
	public static String getClockLikeTime(long l, boolean tick) {
		int hour = (int) (l / (60 * 60 * 1000));
		int min = (int) ((l - hour * 60 * 60 * 1000) / (60 * 1000));
		int sec = (int) ((l - hour * 60 * 60 * 1000 - min * 60 * 1000) / (1000));
		int sec2 = (int) ((l - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000) / (100));
		String min_s = (min < 10 ? "0" : "") + min;
		String sec_s = (sec < 10 ? "0" : "") + sec;
		return hour + ":" + min_s + (tick ? ":" : " ") + sec_s + "." + sec2;
	}

	/**
	 * 获得计时器格式时间
	 *
	 * @param l 时间，毫秒
	 * @return 计时器格式时间
	 */
	public static String getClockLikeTime(long l) {
		return getClockLikeTime(l, true);
	}

	/**
	 * 获得计时器格式时间
	 *
	 * @param s 时间
	 * @return 计时器格式时间
	 */
	public static long getClockLikeTime(String s) {
		s = s.replace("\\.", ":");
		String[] data = s.split(":", -1);
		int hour = Integer.parseInt(data[0]);

		int min = Integer.parseInt(data[1]);
		int sec = Integer.parseInt(data[2]);
//        int sec2 = Integer.parseInt(data[3]);
		return hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
	}

	//	public static void onIntentError(AngelActivityV4 act) {
//		Notifier.showNormalMsg(act, getString(R.string.common_wrong_parameter));
//		act.finish();
//	}

}
