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
//			//得到当前显示的最后一个item的view
//			View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
//			if (lastChildView == null) {
//				return;
//			}
//			//得到lastChildView的bottom坐标值
//			int lastChildBottom = lastChildView.getBottom();
//			//得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
//			int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
//			//通过这个lastChildView得到这个view当前的position值
//			int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);
//
//			//判断lastChildView的bottom值跟recyclerBottom
//			//判断lastPosition是不是最后一个position
//			//如果两个条件都满足则说明是真正的滑动到了底部
//			if (lastChildBottom >= recyclerBottom - 10
//					&& lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
//				checkToLoad(recyclerView);
//			}

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
