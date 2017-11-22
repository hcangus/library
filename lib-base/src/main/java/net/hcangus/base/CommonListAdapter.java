package net.hcangus.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的List数据列表适配器的基类，带有缓存
 * 
 */
public abstract class CommonListAdapter<E> extends BaseAdapter {
	protected Context mContext;
	/** 当前适配器中的数据列表 */
	private List<E> list;

	public List<E> getDatas() {
		return list;
	}

	/** 构造函数 */
	private CommonListAdapter() {
	}

	public CommonListAdapter(Context context, List<E> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public E getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		return bindView(LayoutInflater.from(mContext), position, convertView, parent);
	}


	/**
	 * 往当前ListView中增加数据列表
	 * 
	 * @param datas
	 *            增加的数据列表
	 */
	public void addDatas(List<E> datas) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		if (this.list == null)
			this.list = new ArrayList<>();
		this.list.addAll(datas);
		notifyDataSetChanged();
	}

	public void addData(E data) {
		if (data == null) {
			return;
		}
		if (this.list == null)
			this.list = new ArrayList<>();
		list.add(data);
		notifyDataSetChanged();
	}

	/**
	 * 往当前ListView中增加数据列表
	 * 
	 * @param datas
	 *            增加的数据列表
	 * @param position
	 *            加入的位置
	 */
	public void addDatas(List<E> datas, int position) {
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

		notifyDataSetChanged();
	}

	/**
	 * 更改当前ListView中的数据列表
	 * 
	 * @param datas
	 *            新的数据列表
	 */
	public void bindDatas(List<E> datas) {

		if (this.list == null)
			this.list = new ArrayList<>();
		list.clear();
		if (datas != null && datas.size() != 0) {
			list.addAll(datas);
		}
		notifyDataSetChanged();
	}

	public void removeDatas(List<E> datas) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		if (this.list != null && this.list.size() > 0) {
			for (E object : datas) {
				this.list.remove(object);
			}
			notifyDataSetChanged();
		}
	}

	public void removeData(E e) {
		if (this.list != null && this.list.size() > 0) {
			this.list.remove(e);
			notifyDataSetChanged();
		}
	}

	public void removeData(int position) {
		if (this.list != null && this.list.size() > 0) {
			this.list.remove(position);
			notifyDataSetChanged();
		}
	}

	public abstract View bindView(LayoutInflater inflater, int position, View convertView, ViewGroup parent);

}