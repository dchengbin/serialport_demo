package android_serialport_api;

import android.util.Log;

/**
 * @author ZY
 * @Description: TODO
 * @date 2016年4月13日 下午5:14:55
 */
public class LogUtil {

	static boolean flag = false;
	public static void LogI(String tag,String msg){
		if(flag){
			Log.i(tag, msg);
		}
	}
}

