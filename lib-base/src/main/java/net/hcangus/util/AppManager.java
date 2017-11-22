package net.hcangus.util;

import android.app.Activity;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

public class AppManager {
	/**
	 * 用于activity的管理和程序的
	 */
	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (instance == null) {
			instance = new AppManager();
		}
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆
	 */
	public void addActivity(Activity activity) {
		activityStack.add(activity);
	}

	public void removeActivity(Activity activity) {
		activityStack.remove(activity);
	}

	/**
	 * 获取当前Activity（堆栈中后一个压入的
	 */
	public Activity currentActivity() {
		return activityStack.lastElement();
	}

	/**
	 * 保留指定的activity,其他的finish掉
	 */
	public void removeOther(Class<?> cls) {
		Iterator<Activity> iterator = activityStack.iterator();
		while (iterator.hasNext()) {
			Activity next = iterator.next();
			if (!next.getClass().equals(cls)) {
				iterator.remove();
				next.finish();
			}
		}
	}

	public void removeOther(Activity activity) {
		removeOther(activity.getClass());
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			if (!activity.isFinishing())
				activity.finish();
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
				break;
			}
		}
	}

	/**
	 * 结束有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 判断某个Activity是否存在
	 */
	public boolean isExit(Class activityClass) {
		boolean isExit = false;
		for (Activity activity : activityStack) {
			isExit = activity.getClass().getSimpleName().equals(activityClass.getSimpleName());
			if (isExit) {
				break;
			}
		}
		return isExit;
	}

	public Activity getActivity(Class activityClass) {
		for (Activity activity : activityStack) {
			if (activity.getClass().getSimpleName().equals(activityClass.getSimpleName())) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 出应用程
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
//			System.exit(0);
		} catch (Exception ignored) {
		}
	}
}
