package net.hcangus.tips;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * 提示信息
 *
 * @author hc
 */
public class Tips {

	private static WeakReference<ToastView> toast;

	private static ToastView getToast(Context context){
		if (toast == null || toast.get() == null) {
			toast = new WeakReference<>(new ToastView(context));
		}
		return toast.get();
	}


	/**
	 * 错误消息
	 */
	public static void showError(Context context, CharSequence msg) {
		getToast(context).showError(msg);
	}

	/**
	 * 提示消息
	 */
	public static void showInfo(Context context, CharSequence msg) {
		getToast(context).showInfo(msg);
	}

	/**
	 * 成功消息
	 */
	public static void showSuccess(Context context, CharSequence msg) {
		getToast(context).showSuccess(msg);
	}

}
