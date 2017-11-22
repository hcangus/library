package net.hcangus.imagepick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.hcangus.base.R;

import java.util.ArrayList;
import java.util.List;

class AllPhotoPopWindow extends PopupWindow {
	static final int WHAT_SINGLE = 1111;
	static final int WHAT_ALL = 1112;

	private final String TAG = getClass().getSimpleName();
	private View rootView;
	private Handler handler;
	private Context context;
	private List<ImageBucket> buckets = new ArrayList<>();
	private boolean isFirst;
	private boolean isFirstPhoto = true;
	private List<Integer> sum = new ArrayList<>();

	AllPhotoPopWindow(Context mContext, Handler mHandler, int height,
					  List<ImageBucket> mBuckets, boolean isFirst, boolean isFirstPhoto, List<Integer> sum) {
		this.context = mContext;
		this.handler = mHandler;
		this.buckets = mBuckets;
		this.isFirst = isFirst;
		this.isFirstPhoto = isFirstPhoto;
		this.sum = sum;
		initViews();

		// 设置SelectPicPopupWindow的View
		this.setContentView(rootView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.c_bg_default));
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
	}

	private void initViews() {
		rootView = LayoutInflater.from(context).inflate(R.layout.pop_image_list, null);
		ImageListAdapter adapter = new ImageListAdapter(context, buckets);
		ListView lv_photos = (ListView) rootView.findViewById(R.id.lv_photos);
		lv_photos.setAdapter(adapter);
	}

	private class ImageListAdapter extends BaseAdapter {

		private List<ImageBucket> buckets;
		BitmapCache cache;
		BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap,
								  Object... params) {
				if (imageView != null && bitmap != null) {
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag())) {
						((ImageView) imageView).setImageBitmap(bitmap);
					} else {
						Log.e(TAG, "callback, bmp not match");
					}
				} else {
					Log.e(TAG, "callback, bmp null");
				}
			}
		};


		ImageListAdapter(Context context, List<ImageBucket> buckets) {
			cache = new BitmapCache();
			if (buckets == null) {
				this.buckets = new ArrayList<>();
			} else {
				this.buckets = buckets;
			}
		}

		@Override
		public int getCount() {
			return buckets.size();
		}

		@Override
		public ImageBucket getItem(int position) {
			return buckets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list, parent, false);
				holder.tv_photo_num = (TextView) convertView.findViewById(R.id.tv_photo_num);
				holder.tv_photo_type = (TextView) convertView.findViewById(R.id.tv_photo_type);
				holder.iv_first_photo = (ImageView) convertView.findViewById(R.id.iv_first_photo);
				holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ImageBucket item = buckets.get(position);
			if (item.count > 0) {
				holder.tv_photo_num.setText("" + item.count);
			} else {
				holder.tv_photo_num.setText("");
			}
			holder.tv_photo_type.setText(item.bucketName);
			holder.iv_first_photo.setTag(item.bucketName);
			holder.iv_select.setVisibility(View.GONE);
			if (position == 0) {
				holder.iv_first_photo.setImageResource(R.mipmap.default_picture);
			} else {
				if (item.imageList != null && item.imageList.size() > 0) {
					String thumbPath;
					String sourcePath;
					if (!isFirstPhoto) {
						if (sum.contains(position)) {
							thumbPath = item.imageList.get(0).thumbnailPath;
							sourcePath = item.imageList.get(0).imagePath;
						} else {
							thumbPath = item.imageList.get(item.imageList.size() - 1).thumbnailPath;
							sourcePath = item.imageList.get(item.imageList.size() - 1).imagePath;
						}
					} else {
						thumbPath = item.imageList.get(item.imageList.size() - 1).thumbnailPath;
						sourcePath = item.imageList.get(item.imageList.size() - 1).imagePath;
					}
					holder.iv_first_photo.setTag(sourcePath);
					cache.displayBmp(holder.iv_first_photo, thumbPath, sourcePath, callback);
				} else {
					holder.iv_first_photo.setImageBitmap(null);
				}
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						handler.obtainMessage(WHAT_ALL).sendToTarget();
					} else {
						item.position = position;
						Log.i("mPosition", item.position + "cc");
						handler.obtainMessage(WHAT_SINGLE, item).sendToTarget();
					}
					dismiss();
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView tv_photo_type, tv_photo_num;
			ImageView iv_first_photo, iv_select;
		}
	}

}
