package net.hcangus.imagepick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import net.hcangus.base.R;
import net.hcangus.itf.Action;
import net.hcangus.mvp.present.BasePresent;
import net.hcangus.tips.Tips;
import net.hcangus.base.BaseActivity;
import net.hcangus.util.DataCleanManage;
import net.hcangus.util.DeviceUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageGridActivity extends BaseActivity implements OnClickListener {
	/**
	 * 单个相册中的图片集合
	 */
	List<ImageItem> allDataList = new ArrayList<>();
	List<ImageItem> itemDataList = new ArrayList<>();
	private ImageGridAdapter adapter;
	private GridView gridView;
	public static int size = 0;
	AlbumHelper helper;
	private TextView tv_all_photos, tv_finish;
//	public static Bitmap bimap;
	/**
	 * 相册集合
	 */
	private List<ImageBucket> buckets;
	private boolean isFirstWindow = true;
	private boolean isFirstPhoto = true;
	private List<Integer> sum = new ArrayList<>();
	private MyHandler mHandler = new MyHandler(this);

	public static final String Key_Result = "data";
	private boolean hasCamera = true;

	public static void startMeForResult(Activity activity, int max, int requestCode) {
		Intent intent = new Intent(activity, ImageGridActivity.class);
		intent.putExtra("max", max);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startMeForResult(Activity activity, int max, boolean hasCamera, int requestCode) {
		Intent intent = new Intent(activity, ImageGridActivity.class);
		intent.putExtra("max", max);
		intent.putExtra("hasCamera", hasCamera);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startMeForResult(Fragment fragment, int max, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), ImageGridActivity.class);
		intent.putExtra("max", max);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void startMeForResult(Fragment fragment, int max, boolean hasCamera, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), ImageGridActivity.class);
		intent.putExtra("max", max);
		intent.putExtra("hasCamera", hasCamera);
		fragment.startActivityForResult(intent, requestCode);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.activity_image_grid;
	}

	@Override
	protected int getFragmentContentId() {
		return 0;
	}

	@Override
	protected void setupViews() {

	}

	@Override
	protected BasePresent createPresent(Context context) {
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataCleanManage.cleanPathBySize(DeviceUtil.getLocalPath(this, DeviceUtil.LocalPathType.iCache), 60);
		initViews();
		addListeners();
	}

	private void addListeners() {
		tv_all_photos.setOnClickListener(this);
		tv_finish.setOnClickListener(this);
	}

	private void initViews() {
		size = getIntent().getIntExtra("max", 0);
		hasCamera = getIntent().getBooleanExtra("hasCamera", true);
		mTitleBar.setTitle("选择图片");
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		buckets = helper.getPhotoAlbum();
		List<ImageItem> dataList;
		for (int i = 0; i < buckets.size(); i++) {
			dataList = buckets.get(i).imageList;
			allDataList.addAll(dataList);
		}
		Collections.sort(allDataList, new YMComparator());
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		tv_all_photos = (TextView) findViewById(R.id.tv_all_photos);
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, allDataList, mHandler);
		adapter.setAll(hasCamera);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
			public void onListen(int count) {
				tv_finish.setText("完成" + "(" + count + "/" + size + ")");
			}
		});

	}

	private File photoFile;

	protected void takePhoto() {
		photoFile = DeviceUtil.takeCameraFile(this, Action.Code._Camera);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.tv_all_photos) {
			showPhotoWindow();
		} else if (i == R.id.tv_finish) {
			ArrayList<String> list = new ArrayList<>();
			if (null != adapter && null != adapter.pathList && !adapter.pathList.isEmpty())
				list.addAll(adapter.pathList);
			setResultFinish(list);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (photoFile != null && requestCode == Action.Code._Camera && resultCode == RESULT_OK) {
			String picturePath = photoFile.getPath();
			mHandler.obtainMessage(Action.Code._Compress, picturePath).sendToTarget();
		}
	}

	private void setResultFinish(ArrayList<String> list) {
		Intent intent = new Intent();
		intent.putStringArrayListExtra(Key_Result, list);
		setResult(RESULT_OK, intent);
		finish();
	}

	private AllPhotoPopWindow photoWindow;

	private void showPhotoWindow() {
		if (null != photoWindow && photoWindow.isShowing()) return;
		if (null == photoWindow) {
			ImageBucket bucket = new ImageBucket();
			bucket.bucketName = "所有图片";
			if (isFirstWindow) {
				buckets.add(0, bucket);
			}
			int screenW = DeviceUtil.getScreenWidth(this);
			photoWindow = new AllPhotoPopWindow(this, mHandler, screenW * 4 / 7, buckets, isFirstWindow, isFirstPhoto, sum);
		}
		photoWindow.showAtLocation(mTitleBar, Gravity.BOTTOM, 0, 0);
		isFirstWindow = false;
	}

	@Override
	protected void onDestroy() {
		if (null != photoWindow && photoWindow.isShowing()) photoWindow.dismiss();
		super.onDestroy();
	}

	private static class YMComparator implements Comparator<ImageItem> {
		@Override
		public int compare(ImageItem o1, ImageItem o2) {
			if (o1 != null && o2 != null) {
				return (int) (o2.time - o1.time);
			}
			return 0;
		}

	}

	private static class MyHandler extends Handler {
		WeakReference<ImageGridActivity> reference;

		MyHandler(ImageGridActivity activity) {
			reference = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ImageGridActivity activity = reference.get();
			switch (msg.what) {
				case ImageGridAdapter.WHAT_SHOW_TIPS:
					Tips.showError(activity, "最多只能选择" + size + "张图片");
					break;
				case ImageGridAdapter.WHAT_TAKE_PHOTO:
					activity.takePhoto();
					break;
				case ImageGridAdapter.WHAT_SHOW_TIPS_PATH_NULL:
					Tips.showError(activity, "图片有问题哦，请更换一张");
					break;
				case AllPhotoPopWindow.WHAT_SINGLE:
					activity.isFirstPhoto = false;
					ImageBucket bucket = (ImageBucket) msg.obj;
					if (bucket != null) {
						activity.sum.add(bucket.position);
						activity.itemDataList = bucket.imageList;
						activity.tv_all_photos.setText(bucket.bucketName);
					}
					if (activity.itemDataList != null && activity.itemDataList.size() > 0) {
						Collections.sort(activity.itemDataList, new YMComparator());
						activity.adapter.setDataList(activity.itemDataList);
						activity.adapter.setAll(false);
						activity.adapter.notifyDataSetChanged();
						activity.gridView.smoothScrollToPosition(0);
					}
					break;
				case AllPhotoPopWindow.WHAT_ALL:
					activity.isFirstPhoto = false;
					if (activity.allDataList != null && activity.allDataList.size() > 0) {
						Collections.sort(activity.allDataList, new YMComparator());
						activity.tv_all_photos.setText("所有图片");
						activity.adapter.setAll(activity.hasCamera);
						activity.adapter.setDataList(activity.allDataList);
						activity.adapter.notifyDataSetChanged();
						activity.gridView.smoothScrollToPosition(0);
					}
					break;
				case Action.Code._Compress:
					String tempPath = (String) msg.obj;
					ArrayList<String> list = new ArrayList<>();
					list.add(tempPath);
					activity.setResultFinish(list);
					break;
				default:
					break;
			}
		}
	}
}
