package net.hcangus.mvp.view;

/**
 * Anydoor
 * Created by Administrator on 2017/4/20.
 */

public interface View_Bean<E> extends BaseView {
	/**
	 * 设置View的数据
	 * @param e E
	 */
	void setViewData(E e);
}
