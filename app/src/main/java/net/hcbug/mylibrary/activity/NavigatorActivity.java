package net.hcbug.mylibrary.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import net.hcangus.base.AppActivity;
import net.hcbug.mylibrary.util.Navigator;


public class NavigatorActivity extends AppActivity {

	public static void startMe(Context context, Navigator demo, @Nullable String _id) {
		Intent intent = new Intent(context, NavigatorActivity.class);
		if (_id != null) {
			intent.putExtra("_id", _id);
		}
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", false);
		context.startActivity(intent);
	}

	public static void startMe(Context context, Navigator demo, @NonNull Bundle args) {
		Intent intent = new Intent(context, NavigatorActivity.class);
		intent.putExtra("bundle", args);
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", true);
		context.startActivity(intent);
	}

	public static void startMeForResult(Activity activity, Navigator demo, @Nullable String _id, int requestCode) {
		Intent intent = new Intent(activity, NavigatorActivity.class);
		if (_id != null) {
			intent.putExtra("_id", _id);
		}
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", false);
		activity.startActivityForResult(intent,requestCode);
	}

	public static void startMeForResult(Activity activity, Navigator demo, @NonNull Bundle args,int requestCode) {
		Intent intent = new Intent(activity, NavigatorActivity.class);
		intent.putExtra("bundle", args);
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", true);
		activity.startActivityForResult(intent,requestCode);
	}

	public static void startMeForResult(Fragment fragment, Navigator demo, @Nullable String _id, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), NavigatorActivity.class);
		if (_id != null) {
			intent.putExtra("_id", _id);
		}
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", false);
		fragment.startActivityForResult(intent,requestCode);
	}

	public static void startMeForResult(Fragment fragment, Navigator demo, @NonNull Bundle args, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), NavigatorActivity.class);
		intent.putExtra("bundle", args);
		intent.putExtra("demo_name", demo.name());
		intent.putExtra("isBundle", true);
		fragment.startActivityForResult(intent,requestCode);
	}

	@Override
	protected Fragment getFirstFragment() {
		boolean isBundle = getIntent().getBooleanExtra("isBundle", false);
		String demoName = getIntent().getStringExtra("demo_name");
		Navigator demo = Navigator.valueOf(demoName);
		Fragment firstFragment;
		if (isBundle) {
			Bundle args = getIntent().getBundleExtra("bundle");
			firstFragment = demo.getFragment(args);
		} else {
			String _id = getIntent().getStringExtra("_id");
			firstFragment = demo.getFragment(_id);
		}
		return firstFragment;
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
	}
}
