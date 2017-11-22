package net.hcangus.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hcangus.http.HttpError;
import net.hcangus.http.HttpManage;
import net.hcangus.itf.Action;
import net.hcangus.itf.ActionCallBack;
import net.hcangus.loadretry.DefaultLoadingRetryListener;
import net.hcangus.loadretry.LoadingAndRetryManager;
import net.hcangus.mvp.present.BasePresent;
import net.hcangus.mvp.view.BaseView;
import net.hcangus.permission.PermissionUtil;
import net.hcangus.tips.Tips;
import net.hcangus.util.DialogUtil;
import net.hcangus.widget.TitleBar;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Anydoor
 * Created by hcangus
 */

public abstract class BaseFragment extends Fragment
		implements BaseView, HttpManage.HttpImpl {

	protected TitleBar mTitleBar;
	protected Context mContext;
	protected BaseActivity mActivity;
	protected BasePresent mPresent;
	protected Unbinder mUnbinder;
	protected LoadingAndRetryManager mManager;
	protected View rootView;
	/**
	 * 是否第一次加载
	 */
	protected boolean isFirstLoad = true;

	protected abstract void initView(View view, Bundle savedInstanceState);

	//获取fragment布局文件ID
	protected abstract int getLayoutId();

	//创建Present
	protected abstract BasePresent createPresent(Context mContext);

	//获取宿主Activity
	protected BaseActivity getHoldingActivity() {
		return mActivity;
	}

	public BasePresent getPresent() {
		return mPresent;
	}

	//添加fragment
	protected void addFragment(BaseFragment fragment) {
		if (null != fragment) {
			getHoldingActivity().addFragment(fragment);
		}
	}

	protected void addFragmentForResult(BaseFragment fragment, int requestCode) {
		if (fragment != null) {
			getHoldingActivity().addFragmentForResult(fragment, this, requestCode);
		}
	}

	public void setFragmentResult(int resultCode) {
		setFragmentResult(resultCode, null);
	}

	public void setFragmentResult(int resultCode, Intent intent) {
		Fragment targetFragment = getTargetFragment();
		int targetRequestCode = getTargetRequestCode();
		if (targetFragment != null) {
			targetFragment.onActivityResult(targetRequestCode, resultCode, intent);
		}
	}

	//替换fragment
	protected void replaceFragment(BaseFragment fragment) {
		if (fragment != null) {
			getHoldingActivity().replaceFragment(fragment);
		}
	}

	//移除fragment
	protected void removeFragment() {
		getHoldingActivity().removeFragment();
	}

	/**
	 * 返回到某个Fragment
	 * @param fragment 要返回到的Fragment
	 */
	protected void backTo(BaseFragment fragment) {
		getHoldingActivity().backTo(fragment);
	}

	/**
	 * 返回到某个Fragment
	 * @param name 要返回到的Fragment的simpleName
	 */
	protected void backTo(String name) {
		getHoldingActivity().backTo(name);
	}

	//处理参数
	protected void handleArguments(Bundle bundle) {
	}

	/*进行一些数据初始化*/
	protected void initData() {
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (BaseActivity) activity;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null && getArguments().size() > 0) {
			handleArguments(getArguments());
		}
		initData();
		mPresent = createPresent(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		}
		rootView = inflater.inflate(getLayoutId(), container, false);
		mUnbinder = ButterKnife.bind(this, rootView);
		initTitleBar(rootView);
		initView(rootView, savedInstanceState);
		if (mManager == null) {
			mManager = createViewManager();
		}
		return rootView;
	}

	@Override
	public void onDestroyView() {
		if (mPresent != null) {
			mPresent.detachView();
			mPresent = null;
		}
		HttpManage.cancelAll(mContext, this);
		mUnbinder.unbind();
		mManager = null;
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// for bug ---> java.lang.IllegalStateException: Activity has been destroyed
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void showLoading() {
		if (mManager != null) {
			mManager.showLoading();
		} else {
			getHoldingActivity().showLoading();
		}
	}

	@Override
	public boolean isShowLoading() {
		return (mManager != null && mManager.isShowLoading()) || getHoldingActivity().isShowLoading();
	}

	@Override
	public boolean isShowEmptyOrRetry() {
		return (mManager != null && mManager.isShowEmptyOrRetry()) || getHoldingActivity().isShowEmptyOrRetry();
	}

	@Override
	public void hideLoading() {
		if (mManager != null) {
			mManager.showContent();
		} else {
			getHoldingActivity().hideLoading();
		}
	}

	@Override
	public void showError(int errCode, String msg) {
		if (mManager != null) {
			if (errCode == HttpError._NoNet_Code) {
				mManager.showNoNet();
			} else if (errCode == 10012 || errCode == 10011) {
				mManager.showEmpty();
			} else {
				if (mManager.getListener() instanceof DefaultLoadingRetryListener) {
					((DefaultLoadingRetryListener) mManager.getListener()).setRetryText(msg);
				}
				mManager.showRetry();
			}
		} else {
			getHoldingActivity().showError(errCode, msg);
		}

	}

	@Override
	public void showEmpty() {
		if (mManager != null) {
			mManager.showEmpty();
		} else {
			getHoldingActivity().showEmpty();
		}
	}

	@Override
	public void onRetry() {
		if (mPresent != null) {
			showLoading();
			mPresent.loadData();
		}
	}

	/**
	 * <pre>
	 * 创建显示加载、重试、空白页面的管理器
	 * </pre>
	 */
	protected LoadingAndRetryManager createViewManager() {
		return null;
	}

	////////////////////////设置状态栏颜色//////////////////////////////////////////////////////
	/**
	 * <li>设置状态栏颜色</li>
	 * <p>一般情况下，不需要调用此方法，因为fragment所依附的
	 * Activity已经做了处理。对于要变为其他颜色，或者要变为透明，调用该方法即可。</p>
	 */
	public final void setStatusBar(@ColorRes int colorId) {
		getHoldingActivity().setStatusBar(colorId);
	}

	public final void setStatusBar() {
		setStatusBar(R.color.c_bg_main);
	}

	public final void setStatusBarForCollapsingToolbar(AppBarLayout appBarLayout,
													   CollapsingToolbarLayout collapsingToolbarLayout,
													   Toolbar toolbar, @ColorRes int statusColor) {
		getHoldingActivity().setStatusBarForCollapsingToolbar(appBarLayout, collapsingToolbarLayout, toolbar, statusColor);
	}

	///////////////////////////////设置自定义标题栏//////////////////////////////////////////////////////
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

	protected void initTitleBar(View view) {
		mTitleBar = (TitleBar) view.findViewById(R.id.titleBar);
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


	//////////////////////////请求权限////////////////////////////////

	protected String phoneNumber;

	protected void requestCall(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		if (PermissionUtil.requestPermission(getHoldingActivity(),
				Manifest.permission.CALL_PHONE, Action.Permission._Call)) {
			showCallDialog();
		}
	}

	protected void requestCamera() {
		if (PermissionUtil.requestPermission(getHoldingActivity(),
				Manifest.permission.CAMERA, Action.Permission._Camera)) {
			onCanCamera();
		}
	}

	protected void requestLocation() {
		if (PermissionUtil.requestPermission(getHoldingActivity(),
				Manifest.permission_group.LOCATION, Action.Permission._Location)) {
			onCanLocation();
		}
	}

	protected void requestPhone() {
		if (PermissionUtil.requestPermission(getHoldingActivity(),
				Manifest.permission_group.PHONE, Action.Permission._Phone)) {
			onCanReadPhone();
		}
	}

	protected void requestStorage() {
		if (PermissionUtil.requestPermission(getHoldingActivity(),
				Manifest.permission_group.STORAGE, Action.Permission._Storage)) {
			onCanStorage();
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == Action.Permission._Call ) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				showCallDialog();
			} else {
				Tips.showError(mContext, BaseDefine.Tips._Permission_Phone_Call_Denyed);
				onPermissionDenyed(requestCode);
			}
		}else if (requestCode == Action.Permission._Camera ) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				onCanCamera();
			} else {
				Tips.showError(mContext, BaseDefine.Tips._Permission_Camera_Denyed);
				onPermissionDenyed(requestCode);
			}
		}else if (requestCode == Action.Permission._Location ) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				onCanLocation();
			} else {
				Tips.showError(mContext, BaseDefine.Tips._Permission_Location_Denyed);
				onPermissionDenyed(requestCode);
			}
		}else if (requestCode == Action.Permission._Phone ) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				onCanReadPhone();
			} else {
				Tips.showError(mContext, BaseDefine.Tips._Permission_Phone_Denyed);
				onPermissionDenyed(requestCode);
			}
		}else if (requestCode == Action.Permission._Storage ) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				onCanStorage();
			} else {
				Tips.showError(mContext, BaseDefine.Tips._Permission_Storage_Denyed);
				onPermissionDenyed(requestCode);
			}
		}
	}

	protected void showCallDialog() {
		if (TextUtils.isEmpty(phoneNumber)) {
			return;
		}
		new DialogUtil.Builder(mContext).setTips(phoneNumber).setLeft("取消").setRight("拨打")
				.setCallBack(new ActionCallBack<Integer>() {
					@Override
					public void handleAction(Integer integer) {
						if (integer == Action.Dialog.OnRightClick) {
							Uri uriPhone = Uri.parse("tel:" + phoneNumber);
							Intent intentPhone = new Intent(Intent.ACTION_CALL);
							intentPhone.setData(uriPhone);
							startActivity(intentPhone);
						}
					}
				}).build().show();
	}

	protected void onCanLocation() {
	}

	protected void onCanCamera(){
	}

	protected void onCanReadPhone() {
	}

	protected void onCanStorage() {
	}

	protected void onPermissionDenyed(int requestCode) {
	}

	//////////////////添加和删除触摸事件不关闭软键盘的View/////////////////////
	public final void addNotHideKeyboardView(View view) {
		getHoldingActivity().addNotHideKeyboardView(view);
	}

	public final void removeNotHideKeyboardView(View view) {
		getHoldingActivity().removeNotHideKeyboardView(view);
	}
}
