package net.hcangus.util;

import android.content.Context;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * Android
 * Created by Administrator on 2016/8/12.
 */

public class DataCleanManage {

	/**
	 * 当路径下文件占用空间大于给定值时，进行清理
	 * @param path 文件路径
	 * @param maxSize 占用空间大于此值时进行清理
	 */
	public static void cleanPathBySize(String path, int maxSize) {
		long dirSize = getDirSize(new File(path));
		if (dirSize / (1024 * 1024) > maxSize) {
			cleanCustomCache(path);
		}
	}

	/**
	 * 计算缓存的大小
	 */
	public static String caculateCacheSize(Context context) {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = context.getFilesDir();
		File cacheDir = context.getCacheDir();

		fileSize += getDirSize(filesDir);
		fileSize += getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat.getExternalCacheDir(context);
			fileSize += getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = formatFileSize(fileSize);
		return cacheSize;
	}

	private static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
	 */
	private static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/**
	 * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
	 */
	private static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File(context.getFilesDir().getParent() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	private static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File(context.getFilesDir().getParent() + "/shared_prefs"));
	}

	/**
	 * 按名字清除本应用数据库 * * @param context * @param dbName
	 */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/**
	 * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
	 */
	private static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	private static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	 * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
	 */
	private static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	/**
	 * 清除本应用所有的数据 * * @param context * @param filepath
	 */
	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
//		cleanDatabases(context);
//		cleanSharedPreference(context);
		cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	/**
	 * 删除方法
	 */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				if (null == item || !item.exists()) {
					continue;
				}
				if ( item.isDirectory()) {
					Logger.d("---item is directory----"+item.getName());
					deleteFilesByDirectory(item);
				}else {
					boolean b= item.delete();
					Logger.d("-----delete item = " + item.getAbsolutePath() + "     " + b);
				}
			}
		}
	}

	/**
	 * 获取目录文件大小
	 */
	private static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 转换文件大小
	 *
	 * @return B/KB/MB/GB
	 */
	private static String formatFileSize(long fileS) {
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
		if (fileSizeString.endsWith(".00")) {
			fileSizeString = fileSizeString.substring(fileSizeString.length() - 3);
		}
		return fileSizeString;
	}
}
