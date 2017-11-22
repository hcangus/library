package net.hcangus.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 * Anydoor
 * Created by Administrator
 */

class ImageSizeModelFactory implements ModelLoaderFactory<ImageSizeModel, InputStream> {
	@Override
	public ModelLoader<ImageSizeModel, InputStream> build(Context context, GenericLoaderFactory factories) {
		return new ImageSizeUrlLoader( context );
	}

	@Override
	public void teardown() {

	}
}
