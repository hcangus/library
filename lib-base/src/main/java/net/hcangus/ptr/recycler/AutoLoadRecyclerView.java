package net.hcangus.ptr.recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import net.hcangus.decoration.HorizontalDividerItemDecoration;
import net.hcangus.decoration.VerticalDividerItemDecoration;
import net.hcangus.divider.DividerBuilder;
import net.hcangus.divider.DividerItemDecoration;
import net.hcangus.divider.Layer;
import net.hcangus.divider.LayersBuilder;
import net.hcangus.divider.selector.AllGroupSelector;
import net.hcangus.divider.selector.AllItemsSelector;
import net.hcangus.divider.selector.GridSelector;
import net.hcangus.divider.selector.WithoutHeaderFooterSelector;
import net.hcangus.ptr.AutoLoadListener;
import net.hcangus.ptr.LoadingFooter;

import java.util.Collection;


/**
 * <p/>
 * 能够自动加载和添加Header、Footer的RecyclerView。
 * Created by Administrator on 2017/3/30.
 * 在获取item的position时,
 * {@link RecyclerViewUtils#getAdapterPosition(RecyclerView, ViewHolder)},
 * {@link RecyclerViewUtils#getLayoutPosition(RecyclerView, ViewHolder)}
 *
 * <p/>
 *     添加分割线：{@link #addItemDecoration(ItemDecoration)}
 *     <lt>{@link HorizontalDividerItemDecoration.Builder#drawable(int)} }</lt>
 *     <li> new HorizontalDividerItemDecoration.Builder(getContext()).colorResId(resId).sizeResId(sizeId).marginResId(margin).build()</li>
 *     <li> new VerticalDividerItemDecoration.Builder(getContext()).colorResId(resId).sizeResId(sizeId).build();</li>
 *
 *	  <code>Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
			Layer itemLayer = new Layer(new AllItemsSelector(), DividerBuilder.from(drawable).build());
 			Layer groupLayer = new Layer(new AllGroupSelector(), DividerBuilder.fromEmpty().build());
 			Collection<Layer> build = LayersBuilder.with(itemLayer, groupLayer).build();
 			recyclerView.addItemDecoration(new DividerItemDecoration(build));</code>

 *	  <li>int size = getResources().getDimensionPixelSize(sizeId);
 			GradientDrawable drawable = new GradientDrawable();
 			drawable.setColor(ContextCompat.getColor(getContext(), resId));
 			drawable.setSize(size, size);
 			Layer itemLayer = new Layer(new GridSelector(), DividerBuilder.from(drawable).build());
 			recyclerView.addItemDecoration(new DividerItemDecoration(itemLayer));
 		</li>
 */

public class AutoLoadRecyclerView extends RecyclerView {
	private AutoLoadListener autoLoadListener;
	private EndlessRecyclerOnScrollListener scrollListener;
	private HeaderFooterRecyclerAdapter headerAndFooterAdapter;

	public AutoLoadRecyclerView(Context context) {
		this(context, null, 0);
	}

