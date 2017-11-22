package net.hcangus.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.hcangus.mvp.present.BasePresent;
import net.hcangus.mvp.present.PtrListPresent;
import net.hcangus.mvp.view.View_PtrList;
import net.hcangus.ptr.recycler.AutoLoadRecyclerView;
import net.hcangus.widget.PtrAutoLoadRecyclerView;

import java.util.List;

/**
 * SCRM 下拉刷新和自动加载的{@link net.hcangus.widget.PtrAutoLoadRecyclerView}的{@link LazyFragment}基类
 * <p>注意将布局中{@link net.hcangus.widget.PtrAutoLoadRecyclerView}的<em> id </em>
 * 设置为<em> R.id.ptr_autoload_recyclerView</em>
 *
 * @param <E> List中数据的类型
 *            <p>Created by Administrator on 2017/4/5.
 */

public abstract class PtrRecyclerLazyFragment<E, V extends View_PtrList<E>>
		extends LazyFragment implements View_PtrList<E> {
	protected PtrAutoLoadRecyclerView ptrAutoLoadRecyclerView;
	protected CommonRecyclerAdapter<E, ? extends RecyclerView.ViewHolder> adapter;

	/*创建控制器*/
	protected abstract PtrListPresent<E, V> createListPresent(Context context);

	/*创建ListView的Adapter*/
	protected abstract CommonRecyclerAdapter<E, ? extends RecyclerView.ViewHolder> createAdapter(Context context);

	public void autoRefresh() {
		ptrAutoLoadRecyclerView.autoRefresh();
	}

	@Override
	protected void initView(View view, Bundle savedInstanceState) {
		ptrAutoLoadRecyclerView = (PtrAutoLoadRecyclerView) view.findViewById(R.id.ptr_autoload_recyclerView);
		adapter = createAdapter(mContext);
		if (adapter != null) {
			ptrAutoLoadRecyclerView.setAdapter(adapter);
		}
		ptrAutoLoadRecyclerView.setOnRefreshListener(this);
	}

	public void setCanRefresh(boolean canRefresh) {
		ptrAutoLoadRecyclerView.setCanRefresh(canRefresh);
	}

	public AutoLoadRecyclerView getRecyclerView() {
		return ptrAutoLoadRecyclerView == null ? null : ptrAutoLoadRecyclerView.getRecyclerView();
	}

	@Override
	protected BasePresent createPresent(Context mContext) {
		return createListPresent(mContext);
	}

	@Override
	public void onRefresh() {
		if (mPresent != null) {
			((PtrListPresent) mPresent).onRefresh();
		}
	}

	@Override
	public void onLoad() {
		if (mPresent != null) {
			((PtrListPresent) mPresent).onLoad();
		}
	}

	@Override
	public void onGetListFail(String errMsg) {

	}

	@Override
	public void onRefreshComplete(List<E> list) {
		if (ptrAutoLoadRecyclerView != null) {
			ptrAutoLoadRecyclerView.onRefreshComplete();
		}
		if (adapter != null) {
			adapter.bindDatas(list);
		}
	}

	@Override
	public void onLoadComplete(List<E> list) {
		if (adapter != null) {
			adapter.addDatas(list);
		}
	}

	@Override
	public void onloadFail() {
		if (getRecyclerView() != null) {
			getRecyclerView().onLoadMoreFailed();
		}
	}

	@Override
	public void hasMoreData(boolean hasMore) {
		ptrAutoLoadRecyclerView.hasMore(hasMore);
	}

	@Override
	public void onRetry() {
		if (mPresent != null) {
			showLoading();
			((PtrListPresent) mPresent).onRefresh();
		}
	}

	@Override
	public void onErrorClick() {
		if (mPresent != null) {
			showLoading();
			mPresent.loadData();
		}
	}
}
