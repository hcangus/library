package net.hcangus.imagepick;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import net.hcangus.base.R;

import java.util.ArrayList;
import java.util.List;

class ImageGridAdapter extends BaseAdapter {
    static final int WHAT_TAKE_PHOTO = 222;//点击到图片查看大图
    static final int WHAT_SHOW_TIPS = 0;//超过最大图片数量
    static final int WHAT_SHOW_TIPS_PATH_NULL = 1;//图片有问题

    private boolean isAll;
    private TextCallback textcallback = null;
    private final String TAG = getClass().getSimpleName();
    private Activity act;
    private List<ImageItem> dataList;
    //	public Map<String, String> map = new HashMap<>();
	List<String> pathList = new ArrayList<>();
    private Handler mHandler;
    private BitmapCache cache;

    private BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "callback, bmp not match");
                }
            } else {
                Log.e(TAG, "callback, bmp null");
            }
        }
    };

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean isAll) {
        this.isAll = isAll;
    }

    void setDataList(List<ImageItem> dataList) {
        if (dataList != null && dataList.size() > 0) {
            this.dataList = dataList;
        } else {
            this.dataList = new ArrayList<>();
        }
    }

    interface TextCallback {
        void onListen(int count);
    }

    void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

    ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
        cache = new BitmapCache();
        this.act = act;
        this.setDataList(list);
        this.mHandler = mHandler;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return isAll ? count + 1 : count;
    }

    @Override
    public ImageItem getItem(int position) {
        return dataList.get(isAll ? position - 1 : position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        private ImageView iv;
        private CheckBox cb_photo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(act, R.layout.item_image_grid, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            holder.cb_photo = (CheckBox) convertView
                    .findViewById(R.id.cb_photo);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        try {
            if (isAll && position == 0) {
                holder.iv.setImageResource(R.mipmap.tk_photo);
                holder.cb_photo.setVisibility(View.GONE);
                holder.iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mHandler.sendEmptyMessage(WHAT_TAKE_PHOTO);
                    }
                });
            } else {
                final ImageItem item = getItem(position);
                final String imagePath = item.imagePath;
                holder.iv.setTag(imagePath);
                holder.iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ArrayList<String> url = new ArrayList<>();
                        url.add(imagePath);
                        ImagePagerActivity.startMe(act, url, 0, null);
                    }
                });
                holder.cb_photo.setVisibility(View.VISIBLE);
                setViews(holder, item, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void setViews(final Holder holder, final ImageItem item, final int index) {
        cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
                callback);
        if (item.isSelected) {
            holder.cb_photo.setChecked(true);
        } else {
            holder.cb_photo.setChecked(false);
        }
        if (textcallback != null)
            textcallback.onListen(pathList.size());
        holder.cb_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String path = getItem(index).imagePath;

                if (pathList.size() < ImageGridActivity.size) {
                    item.isSelected = !item.isSelected;
                    if (item.isSelected) {
                        holder.cb_photo.setChecked(true);
//						map.put(path, path);
                        pathList.add(path);
                        if (textcallback != null) {
                            textcallback.onListen(pathList.size());
                        }
                    } else {
                        holder.cb_photo.setChecked(false);
                        pathList.remove(path);
                        if (textcallback != null) {
                            textcallback.onListen(pathList.size());
                        }
                    }
                } else {
                    if (item.isSelected) {
                        item.isSelected = !item.isSelected;
                        holder.cb_photo.setChecked(false);
                        pathList.remove(path);
                        if (textcallback != null) {
                            textcallback.onListen(pathList.size());
                        }

                    } else {
                        holder.cb_photo.setChecked(false);
                        mHandler.sendEmptyMessage(WHAT_SHOW_TIPS);
                    }
                }
            }

        });
    }

}
