package net.hcangus.ptr.recycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * RecyclerView.Adapter with Header and Footer
 */
public class HeaderFooterRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;

	private boolean hasAddLoading = false;
	/**
	 * RecyclerView使用的，真正的Adapter
	 */
	private RecyclerView.Adapter mInnerAdapter;

	private ArrayList<View> mHeaderViews = new ArrayList<>();
	private ArrayList<View> mFooterViews = new ArrayList<>();

	private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

		@Override
		public void onChanged() {
			super.onChanged();
			notifyDataSetChanged();
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount) {
			super.onItemRangeChanged(positionStart, itemCount);
			notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			super.onItemRangeInserted(positionStart, itemCount);
			notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
			notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			super.onItemRangeMoved(fromPosition, toPosition, itemCount);
			int headerViewsCountCount = getHeaderViewsCount();
			notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
		}
	};

	public HeaderFooterRecyclerAdapter(RecyclerView.Adapter innerAdapter) {
		setAdapter(innerAdapter);
	}

	/**
	 * 设置adapter
	 */
	public void setAdapter(RecyclerView.Adapter adapter) {

		if (mInnerAdapter != null) {
			notifyItemRangeRemoved(getHeaderViewsCount(), mInnerAdapter.getItemCount());
			mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
		}

		this.mInnerAdapter = adapter;
		mInnerAdapter.registerAdapterDataObserver(mDataObserver);
		notifyItemRangeInserted(getHeaderViewsCount(), mInnerAdapter.getItemCount());
	}

	public RecyclerView.Adapter getInnerAdapter() {
		return mInnerAdapter;
	}

	public void addHeaderView(View header) {

		if (header == null) {
			throw new RuntimeException("header is null");
		}

		mHeaderViews.add(header);
		this.notifyDataSetChanged();
	}

	public void addFooterView(View footer) {

		if (footer == null) {
			throw new RuntimeException("footer is null");
		}
		if (hasAddLoading) {
			mFooterViews.add(mFooterViews.size() - 1, footer);
		} else {
			mFooterViews.add(footer);
		}
		this.notifyDataSetChanged();
	}

	public void addLoadingFooter(View footer) {

		if (footer == null) {
			throw new RuntimeException("footer is null");
		}

		mFooterViews.add(footer);
		hasAddLoading = true;
		this.notifyDataSetChanged();
	}

	/**
	 * 返回最后一个FootView
	 */
	public View getFooterView() {
		return getFooterViewsCount() > 0 ? mFooterViews.get(getFooterViewsCount() - 1) : null;
	}

	/**
	 * 返回第一个HeaderView
	 */
	public View getHeaderView() {
		return getHeaderViewsCount() > 0 ? mHeaderViews.get(0) : null;
	}

	public void removeHeaderView(View view) {
		mHeaderViews.remove(view);
		this.notifyDataSetChanged();
	}

	public void removeFooterView(View view) {
		mFooterViews.remove(view);
		this.notifyDataSetChanged();
	}

	public int getHeaderViewsCount() {
		return mHeaderViews.size();
	}

	public int getFooterViewsCount() {
		return mFooterViews.size();
	}

	public boolean isHeader(int position) {
		return position < getHeaderViewsCount();
	}

	public boolean isFooter(int position) {
		int innerCount = mInnerAdapter.getItemCount();
		return position >= getHeaderViewsCount() + innerCount;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		int headerViewsCountCount = getHeaderViewsCount();
		if (viewType < TYPE_HEADER_VIEW + headerViewsCountCount) {
			return new ViewHolder(mHeaderViews.get(viewType - TYPE_HEADER_VIEW));
		} else if (viewType >= TYPE_HEADER_VIEW + headerViewsCountCount
				&& viewType < TYPE_HEADER_VIEW + headerViewsCountCount + getFooterViewsCount()) {
			return new ViewHolder(mFooterViews.get(viewType - TYPE_HEADER_VIEW - headerViewsCountCount));
		} else {
			return mInnerAdapter.onCreateViewHolder(parent, viewType - Integer.MAX_VALUE / 2);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		int headerViewsCountCount = getHeaderViewsCount();
		if (position >= headerViewsCountCount && position < headerViewsCountCount + mInnerAdapter.getItemCount()) {
			mInnerAdapter.onBindViewHolder(holder, position - headerViewsCountCount);
		} else {
			ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
			if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
				((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
			}
		}
	}

	@Override
	public int getItemCount() {
		return getHeaderViewsCount() + getFooterViewsCount() + mInnerAdapter.getItemCount();
	}

	@Override
	public int getItemViewType(int position) {
		int innerCount = mInnerAdapter.getItemCount();
		int headerViewsCountCount = getHeaderViewsCount();
		if (position < headerViewsCountCount) {
			return TYPE_HEADER_VIEW + position;
		} else if (headerViewsCountCount <= position && position < headerViewsCountCount + innerCount) {

			int innerItemViewType = mInnerAdapter.getItemViewType(position - headerViewsCountCount);
			if (innerItemViewType >= Integer.MAX_VALUE / 2) {
				throw new IllegalArgumentException("your adapter's return value of getViewTypeCount() must < Integer.MAX_VALUE / 2");
			}
			return innerItemViewType + Integer.MAX_VALUE / 2;
		} else {
			return TYPE_HEADER_VIEW + position - innerCount;
		}
	}

	private static class ViewHolder extends RecyclerView.ViewHolder {
		ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
