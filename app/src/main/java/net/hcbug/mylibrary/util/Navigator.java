package net.hcbug.mylibrary.util;

import android.os.Bundle;
import net.hcangus.base.BaseFragment;

public enum Navigator {
	Demo{
		@Override
		public BaseFragment getFragment(String _id) {
			return super.getFragment(_id);
		}
	};

	public BaseFragment getFragment(String _id) {
		return null;
	}

	public BaseFragment getFragment(Bundle args) {
		return null;
	}
}
