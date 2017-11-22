package net.hcangus.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class DeviceUtil {
	private final static String IMSI = "IMSI";
	private final static String IMEI = "IMEI";
	private static int SCREEN_WIDTH = 0;
	private static int SCREEN_HEIGHT = 0;
	private static float SCREEN_DENSITY = 0f;


	private static long lastClickTime;

	/**
	 * 点击事件连点判断
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isWiFiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (connectivity != null && connectivity.getActiveNetworkInfo() != null) {
			if (connectivity.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/**
	 * sdcard是否可用
	 */
	@SuppressWarnings("WeakerAccess")
	public static boolean isSdcardEnable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断设备是否连接了网络
	 */
	public static boolean isNetworkAvailable(Context context) {
		boolean netSataus = false;
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			cwjManager.getActiveNetworkInfo();

			if (cwjManager.getActiveNetworkInfo() != null) {
				netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return netSataus;
	}

	/**
	 * 获取sdcard总大小
	 */
	@SuppressWarnings("deprecation")
	public static long getSDCardTotal() {
		if (DeviceUtil.isSdcardEnable()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			return sf.getBlockSize() * sf.getBlockCount();
		}
		return 0;
	}

	/**
	 * 获取sdcard剩余大小
	 */
	@SuppressWarnings("deprecation")
	public static double getSDCardFree() {
		if (DeviceUtil.isSdcardEnable()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			return sf.getBlockSize() * 1.0 * sf.getAvailableBlocks();
		}
		return 0;
	}

	/**
	 * 获取文件夹下的所有文件
	 */
	@SuppressWarnings("WeakerAccess")
	public static File[] getDirChildFile(File dirFile) {
		if (dirFile.exists() && dirFile.isDirectory()) {
			return dirFile.listFiles();
		}

		return null;
	}

	/**
	 * 获取设备IMSI和IMEI号
	 */
	@SuppressLint("HardwareIds")
	public static Map<String, String> getIMSIAndIMEI(Context context) {
		HashMap<String, String> map = new HashMap<>();

		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		String imei = mTelephonyMgr.getDeviceId();

		map.put(IMSI, imsi);
		map.put(IMEI, imei);

		return map;
	}

	/**
	 * 获取设备分辨率
	 */
	public static Display getScreenPixels(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		return activity.getWindowManager().getDefaultDisplay();
		// return metrics;
	}

	/**
	 * 获取屏幕密度
	 */
	public static float getScreenDensity(Context context) {
		if (SCREEN_DENSITY == 0) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			SCREEN_DENSITY = dm.density;
		}
		return SCREEN_DENSITY;
	}

	/**
	 * 获取屏幕宽
	 */
	public static int getScreenWidth(Context context) {
		if (SCREEN_WIDTH == 0) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			SCREEN_WIDTH = dm.widthPixels;
		}
		return SCREEN_WIDTH;
	}

	/**
	 * 获取屏幕高
	 */
	public static int getScreenHeight(Context context) {
		if (SCREEN_HEIGHT == 0) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			SCREEN_HEIGHT = dm.heightPixels;
		}
		return SCREEN_HEIGHT;
	}

	/**
	 * 获取程序版本号
	 */
	public static int getAppVesionCode(Context context)
			throws NameNotFoundException {
		return context.getPackageManager().getPackageInfo(
				context.getPackageName(), Context.MODE_PRIVATE).versionCode;
	}
	
	/**
	 * 获取程序名称
	 */
	public static String getAppName(Context context)
			throws NameNotFoundException {
		return context.getPackageManager().getPackageInfo(
				context.getPackageName(), Context.MODE_PRIVATE)
				.applicationInfo.loadLabel(context.getPackageManager()).toString();
	}

	/**
	 * 获取程序版本名称
	 */
	public static String getAppVesionName(Context context)
			throws NameNotFoundException {
		return context.getPackageManager().getPackageInfo(
				context.getPackageName(), Context.MODE_PRIVATE).versionName;
	}

	/**
	 * 获取系统版本号
	 */
	public static String getSystemVesionCode(Context context) {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机型号
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取设备类型(phone、pad)
	 */
	public static String deviceStyle(Context context) {
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			return "phone";
		}

		return "pad";
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 sp 的单位 转成为 px
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px 的单位 转成为 sp
	 */
	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 显示软键盘
	 */
	public static void showInputMethod(@NonNull final View view) {
		view.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) view.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

//				imm.showSoftInput(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
				if (imm != null) {
					imm.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
				}
			}
		}, 100);
	}

	/**
	 * 关闭输入法
	 */
	public static void hideSoftInputView(final Activity context) {
		Runnable runnable = new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null && context.getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
				}
			}
		};
		new Handler().postDelayed(runnable, 100);
	}

	/**
	 * 判断软键盘是否显示
	 */
	public static boolean isKeyboardShowing(Activity activity) {
		//获取当前屏幕内容的高度
		int screenHeight = activity.getWindow().getDecorView().getHeight();
		//获取View可见区域的bottom
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		return screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0;
	}

	/**
	 * 设置当前界面为全屏模式
	 */
	public static void setFullScreen(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 如果当前为全屏，那么取消全屏模式，回到正常的模式
	 */
	public static void cancelFullScreen(Activity activity) {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 判断当前手机是否是全屏
	 *
	 * @return 如果是true，那么当前就是全屏
	 */
	public static boolean isFullScreen(Activity activity) {
		int flag = activity.getWindow().getAttributes().flags;
		return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
				== WindowManager.LayoutParams.FLAG_FULLSCREEN;
	}

	/**
	 * 判断当前屏幕是否是横屏
	 *
	 * @param activity 当前的activity
	 * @return 如果true就是竖屏
	 */
	public static boolean isVerticalScreen(Activity activity) {
		int flag = activity.getResources().getConfiguration().orientation;
		return flag != 0;
	}

	/**
	 * return statusBar's Height in pixels
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resId > 0) {
			result = context.getResources().getDimensionPixelOffset(resId);
		}
		return result;
	}

	/**
	 * 底部虚拟按键栏的高度
	 */
	@SuppressWarnings("WeakerAccess")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static int getSoftButtonsBarHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		//这个方法获取可能不是真实屏幕的高度
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int usableHeight = metrics.heightPixels;
		//获取当前屏幕的真实高度
		activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
		int realHeight = metrics.heightPixels;
		if (realHeight > usableHeight) {
			return realHeight - usableHeight;
		} else {
			return 0;
		}
	}

	@SuppressLint("HardwareIds")
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			//For advertising use-cases, use AdvertisingIdClient$Info#getId and for analytics, use InstanceId#getId.
			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getApplicationContext()
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//判定是否在前台工作
	public static boolean isRunningForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
		if (appProcessInfos == null) {
			return false;
		}
		// 枚举进程
		for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
			if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			Log.e("FileUtils|fileIsExists", "FileNotFoundException", e);
			return false;
		}
		return true;
	}

	public static String getLocalDirPath(Context context) {
		String appFilePath;
		String packageName = context.getPackageName();
		String directoryName = packageName.substring(packageName.indexOf(".") + 1, packageName.lastIndexOf("."));
		if (isSdcardEnable()) {
			appFilePath = Environment.getExternalStorageDirectory()+ File.separator
					+ directoryName + File.separator;
			createIfNotExist(appFilePath);
			return appFilePath;
		}
		appFilePath = File.separator+ directoryName + File.separator;
		createIfNotExist(appFilePath);
		return appFilePath;
	}

	public static String getLocalPath(Context context, LocalPathType pathType) {
		String path = getLocalDirPath(context) + pathType.name() + "/";
		createIfNotExist(path);
		return path;
	}

	private static boolean createIfNotExist(String path) {
		File file = new File(path);
		boolean b = false;
		if (!file.exists()) {
			b = file.mkdirs();
		}
		return b;
	}

	/**
	 * 保存图片到SD卡
	 *
	 * @param bitmap   图片格式
	 * @param filePath 图片路径
	 */
	@SuppressWarnings("WeakerAccess")
	public static boolean saveImage(Bitmap bitmap, String filePath) {
		boolean b = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			b = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件,第二个参数为压缩率，100表示不压缩
		} catch (FileNotFoundException e) {
			b = false;
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	public static boolean saveToFile(Context context, String content, String fileName) {
		boolean isSuccess = false;
		String path = getLocalPath(context, LocalPathType.files);
		if (path != null) {
			String filePath = path + fileName;
			File file = new File(filePath);
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(content);
				writer.close();
				Log.i("DeviceUtil","<<" + fileName + ">>文件保存成功");
				isSuccess = true;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("DeviceUtil","<<" + fileName + ">>文件保存失败");
				isSuccess = false;
			}
		}
		return isSuccess;
	}

	public static String getFromFile(String path, String fileName) {
		String content = null;
		File file = new File(path, fileName);
		if (file.exists() && file.length() > 0) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				content = reader.readLine();
				Log.i("DeviceUtil","<<" + fileName + ">>文件读取成功，内容=" + content);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("DeviceUtil","<<" + fileName + ">>文件读取失败");
			}
		}
		return content;
	}

	//拍照并返回照片文件
	public static File takeCameraFile(Activity activity, int requestCode) {
		String timeStr_pic = DateFormat.format("yyyyMMddhhmmss", System.currentTimeMillis()).toString();
		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		String pathname = DeviceUtil.getLocalPath(activity, LocalPathType.images) + timeStr_pic + ".jpg";
		Uri uri = Uri.fromFile(new File(pathname));
		intent.putExtra(
				MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		try {
			activity.startActivityForResult(intent, requestCode);
			return new File(pathname);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity,"该手机摄像头存在问题！",Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	//拍照并返回照片文件
	public static File takeCameraFile(Fragment fragment, int requestCode) {
		String timeStr_pic = DateFormat.format("yyyyMMddhhmmss", System.currentTimeMillis()).toString();
		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		String pathname = DeviceUtil.getLocalPath(fragment.getContext(), LocalPathType.images) + timeStr_pic
				+ ".jpg";
		Uri uri = Uri.fromFile(new File(pathname));
		intent.putExtra(
				MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		try {
			fragment.startActivityForResult(intent, requestCode);
			return new File(pathname);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(fragment.getContext(),"该手机摄像头存在问题！",Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	public enum LocalPathType {
		logs,//存放日志
		iCache,//存放上传图片的缓存，以.jp结尾
		files,//文件
		images//图片
	}

}
