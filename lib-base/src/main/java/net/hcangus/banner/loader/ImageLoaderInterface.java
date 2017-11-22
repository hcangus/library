package net.hcangus.banner.loader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;


public interface ImageLoaderInterface<T> extends Serializable {

    void displayImage(Context context, T path, ImageView imageView);

    View createView(Context context);

	ImageView getImageView();
}
