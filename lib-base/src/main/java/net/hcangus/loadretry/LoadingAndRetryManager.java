package net.hcangus.loadretry;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

public class LoadingAndRetryManager {
	private final int STATUS_CONTENT = 1;
	private final int STATUS_EMPTY = 2;
	private final int STATUS_RETRY = 3;
	private final int STATUS_LOADING = 4;
	private final int STATUS_NONET = 5;
	private LoadingAndRetryLayout mLoadingAndRetryLayout;
	private OnLoadingAndRetryListener listener;
	private int status;

	private LoadingAndRetryManager(Object activityOrFragmentOrView,
								   @NonNull OnLoadingAndRetryListener listener, boolean isFitsSystemWindows) {
		this.listener = listener;
		ViewGroup contentParent;
		Context context;
		if (activityOrFragmentOrView instanceof Activity) {
			Activity activity = (Activity) activityOrFragmentOrView;
			context = activity;
			contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
		} else if (activityOrFragmentOrView instanceof Fragment) {
			Fragment fragment = (Fragment) activityOrFragmentOrView;
			context = fragment.getActivity();
			//noinspection ConstantConditions
			contentParent = (ViewGroup) (fragment.getView().getParent());
		} else if (activityOrFragmentOrView instanceof View) {
			View view = (View) activityOrFragmentOrView;
			contentParent = (ViewGroup) (view.getParent());
			context = view.getContext();
		} else {
			throw new IllegalArgumentException("the argument's type must be Fragment or Activity: init(context)");
		}
		int childCount = contentParent.getChildCount();
		//get contentParent
		int index = 0;
		View oldContent;
		if (activityOrFragmentOrView instanceof View) {
			oldContent = (View) activityOrFragmentOrView;
			for (int i = 0; i < childCount; i++) {
				if (contentParent.getChildAt(i) == oldContent) {
					index = i;
					break;
				}
			}
		} else {
			oldContent = contentParent.getChildAt(0);
		}
		/**
		 * 对于CoordinatorLayout中设置改变状态栏的问题，这里要设置为false。
		 * 通过 {@link #getContentView()}获取到这个oldContent，然后设置。
		 */
		oldContent.setFitsSystemWindows(isFitsSystemWindows);
		contentParent.removeView(oldContent);
		//setup content layout
		LoadingAndRetryLayout loadingAndRetryLayout = new LoadingAndRetryLayout(context);
		ViewGroup.LayoutParams lp = oldContent.getLayoutParams();
		contentParent.addView(loadingAndRetryLayout, index, lp);
		loadingAndRetryLayout.setContentView(oldContent);
		// setup loading,retry,empty layout
		loadingAndRetryLayout.setEmptyView(listener.generateEmptyLayoutId());
		loadingAndRetryLayout.setLoadingView(listener.generateLoadingLayoutId());
		loadingAndRetryLayout.setRetryView(listener.generateRetryLayoutId());
		loadingAndRetryLayout.setNoNetView(listener.generateNoNetLayoutId());
		//callback
		listener.setRetryEvent(loadingAndRetryLayout.getRetryView());
		listener.setLoadingEvent(loadingAndRetryLayout.getLoadingView());
		listener.setEmptyEvent(loadingAndRetryLayout.getEmptyView());
		listener.setNoNetEvent(loadingAndRetryLayout.getNoNetView());
		mLoadingAndRetryLayout = loadingAndRetryLayout;
		showContent();
	}

	public static LoadingAndRetryManager generate(Object activityOrFragment, OnLoadingAndRetryListener listener, boolean isFitsSystemWindows) {
		return new LoadingAndRetryManager(activityOrFragment, listener, isFitsSystemWindows);
	}

	public OnLoadingAndRetryListener getListener() {
		return listener;
	}

	public boolean isShowContent() {
		return status == STATUS_CONTENT;
	}

	public boolean isShowLoading() {
		return status == STATUS_LOADING;
	}

	public boolean isShowEmptyOrRetry() {
		return status == STATUS_EMPTY || status == STATUS_RETRY || status == STATUS_NONET;
	}

	public void showLoading() {
		if (status != STATUS_LOADING) {
			status = STATUS_LOADING;
			mLoadingAndRetryLayout.showLoading();
		}
	}

	public void showRetry() {
		if (status != STATUS_RETRY) {
			status = STATUS_RETRY;
			mLoadingAndRetryLayout.showRetry();
		}
	}

	public void showContent() {
		if (!isShowContent()) {
			status = STATUS_CONTENT;
			mLoadingAndRetryLayout.showContent();
		}
	}

	public void showEmpty() {
		if (status != STATUS_EMPTY) {
			status = STATUS_EMPTY;
			mLoadingAndRetryLayout.showEmpty();
		}
	}

	public void showNoNet() {
		if (status != STATUS_NONET) {
			status = STATUS_NONET;
			mLoadingAndRetryLayout.showNoNet();
		}
	}

	public View getContentView() {
		return mLoadingAndRetryLayout.getContentView();
	}

}
