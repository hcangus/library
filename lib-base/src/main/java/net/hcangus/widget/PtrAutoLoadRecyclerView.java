package net.hcangus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import net.hcangus.base.R;
import net.hcangus.itf.OnRefreshListener;
import net.hcangus.ptr.AutoLoadListener;
import net.hcangus.ptr.PtrFrameLayout;
import net.hcangus.ptr.PtrHandler;
import net.hcangus.ptr.header.MaterialHeader;
import net.hcangus.ptr.recycler.AutoLoadRecyclerView;
import net.hcangus.util.DeviceUtil;

/**
 * Anydoor
 * Created by Administrator
 */

public class PtrAutoLoadRecyclerView extends LinearLayout {
	AutoLoadRecyclerView recyclerView;
	PtrFrameLayout ptrFrame;

	private boolean canRefresh = true;

	public PtrAutoLoadRecyclerView(Context context) {
		this(context, null, 0);
	}

	public PtrAutoLoadRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PtrAutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.layout_ptrload_recyclerview, this);
		recyclerView = (AutoLoadRecyclerView) findViewById(R.id.recyclerView);
		ptrFrame = (PtrFrameLayout) findViewById(R.id.ptrFrame);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
//		StoreHouseHeader header = new StoreHouseHeader(getContext());
//		header.setPadding(0, DeviceUtil.dp2px(getContext(), 16), 0, 0);
//		header.initWithString(getContext().getString(R.string.app_name));
//		ptrFrame.setHeaderView(header);
//		ptrFrame.addPtrUIHandler(header);

		MaterialHeader materialHeader = new MaterialHeader(getContext());
		int[] colors = new int[]{ContextCompat.getColor(getContext(), android.R.color.holo_red_light),
				ContextCompat.getColor(getContext(), android.R.color.holo_blue_light),
				ContextCompat.getColor(getContext(), android.R.color.holo_green_light),
				ContextCompat.getColor(getContext(), android.R.color.holo_orange_light)};
		materialHeader.setColorSchemeColors(colors);
		materialHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		materialHeader.setPadding(0, DeviceUtil.dp2px(getContext(), 14), 0, DeviceUtil.dp2px(getContext(), 10));
		materialHeader.setPtrFrameLayout(ptrFrame);
		ptrFrame.setLoadingMinTime(1000);
		ptrFrame.setDurationToCloseHeader(1500);
		ptrFrame.setHeaderView(materialHeader);
		ptrFrame.addPtrUIHandler(materialHeader);
		ptrFrame.setPinContent(false);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PtrAutoLoadRecyclerView);
		Drawable background = a.getDrawable(R.styleable.PtrAutoLoadRecyclerView_ptr_background);
		if (background != null) {
			recyclerView.setBackgroundDrawable(background);
		}
		a.recycle();
	}

	public AutoLoadRecyclerView getRecyclerView() {
		return recyclerView;
	}

	public void setCanRefresh(boolean canRefresh) {
		this.canRefresh = canRefresh;
	}

	public void setCanAutoLoad(boolean canAutoLoad) {
		recyclerView.setCanAutoLoad(canAutoLoad);
	}

	/**
	 * 设置下拉刷新和自动加载时的回调方法
	 *
	 * @param listener The callback that will run
	 */
	public void setOnRefreshListener(@NonNull final OnRefreshListener listener) {
		ptrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return canRefresh && !ViewCompat.canScrollVertically(recyclerView, -1);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				recyclerView.setCanLoad(false);//正在刷新时，不进行加载
				listener.onRefresh();
			}
		});
		recyclerView.setAutoLoadListener(new AutoLoadListener() {
			@Override
			public void onLoadMore(View listView) {
				listener.onLoad();
			}

			@Override
			public void onErrorClick() {
				listener.onErrorClick();
			}
		});
	}

	public void setAdapter(RecyclerView.Adapter adapter) {
		recyclerView.setAdapter(adapter);
	}

	/**
	 * 设置背景色
	 *
	 * @param colorId 颜色的资源ID
	 * @return {@link PtrAutoLoadRecyclerView}
	 */
	public PtrAutoLoadRecyclerView setBackground(@ColorRes int colorId) {
		recyclerView.setBackgroundResource(colorId);
		return this;
	}

	/**
	 * 设置ListView的Padding
	 *
	 * @param leftDp  padding left in dp
	 * @param topDp   padding top in dp
	 * @param rightDp padding right in dp
	 * @param botDp   padding bottom in dp
	 * @return {@link PtrAutoLoadRecyclerView}
	 */
	public PtrAutoLoadRecyclerView setListPadding(int leftDp, int topDp, int rightDp, int botDp) {
		int leftPx = DeviceUtil.dp2px(getContext(), leftDp);
		int topPx = DeviceUtil.dp2px(getContext(), topDp);
		int rightPx = DeviceUtil.dp2px(getContext(), rightDp);
		int botPx = DeviceUtil.dp2px(getContext(), botDp);
		recyclerView.setPadding(leftPx, topPx, rightPx, botPx);
		return this;
	}

	public void addHeaderView(View view) {
		recyclerView.addHeadView(view);
	}

	public void addFooterView(View view) {
		recyclerView.addFootView(view);
	}

	/**
	 * 200ms延时后自动刷新
	 */
	public void autoRefresh() {
		ptrFrame.postDelayed(new Runnable() {
			@Override
			public void run() {
				ptrFrame.autoRefresh();
			}
		}, 200);
	}

	/**
	 * 下拉刷新完成
	 */
	public void onRefreshComplete() {
		ptrFrame.refreshComplete();
	}

	/**
	 * 加载更多失败
	 */
	public void loadMoreFailed() {
		recyclerView.onLoadMoreFailed();
	}

	/**
	 * 是否有更多数据
	 */
	public void hasMore(boolean hasMore) {
		recyclerView.hasMore(hasMore);
	}

}
