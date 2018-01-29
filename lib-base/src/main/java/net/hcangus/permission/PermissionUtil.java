package net.hcangus.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import net.hcangus.itf.Action;
import net.hcangus.itf.ActionCallBack;
import net.hcangus.base.BaseDefine;
import net.hcangus.util.DialogUtil;


/**
 * source_uni
 * Created by Administrator on 2017/3/22 0022.
 */

public class PermissionUtil {

	public static boolean checkPermission(Activity activity, String permission) {
		boolean result;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			result = PermissionChecker.checkSelfPermission(activity, permission)
					== PermissionChecker.PERMISSION_GRANTED;
		} else {
			result = ActivityCompat.checkSelfPermission(activity, permission)
					== PackageManager.PERMISSION_GRANTED;
		}

		return result;
	}

	public static boolean requestPermission(@NonNull final Activity activity, @NonNull final String permission, final int requestCode) {
		if (checkPermission(activity, permission)) {
			return true;
		}
		final boolean c = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
		String msg = "";
		if (TextUtils.equals(permission, Manifest.permission_group.LOCATION)) {
			msg = BaseDefine.Tips._Permission_Location;
		} else if (TextUtils.equals(permission, Manifest.permission.READ_PHONE_STATE)) {
			msg = BaseDefine.Tips._Permission_Phone;
		}else if (TextUtils.equals(permission, Manifest.permission.CALL_PHONE)) {
			msg = BaseDefine.Tips._Permission_Phone_Call;
		}else if (TextUtils.equals(permission, Manifest.permission.CAMERA)) {
			msg = BaseDefine.Tips._Permission_Camera;
		}else if (TextUtils.equals(permission, Manifest.permission_group.STORAGE)) {
			msg = BaseDefine.Tips._Permission_Storage;
		}
		if (!c) {
			new DialogUtil.Builder(activity).setTips(msg).setLeft("取消").setRight("确定")
					.setCallBack(new ActionCallBack<Integer>() {
						@Override
						public void handleAction(Integer integer) {
							if (integer == Action.Dialog.OnRightClick) {
								ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
							}
						}
					})
					.build()
					.show();
			return false;
		}
		ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
		return false;
	}

	public static void goToSetting(Activity activity) {
		Intent localIntent = new Intent();
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
		activity.startActivity(localIntent);
	}
}
