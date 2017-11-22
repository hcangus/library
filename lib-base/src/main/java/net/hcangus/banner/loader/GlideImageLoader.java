package net.hcangus.banner.loader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.hcangus.glide.GlideUtil;

public class GlideImageLoader implements ImageLoaderInterface<String> {

	private final int imgWidth;
	private final int imgHeight;
	private ImageView imageView;

	public GlideImageLoader(Context context, int width, int height) {
		imgWidth = width;
		imgHeight = height;
	}

	@Override
	public ImageView getImageView() {
		return imageView;
	}

	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		GlideUtil.load(context, path, imageView);
	}

	@Override
	public View createView(Context context) {
		imageView = new ImageView(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(imgWidth, imgHeight);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		return imageView;
	}
}
