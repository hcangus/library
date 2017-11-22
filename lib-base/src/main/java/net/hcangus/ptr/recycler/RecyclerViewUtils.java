package net.hcangus.ptr.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * RecyclerView设置Header/Footer所用到的工具类
 */
public class RecyclerViewUtils {

    /**
     * 请使用本方法替代RecyclerView.ViewHolder的getLayoutPosition()方法
     */
    public static int getLayoutPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderFooterRecyclerAdapter) {

            int headerViewCounter = ((HeaderFooterRecyclerAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                return holder.getLayoutPosition() - headerViewCounter;
            }
        }

        return holder.getLayoutPosition();
    }

    /**
     * 请使用本方法替代RecyclerView.ViewHolder的getAdapterPosition()方法
     */
    public static int getAdapterPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderFooterRecyclerAdapter) {

            int headerViewCounter = ((HeaderFooterRecyclerAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                return holder.getAdapterPosition() - headerViewCounter;
            }
        }

        return holder.getAdapterPosition();
    }


	/**
	 * 判断RecyclerView是否滚动到底部
	 */
	public static boolean isRecyclerBottom(RecyclerView recyclerView) {
		//得到当前显示的最后一个item的view
		View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
		//得到lastChildView的bottom坐标值
		int lastChildBottom = lastChildView.getBottom() + recyclerView.getTop();
		//得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
		int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
		//通过这个lastChildView得到这个view当前的position值
		int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

		//判断lastChildView的bottom值跟recyclerBottom
		//判断lastPosition是不是最后一个position
		//如果两个条件都满足则说明是真正的滑动到了底部
		return lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1;
	}

	/**
	 * 获取RecyclerView当前可见的最后一条数据的位置
	 */
	public static int getRecyclerLastVisible(RecyclerView recyclerView) {
		int lastVisibleItemPosition;
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
		} else if (layoutManager instanceof LinearLayoutManager) {
			lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
			int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
			staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
			lastVisibleItemPosition = findMax(lastPositions);
		} else {
			throw new RuntimeException(
					"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
		}
		return lastVisibleItemPosition;
	}

	/**
	 * 获取RecyclerView当前可见的完整的最后一条数据的位置
	 */
	public static int getRecyclerLastCompletelyVisible(RecyclerView recyclerView) {
		int lastVisibleItemPosition;
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
		} else if (layoutManager instanceof LinearLayoutManager) {
			lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
			int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
			staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
			lastVisibleItemPosition = findMax(lastPositions);
		} else {
			throw new RuntimeException(
					"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
		}
		return lastVisibleItemPosition;
	}

	/**
	 * 取数组中最大值
	 */
	private static int findMax(int[] lastPositions) {
		int max = lastPositions[0];
		for (int value : lastPositions) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}
}