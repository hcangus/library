package net.hcangus.imagepick;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import net.hcangus.util.DeviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AlbumHelper {
	private final String TAG = getClass().getSimpleName();
	Context context;
	private ContentResolver cr;

	// 缩略图列表
	private HashMap<String, String> thumbnailList = new HashMap<>();
	// 专辑列表
	private List<HashMap<String, String>> albumList = new ArrayList<>();
	private HashMap<String, ImageBucket> bucketList = new HashMap<>();

	private static WeakReference<AlbumHelper> instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null || instance.get() == null) {
			instance = new WeakReference<>(new AlbumHelper());
		}
		return instance.get();
	}

	/**
	 * 初始化
	 *
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	/**
	 * 得到缩略图
	 */
	private void getThumbnail() {
		String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID,
				Thumbnails.DATA};
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
				null, null, null);
		getThumbnailColumnData(cursor);
	}

	/**
	 * 从数据库中得到缩略图
	 *
	 */
	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			@SuppressWarnings("unused")
			int _id;
			int image_id;
			String image_path;
			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				// Get the field values
				//noinspection UnusedAssignment
				_id = cur.getInt(_idColumn);
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	/**
	 * 得到原图
	 */
	void getAlbum() {
		String[] projection = {Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
				Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS};
		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,
				null, null);
		getAlbumColumnData(cursor);

	}

	/**
	 * 从本地数据库中得到原图
	 */
	private void getAlbumColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			String album;
			String albumArt;
			String albumKey;
			String artist;
			int numOfSongs;

			int _idColumn = cur.getColumnIndex(Albums._ID);
			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				album = cur.getString(albumColumn);
				albumArt = cur.getString(albumArtColumn);
				albumKey = cur.getString(albumKeyColumn);
				artist = cur.getString(artistColumn);
				numOfSongs = cur.getInt(numOfSongsColumn);

				// Do something with the values.
				HashMap<String, String> hash = new HashMap<>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cur.moveToNext());

		}
	}

	/**
	 * 是否创建了图片集
	 */
	private boolean hasBuildImagesBucketList = false;

	// 设置获取图片的字段信息
	private static final String[] STORE_IMAGES = {
			Media.DISPLAY_NAME, // 显示的名�?
			Media.DATA,
			Media.LONGITUDE, // 经度
			Media._ID, // user_id
			Media.BUCKET_ID, // dir user_id 目录
			Media.BUCKET_DISPLAY_NAME,
			Media.DATE_ADDED// dir name 目录名字

	};

	/**
	 * 得到图片集
	 */
	private void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();

		// 构造缩略图索引
		getThumbnail();

		// 构造相册索引
		String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
				Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
				Media.SIZE, Media.BUCKET_DISPLAY_NAME, Media.DATE_ADDED};
		// 得到一个游标
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
				null);
		if (cur != null && cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur
					.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
			int timeIndex = cur.getColumnIndexOrThrow(Media.DATE_ADDED);
			// 获取图片总数
			int totalNum = cur.getCount();

			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);
				long time = cur.getLong(timeIndex);
				if (!DeviceUtil.isExists(path)) {
					continue;
				}
				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<>();
					bucket.bucketName = bucketName;
					bucket.time = time;
				}
				bucket.count++;
				ImageItem imageItem = new ImageItem(_id, path, time);
//				imageItem.imageId = _id;
//				imageItem.imagePath = path;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				bucket.imageList.add(imageItem);

			} while (cur.moveToNext());
		}

		try {
			if (cur != null) {
				cur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Entry<String, ImageBucket> entry : bucketList.entrySet()) {
			ImageBucket bucket = entry.getValue();
			for (int i = 0; i < bucket.imageList.size(); ++i) {
				bucket.imageList.get(i);
			}
		}
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}


	/**
	 * 得到图片集
	 *
	 * @param refresh true 每次都加载手机相册  false 第一次加载之后用缓存
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || !hasBuildImagesBucketList) {
			bucketList = new HashMap<>();
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = new ArrayList<>();
		for (Entry<String, ImageBucket> entry : bucketList.entrySet()) {
			tmpList.add(entry.getValue());
		}
		Collections.sort(tmpList, new YMComparator());
		return tmpList;
	}

	/**
	 * 得到原始图像路径
	 */
	String getOriginalImagePath(String image_id) {
		String path = null;
		String[] projection = {Media._ID, Media.DATA};
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
				Media._ID + "=" + image_id, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(Media.DATA));

		}
		if (cursor != null) {
			cursor.close();
		}
		return path;
	}

	private class YMComparator implements Comparator<ImageBucket> {

		@Override
		public int compare(ImageBucket o1, ImageBucket o2) {
			return (int) (o2.time - o1.time);
		}

	}

	/**
	 * 方法描述：按相册获取图片信息
	 *
	 */
	public List<ImageBucket> getPhotoAlbum() {
		List<ImageBucket> aibumList = new ArrayList<>();
		Cursor cursor = Media.query(cr,
				Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		Map<String, ImageBucket> countMap = new HashMap<>();
		ImageBucket pa;
		int count = 0;
		while (cursor.moveToNext()) {
			String path = cursor.getString(1);
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			long time = cursor.getLong(6);
			if (!DeviceUtil.isExists(path)) {
				continue;
			}
			if (!countMap.containsKey(dir_id)) {
				pa = new ImageBucket();
				pa.bucketName = dir;
				pa.count = 1;
				pa.time = time;
				pa.imageList.add(new ImageItem(id, path, time));
				countMap.put(dir_id, pa);
			} else {
				pa = countMap.get(dir_id);
				pa.time = time;
				pa.count = pa.count + 1;
				pa.imageList.add(new ImageItem(id, path, time));
			}
			count++;
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			aibumList.add(countMap.get(key));
		}
		Collections.sort(aibumList, new YMComparator());
		return aibumList;
	}
}
