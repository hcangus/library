package net.hcangus.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.hcangus.mvp.present.BasePresent;
import net.hcangus.mvp.present.PtrListPresent;
import net.hcangus.mvp.view.View_PtrList;
import net.hcangus.ptr.AutoLoadListener;
import net.hcangus.ptr.PtrFrameLayout;
import net.hcangus.ptr.recycler.AutoLoadRecyclerView;

import java.util.List;

/**
 * <p>
 * 自动加载的{@link AutoLoadRecyclerView}
 * 的{@link android.support.v4.app.Fragment}基类
 * <p>注意将布局中{@link AutoLoadRecyclerView}的<em> id </em>
 * 设置为<em> R.id.autoload_recyclerView</em>
 *
 * @param <E> List中数据的类型
 *            <p>Created by hcangus
 */

public abstract class AutoLoadRecyclerFragment<E, V extends View_PtrList<E>> extends BaseFragment
		implements View_PtrList<E> {

	protected PtrFrameLayout ptrFrame;
	protected AutoLoadRecyclerView autoLoadRecyclerView;
	protected CommonRecyclerAdapter<E, ? extends RecyclerView.ViewHolder> adapter;
	protected boolean isFirst = true;

	/*创建控制器*/
	protected abstract PtrListPresent<E, V> createListPresent(Context context);

	/*创建ListView的Adapter*/
	protected abstract CommonRecyclerAdapter<E, ? extends RecyclerView.ViewHolder> createAdapter(Context context);

	@Override
	protected BasePresent createPresent(Context mContext) {
		return createListPresent(mContext);
	}

	@Override
	protected void initView(View view, Bundle savedInstanceState) {
		ptrFrame = (PtrFrameLayout) view.findViewById(R.id.ptrFrame);
		autoLoadRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.autoload_recyclerView);
		autoLoadRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
		adapter = createAdapter(mContext);
		if (adapter != null) {
			autoLoadRecyclerView.setAdapter(adapter);
		}
		autoLoadRecyclerView.setAutoLoadListener(new AutoLoadListener() {
			@Override
			public void onLoadMore(View view) {
				onLoad();
			}

			@Override
			public void onErrorClick() {
				AutoLoadRecyclerFragment.this.onErrorClick();
			}
		});
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
		isFirst = false;
		if (ptrFrame != null) {
			ptrFrame.refreshComplete();
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
		if (autoLoadRecyclerView != null) {
			autoLoadRecyclerView.onLoadMoreFailed();
		}
	}

	@Override
	public void hasMoreData(boolean hasMore) {
		autoLoadRecyclerView.hasMore(hasMore);
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

	public List<E> getListDatas() {
		return adapter == null ? null : adapter.getDatas();
	}
}
