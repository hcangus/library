package net.hcangus.mvp.present;

/**
 * SCRM
 * Created by Administrator on 2017/3/21.
 */

public interface Present<V> {
	void attachView(V mvpView);

	void detachView();
}
