package net.hcangus.mvp.present;

import android.content.Context;

import net.hcangus.http.GetListHelper;
import net.hcangus.mvp.view.View_PtrList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下拉刷新和自动加载的ListView界面控制器的基类
 * <p/>
 * Created by Administrator on 2017/3/20.
 */

public abstract class PtrListPresent<E, V extends View_PtrList<E>> extends BasePresent<V> {
	/*分页加载时用到*/
	protected int PageSize = 20;
	protected int PageIndex = 0;

	private GetListHelper<E> getListHelper;

	public PtrListPresent(Context context, V mvpView) {
		super(context, mvpView);
		getListHelper = createGetListHelper();
	}

	public void setData(List<E> list) {
		PageIndex = 0;
		handleData(list);
	}

	//网络请求成功后处理数据
	public void handleData(List<E> list) {
		if (list == null || list.size() == 0) {
			if (PageIndex == 0) {
				getMvpView().onRefreshComplete(null);
				getMvpView().showEmpty();
			} else {
				getMvpView().onLoadComplete(null);
			}
			getMvpView().hasMoreData(false);
		} else {
			if (PageIndex == 0) {
				getMvpView().onRefreshComplete(list);
				getMvpView().hideLoading();
			} else {
				getMvpView().onLoadComplete(list);
			}
			getMvpView().hasMoreData(list.size() == PageSize);
		}
	}

	//下拉刷新
	public void onRefresh() {
		PageIndex = 0;
		loadData();
	}

	//自动加载
	public void onLoad() {
		PageIndex++;
		loadData();
	}

	@Override
	public void loadData() {
		Map<String, String> map = new HashMap<>();
		map.put("page_size", String.valueOf(PageSize));
		map.put("page_no", String.valueOf(PageIndex));
		setParamMap(map);
		getListHelper.excute(getUrl(), map, this);
	}

	public void onCallBackError(int errCode, String errMsg) {
		if (PageIndex == 0) {
			getMvpView().onGetListFail(errMsg);
			getMvpView().onRefreshComplete(null);
			getMvpView().showError(errCode, errMsg);
		} else {
			getMvpView().onloadFail();
			getMvpView().onLoadComplete(null);
			getMvpView().hasMoreData(false);
		}
	}

	/**
	 * 获取列表数据的URL
	 */
	protected abstract String getUrl();

	/**
	 * 获取列表数据的请求参数, start、limit已经添加，不需要再添加
	 *
	 * @param params 请求参数
	 */
	protected abstract void setParamMap(Map<String, String> params);

	protected abstract GetListHelper<E> createGetListHelper();

}
