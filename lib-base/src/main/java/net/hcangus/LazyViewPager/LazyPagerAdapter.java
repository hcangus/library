package net.hcangus.LazyViewPager;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * PagerAdapter that items added lazily
 * @param <T>
 */
abstract class LazyPagerAdapter<T> extends PagerAdapter {

    SparseArray<T> mLazyItems = new SparseArray<T>();
    private T mCurrentItem;
	private int lastPrimaryPosition = -1;

    /**
     * add lazy item to container
     * @param container {@link LazyViewPager}
     * @param position the position that the item added to
     * @return the item added
     */
    protected abstract T addLazyItem(ViewGroup container, int position);

    /**
     * get the lazy item
     * @param container {@link LazyViewPager}
     * @param position the position of lazy item
     * @return the lazy item
     */
    protected abstract T getItem(ViewGroup container, int position);

    /**
     * whether the item is lazily or not
     * @param position the position of item
     * @return the item is lazily
     */
    public boolean isLazyItem(int position) {
        return mLazyItems.get(position) != null;
    }

    /**
     * get the current item
     * @return the current item
     */
    public T getCurrentItem() {
        return mCurrentItem;
    }


	/**
     * call {@link LazyPagerAdapter#addLazyItem(ViewGroup, int)}
     * to prevent {@link LazyViewPager#onPageScrolled(int, float, int)} not working when the offset of {@link LazyViewPager} is too big
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (position !=lastPrimaryPosition) {
			lastPrimaryPosition = position;
			mCurrentItem = addLazyItem(container, position);
		}
    }
}
