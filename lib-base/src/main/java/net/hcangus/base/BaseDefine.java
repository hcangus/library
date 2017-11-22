package net.hcangus.base;

/**
 * SCRM
 * Created by Administrator on 2017/2/27.
 */

public interface BaseDefine {

    interface Tips{
		String _Permission_Phone = "请授予读取电话状态权限";
		String _Permission_Phone_Call = "请授予拨打电话权限";
		String _Permission_Camera = "请授予拍照权限";
		String _Permission_Location = "请授予定位权限";
		String _Permission_Storage = "请授予读取内存卡权限";

		String _Permission_Phone_Denyed = "没有读取电话状态的权限";
		String _Permission_Phone_Call_Denyed = "没有拨打电话的权限";
		String _Permission_Camera_Denyed = "没有手机拍照的权限";
		String _Permission_Location_Denyed = "没有定位权限";
		String _Permission_Storage_Denyed = "没有读取内存卡权限";
	}

	interface Location{
		String LATITUDE = "latitude";// 纬度
		String LONGITUDE = "longitude";// 经度
		String ADDRESS = "address";// 地址
		String NAME = "name";// 名称
		String CITY = "city";// 城市
		String RESULT = "result";// 结果
		String LOCATION_SERVICE_FILTER = "locationServiceFilter";//定位服务广播过滤器

	}

	/************************************************* 其他常量 ********************************************************/
	//图片处理相关
	interface IMAGE {
		int IMAGE_COMPRESSION_100 = 100;// 图片压缩率，100表示不压缩，70表示压缩为原图的70%
		int IMAGE_COMPRESSION_30 = 30;// 图片压缩为原图的30%
		int IMAGE_SIZE = 500;// 图片压缩目标大小,单位kb
		int IMAGE_BIG_SIZE = 1000;// 图片压缩目标大小,单位kb
		int SIZE_CHANNEL_PUBLISH = 30;//文章发布的图片数量
		int SIZE_WELFARE_COVER_PUBLISH = 3;//福利的头部图片数量
		int SIZE_WELFARE_PUBLISH = 9;//福利的图片数量
		int SIZE_ACTIVITY_PUBLISH = 9;//活动的图片数量
	}

	/**
	 * 时间翟格式
	 */
	interface TIME {
		String dataFormat = "yyyy/MM/dd H:m";
		String publicDataFormat = "yyyy-MM-dd HH:mm:ss";
	}

}
