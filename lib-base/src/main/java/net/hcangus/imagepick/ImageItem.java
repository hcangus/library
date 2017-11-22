package net.hcangus.imagepick;

import java.io.Serializable;


/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String imageId;
	String thumbnailPath;
	String imagePath;
	boolean isSelected = false;
	public long time;

	public ImageItem(String id, String path, long time) {
		imageId = id;
		isSelected = false;
		this.imagePath=path;
		this.time = time;
	}
}
