package net.hcangus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Anydoor
 * Created by Administrator
 */

public class ViewUtils {
	/**
	 * 设置view宽高(线性布局linearLayout)
	 *
	 * @param wh 控件的宽高,单位px
	 * @param v  控件view
	 */
	public static void setViewWH_Line(int wh, View v) {
		setViewWH_Line(wh, wh, v);
	}

	/**
	 * 设置view宽高(ViewGroup)
	 *
	 * @param wh 控件的宽高,单位px
	 * @param v  控件view
	 */
	public static void setViewWH_Group(int wh, View v) {
		setViewWH_Group(wh, wh, v);
	}

	/**
	 * 设置View宽高(相对布局relativeLayout)
	 *
	 * @param wh 控件的宽高,单位px
	 * @param v  控件
	 */
	public static void setViewWH_Rela(int wh, View v) {
		setViewWH_Rela(wh, wh, v);
	}

	/**
	 * 设置view宽高(线性布局linearLayout)
	 *
	 * @param w 控件宽 ,单位px
	 * @param h 控件高,单位px
	 * @param v 控件view
	 */
	public static void setViewWH_Line(int w, int h, View v) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
		if (params == null) {
			params = new LinearLayout.LayoutParams(w, h);
		} else {
			params.width = w;
			params.height = h;
		}
		v.setLayoutParams(params);
	}

	/**
	 * 设置view宽高(ViewGroup)
	 *
	 * @param w 控件宽 ,单位px
	 * @param h 控件高,单位px
	 * @param v 控件view
	 */
	public static void setViewWH_Group(int w, int h, View v) {
		ViewGroup.LayoutParams params = v.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(w, h);
		} else {
			params.width = w;
			params.height = h;
		}
		v.setLayoutParams(params);
	}

	/**
	 * 设置view宽高(相对布局relativeLayout)
	 *
	 * @param w 控件宽,单位px
	 * @param h 控件高,单位px
	 * @param v 控件view
	 */
	public static void setViewWH_Rela(int w, int h, View v) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
		if (params == null) {
			params = new RelativeLayout.LayoutParams(w, h);
		} else {
			params.width = w;
			params.height = h;
		}
		v.setLayoutParams(params);
	}

	/**
	 * 根据屏幕宽获取控件宽度
	 *
	 * @param minus_dp 要从总宽度中减去的宽度
	 * @param ratio    控件占用的比例
	 */
	public static int getWidthByScreen(Context context, int minus_dp, float ratio) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		float scale = context.getResources().getDisplayMetrics().density;
		int minus_px = (int) (minus_dp * scale + 0.5f);
		return (int) ((screenWidth - minus_px) * ratio + 0.5f);
	}

	/**
	 * 通过宽得到高
	 *
	 * @param width     宽
	 * @param ratio_w_h 宽 / 高 * 1.0f，注意乘以1.0f才能得到比较精确的值
	 */
	public static int getHeightByWidth(int width, float ratio_w_h) {
		return (int) (width / ratio_w_h + 0.5f);
	}

	/**
	 * 判断RecyclerView是否滚动到底部
	 */
	public static boolean isRecyclerBottom(RecyclerView recyclerView) {
		//得到当前显示的最后一个item的view
		View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
		//得到lastChildView的bottom坐标值
		int lastChildBottom = lastChildView.getBottom();
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

	public static void setViewWhBaseOnW(Context context, ImageView view, int drawableRes) {
		Bitmap b1 = BitmapFactory.decodeResource(context.getResources(),drawableRes);
		int w = b1.getWidth();
		int h = b1.getHeight();
		int height1 = (int) (h * 1f / w * view.getWidth() + 0.5f);
		view.setMinimumHeight(height1);
		view.setMaxHeight(height1);
		b1.recycle();
	}
}
