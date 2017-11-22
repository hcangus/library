package net.hcangus.glide;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 * Anydoor
 * Created by hcangus
 */

class ImageSizeUrlLoader extends BaseGlideUrlLoader<ImageSizeModel> {
	ImageSizeUrlLoader(Context context) {
		super( context );
	}

	@Override
	protected String getUrl(ImageSizeModel model, int width, int height) {
		return model.requestCustomSizeUrl( width, height );
	}
}
