package android_serialport_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author ZY
 * @Description: TODO
 * @date 2016年4月12日 下午9:09:55
 */
public class Perference {
	
	public static int maxTestTimes = 20;//最大测试次数
 
	/**保存是否测试过功能次数*/
	public static void saveCompletePreference(Context context,int  testCompleteTime){
		    SharedPreferences sp = context.getSharedPreferences("testCompleteTime", Context.MODE_PRIVATE);
 	        SharedPreferences.Editor editor = sp.edit();
 	        editor.putInt("testCompleteTime",testCompleteTime);
 	        editor.commit();
	    }
	
	public static int getCompletePerference(Context context){
		int testCompleteTime = 0;
		SharedPreferences sp = context.getSharedPreferences("testCompleteTime", Context.MODE_PRIVATE);       
		testCompleteTime = sp.getInt("testCompleteTime", 0);        
        return testCompleteTime;
	}


}

