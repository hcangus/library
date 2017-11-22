package net.hcangus.http;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * SCRM
 * Created by Administrator on 2017/2/27.
 */

public class JsonUtil {

	static <E> E jsonToBean(JSONObject response, GetModelHelper<E> helper) throws Exception {
		Type mySuperClass = helper.getClass().getGenericSuperclass();
		Type type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
		return new Gson().fromJson(response.getString("data"), type);
	}


	public static <E> E jsonToBean(String json, Class<E> claz) {
		return new Gson().fromJson(json, claz);
	}

	public static String beanToJson(Object object) {
		return new Gson().toJson(object);
	}

	static <E> List<E> jsonToList(JSONObject response, GetListHelper<E> helper) throws Exception {
		Type mySuperClass = helper.getClass().getGenericSuperclass();
		final Type type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
		return new Gson().fromJson(response.getString("data"), new ListType<E>(type));
	}

	public static <E> List<E> jsonToList(String json, Class<E> claz) {
		return new Gson().fromJson(json, new ListType<E>(claz));
	}

	private static class ListType<E> implements ParameterizedType {
		Type[] types;
		ListType(Type... args){
			types = args;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return types;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}

		@Override
		public Type getRawType() {
			return List.class;
		}
	}
}
