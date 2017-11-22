package net.hcangus.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

	private static String[] WEEKS = {"日","一", "二", "三", "四", "五", "六"};
	private static String DefaultFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 从 yyyy-MM-dd HH:mm:ss 中获取到 HH:mm:ss 的时间
	 *
	 * @param formatedString {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 * @return {@link String String as "HH:mm:ss"}
	 */
	public static String get_HMS(String formatedString) {
		return formatedString.substring(11);
	}

	/**
	 * 从 yyyy-MM-dd HH:mm:ss 中获取到 yyyy-MM-dd 的日期
	 *
	 * @param formatedString {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 * @return {@link String String as "yyyy-MM-dd"}
	 */
	public static String get_YMD(String formatedString) {
		return formatedString.substring(0, 11);
	}

	/**
	 * 从 yyyy-MM-dd HH:mm:ss 中获取到 MM-dd HH:mm 的信息
	 *
	 * @param formatedString {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 * @return {@link String String as "MM-dd HH:mm" }
	 */
	public static String get_MDHM(String formatedString) {
		return formatedString.substring(5, 16);
	}
	/**
	 * 从 yyyy-MM-dd HH:mm:ss 中获取到 MM月dd日 HH:mm 的信息
	 *
	 * @param formatedString {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 * @return {@link String String as "MM月dd日 HH:mm" }
	 */
	public static String get_MDHM_CN(String formatedString) {
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
		return format.format(getDate(formatedString));
	}

	public static Date getDate(String format, String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
		try {
			return sdf.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
			return new Date();
		}
	}

	/**
	 * 把 yyyy-MM-dd HH:mm:ss 格式的字符串转化为Date
	 *
	 * @param str {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 * @return {@link Date}
	 */
	public static Date getDate(String str) {
		return getDate("yyyy-MM-dd HH:mm:ss", str);
	}

	public static String getString(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
		return sdf.format(date);
	}

	public static String getString(Calendar calendar) {
		return getString(calendar.getTime());
	}

	public static String getString(String format, long mills) {
		Date date = new Date(mills);
		return getString(format, date);
	}

	/**
	 * 获取 yyyy-MM-dd HH:mm:ss 格式的字符串
	 *
	 * @param date {@link Date}
	 * @return {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 */
	public static String getString(Date date) {
		return getString(DefaultFormat, date);
	}

	/**
	 * 获取 yyyy-MM-dd HH:mm:ss 格式的字符串
	 *
	 * @param mills {@link Date} in mills
	 * @return {@link String String as "yyyy-MM-dd HH:mm:ss"}
	 */
	public static String getString(long mills) {
		return getString(DefaultFormat, mills);
	}

	/**
	 * @return 今天在一周中的第几天，以周一的索引为0且为第一天
	 */
	public static int getTodayIndex() {
		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_WEEK) - 2;
		return i < 0 ? 6 : i;
	}

	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar getCalendarFromString(String str) {
		return getCalendar(getDate(str));
	}

	public static Calendar getCalendarFromString(String format, String str) {
		return getCalendar(getDate(format, str));
	}

	/**
	 * 获取 {@value DefaultFormat} 日期的星期
	 * @param formatedString {@value DefaultFormat}
	 * @return {@link String}
	 */
	public static String getWeekStr(String formatedString, boolean isWeekXQ) {
		int i = getWeekIndex(formatedString) - 1;
		return String.format(isWeekXQ ? "星期%s" : "周%s", WEEKS[i]);
	}

	public static String getWeekStr(String fomat, String str, boolean isWeekXQ) {
		int i = getWeekIndex(fomat, str) - 1;
		return String.format(isWeekXQ ? "星期%s" : "周%s", WEEKS[i]);
	}

	public static String getWeekStr(Date date, boolean isWeekXQ) {
		int i = getWeekIndex(date) - 1;
		return String.format(isWeekXQ ? "星期%s" : "周%s", WEEKS[i]);
	}

	/**
	 * 获取指定时间在一周内的第几天
	 *
	 * @param str {@value DefaultFormat}
	 * @return {@link Integer}:周日：1，周一：2 ... 周六：7
	 */
	public static int getWeekIndex(String str) {
		Date date = getDate(str);
		return getWeekIndex(date);
	}

	public static int getWeekIndex(String format, String str) {
		Date date = getDate(format, str);
		return getWeekIndex(date);
	}

	/**
	 * 获取指定时间在一周内的第几天
	 * 周日：1，周一：2 ... 周六：7
	 *
	 * @param date {@link Date}
	 * @return {@link Integer}:周日：1，周一：2 ... 周六：7
	 */
	public static int getWeekIndex(Date date) {

		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);

		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	//获得年龄
	@SuppressLint("WrongConstant")
	public static int getAge(long time){
		Date birthDay = new Date(time);
		//获取当前系统时间
		Calendar cal = Calendar.getInstance();
		//如果出生日期大于当前时间，则抛出异常
		if (cal.before(birthDay)) {
			return 0;
		}
		//取出系统当前时间的年、月、日部分
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		//将日期设置为出生日期
		cal.setTime(birthDay);
		//取出出生日期的年、月、日部分
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		//当前年份与出生年份相减，初步计算年龄
		int age = yearNow - yearBirth;
		//当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
		if (monthNow <= monthBirth) {
			//如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) age--;
			}else{
				age--;
			}
		}
		return age;
	}
}
