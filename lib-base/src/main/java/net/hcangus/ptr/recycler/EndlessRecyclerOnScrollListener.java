package net.hcangus.ptr.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.hcangus.ptr.LoadingFooter;


/**
 * 继承自RecyclerView.OnScrollListener，可以监听到是否滑动到页面最低部
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

	private boolean canAutoLoad = false;
	public boolean canLoad = true;

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);
	}

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		super.onScrollStateChanged(recyclerView, newState);
		/* 当前状态为停止滑动状态：SCROLL_STATE_IDLE 时 */
		if (canAutoLoad && canLoad && newState == RecyclerView.SCROLL_STATE_IDLE) {
			if (RecyclerViewUtils.isRecyclerBottom(recyclerView)) {
				checkToLoad(recyclerView);
			}
		}
	}

	public void setCanAutoLoad(boolean canAutoLoad) {
		this.canAutoLoad = canAutoLoad;
	}

	public void setCanLoad(boolean canLoad) {
		this.canLoad = canLoad;
	}

	private void checkToLoad(final RecyclerView recyclerView) {
		RecyclerView.Adapter adapter = recyclerView.getAdapter();
		if (adapter == null || !(adapter instanceof HeaderFooterRecyclerAdapter)) {
			return;
		}
		HeaderFooterRecyclerAdapter outAdapter = (HeaderFooterRecyclerAdapter) adapter;
		LoadingFooter footerView;

		//已经有footerView了
		if (outAdapter.getFooterViewsCount() > 0) {
			footerView = (LoadingFooter) outAdapter.getFooterView();
			if (footerView.getState() == LoadingFooter.State.Normal) {
				footerView.setState(LoadingFooter.State.Loading);
				recyclerView.smoothScrollToPosition(outAdapter.getItemCount());
				onLoadMore(recyclerView);
			}
		} else {
			footerView = new LoadingFooter(recyclerView.getContext());
			footerView.setState(LoadingFooter.State.Loading);
			outAdapter.addLoadingFooter(footerView);
			recyclerView.getLayoutManager().scrollToPosition(outAdapter.getItemCount());
			onLoadMore(recyclerView);
		}
	}

	public abstract void onLoadMore(View view);
}
