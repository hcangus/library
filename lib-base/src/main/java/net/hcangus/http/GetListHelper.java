package net.hcangus.http;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;

/**
 * @param <E>
 */
public abstract class GetListHelper<E> extends BaseHelper {

	public GetListHelper(Context context) {
		super(context);
	}


	@Override
	public void rightCallBack(JSONObject resultJson) throws Exception {
		onSuccess(JsonUtil.jsonToList(resultJson, this));
	}

	public abstract void onSuccess(List<E> list);
}
