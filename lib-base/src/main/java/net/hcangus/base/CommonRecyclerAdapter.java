package net.hcangus.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.hcangus.itf.OnRecyclerItemClickListener;
import net.hcangus.ptr.recycler.AutoLoadRecyclerView;
import net.hcangus.ptr.recycler.RecyclerViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Anydoor
 * Created by hcangus
 */

public abstract class CommonRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH> {

	protected final RecyclerView recyclerView;
	protected Context mContext;
	protected List<T> list;
	private OnRecyclerItemClickListener<T, VH> itemClickListener;

	public CommonRecyclerAdapter(Context context, RecyclerView recyclerView, List<T> list) {
		this.mContext = context;
		this.recyclerView = recyclerView;
		this.list = list;
	}

	public List<T> getDatas() {
		return list;
	}

	@Override
	public int getItemCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		VH holder = createHolder(parent, viewType);
		if (getClickView(holder) != null) {
			setItemClick(getClickView(holder), holder);
		}
		return holder;
	}

	/**
	 * 如果不需要整个条目的点击事件，重写返回null。
	 * 对于侧滑{@link net.hcangus.widget.SwipeMenuLayout}，返回对应的View
	 */
	protected View getClickView(VH holder) {
		return holder.itemView;
	}

	public T getItem(int position) {
		return list == null || position > list.size() - 1 ? null : list.get(position);
	}

	/**
	 * 往当前ListView中增加数据列表
	 *
	 * @param datas 增加的数据列表
	 */
	public void addDatas(List<T> datas) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		if (this.list == null)
			this.list = new ArrayList<>();
		int start = list.size();
		this.list.addAll(datas);
		notifyItemRangeInserted(start, datas.size());
	}

	public void addData(T data) {
		if (data == null) {
			return;
		}
		if (this.list == null)
			this.list = new ArrayList<>();
		list.add(data);
		notifyItemInserted(getItemCount() - 1);
	}

	/**
	 * 往当前ListView中增加数据列表
	 *
	 * @param datas    增加的数据列表
	 * @param position 加入的位置
	 */
	public void addDatas(List<T> datas, int position) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		if (this.list == null)
			this.list = new ArrayList<>();
		if (this.list.size() + 1 <= position) {
			this.list.addAll(this.list.size(), datas);
		} else {
			this.list.addAll(position, datas);
		}
		notifyItemRangeInserted(position, datas.size());
	}

	/**
	 * 更改当前ListView中的数据列表
	 *
	 * @param datas 新的数据列表
	 */
	public void bindDatas(List<T> datas) {
		if (this.list == null) {
			this.list = new ArrayList<>();
		}
		list.clear();
		if (datas != null) {
			list.addAll(datas);
		} else {
			if (recyclerView instanceof AutoLoadRecyclerView) {
				((AutoLoadRecyclerView) recyclerView).hasMore(true);
			}
		}
		notifyDataSetChanged();
	}

	public void removeDatas(List<T> datas) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		if (this.list != null && this.list.size() > 0) {
			for (T object : datas) {
				this.list.remove(object);
			}
			notifyDataSetChanged();
		}
	}

	public void removeData(T e) {
		if (this.list != null && this.list.size() > 0) {
			this.list.remove(e);
			notifyDataSetChanged();
		}
	}

	public void removeData(int position) {
		if (this.list != null && this.list.size() > 0) {
			this.list.remove(position);
			notifyItemRemoved(position);
//			notifyDataSetChanged();
		}
	}

	/**
	 * 设置{@link VH}中某个View的点击事件,整个item的点击事件{@link #onItemClick(View, RecyclerView.ViewHolder, Object, int)}
	 */
	public void setHolderItemClickListener(OnRecyclerItemClickListener<T, VH> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	protected void setItemClick(View v, final VH holder) {
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = RecyclerViewUtils.getAdapterPosition(recyclerView, holder);
				T t = getItem(position);
				if (itemClickListener != null) {
					itemClickListener.onHolderItemClick(v, holder, t, position);
				} else {
					onItemClick(v, holder, t, position);
				}
			}
		});
	}

	/**
	 * 如果{@link #setHolderItemClickListener(OnRecyclerItemClickListener)}，会屏蔽此事件，
	 * 而在{@link OnRecyclerItemClickListener#onHolderItemClick(View, RecyclerView.ViewHolder, Object, int)}中调用。
	 * @param view The view be clicked.
	 * @param holder {@link VH}
	 * @param t {@link T}
	 * @param position The real clicking position without header.
	 */
	protected abstract void onItemClick(View view, VH holder, T t, int position);

	protected abstract VH createHolder(ViewGroup parent, int viewType);
}
