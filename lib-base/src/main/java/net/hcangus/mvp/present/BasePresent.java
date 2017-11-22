package net.hcangus.mvp.present;

import android.content.Context;

import net.hcangus.http.HttpManage;
import net.hcangus.mvp.view.BaseView;


/**
 * Anydoor
 * Created by Administrator on 2017/3/20.
 */

public abstract class BasePresent<V extends BaseView> implements Present<V>,HttpManage.HttpImpl {

	protected Context mContext;
	private V mvpView;
	private boolean hasFindView = false;

	public BasePresent(Context context, V mvpView) {
		this.mContext = context;
		attachView(mvpView);
	}

	public abstract void loadData();

	@Override
	public void detachView() {
		HttpManage.cancelAll(mContext, this);
		mvpView = null;
	}

	@Override
	public void attachView(V mvpView) {
		this.mvpView = mvpView;
	}

	private boolean isViewAttached() {
		return mvpView != null;
	}

	public V getMvpView() {
		checkViewAttached();
		return mvpView;
	}

	private void checkViewAttached() {
		if (!isViewAttached()) throw new RuntimeException("未调用 Present.attachView(MvpView) !");
	}

}
