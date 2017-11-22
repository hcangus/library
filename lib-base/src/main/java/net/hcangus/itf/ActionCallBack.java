package net.hcangus.itf;

/**
 * 通用的回调接口
 * 可将泛型 T 定义为  {@link android.os.Message},
 * 在message中附带数据，回调多种数据类型
 * Created by Administrator on 2017/3/20.
 */

public interface ActionCallBack<T> {
	void handleAction(T t);
}
