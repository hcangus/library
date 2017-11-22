package net.hcangus.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by Administrator
 */

public class ImageSizeGlideModule implements GlideModule {
	@Override
	public void applyOptions(Context context, GlideBuilder builder) {

	}

	@Override
	public void registerComponents(Context context, Glide glide) {
		glide.register(ImageSizeModel.class, InputStream.class, new ImageSizeModelFactory());
	}
}
