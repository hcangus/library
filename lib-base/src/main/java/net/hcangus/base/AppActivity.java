package net.hcangus.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.hcangus.mvp.present.BasePresent;


public abstract class AppActivity extends BaseActivity {

	//获取第一个fragment
	protected abstract Fragment getFirstFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//避免重复添加Fragment
		if (null == getSupportFragmentManager().getFragments()
				|| getSupportFragmentManager().getFragments().size() == 0) {
			Fragment firstFragment = getFirstFragment();
			if (null != firstFragment) {
				addFragment(firstFragment);
			}
		}

	}

	@Override
	protected BasePresent createPresent(Context context) {
		return null;
	}

	@Override
	protected void setupViews() {

	}

	@Override
	protected int getContentViewId() {
		return R.layout.activity_base;
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}
}
