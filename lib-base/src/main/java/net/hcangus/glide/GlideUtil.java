package net.hcangus.glide;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.hcangus.base.R;
import net.hcangus.util.DeviceUtil;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * <p>
 *     根据控件的大小从阿里云获取图片
 *     在AndroidManifest中加入了
 			<meta-data
 				android:name="net.hcangus.glide.ImageSizeGlideModule"
 				android:value="GlideModule" >
 			</meta-data>
 * </p>
 * Created by hcangus
 */

public class GlideUtil {

	public static void load(Context context, String url, ImageView imageView) {
		Glide.with(context)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? R.color.c_bg_white_pressed :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
								url)
				.placeholder(R.color.c_bg_white_pressed)
				.into(imageView);
	}

	public static void load(Fragment fragment, String url, ImageView imageView) {
		Glide.with(fragment)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? R.color.c_bg_white_pressed :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
						url)
				.placeholder(R.color.c_bg_white_pressed)
				.into(imageView);
	}

	public static void loadCircle(Context context, String url, ImageView imageView) {
		Glide.with(context)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? R.color.c_bg_white_pressed :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
								url)
				.bitmapTransform(new CropCircleTransformation(context))
				.placeholder(R.color.c_bg_white_pressed)
				.error(R.color.c_bg_white_pressed)
				.into(imageView);
	}

	public static void loadCircle(Context context, String url, ImageView imageView, @DrawableRes int errorRes) {
		Glide.with(context)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? errorRes :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
								url)
				.bitmapTransform(new CropCircleTransformation(context))
				.placeholder(R.color.c_bg_white_pressed)
				.error(errorRes)
				.into(imageView);
	}

	public static void loadCircle(Fragment fragment, String url, ImageView imageView) {
		Glide.with(fragment)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? R.color.c_bg_white_pressed :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
								url)
				.bitmapTransform(new CropCircleTransformation(fragment.getContext()))
				.placeholder(R.color.c_bg_white_pressed)
				.error(R.color.c_bg_white_pressed)
				.into(imageView);
	}

	public static void loadRound(Context context, String url, ImageView imageView) {
		loadRound(context, url, imageView, 4);
	}

	public static void loadRound(Context context, String url, ImageView imageView, float radisDp) {
		Glide.with(context)
				//判断链接是否来自与阿里云，否则从原地址获取图片
				.load(url == null ? R.color.c_bg_white_pressed :
//						url.contains(".aliyuncs.") ? new ImageSizeModelFutureStudio(url) :
								url)
				.bitmapTransform(new RoundedCornersTransformation(context, DeviceUtil.dp2px(context,radisDp),0))
				.placeholder(R.color.c_bg_white_pressed)
				.error(R.color.c_bg_white_pressed)
				.centerCrop()
				.into(imageView);
	}

}
