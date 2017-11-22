package net.hcangus.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import net.hcangus.http.HttpError;
import net.hcangus.http.HttpManage;
import net.hcangus.loadretry.DefaultLoadingRetryListener;
import net.hcangus.loadretry.LoadingAndRetryManager;
import net.hcangus.mvp.present.BasePresent;
import net.hcangus.mvp.view.BaseView;
import net.hcangus.statusbar.StatusBarCompat;
import net.hcangus.util.AppManager;
import net.hcangus.util.DeviceUtil;
import net.hcangus.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Anydoor
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseView, HttpManage.HttpImpl {

	protected TitleBar mTitleBar;
	protected BasePresent mPresent;
	public LoadingAndRetryManager mManager;
	/*是否设置标题栏的标志位*/
	protected boolean FLAG_STATUSBAR = true;

	private List<View> notHideKeyboardViews = new ArrayList<>();

	//布局文件ID
	protected abstract int getContentViewId();

	//布局中Fragment的ID
	protected abstract int getFragmentContentId();

	/*设置控件*/
	protected abstract void setupViews();

	protected abstract BasePresent createPresent(Context context);

	//添加fragment,返回可返回上一个fragment
	public void addFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			int entryCount = getSupportFragmentManager().getBackStackEntryCount();
			if (entryCount > 0) {
				FragmentManager.BackStackEntry backStackEntryAt = getSupportFragmentManager()
						.getBackStackEntryAt(entryCount - 1);
				String name = backStackEntryAt.getName();
				Fragment lastFragment = getSupportFragmentManager().findFragmentByTag(name);
				if (lastFragment != null) {
					transaction.hide(lastFragment);
				}
			}
			transaction.add(getFragmentContentId(), fragment, fragment.getClass().getSimpleName());
			transaction.addToBackStack(fragment.getClass().getSimpleName());
			transaction.commitAllowingStateLoss();
		}
	}

	public void addFragmentForResult(@NonNull Fragment toFragment, @NonNull Fragment currentFragment, int requestCode) {
		toFragment.setTargetFragment(currentFragment, requestCode);
		addFragment(toFragment);
	}

	//直接替换上一个fragment
	public void replaceFragment(Fragment fragment) {
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
					.commitAllowingStateLoss();
		}
	}

	//移除fragment
	public void removeFragment() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			getSupportFragmentManager().popBackStack();
		} else {
			finish();
		}
	}

	public void backTo(Fragment fragment) {
		if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			getSupportFragmentManager().popBackStack(fragment.getClass().getSimpleName(), 0);
		} else {
			finish();
		}
	}

	public void backTo(String name) {
		if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			getSupportFragmentManager().popBackStack(name, 0);
		} else {
			finish();
		}
	}

	/*进行一些数据初始化*/
	protected void initData() {

	}

	//获取Intent
	protected void handleIntent(Intent intent) {

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
		// should be in launcher activity, but all app use this can avoid the problem
		if (!isTaskRoot()) {
			Intent intent = getIntent();
			String action = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;
			}
		}
		AppManager.getInstance().addActivity(this);
		if (null != getIntent()) {
			handleIntent(getIntent());
		}
		initData();
		mPresent = createPresent(this);
		setContentView(getContentViewId());
		ButterKnife.bind(this);
		setStatusBar();
		initTitleBar();
		setupViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mManager == null) {
			mManager = createViewManager();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * <pre>
	 * 创建显示加载、重试、空白页面的管理器
	 * </pre>
	 */
	protected LoadingAndRetryManager createViewManager() {
		return null;//LoadingAndRetryManager.generate(this, new DefaultLoadingRetryListener(this));
	}

	/**
	 * <li>设置状态栏颜色</li>
	 */
	public final void setStatusBar() {
		if (FLAG_STATUSBAR) {
			setStatusBar(getStatusBarColor());
		}
	}

	public final void setStatusBarForCollapsingToolbar(AppBarLayout appBarLayout,
													   CollapsingToolbarLayout collapsingToolbarLayout,
													   Toolbar toolbar, @ColorRes int statusColor) {
		currentColorId = -2;
		StatusBarCompat.setStatusBarColorForCollapsingToolbar(this,
				appBarLayout, collapsingToolbarLayout, toolbar,
				ContextCompat.getColor(this, statusColor));
	}

	//定义一个当前的颜色，避免重复设置颜色
	protected int currentColorId = -1;

	/**
	 * <li>设置状态栏颜色</li>
	 *
	 * @param colorId 颜色的ID， 如果为 0 ，状态栏透明；否则改变颜色
	 */
	public final void setStatusBar(@ColorRes int colorId) {
		if (currentColorId == colorId) {
			return;
		}
		currentColorId = colorId;
		if (colorId == 0) {
			StatusBarCompat.translucentStatusBar(this);
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				StatusBarCompat.setStatusBarColor(this, getResources().getColor(colorId, getTheme()));
			} else {
				//noinspection deprecation
				StatusBarCompat.setStatusBarColor(this, getResources().getColor(colorId));
			}
		}
	}

	/**
	 * 获取状态栏要设置的颜色，重写该方法可以修改单个Activity的颜色
	 *
	 * @return {@link net.hcangus.base.R.color},大于0的值会设置为状态栏的颜色；
	 * 0  设置为透明
	 */
	protected int getStatusBarColor() {
		return R.color.c_bg_main;
	}

	private View.OnClickListener onLeftClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onLeftClick(v);
		}
	};
	private View.OnClickListener onRightClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onRightClick(v);
		}
	};

	private void initTitleBar() {
		mTitleBar = (TitleBar) findViewById(R.id.titleBar);
		if (mTitleBar != null) {
			mTitleBar.setLeftOnClickListener(onLeftClickListener);
			mTitleBar.setRightOnClickListener(onRightClickListener);
		}
	}

	protected void onLeftClick(View leftContainer) {
		removeFragment();
	}

	protected void onRightClick(View rightContainer) {
	}

	@Override
	public void showLoading() {
		if (mManager != null) {
			mManager.showLoading();
		}
	}

	@Override
	public boolean isShowLoading() {
		return mManager != null && mManager.isShowLoading();
	}

	@Override
	public boolean isShowEmptyOrRetry() {
		return mManager != null && mManager.isShowEmptyOrRetry();
	}

	@Override
	public void hideLoading() {
		if (mManager != null) {
			mManager.showContent();
		}
	}

	@Override
	public void showError(int errCode, String msg) {
		if (mManager != null) {
			if (errCode == HttpError._NoNet_Code) {
				mManager.showNoNet();
			} else if (errCode == 10012) {
				mManager.showEmpty();
			} else {
				if (mManager.getListener() instanceof DefaultLoadingRetryListener) {
					((DefaultLoadingRetryListener) mManager.getListener()).setRetryText(msg);
				}
				mManager.showRetry();
			}
		}
	}

	@Override
	public void onRetry() {
		if (mPresent != null) {
			showLoading();
			mPresent.loadData();
		}
	}

	@Override
	public void showEmpty() {
		if (mManager != null) {
			mManager.showEmpty();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPresent != null) {
			mPresent.detachView();
		}
		mManager = null;
		notHideKeyboardViews.clear();
		notHideKeyboardViews = null;
		AppManager.getInstance().removeActivity(this);
	}

	//返回键返回事件
	protected boolean exitOnceClick = true;//点击直接退出

	@Override
	public boolean  onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (exitOnceClick && getSupportFragmentManager().getBackStackEntryCount() == 1) {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void addNotHideKeyboardView(View view) {
		if (view != null) {
			notHideKeyboardViews.add(view);
		}
	}

	public void removeNotHideKeyboardView(View view) {
		if (view != null) {
			notHideKeyboardViews.remove(view);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			boolean keyboardShowing = DeviceUtil.isKeyboardShowing(this);
			if (keyboardShowing && isShouldHideKeyboard(ev)) {
				DeviceUtil.hideSoftInputView(this);
			}
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
	}

	protected boolean isShouldHideKeyboard(MotionEvent event) {
		if (notHideKeyboardViews.size() > 0) {
			for (View view : notHideKeyboardViews) {
				int[] l = {0, 0};
				view.getLocationInWindow(l);
				int left = l[0],
						top = l[1],
						bottom = top + view.getHeight(),
						right = left + view.getWidth();
				boolean b = event.getX() > left && event.getX() < right
						&& event.getY() > top && event.getY() < bottom;
				if (b) {
					return false;
				}
			}
			return true;
		} else {
			View currentFocus = getCurrentFocus();
			if (currentFocus != null && currentFocus instanceof EditText) {
				int[] l = {0, 0};
				currentFocus.getLocationInWindow(l);
				int left = l[0],
						top = l[1],
						bottom = top + currentFocus.getHeight(),
						right = left + currentFocus.getWidth();

				return !(event.getX() > left && event.getX() < right
						&& event.getY() > top && event.getY() < bottom);
			}
		}
		return false;
	}
}
