package net.hcangus.http;

/**
 * Anydoor
 * Created by Administrator
 */

public interface HttpError {
	String _Null = "网络请求返回为空";
	String _NoNet = "网络未连接";
	String _Error = "网络请求失败";
	String _Catch = "数据错误";
	int _Null_Code = 1;
	int _NoNet_Code = 2;
	int _Error_Code = 3;
	int _Catch_Code = 4;
}
