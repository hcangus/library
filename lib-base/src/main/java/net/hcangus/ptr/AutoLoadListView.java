package net.hcangus.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;


/**
 * android-Ultra-Pull-To-Refresh
 * Created by Administrator on 2017/3/17.
 */

public class AutoLoadListView extends ListView {

	public interface AutoLoadOnScrollListener extends OnScrollListener {

	}

	private boolean canLoad = true;
	private boolean canAutoLoad = true;
	private LoadingFooter footer;
	private AutoLoadOnScrollListener autoLoadOnScrollListener;
	private AutoLoadListener autoLoadListener;

	public AutoLoadListView(Context context) {
		this(context, null, 0);
	}

	public AutoLoadListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initFooter(context);
//		setOnScrollListener(this);
	}

	private void initFooter(Context context) {
		footer = new LoadingFooter(context);
		addFooterView(footer, null, false);
	}

	public void setAutoLoadListener(AutoLoadListener listener) {
		this.autoLoadListener = listener;
	}

	public void setAutoLoadOnScrollListener(AutoLoadOnScrollListener listener) {
		this.autoLoadOnScrollListener = listener;
	}

	public void setCanLoad(boolean canLoad) {
		this.canLoad = canLoad;
	}

	public void setCanAutoLoad(boolean canAutoLoad) {
		this.canAutoLoad = canAutoLoad;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		checkAutoLoad();
	}

//	@Override
//	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		if (autoLoadOnScrollListener != null) {
//			autoLoadOnScrollListener.onScrollStateChanged(view, scrollState);
//		}
//		checkAutoLoad();
//	}
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		if (autoLoadOnScrollListener != null) {
//			autoLoadOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//		}
//		checkAutoLoad();
//	}

	private void checkAutoLoad() {
		if (autoLoadListener != null && footer.getState() == LoadingFooter.State.Normal
				&& canLoad && canAutoLoad && isLastItemVisible()) {
			changeState(LoadingFooter.State.Loading);
		}
	}

	private boolean isLastItemVisible() {
		if (getCount() == 0) {
			return false;
		} else if (getLastVisiblePosition() == (getCount() - 1)) {
			// 滑到底部，且頂部不是第0个，也就是说item数超过一屏后才能自动加载，否则只能点击加载
			if (getFirstVisiblePosition() != 0
					&& getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getTop() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

	/**
	 * 更改加载状态
	 */
	private void changeState(LoadingFooter.State state) {
		footer.setState(state);
		if (state == LoadingFooter.State.Loading) {
			canLoad = false;
			if (autoLoadListener != null) {
				autoLoadListener.onLoadMore(this);
			}
		}
	}

	/**
	 * 当加载失败
	 */
	public void onLoadMoreFailed() {
		changeState(LoadingFooter.State.Error);
		footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (autoLoadListener != null) {
					autoLoadListener.onErrorClick();
				}
			}
		});
	}

	/**
	 * 是否有更多数据
	 */
	public void hasMore(boolean hasMore) {
		setCanLoad(hasMore);
		if (!hasMore && getLastVisiblePosition() < getCount() - 1) {
			changeState(LoadingFooter.State.TheEnd);
		} else {
			changeState(LoadingFooter.State.Normal);
		}
	}
}
