package net.hcangus.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hcangus.LazyViewPager.LazyFragmentPagerAdapter;

/**
 * <pre>
 *  针对用在{@link net.hcangus.LazyViewPager.LazyViewPager}中
 * 把初始化内容放到{@link LazyFragment#lazyLoad()}实现
 * 就是采用Lazy方式加载的Fragment
 * <p>
 * 注1:
 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * <p>
 * 注2:
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 * eg:
 * transaction.hide(aFragment);
 * transaction.show(aFragment);
 * <p>
 * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
 * 一般用于PagerAdapter需要同时刷新全部子Fragment的场景
 * 不要new 新的 PagerAdapter 而采取reset数据的方式
 * 所以要求Fragment重新走initData方法
 * 故使用 {@link LazyFragment#setIsforceLoad(boolean)}来让Fragment下次执行lazyload
 * Created by hcangus
 */

public abstract class LazyFragment extends BaseFragment implements LazyFragmentPagerAdapter.Laziable{

	/**
	 * 是否可见状态 为了避免和{@link Fragment#isVisible()}冲突 换个名字
	 */
	public boolean isFragmentVisible = false;
	/**
	 * 标志位，View已经初始化完成。
	 */
	protected boolean isPrepared = false;

	/**
	 * <pre>
	 * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
	 * 一般用于PagerAdapter需要刷新各个子Fragment的场景
	 * 不要new 新的 PagerAdapter 而采取reset数据的方式
	 * </pre>
	 */
	public boolean isforceLoad = false;

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		isPrepared = true;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		isPrepared = false;
		super.onDestroyView();
	}

	/**
	 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
	 *
	 * @param isVisibleToUser 是否显示出来了
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (!getUserVisibleHint()&& isVisibleToUser) {
			onVisible();
		} else if (getUserVisibleHint() && !isVisibleToUser){
			onInvisible();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
	 * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
	 *
	 * @param hidden hidden True if the fragment is now hidden, false if it is not
	 *               visible.
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			onVisible();
		} else {
			onInvisible();
		}
	}

	protected void onVisible() {
		isFragmentVisible = true;
		checkToLazyLoad();
	}

	protected void onInvisible() {
		isFragmentVisible = false;
	}


	/**
	 * 判断状态，是否延时加载
	 */
	private void checkToLazyLoad() {
		if (isPrepared && isFragmentVisible) {
			if (isforceLoad || isFirstLoad) {
				isforceLoad = false;
				isFirstLoad = false;
				lazyLoad();
			}
		}
	}

	protected abstract void lazyLoad();

	/**
	 * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
	 */
	public void setIsforceLoad(boolean isforceLoad) {
		this.isforceLoad = isforceLoad;
	}
}
