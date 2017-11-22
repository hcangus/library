package net.hcangus.statusbar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.hcangus.util.SPUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utils for status bar
 * Created by qiu on 3/29/16.
 */
public class StatusBarCompat {
	private static int romTypeValue = -1;

	//获取手机的Rom类型，并保存，在给状态栏设置白色时要用到
	public static void initRomType(Context context) {
		String key_ROM_TYPE = "StatusBarCompat_type_phone";
		if (!SPUtils.contains(context, key_ROM_TYPE)) {
			romTypeValue = new OSUtil().getRomType();
			SPUtils.put(context, key_ROM_TYPE, romTypeValue);
		} else {
			romTypeValue = (int) SPUtils.get(context, key_ROM_TYPE, ROM_TYPE.Others);
		}
	}

	//Get alpha color
	@SuppressWarnings("NumericOverflow")
	private static int calculateStatusBarColor(int color, int alpha) {
		float a = 1 - alpha / 255f;
		int red = color >> 16 & 0xff;
		int green = color >> 8 & 0xff;
		int blue = color & 0xff;
		red = (int) (red * a + 0.5);
		green = (int) (green * a + 0.5);
		blue = (int) (blue * a + 0.5);
		return 0xff << 24 | red << 16 | green << 8 | blue;
	}

	/**
	 * set statusBarColor
	 *
	 * @param statusColor color
	 * @param alpha       0 - 255
	 */
	public static void setStatusBarColor(@NonNull Activity activity, @ColorInt int statusColor, int alpha) {
		setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
	}

	public static void setStatusBarColor(@NonNull Activity activity, @ColorInt int statusColor) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			StatusBarCompatLollipop.setStatusBarColor(activity, statusColor);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			StatusBarCompatKitKat.setStatusBarColor(activity, statusColor);
		}
	}

	public static boolean isEMUI3_1() {
		String property = getSystemProperty("ro.build.version.emui", null);
		if ("EmotionUI 3".equals(property) || "EmotionUI_3.1".equals(property) || "EmotionUI_3.1.1".equals(property)) {
			return true;
		}
		return false;
	}

	private static String getSystemProperty(String key, String defaultValue) {
		try {
			Class<?> clz = Class.forName("android.os.SystemProperties");
			Method get = clz.getMethod("get", String.class, String.class);
			return (String) get.invoke(clz, key, defaultValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static void translucentStatusBar(@NonNull Activity activity) {
		translucentStatusBar(activity, false);
	}

	/**
	 * change to full screen mode
	 *
	 * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
	 */
	public static void translucentStatusBar(@NonNull Activity activity, boolean hideStatusBarBackground) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			StatusBarCompatLollipop.translucentStatusBar(activity, hideStatusBarBackground);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			StatusBarCompatKitKat.translucentStatusBar(activity);
		}
	}

	public static void setStatusBarColorForCollapsingToolbar
			(@NonNull Activity activity,
			 AppBarLayout appBarLayout,
			 CollapsingToolbarLayout collapsingToolbarLayout,
			 Toolbar toolbar,
			 @ColorInt int statusColor) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			StatusBarCompatLollipop.setStatusBarColorForCollapsingToolbar(activity, appBarLayout, collapsingToolbarLayout, toolbar, statusColor);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			StatusBarCompatKitKat.setStatusBarColorForCollapsingToolbar(activity, appBarLayout, collapsingToolbarLayout, toolbar, statusColor);
		}
	}

	/**
	 * 已知系统类型时，设置状态栏黑色字体图标。
	 * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
	 *
	 */
	public static boolean StatusBarLightMode(Activity activity) {
		if (romTypeValue == -1) {
			initRomType(activity);
		}
		if (romTypeValue == ROM_TYPE.MIUI) {
			return MIUISetStatusBarLightMode(activity.getWindow(), true);
		} else if (romTypeValue == ROM_TYPE.Flyme) {
			return FlymeSetStatusBarLightMode(activity.getWindow(), true);
		} else if (romTypeValue == ROM_TYPE.AndroidM) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				//View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
				return true;
			}
		}
		return false;
	}

	/**
	 * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
	 */
	public static void StatusBarDarkMode(Activity activity) {
		if (romTypeValue == -1) {
			initRomType(activity);
		}
		if (romTypeValue == ROM_TYPE.MIUI) {
			MIUISetStatusBarLightMode(activity.getWindow(), false);
		} else if (romTypeValue == ROM_TYPE.Flyme) {
			FlymeSetStatusBarLightMode(activity.getWindow(), false);
		} else if (romTypeValue == ROM_TYPE.AndroidM) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}

	}


	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格
	 * 可以用来判断是否为Flyme用户
	 *
	 * @param window 需要设置的窗口
	 * @param dark   是否把状态栏字体及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 */
	private static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class
						.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class
						.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 设置状态栏字体图标为深色，需要MIUIV6以上
	 *
	 * @param window 需要设置的窗口
	 * @param dark   是否把状态栏字体及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 */
	private static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			Class clazz = window.getClass();
			try {
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				if (dark) {
					extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
				} else {
					extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
				}
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	interface ROM_TYPE {
		/**
		 * 小米
		 */
		int MIUI = 1;
		/**
		 * 魅族系统
		 */
		int Flyme = 2;
		/**
		 * Android 6.0
		 */
		int AndroidM = 3;
		/**
		 * 其他
		 */
		int Others = 0;
	}
}
