package net.hcangus.http;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.orhanobut.logger.Logger;

import net.hcangus.util.DeviceUtil;
import net.hcangus.util.DialogUtil;

import org.json.JSONObject;

import java.util.Map;

public class HttpManage {
	private CallBack callBack;
	private Dialog dialog;
	private boolean isShowLoading = false;

	HttpManage(CallBack callBack) {
		this.callBack = callBack;
	}

	void setIsShowLoading(boolean isShowLoading) {
		this.isShowLoading = isShowLoading;
	}

	/**
	 * @param context
	 * @param url
	 */
	public void volleyGet(Context context, String url, @NonNull HttpImpl impl) {
		if (!DeviceUtil.isNetworkAvailable(context)) {
			try {
				callBack.errCallBack(HttpError._NoNet_Code, HttpError._NoNet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Response.Listener<String> listener = new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				if (isShowLoading && dialog != null) {
					dialog.dismiss();
				}
				if (response != null) {
					try {
						Logger.json(response);
						JSONObject resultJson = new JSONObject(response);
						callBack.rightCallBack(resultJson);
					} catch (Exception e) {
						try {
							callBack.errCallBack(HttpError._Catch_Code, e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				} else {
					try {
						callBack.errCallBack(HttpError._Null_Code, HttpError._Null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				Logger.e(error.getCause(), error.getMessage());
				if (isShowLoading && dialog != null) {
					dialog.dismiss();
				}
				try {
					callBack.errCallBack(HttpError._Error_Code, HttpError._Error);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		StringRequest stringRequest = new StringRequest(Method.GET, url,//+"&timercuo="+System.currentTimeMillis()
				listener, errorListener);
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 10, 1, 1.0f));
		stringRequest.setTag(impl.getClass().getSimpleName());
		VolleyHandler.getRequestQueue(context).add(stringRequest);
	}

	/**
	 * Post请求
	 *
	 * @param mContext context
	 * @param url      url
	 * @param params   params
	 * @param impl     {@link HttpImpl}
	 * @deprecated 服务器返回的数据类型限制为Json时，使用该方法。而现在
	 * 服务器返回的是String类型的数据，故使用{@link #postString(Context, String, Map, HttpImpl)}
	 */
	void post(final Context mContext, final String url, final Map<String, String> params, @NonNull HttpImpl impl) {
		if (callBack == null) {
			throw new IllegalArgumentException("*&*&*&----Callback  Is  Null!!-----*&*&*&*");
		}
		if (!DeviceUtil.isNetworkAvailable(mContext)) {
			try {
				callBack.errCallBack(HttpError._NoNet_Code, HttpError._NoNet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		final String tag = impl.getClass().getSimpleName();
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					try {
						Logger.t(tag).json(response.toString());
						int code = response.getInt("code");
						if (code == 0) {
							callBack.rightCallBack(response);
						} else {
							String errMsg = response.getString("msg");
							Logger.t(tag).e("http请求错误：" + errMsg);
							callBack.errCallBack(code, errMsg);
						}
					} catch (Exception e) {
						Logger.t(tag).e(e.getCause(), e.getMessage());
						try {
							callBack.errCallBack(HttpError._Catch_Code, e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				} else {
					try {
						callBack.errCallBack(HttpError._Null_Code, HttpError._Null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					callBack.errCallBack(HttpError._Error_Code, HttpError._Error);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Logger.t(tag).e(error.getCause(), error.getMessage());
				error.printStackTrace();
			}
		};
		volleyPost(mContext, url, params, listener, errorListener, impl);
	}

	private void volleyPost(Context context, String url,
							Map<String, String> params,
							Response.Listener<JSONObject> listener,
							Response.ErrorListener errorListener,
							HttpImpl impl) {
		String tag = impl.getClass().getSimpleName();
		Logger.t(url).d(params);
		JSONObject jsonObject = new JSONObject(params);
		JsonObjectRequest request = new JsonObjectRequest(Method.POST, url, jsonObject, listener, errorListener);
		request.setRetryPolicy(new DefaultRetryPolicy(1000 * 10, 1, 1.0f));
		request.setTag(tag);
		VolleyHandler.getRequestQueue(context).add(request);
	}


	/**
	 * Post请求
	 *
	 * @param mContext context
	 * @param url      url
	 * @param params   params
	 * @param impl     {@link HttpImpl},
	 */
	void postString(final Context mContext, final String url, final Map<String, String> params, @NonNull HttpImpl impl) {
		if (callBack == null) {
			throw new IllegalArgumentException("*&*&*&----Callback  Is  Null!!-----*&*&*&*");
		}
		if (!DeviceUtil.isNetworkAvailable(mContext)) {
			try {
				callBack.errCallBack(HttpError._NoNet_Code, HttpError._NoNet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Response.Listener<String> listener = new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				if (isShowLoading && dialog != null) {
					dialog.dismiss();
				}
				if (response != null) {
					try {
						Logger.t(url).json(response);
						final JSONObject resultJson = new JSONObject(response);
						int code = resultJson.getInt("code");
						if (code == 0) {
							callBack.rightCallBack(resultJson);
						} else {
							String errMsg = resultJson.getString("msg");
							Logger.t(url).e("http请求错误：" + errMsg);
							callBack.errCallBack(code, resultJson);
						}
					} catch (Exception e) {
						try {
							callBack.errCallBack(HttpError._Catch_Code, e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				} else {
					try {
						callBack.errCallBack(HttpError._Null_Code, HttpError._Null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				Logger.t(url).e(error.getCause(), error.getMessage());
				if (isShowLoading && dialog != null) {
					dialog.dismiss();
				}
				try {
					callBack.errCallBack(HttpError._Error_Code, HttpError._Error);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		volleyPostString(mContext, url, params, listener, errorListener, impl);

	}

	private void volleyPostString(final Context context, String url,
								  final Map<String, String> params,
								  Response.Listener<String> listener,
								  Response.ErrorListener errorListener,
								  final HttpImpl impl) {
		String tag = impl.getClass().getSimpleName();
		Logger.t(url).d(params);
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}
		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 15, 1, 1.0f));
		stringRequest.setTag(tag);
		if (isShowLoading) {
			if (dialog == null) {
				dialog = DialogUtil.showLoadingCancelable(context, null);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancelAll(context, impl);
					}
				});
			} else {
				dialog.show();
			}
		}
		VolleyHandler.getRequestQueue(context).add(stringRequest);
	}

	public static void cancelAll(Context context, HttpImpl impl) {
		VolleyHandler.getRequestQueue(context).cancelAll(impl.getClass().getSimpleName());
	}

	interface CallBack {
		void rightCallBack(JSONObject resultJson) throws Exception;

		void errCallBack(int errorCode, String errMsg) throws Exception;

		void errCallBack(int errorCode, JSONObject resultJson) throws Exception;
	}

	public interface HttpImpl {

	}

}
