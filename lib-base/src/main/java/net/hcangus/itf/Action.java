package net.hcangus.itf;

/**
 * SCRM
 * Created by Administrator on 2017/3/20.
 */

public interface Action {

	//上传图片
	interface Upload {
		int Start = 0x1;
		int End = 0x2;
		int Fail = 0x3;
	}

	//对话框
	interface Dialog {
		int OnLeftClick = 0x4;
		int OnRightClick = 0x5;
	}

	interface Permission{
		/**定位权限*/
		int _Location = 0xA0;
		/**读取手机状态权限*/
		int _Phone = 0xA1;
		/**读取内存卡权限*/
		int _Storage = 0xA2;
		/**拨打电话权限*/
		int _Call = 0xA3;
		/**相机权限*/
		int _Camera = 0xA4;
	}

	interface Code {
		/**选择图片*/
		int _Picture = 0x9;
		/**打开相机*/
		int _Camera = 0xA;
		/**压缩图片*/
		int _Compress = 0xB;
		/**替换*/
		int _Replace = 0xC;
		/**删除*/
		int _Delete = 0xD;
		/**成功*/
		int What_Success = 0xE;
		/**失败*/
		int What_Fail = 0xF;
		/**扩展1*/
		int _Extra1 = 0x10;
		/**扩展2*/
		int _Extra2 = 0x11;

		/**地图搜索*/
		int Map_Search = 0x12;

	}
}
