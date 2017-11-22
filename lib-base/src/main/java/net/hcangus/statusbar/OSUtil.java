package net.hcangus.statusbar;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 判断手机型号的工具类
 * Created by Administrator on 2017/3/25.
 */

public class OSUtil {

	//MIUI标识
	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

	//EMUI标识
	private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
	private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
	private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

	//Flyme标识
	private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.user_id";
	private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
	private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
	private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
	private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

	public int getRomType() {
		try {
			BuildProperties buildProperties = new BuildProperties();

			if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE)
					|| buildProperties.containsKey(KEY_EMUI_API_LEVEL)
					|| buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
				//华为，对于华为不需要做特殊处理，定义为其他型号
				return ROM_TYPE.Others;
			}
			if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE)
					|| buildProperties.containsKey(KEY_MIUI_VERSION_NAME)
					|| buildProperties.containsKey(KEY_MIUI_VERSION_NAME)) {
				return ROM_TYPE.MIUI;
			}
			if (buildProperties.containsKey(KEY_FLYME_ICON_FALG)
					|| buildProperties.containsKey(KEY_FLYME_SETUP_FALG)
					|| buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
				return ROM_TYPE.Flyme;
			}
			if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
				String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
				if (!TextUtils.isEmpty(romName)
						&& romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
					return ROM_TYPE.Flyme;
				}
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				return ROM_TYPE.AndroidM;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ROM_TYPE.Others;
	}

	/**
	 * <pre>
	 * 小米的AndroidM还是以小米的方式
	 * </pre>
	 */
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

	private class BuildProperties {

		private Properties properties;

		BuildProperties() throws IOException {

			properties = new Properties();

			properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

		}

		boolean containsKey(final Object key) {

			return properties.containsKey(key);

		}

		boolean containsValue(final Object value) {

			return properties.containsValue(value);

		}

		String getProperty(final String name) {

			return properties.getProperty(name);

		}

		String getProperty(final String name, final String defaultValue) {

			return properties.getProperty(name, defaultValue);

		}

		Set<Map.Entry<Object, Object>> entrySet() {

			return properties.entrySet();

		}

		boolean isEmpty() {

			return properties.isEmpty();

		}

		Enumeration keys() {

			return properties.keys();

		}

		Set keySet() {

			return properties.keySet();

		}

		int size() {

			return properties.size();

		}

		Collection values() {

			return properties.values();

		}

	}
}
