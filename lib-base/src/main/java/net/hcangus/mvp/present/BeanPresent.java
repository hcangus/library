package net.hcangus.mvp.present;

import android.content.Context;

import net.hcangus.http.GetModelHelper;
import net.hcangus.mvp.view.View_Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Anydoor
 * Created by Administrator.
 */

public abstract class BeanPresent<E, V extends View_Bean<E>> extends BasePresent<V> {

	protected String id;
	private GetModelHelper<E> getModelHelper;

	public BeanPresent(Context context, V mvpView) {
		super(context, mvpView);
		getModelHelper = createGetModelHelper();
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void loadData() {
		Map<String, String> params = new HashMap<>();
		setParamMap(params);
		getModelHelper.excute(getUrl(), params, this);
	}

	protected void onLoadSuccess(E e) {
		getMvpView().setViewData(e);
		if (getMvpView().isShowLoading()) {
			getMvpView().hideLoading();
		}
	}

	protected void onLoadFail(int errCode, String errMsg) {
		if (getMvpView().isShowLoading()) {
			getMvpView().showError(errCode, errMsg);
		}
	}

	/**
	 * 获取列表数据的URL
	 */
	protected abstract String getUrl();

	/**
	 * @param params 请求参数
	 */
	protected abstract void setParamMap(Map<String, String> params);

	protected abstract GetModelHelper<E> createGetModelHelper();
}
