package net.hcangus.mvp.view;

/**
 * Anydoor
 * Created by hcangus
 */

public interface BaseView {
	void showLoading();

	void hideLoading();

	void showError(int errCode, String msg);

	void showEmpty();

	void onRetry();

	boolean isShowLoading();

	boolean isShowEmptyOrRetry();
}
