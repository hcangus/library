package net.hcangus.http;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 *
 * Created by hcangus
 */
public abstract class GetHelper implements HttpManage.CallBack {
	protected Context context;
	private HttpManage httpManage;

	public GetHelper(Context context) {
		this.context = context;
		httpManage = new HttpManage(this);
	}

	/**
	 * post请求,不显示加载框
	 * @param url 请求的接口
	 * @param impl 使{@link android.app.Activity}或者{@link android.app.Fragment}
	 *                实现{@link HttpManage.HttpImpl},
	 *             在Actvity或者Fragment结束时，从请求队列移除请求
	 */
	public void excute(String url, @NonNull HttpManage.HttpImpl impl) {
		httpManage.setIsShowLoading(false);
		httpManage.volleyGet(context, url, impl);
	}

	/**
	 * post请求，显示加载框，立即消失加载框
	 */
	public void excuteLoading(String url, @NonNull HttpManage.HttpImpl impl) {
		httpManage.setIsShowLoading(true);
		httpManage.volleyGet(context, url, impl);
	}

	@Override
	public void errCallBack(int errorCode, JSONObject resultJson) throws Exception {
		String errMsg = resultJson.getString("msg");
		errCallBack(errorCode, errMsg);
	}
}
