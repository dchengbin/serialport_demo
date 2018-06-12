package android_serialport_api.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android_serialport_api.LogUtil;
import android_serialport_api.Perference;

/**
 * @author ZY
 * @Description: TODO
 * @date 2016年4月11日 下午7:35:16
 */
public class BootBroadcastReceiver extends BroadcastReceiver {  
	  
	private String TAG = " BootBroadcastReceiver";
	 static final String ACTION = "android.intent.action.BOOT_COMPLETED";  
	 private int testCompleteTime; 
	 @Override  
	 public void onReceive(Context context, Intent intent) {  
	    
	  Log.i(TAG, "BootBroadcastReceiver...");
	  if (intent.getAction().equals(ACTION)){  
//		   testCompleteTime = Perference.getCompletePerference(context);//是否之前测试完成
//		   LogUtil.LogI(TAG, "testCompleteTime:"+testCompleteTime);
//		   if(testCompleteTime<=Perference.maxTestTimes){
				
				Intent mIntent=new Intent(context,ConsoleActivity.class);  //
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  		  
				context.startActivity(mIntent);  
//		    }		   
	     }  
	  }  
	  
	} 

