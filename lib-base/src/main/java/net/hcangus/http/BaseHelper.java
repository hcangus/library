package net.hcangus.http;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.Map;

/**
 *
 * Created by hcangus
 */
public abstract class BaseHelper implements HttpManage.CallBack {
	protected Context context;
	private HttpManage httpManage;

	public BaseHelper(Context context) {
		this.context = context;
		httpManage = new HttpManage(this);
	}

	/**
	 * post请求,不显示加载框
	 * @param url 请求的接口
	 * @param params 请求的参数
	 * @param impl 使{@link android.app.Activity}或者{@link android.app.Fragment}
	 *                实现{@link HttpManage.HttpImpl},
	 *             在Actvity或者Fragment结束时，从请求队列移除请求
	 */
	public void excute(String url, Map<String, String> params, @NonNull HttpManage.HttpImpl impl) {
		httpManage.setIsShowLoading(false);
		httpManage.postString(context, url, params, impl);
	}

	/**
	 * post请求，显示加载框，立即消失加载框
	 */
	public void excuteLoading(String url, Map<String, String> params, @NonNull HttpManage.HttpImpl impl) {
		httpManage.setIsShowLoading(true);
		httpManage.postString(context, url, params, impl);
	}

	@Override
	public void errCallBack(int errorCode, JSONObject resultJson) throws Exception {
		String errMsg = resultJson.getString("msg");
		errCallBack(errorCode, errMsg);
	}
}
