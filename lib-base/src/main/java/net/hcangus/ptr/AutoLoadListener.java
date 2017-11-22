package net.hcangus.ptr;

import android.view.View;

/**
 * android-Ultra-Pull-To-Refresh
 * Created by Administrator on 2017/3/17.
 */

public interface AutoLoadListener {
	void onLoadMore(View view);

	/**
	 * 自动加载出错状态的点击事件回调
	 */
	void onErrorClick();
}
