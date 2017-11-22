package net.hcangus.http;

import android.content.Context;

import org.json.JSONObject;

/**
 * 网络请求获取单个Model的工具类
 * <p/>
 * Created by hcangus
 */
public abstract class GetModelHelper<E> extends BaseHelper {


	protected GetModelHelper(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void rightCallBack(JSONObject resultJson) throws Exception {
		onSuccess(JsonUtil.jsonToBean(resultJson, this));
	}

	/**
	 * @param e E
	 */
	public abstract void onSuccess(E e) throws Exception;
}
