package net.hcangus.glide;

/**
 * Anydoor
 * Created by hcangus
 */

class ImageSizeModelFutureStudio implements ImageSizeModel {

	private String baseImageUrl;

	ImageSizeModelFutureStudio(String baseImageUrl) {
		this.baseImageUrl = baseImageUrl;
	}

	@Override
	public String requestCustomSizeUrl(int width, int height) {
		String url;
//		if (imageBean != null) {
//			int w_resize;
//			int h_resize;
//			if (imageBean.img_degree == 90 || imageBean.img_degree == 270) {
//				w_resize = height;
//				h_resize = width;
//			} else {
//				w_resize = width;
//				h_resize = height;
//			}
//			url = imageBean.img_big_url + "?x-oss-process=image/resize,m_fill,w_" + w_resize +
//					",h_" + h_resize + "/rotate," + imageBean.img_degree;
//		}
		url = baseImageUrl;// + "?x-oss-process=image/resize,m_fill,w_" + width + ",h_" + height;
		return url;
	}
}
