package net.hcangus.itf;

/**
 * SCRM
 * Created by Administrator on 2017/5/11.
 */

public interface OnRefreshListener {
	/**
	 * 下拉刷新
	 */
	void onRefresh();

	/**
	 * 自动加载
	 */
	void onLoad();

	/**
	 * <em>自动加载出错的点击回调<em/>
	 */
	void onErrorClick();
}
