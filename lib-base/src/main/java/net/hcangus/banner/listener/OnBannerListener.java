package net.hcangus.banner.listener;

public interface OnBannerListener {

	/**
	 * @param position 图片索引
	 * {@link OnBannerListener}Banner点击事件
	 * @see net.hcangus.banner.view.Banner#setOnBannerListener(OnBannerListener)
	 */
    void OnBannerClick(int position);
}
