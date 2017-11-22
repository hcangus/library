package net.hcangus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import net.hcangus.itf.Action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class ImageUtils {

	/**
	 * 阿里云缩略图大小，在原图链接后加上 即可
	 */
	public interface ImageSize {
		String h0 = "@!h0";// 50 * 50
		String h1 = "@!h1";// 100 * 100
		String t0 = "@!t0";// 320 * auto
		String t1 = "@!t1";// 640 * auto
	}

	/**
	 * 质量压宿方法
	 *
	 * @param image 需要压缩的图片
	 * @param size  图片目标大小kb
	 */
	public static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size && options > 0) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * 图片按比例大小压缩方法
	 *
	 * @param srcPath 图片路径
	 * @param imH     图片高度
	 * @param imW     图片宽度
	 */
	public static Bitmap compressImage(String srcPath, float imW, float imH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > imW) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / imW);
		} else if (w < h && h > imH) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / imH);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		return BitmapFactory.decodeFile(srcPath, newOpts);
	}

	/**
	 * Get bitmap from specified image path
	 */
	public static Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}

	/**
	 * 图片按比例大小压缩方法
	 *
	 * @param image 需要压缩的图片
	 * @param size  目标图片大小，size不大于0时，不进行大小压缩
	 * @param imW   图片宽度
	 * @param imH   图片高度
	 */
	public static Bitmap compressImage(Bitmap image, int size, float imW,
									   float imH) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (size > 0) {
			int options = 100;
			while (baos.toByteArray().length / 1024 > size && options > 0) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > imW) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / imW);
		} else if (w < h && h > imH) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / imH);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, newOpts);
	}

	/**
	 * 压缩图片
	 */
	public static void compressSave_Hashname(final Context context, final String photoPath,
											 final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = compressImage(photoPath, 720, 1280);
				bitmap = compressImage(bitmap, 500);
				String tempPath = DeviceUtil.getLocalPath(context, DeviceUtil.LocalPathType.iCache)
						+ photoPath.hashCode() + ".jp";// 临时文件名
				boolean b = DeviceUtil.saveImage(bitmap, tempPath);
				if (b) {
					Message message = new Message();
					message.what = Action.Code._Compress;
					message.obj = tempPath;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 压缩图片
	 */
	public static void compressSaveAvatar(final Context context, @NonNull final Bitmap bitmap,
										  @NonNull final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap tempBitmap = compressImage(bitmap, 500);
//				Bitmap bitmapCompress = compressImage(tempBitmap,
//						Define.IMAGE_SIZE, 800f, 480f);
				String tempPath = DeviceUtil.getLocalPath(context, DeviceUtil.LocalPathType.iCache) + getImageName();// 临时文件名
				boolean b = DeviceUtil.saveImage(tempBitmap, tempPath);
				if (b) {
					Message message = new Message();
					message.what = Action.Code._Compress;
					message.obj = tempPath;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 生成一个时间翟，作为图片名字，时间格式：yyyyMMdd_hhmmss
	 */
	private static String getImageName() {
		return DateFormat.format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA)) + ".jp";
	}

	public static String getNameFromPath(String path) {
		try {
			return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 *
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		// 创建新的图片
		return Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
}