	public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (adapter instanceof HeaderFooterRecyclerAdapter) {
			headerAndFooterAdapter = (HeaderFooterRecyclerAdapter) adapter;
		} else {
			headerAndFooterAdapter = new HeaderFooterRecyclerAdapter(adapter);
		}
		super.setAdapter(headerAndFooterAdapter);
	}

	public HeaderFooterRecyclerAdapter getOutAdapter() {
		return headerAndFooterAdapter;
	}

	public int getHeaderViewsCount() {
		return headerAndFooterAdapter.getHeaderViewsCount();
	}

	public int getFooterViewsCount() {
		return headerAndFooterAdapter.getFooterViewsCount();
	}

	/**
	 * 设置了{@link HeaderFooterRecyclerAdapter}后才能生效
	 */
	public void addHeadView(View view) {
		if (headerAndFooterAdapter != null) {
			headerAndFooterAdapter.addHeaderView(view);
		} else {
			throw new RuntimeException("You must set a HeaderFooterRecyclerAdapter for an AutoLoadRecyclerView");
		}
	}

	public void removeHeadView(View view) {
		if (headerAndFooterAdapter != null) {
			headerAndFooterAdapter.removeHeaderView(view);
		}
	}

	/**
	 * 设置了{@link HeaderFooterRecyclerAdapter}后才能生效
	 */
	public void addFootView(View view) {
		if (headerAndFooterAdapter != null) {
			headerAndFooterAdapter.addFooterView(view);
		} else {
			throw new RuntimeException("You must set a HeaderFooterRecyclerAdapter for a AutoLoadRecyclerView");
		}
	}

	public void removeFootView(View view) {
		if (headerAndFooterAdapter != null) {
			headerAndFooterAdapter.removeFooterView(view);
		}
	}

	public void setAutoLoadListener(AutoLoadListener autoLoadListener) {
		this.autoLoadListener = autoLoadListener;
		addScrollListener();
		setCanAutoLoad(true);
		RecyclerView.Adapter adapter = getAdapter();
		if (adapter != null && adapter instanceof HeaderFooterRecyclerAdapter) {
			HeaderFooterRecyclerAdapter outAdapter = (HeaderFooterRecyclerAdapter) adapter;
			LoadingFooter footerView = new LoadingFooter(getContext());
			footerView.setState(LoadingFooter.State.Normal);
			outAdapter.addLoadingFooter(footerView);
		} else {
			throw new IllegalArgumentException("Please set an adapter before this method!");
		}
	}

	/**
	 * 设置是否能自动加载
	 */
	public void setCanAutoLoad(boolean isAutoLoad) {
		if (scrollListener != null) {
			scrollListener.setCanAutoLoad(isAutoLoad);
		}
	}

	/**
	 * 设置是否能够加载，只有当能自动加载且能加载时，滑动
	 * 到底部才进行加载
	 */
	public void setCanLoad(boolean isCanLoad) {
		if (scrollListener != null) {
			scrollListener.setCanLoad(isCanLoad);
		}
	}

	private void addScrollListener() {
		if (scrollListener == null) {
			scrollListener = new EndlessRecyclerOnScrollListener() {
				@Override
				public void onLoadMore(View view) {
					if (autoLoadListener != null) {
						autoLoadListener.onLoadMore(view);
					}
				}
			};
			addOnScrollListener(scrollListener);
		}
	}

	private LoadingFooter getFootView() {
		//已经有footerView了
		if (headerAndFooterAdapter != null && headerAndFooterAdapter.getFooterViewsCount() > 0) {
			return (LoadingFooter) headerAndFooterAdapter.getFooterView();
		}
		return null;
	}

	/**
	 * 当加载失败
	 */
	public void onLoadMoreFailed() {
		LoadingFooter footerView = getFootView();
		if (footerView != null) {
			scrollToPosition(getAdapter().getItemCount() - 1);
			footerView.setState(LoadingFooter.State.Error);
			footerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (autoLoadListener != null) {
						autoLoadListener.onErrorClick();
					}
				}
			});
		}
	}

	/**
	 * 是否有更多数据
	 */
	public void hasMore(final boolean hasMore) {
		setCanLoad(hasMore);
		final LoadingFooter footer = getFootView();
		if (footer != null) {
			int recyclerLastVisible = RecyclerViewUtils.getRecyclerLastVisible(this);
			if (recyclerLastVisible < 0) {
				getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int recyclerLastVisible = RecyclerViewUtils.getRecyclerLastVisible(AutoLoadRecyclerView.this);
						if (recyclerLastVisible >= 0) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								getViewTreeObserver().removeOnGlobalLayoutListener(this);
							} else {
								getViewTreeObserver().removeGlobalOnLayoutListener(this);
							}
							setFootView(hasMore, footer);
						}
					}
				});
			} else {
				setFootView(hasMore, footer);
			}
		}
	}

	private void setFootView(boolean hasMore, final LoadingFooter footer) {
		if (!hasMore && isOutScreen()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					footer.setState(LoadingFooter.State.TheEnd);
				}
			}, 500);
		} else {
			footer.setState(LoadingFooter.State.Normal);
		}
	}

	/**
	 * 判断RecyclerView是否超出屏幕
	 */
	protected boolean isOutScreen() {
		int childCount = getLayoutManager().getChildCount();
		int itemCount = getLayoutManager().getItemCount();

		int recyclerLastCompletelyVisible = RecyclerViewUtils.getRecyclerLastCompletelyVisible(this);
		int recyclerLastVisible = RecyclerViewUtils.getRecyclerLastVisible(this);

//		return childCount < itemCount || recyclerLastCompletelyVisible < recyclerLastVisible;
		return recyclerLastCompletelyVisible < recyclerLastVisible;
	}

	/**
	 * 设置RecyclerView的水平分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId  分割线的资源ID
	 * @param sizeId 分割线的高度
	 */
	public void setDivider(@ColorRes int resId, @DimenRes int sizeId) {
		HorizontalDividerItemDecoration build = new HorizontalDividerItemDecoration.Builder(getContext()).
				colorResId(resId).sizeResId(sizeId).build();
		addItemDecoration(build);
	}

	/**
	 * 设置RecyclerView的水平分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId       分割线的资源ID
	 * @param sizeId      分割线的高度
	 * @param leftMargin  左间距
	 * @param rightMargin 右间距
	 */
	public void setDivider(@ColorRes int resId, @DimenRes int sizeId,
											  @DimenRes int leftMargin, @DimenRes int rightMargin) {
		HorizontalDividerItemDecoration build = new HorizontalDividerItemDecoration.Builder(getContext()).
				colorResId(resId).sizeResId(sizeId).marginResId(leftMargin, rightMargin).build();
		addItemDecoration(build);
	}

	/**
	 * 设置RecyclerView的水平分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId       分割线的资源ID
	 * @param sizeId      分割线的高度
	 * @param margin  左右间距
	 */
	public void setDivider(@ColorRes int resId, @DimenRes int sizeId,
								@DimenRes int margin) {
		HorizontalDividerItemDecoration build = new HorizontalDividerItemDecoration.Builder(getContext()).
				colorResId(resId).sizeResId(sizeId).marginResId(margin).build();
		addItemDecoration(build);
	}

	/**
	 * 设置RecyclerView的垂直分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId  分割线的资源ID
	 * @param sizeId 分割线的高度
	 */
	public void setVerticalDivider(@ColorRes int resId, @DimenRes int sizeId) {
		VerticalDividerItemDecoration build = new VerticalDividerItemDecoration.Builder(getContext())
				.colorResId(resId).sizeResId(sizeId).build();
		addItemDecoration(build);
	}

	/**
	 * 设置RecyclerView的垂直分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId  分割线的资源ID
	 * @param sizeId 分割线的高度
	 */
	public void setVerticalDivider(@ColorRes int resId, @DimenRes int sizeId, @DimenRes int margin) {
		VerticalDividerItemDecoration build = new VerticalDividerItemDecoration.Builder(getContext())
				.colorResId(resId).sizeResId(sizeId).marginResId(margin).build();
		addItemDecoration(build);
	}

	/**
	 * 设置RecyclerView的垂直分割线,适用于不包含Header和Footer的RecyclerView
	 *
	 * @param resId  分割线的资源ID
	 * @param sizeId 分割线的高度
	 */
	public void setVerticalDivider(@ColorRes int resId, @DimenRes int sizeId, @DimenRes int topMargin, @DimenRes int bottomMargin) {
		VerticalDividerItemDecoration build = new VerticalDividerItemDecoration.Builder(getContext())
				.colorResId(resId).sizeResId(sizeId).marginResId(topMargin,bottomMargin).build();
		addItemDecoration(build);
	}

	/**
	 * 设置Grid分割线
	 *
	 * @param resId  分割线的资源ID
	 * @param sizeId 分割线的高度
	 */
	public void setGridDivider(@ColorRes int resId, @DimenRes int sizeId) {
		int size = getResources().getDimensionPixelSize(sizeId);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(ContextCompat.getColor(getContext(), resId));
		drawable.setSize(size, size);
		Layer itemLayer = new Layer(new GridSelector(), DividerBuilder.from(drawable).build());
		addItemDecoration(new DividerItemDecoration(itemLayer));
	}

	/**
	 * 设置Grid分割线
	 *
	 * @param resId 分割线的资源ID
	 */
	public void setGridDivider(@DrawableRes int resId) {
		Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
		Layer itemLayer = new Layer(new AllItemsSelector(), DividerBuilder.from(drawable).build());
		Layer groupLayer = new Layer(new AllGroupSelector(), DividerBuilder.fromEmpty().build());
		Collection<Layer> build = LayersBuilder.with(itemLayer, groupLayer).build();
		addItemDecoration(new DividerItemDecoration(build));
	}

	/**
	 * 设置Divider，其中的Header和Footer没有分割线
	 * @param resId 分割线的资源ID
	 */
	public void setWithoutHeadFootDivider(@DrawableRes int resId) {
		Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
		Layer layer = new Layer(new WithoutHeaderFooterSelector(this), DividerBuilder.from(drawable).build());
		addItemDecoration(new DividerItemDecoration(layer));
	}

	/**
	 * 设置Divider，其中的Header和Footer没有分割线
	 * @param resId 分割线的资源ID
	 * @param sizeId 分割线的宽或高
	 */
	public void setWithoutHeadFootDivider(@ColorRes int resId, @DimenRes int sizeId) {
		int size = getResources().getDimensionPixelSize(sizeId);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(ContextCompat.getColor(getContext(), resId));
		drawable.setSize(size, size);
		Layer layer = new Layer(new WithoutHeaderFooterSelector(this), DividerBuilder.from(drawable).build());
		addItemDecoration(new DividerItemDecoration(layer));
	}
}
