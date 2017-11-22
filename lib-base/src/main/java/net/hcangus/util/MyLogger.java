package net.hcangus.util;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Anydoor
 * @see Logger#i(String, Object...)
 * @see Logger#d(Object)
 * @see Logger#d(String, Object...)
 * @see Logger#v(String, Object...)
 * @see Logger#w(String, Object...)
 * @see Logger#wtf(String, Object...)
 * @see Logger#e(String, Object...)
 * @see Logger#e(Throwable, String, Object...)
 * @see Logger#xml(String)
 * @see Logger#json(String)
 * Created by Administrator.
 */
public class MyLogger {

	public static void init(String tag, final boolean debug) {
		FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				.showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
				.methodCount(5)         // (Optional) How many method line to show. Default 2
				.tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
				.build();
		Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
			@Override public boolean isLoggable(int priority, String tag) {
				return debug;
			}
		});
	}
}
