package net.hcangus.loadretry;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class LoadingAndRetryLayout extends FrameLayout {
	private View mLoadingView;
	private View mRetryView;
	private View mContentView;
	private View mEmptyView;
	private View mNoNetView;
	private LayoutInflater mInflater;

	private static final String TAG = LoadingAndRetryLayout.class.getSimpleName();


	public LoadingAndRetryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mInflater = LayoutInflater.from(context);
	}


	public LoadingAndRetryLayout(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public LoadingAndRetryLayout(Context context) {
		this(context, null);
	}

	private boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public void showLoading() {
		if (isMainThread()) {
			showView(mLoadingView);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showView(mLoadingView);
				}
			});
		}
	}

	public void showRetry() {
		if (isMainThread()) {
			showView(mRetryView);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showView(mRetryView);
				}
			});
		}

	}

	public void showContent() {
		if (isMainThread()) {
			showView(mContentView);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showView(mContentView);
				}
			});
		}
	}

	public void showEmpty() {
		if (isMainThread()) {
			showView(mEmptyView);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showView(mEmptyView);
				}
			});
		}
	}

	public void showNoNet() {
		if (isMainThread()) {
			showView(mNoNetView);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showView(mNoNetView);
				}
			});
		}
	}


	private void showView(View view) {
		if (view == null) return;

		if (view == mLoadingView) {
			mLoadingView.setVisibility(View.VISIBLE);
			if (mRetryView != null)
				mRetryView.setVisibility(View.GONE);
			if (mContentView != null)
				mContentView.setVisibility(View.GONE);
			if (mEmptyView != null)
				mEmptyView.setVisibility(View.GONE);
			if (mNoNetView != null)
				mNoNetView.setVisibility(View.GONE);
		} else if (view == mRetryView) {
			mRetryView.setVisibility(View.VISIBLE);
			if (mLoadingView != null)
				mLoadingView.setVisibility(View.GONE);
			if (mContentView != null)
				mContentView.setVisibility(View.GONE);
			if (mEmptyView != null)
				mEmptyView.setVisibility(View.GONE);
			if (mNoNetView != null)
				mNoNetView.setVisibility(View.GONE);
		} else if (view == mContentView) {
			mContentView.setVisibility(View.VISIBLE);
			if (mLoadingView != null)
				mLoadingView.setVisibility(View.GONE);
			if (mRetryView != null)
				mRetryView.setVisibility(View.GONE);
			if (mEmptyView != null)
				mEmptyView.setVisibility(View.GONE);
			if (mNoNetView != null)
				mNoNetView.setVisibility(View.GONE);
		} else if (view == mEmptyView) {
			mEmptyView.setVisibility(View.VISIBLE);
			if (mLoadingView != null)
				mLoadingView.setVisibility(View.GONE);
			if (mRetryView != null)
				mRetryView.setVisibility(View.GONE);
			if (mContentView != null)
				mContentView.setVisibility(View.GONE);
			if (mNoNetView != null)
				mNoNetView.setVisibility(View.GONE);
		}else if (view == mNoNetView) {
			mNoNetView.setVisibility(View.VISIBLE);
			if (mLoadingView != null)
				mLoadingView.setVisibility(View.GONE);
			if (mRetryView != null)
				mRetryView.setVisibility(View.GONE);
			if (mContentView != null)
				mContentView.setVisibility(View.GONE);
			if (mEmptyView != null)
				mEmptyView.setVisibility(View.GONE);
		}


	}

	public void setContentView(int layoutId) {
		setContentView(mInflater.inflate(layoutId, this, false));
	}

	public void setLoadingView(int layoutId) {
		setLoadingView(mInflater.inflate(layoutId, this, false));
	}

	public void setEmptyView(int layoutId) {
		setEmptyView(mInflater.inflate(layoutId, this, false));
	}

	public void setNoNetView(int layoutId) {
		setNoNetView(mInflater.inflate(layoutId, this, false));
	}

	public void setRetryView(int layoutId) {
		setRetryView(mInflater.inflate(layoutId, this, false));
	}

	public void setLoadingView(View view) {
		View loadingView = mLoadingView;
		if (loadingView != null) {
			Log.w(TAG, "you have already set a loading view and would be instead of this new one.");
		}
		removeView(loadingView);
		addView(view);
		mLoadingView = view;
	}

	public void setEmptyView(View view) {
		View emptyView = mEmptyView;
		if (emptyView != null) {
			Log.w(TAG, "you have already set a empty view and would be instead of this new one.");
		}
		removeView(emptyView);
		addView(view);
		mEmptyView = view;
	}

	public void setNoNetView(View view) {
		View noNetView = mNoNetView;
		if (noNetView != null) {
			Log.w(TAG, "you have already set a nonet view and would be instead of this new one.");
		}
		removeView(noNetView);
		addView(view);
		mNoNetView = view;
	}

	public void setRetryView(View view) {
		View retryView = mRetryView;
		if (retryView != null) {
			Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
		}
		removeView(retryView);
		addView(view);
		mRetryView = view;
	}

	public void setContentView(View view) {
		View contentView = mContentView;
		if (contentView != null) {
			Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
		}
		removeView(contentView);
		addView(view);
		mContentView = view;
	}

	public View getRetryView() {
		return mRetryView;
	}

	public View getLoadingView() {
		return mLoadingView;
	}

	public View getContentView() {
		return mContentView;
	}

	public View getEmptyView() {
		return mEmptyView;
	}

	public View getNoNetView() {
		return mNoNetView;
	}
}
