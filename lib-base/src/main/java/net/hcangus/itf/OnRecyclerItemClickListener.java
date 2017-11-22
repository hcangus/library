package net.hcangus.itf;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Anydoor
 * Created by hcangus
 */

public interface OnRecyclerItemClickListener<T, VH extends RecyclerView.ViewHolder> {
	/**
	 * 设置<code>VH</code>中某个View的点击事件
	 * @param v        The view is clicked.
	 * @param holder   VH
	 * @param t         T
	 * @param position index
	 */
	void onHolderItemClick(View v, VH holder, T t, int position);
}
