package net.hcangus.mvp.view;


import net.hcangus.itf.OnRefreshListener;

import java.util.List;

/**
 * 下拉刷新和自动加载的视图接口
 * <p/>
 * Created by Administrator on 2017/3/20.
 */

public interface View_PtrList<E> extends BaseView, OnRefreshListener {

	/**
	 * 获取列表失败
	 *
	 * @param errMsg 错误信息
	 */
	void onGetListFail(String errMsg);

	/**
	 * 下拉刷新完成
	 *
	 * @param list 列表数据
	 */
	void onRefreshComplete(List<E> list);

	/**
	 * 自动加载成功
	 *
	 * @param list 列表数据
	 */
	void onLoadComplete(List<E> list);

	/**
	 * 自动加载失败
	 */
	void onloadFail();

	/**
	 * 列表是否有更多数据
	 *
	 * @param hasMore 是否有更多数据
	 */
	void hasMoreData(boolean hasMore);
}
