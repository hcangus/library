package net.hcangus.loadretry;

import android.view.View;

public interface OnLoadingAndRetryListener {
	void setRetryEvent(View retryView);

	void setLoadingEvent(View loadingView);

	void setEmptyEvent(View emptyView);

	void setNoNetEvent(View noNetView);

	int generateLoadingLayoutId();

	int generateRetryLayoutId();

	int generateEmptyLayoutId();

	int generateNoNetLayoutId();
}