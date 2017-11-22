package net.hcangus.LazyViewPager;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class LazyFragmentPagerAdapter extends LazyPagerAdapter<Fragment> {

	private static final String TAG = "LazyFragmentAdapter";

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;
	private SparseBooleanArray updateArray;

	public LazyFragmentPagerAdapter(FragmentManager fm) {
		mFragmentManager = fm;
	}

	@Override
	public void startUpdate(ViewGroup container) {
	}

	@SuppressLint("CommitTransaction")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		Log.w(TAG,"--- instantiateITem position = #"+position);

		final long itemId = getItemId(position);

		// Do we already have this fragment?
		String name = makeFragmentName(container.getId(), itemId);
		Fragment fragment = mFragmentManager.findFragmentByTag(name);
		if (fragment != null) {
			mCurTransaction.attach(fragment);
		} else {
			fragment = getItem(container, position);
			if (fragment instanceof Laziable) {
				mLazyItems.put(position, fragment);
			} else {
				mCurTransaction.add(container.getId(), fragment, name);
			}
		}
		if (fragment != getCurrentItem()) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		return fragment;
	}

	@SuppressLint("CommitTransaction")
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}

		final long itemId = getItemId(position);
		String name = makeFragmentName(container.getId(), itemId);
		if (mFragmentManager.findFragmentByTag(name) == null) {
			mCurTransaction.detach((Fragment) object);
		} else {
            mLazyItems.remove(position);
		}
	}

    @SuppressLint("CommitTransaction")
	@Override
	public Fragment addLazyItem(ViewGroup container, int position) {
		Fragment fragment = mLazyItems.get(position);
		if (fragment == null){
			return null;
		}

		final long itemId = getItemId(position);
		String name = makeFragmentName(container.getId(), itemId);
		if (mFragmentManager.findFragmentByTag(name) == null) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			mCurTransaction.add(container.getId(), fragment, name);
			mLazyItems.remove(position);
		}
		return fragment;
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitNowAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
		if (updateArray == null) {
			updateArray = new SparseBooleanArray();
		}

		for (int i = 0; i < getCount(); i++) {
			final long itemId = getItemId(i);
			String name = makeFragmentName(container.getId(), itemId);
			Fragment tag = mFragmentManager.findFragmentByTag(name);
			if (tag != null) {
				boolean isVisibleToUser = ((LazyViewPager) container).getCurrentItem() == i;
				if (updateArray.get(i) != isVisibleToUser) {
					updateArray.put(i, isVisibleToUser);
					tag.setUserVisibleHint(isVisibleToUser);
				}
			}
		}
	}

    @Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	private long getItemId(int position) {
		return position;
	}

	private static String makeFragmentName(int viewId, long id) {
		return "android:switcher:" + viewId + ":" + id;
	}

    /**
     * mark the fragment can be added lazily
     */
    public interface Laziable {
	}

}