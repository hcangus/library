package net.hcangus.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Angelo
 *         系统崩溃信息
 *         加载：CrashHandler.getInstance().init(context);//Crash 监听开启
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";
	private final int SIZE = 20;//保存的日志文件的数量
	/**
	 * // 系统默认的UncaughtException处理类
	 **/
	private UncaughtExceptionHandler mDefaultHandler;
	/**
	 * // CrashHandler实例
	 **/
	private static CrashHandler INSTANCE = new CrashHandler();

	private Context mContext;// 程序的Context对象

	private Map<String, String> info = new HashMap<>();// 用来存储设备信息和异常信息

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());// 用于格式化日期,作为日志文件名的一部分


	/**
	 * 保证只有一个CrashHandler实例
	 */
	private CrashHandler() {

	}

	/**
	 * 获取CrashHandler实例 ,单例模式
	 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 */
	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
		//清理日志文件
		checkClearCrashInfo();
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

//            ActivityHistory.removeAll();// 退出程序
		}
		android.os.Process.killProcess(android.os.Process.myPid());//退出进程
		System.exit(0);//退出虚拟器
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex 异常信息
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null) return false;
		//显示一个Toast
		new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 *
	 * @param context
	 */
	private void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();// 反射机制
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append("=").append(value).append("\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		long time = System.currentTimeMillis();
		String data = format.format(new Date(time));
		String fileName = "error-" + data + ".log";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			FileOutputStream fos = null;
			try {
				File errorLogDir = new File(DeviceUtil.getLocalPath(mContext, DeviceUtil.LocalPathType.logs));

				fos = new FileOutputStream(new File(errorLogDir, fileName));
				fos.write(sb.toString().getBytes());
				return fileName;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	//日志数大于SIZE时，删除超过2天的日志
	private void checkClearCrashInfo() {
		final String logPath = DeviceUtil.getLocalPath(mContext,DeviceUtil.LocalPathType.logs);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				//"error-" + data + ".log"
				try {
					final File[] files = DeviceUtil.getDirChildFile(new File(logPath));
					if (files != null && files.length > SIZE) {
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, -2);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						long today = calendar.getTimeInMillis();
						for (File file : files) {
							String fileName = file.getName();
							String dateString = fileName.substring(fileName.indexOf("-") + 1, fileName.lastIndexOf("."));
							Date date;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
							try {
								date = sdf.parse(dateString);
							} catch (Exception e) {
								e.printStackTrace();
								date = new Date();
							}
							long time = date.getTime();
							if (time < today) {
								boolean delete = files[0].delete();
								Logger.w("----Delete log >> " + fileName + ",--" + delete);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}
}
