package net.hcangus.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Pair;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串通用类
 *
 * @author zc
 */
public class StringUtil {

	public static boolean isEmpty(String string) {
		return !(string != null && !string.trim().equals(""));
	}

	public static int toInt(String string) {
		if (!StringUtil.isEmpty(string) && !string.equals("null")) {
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static float toFloat(String string) {
		if (!StringUtil.isEmpty(string)) {
			return Float.parseFloat(string);
		}
		return 0;
	}

	public static double toDouble(String string) {
		if (!StringUtil.isEmpty(string)) {
			return Double.parseDouble(string);
		}
		return 0;
	}

	public static long toLong(String string) {
		if (!StringUtil.isEmpty(string)) {
			return Long.parseLong(string);
		}
		return 0;
	}

	public static boolean toBoolean(String string) {
		return !StringUtil.isEmpty(string) && Boolean.parseBoolean(string);
	}

	/**
	 * 转换文件大小
	 *
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString;
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static String turnTime(long time) {
		long diffTime = (new Date().getTime() - time) / 1000;
		if (diffTime == 0) {
			return "刚刚";
		} else if (diffTime > 0 && diffTime < 60) {
			return diffTime + "秒前";
		} else if (diffTime / 60 >= 0 && diffTime / 60 < 60) {
			return diffTime / 60 + "分钟前";
		} else if (diffTime / 3600 >= 0 && diffTime / 3600 < 24) {
			return diffTime / 3600 + "小时前";
		} else if (diffTime / (3600 * 24) >= 0 && diffTime / (3600 * 24) < 6) {
			return diffTime / (3600 * 24) + "天前";
		}

		return null;
	}

	/**
	 * 正则表达式判断手机号码是否合法
	 *
	 * @param mobiles 手机号
	 */
	public static boolean isMobileNum(String mobiles) {
		//    ^((13[0-9])|(14[7])|(15[^4,\\D])|(17[0])|(17[7])|(17[8])|(18[0-9]))\\d{8}$
		Pattern p = Pattern
				.compile("^((13\\d)|(14[579])|(15\\d)|(17[01235678])|(18\\d))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 校验银行卡卡号
	 *
	 * @param cardId 银行卡卡号
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 */
	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			throw new IllegalArgumentException("Bank card code must be number!");
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	public static String getSHA1(Context context, String str) {
		try {
			byte[] cert = str.getBytes();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] signatures = md.digest(cert);
			StringBuilder sha1 = new StringBuilder();
			for (byte key : signatures) {
				String appendString = Integer.toHexString(0xFF & key).toLowerCase(Locale.US);
				if (appendString.length() == 1)
					sha1.append("0");
				sha1.append(appendString);
			}
			return sha1.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (byte byte0 : md) {
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean match(String from, String key) {
		if (TextUtils.isEmpty(from) || TextUtils.isEmpty(key)) {
			return false;
		}
		boolean match = false;
		from = from.toLowerCase();
		key = key.toLowerCase();
		if (ChineseHelper.containsChinese(key)) {
			match = from.contains(key);
		} else {
			List<Pair<CharType, String>> pairList = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			for (int i = 0, len = from.length(); i < len; i++) {
				char c = from.charAt(i);
				CharType type = getCharType(c);
				sb.append(c);
				i++;
				while (i < len && getCharType(from.charAt(i)) == type) {
					sb.append(from.charAt(i));
					i++;
				}
				i--;
				pairList.add(new Pair<>(type, sb.toString()));
				sb.setLength(0);
			}
			for (Pair<CharType, String> pair : pairList) {
				if (pair.first == CharType.Chinese) {
					try {
						String jp = PinyinHelper.getShortPinyin(pair.second);
						if (!(match = jp.contains(key))) {//简拼不匹配
							//进行全拼匹配
							String qp = PinyinHelper.convertToPinyinString(pair.second, "-", PinyinFormat.WITHOUT_TONE);
							String whole = qp.replaceAll("-", "");
							String[] split = qp.split("-");
							if (whole.contains(key)) {
								for (String s : split) {
									match = s.startsWith(key) || key.startsWith(s);
									if (match) {
										break;
									}
								}
							}
						} else {
							match = true;
						}
					} catch (PinyinException e) {
						e.printStackTrace();
					}
				} else {
					match = pair.second.startsWith(key);
				}
				if (match) {
					break;
				}
			}
		}
		return match;
	}

	private static CharType getCharType(char c) {
		CharType type;
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'X')) {
			type = CharType.English;
		} else if (Character.isDigit(c)) {
			type = CharType.Number;
		} else if (ChineseHelper.isChinese(c)) {
			type = CharType.Chinese;
		} else {
			type = CharType.Other;
		}
		return type;
	}

	private enum CharType {
		None,
		Chinese,
		English,
		Number,
		Other
	}

	/**
	 * 高亮加粗显示引号中显示内容
	 */
	public static Spannable getQuotesSpan(CharSequence sequence) {
		SpannableString ss = new SpannableString(sequence);
		Matcher matcher = Pattern.compile("\"[^\"]+\"").matcher(sequence);
		while (matcher.find()) {
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#607099")),matcher.start(), matcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new StyleSpan(Typeface.BOLD),matcher.start(), matcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ss;
	}
}
