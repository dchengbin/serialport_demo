package android_serialport_api.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android_serialport_api.Perference;
import android_serialport_api.SerialPort;
import android_serialport_api.WifiAdmin;


/**
 * @author ZY
 * @Description: TODO
 * @date 2016年4月12日 下午9:43:31
 */
public class ConsoleService extends Service{
	
	private String TAG = "ConsoleService";
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	
	
	
	protected SerialPort mSerialPortLoopBack;
	protected OutputStream mOutputStreamLoopBack;
	private InputStream mInputStreamLoopBack;
 	private ReadThreadLoopBack mReadThreadLoopBack;
 	
 	
    private String test_item;

 	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			
 			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
 					if (mInputStream == null) return;
 					Log.i(TAG, "11111");
 					size = mInputStream.read(buffer);
 					Log.i(TAG, "2222 size:"+size);
  
					if (size > 0) {
						Log.i(TAG, "333333");
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					Log.e(TAG, "/////IOException");
					e.printStackTrace();
					return;
				}
 			}
		}
	}
	
	
	//回路串口线程
	private class ReadThreadLoopBack extends Thread {

		@Override
		public void run() {
			super.run();
 			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
 					if (mInputStreamLoopBack == null) return;
 					size = mInputStreamLoopBack.read(buffer);
 					
					if (size > 0) {
						onDataReceivedLoopBack(buffer, size);
					}
				} catch (IOException e) {
					Log.e(TAG, "/////IOException");
					e.printStackTrace();
					return;
				}
			}
		}
	}
 	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		init();
		test_serial();
	}
	
	/**初始化数据*/
	public void init(){
		mApplication = (Application) getApplication();
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			Log.e(TAG, "mSerialPort:"+mSerialPort);
			Log.e(TAG, "mOutputStream:"+mOutputStream);
			Log.e(TAG, "mInputStream:"+mInputStream);
			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
			
			mSerialPortLoopBack = mApplication.getSerialPortLoopBack();
			mOutputStreamLoopBack = mSerialPortLoopBack.getOutputStream();
			mInputStreamLoopBack = mSerialPortLoopBack.getInputStream();

			Log.e(TAG, "mSerialPortLoopBack:"+mSerialPortLoopBack);
			Log.e(TAG, "mOutputStreamLoopBack:"+mOutputStreamLoopBack);
			Log.e(TAG, "mInputStreamLoopBack:"+mInputStreamLoopBack);
			/* Create a receiving thread */
			mReadThreadLoopBack = new ReadThreadLoopBack();
			mReadThreadLoopBack.start();
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException");
 		} catch (IOException e) {
 			Log.e(TAG, "IOException");
		} catch (InvalidParameterException e) {
 			Log.e(TAG, "InvalidParameterException");
		}
	}
	
	

	
	
 	/**接收数据*/
 	protected void onDataReceived(final byte[] buffer, final int size) {
		String receiveValue = new String(buffer, 0, size);

		Log.i(TAG, "receiveValue:"+receiveValue);
  		if(receiveValue.contains("AT01")){//串口测试
 			test_item = "serial";
  			test_serial_complete();
		}
 		
		if(receiveValue.contains("AE14")){//wifi测试
			test_item = "wifi";
 			test_wifi();
		}
				
		if(receiveValue.contains("AC07")){//喇叭测试
			test_item = "speaker";
			test_speaker();
		}
		
		if(receiveValue.contains("AC08")){//触摸测试
			test_item = "touch";
			touche_complete_flag = false;
			test_touch();
		}
		
		if(receiveValue.contains("AC10")){//串口短路测试
			test_item = "loopback";
			test_loopback();
		}
		
		if(receiveValue.contains("AD00")){//测试结束
			test_item = "all_complete";
			all_test_complete();
		}
		 
	}
 	
 	
 	protected void onDataReceivedLoopBack(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		String receiveLoopBackValue = new String(buffer, 0, size);
		Log.i(TAG, "receiveLoopBackValue:"+receiveLoopBackValue);		
		loopBackValue = receiveLoopBackValue;
		
	}
 	
 	
	/**发送数据*/
	public void sendData(String value){
		int i;
		CharSequence t = value;
		char[] text = new char[t.length()];
		for (i=0; i<t.length(); i++) {
			text[i] = t.charAt(i);
		}
		try {
			mOutputStream.write(new String(text).getBytes());
			mOutputStream.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 	
 	
	
//	/**串口测试*/
 	public void test_serial(){
 		final Timer  timer = new Timer();
 		i=0;
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				i = i+1;
				Log.i(TAG, i+"test_item:"+test_item);
 				if("serial".equals(test_item)){
 					timer.cancel();
				}else{					
					sendData("AT01");
				}
			}
		}, 3000,5000); 
		
 
	}
 	
	/**串口测试完成*/
	public void test_serial_complete(){
		
   	}
	
	
	/**wifi测试*/
	private WifiAdmin mWifiAdmin;
 	public void test_wifi(){
 		mWifiAdmin = new WifiAdmin(this);  
 		mWifiAdmin.Createwifilock();
 		if (mWifiAdmin.isWifiEnabled()) {
        	 Log.d(TAG, "wifi is started");	
         }
        else{
        	 mWifiAdmin.OpenWifi();
         }
 		
 		final Timer  wifi_timer = new Timer();
   		wifi_timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mWifiAdmin.isWifiEnabled())
		   	 	{
		    	 	Log.d(TAG, "wifi is started////");
		             mWifiAdmin.StartScan();  
		           
		            StringBuilder s = mWifiAdmin.LookUpScan();  
		           //List<WifiConfiguration> mListWifiCgn = mWifiAdmin.GetConfinguration();  
		            
		            if(s.length()>3){
		            	Log.d(TAG, "wifi测试成功");
		            	test_wifi_complete("suc");
		            }else{
		            	Log.d(TAG, "wifi测试失败");
		            	test_wifi_complete("fail");
		            }
		            wifi_timer.cancel();
		                
		   	 	}
		   	 	else{
		   	 	   test_wifi_complete("fail");
		   	 	   wifi_timer.cancel();
				}
			}
		}, 1000, 5000);

 		 
	}
 	/**wifi测试完成*/
	public void test_wifi_complete(String wifiResult){
		
 		if("suc".equals(wifiResult)){			
			sendData("AE1411");
		}else if("fail".equals(wifiResult)){
			sendData("AE1422");
		}
	}

 	
 	
 	
	/**喇叭测试*/
	MediaPlayer mp=null;
	public void test_speaker(){
		 if(mp == null){			 
			 mp = MediaPlayer.create(this, R.raw.readsecond);//tada
		 }
        try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mp.setLooping(true);  
        mp.start();
        
        test_speaker_complete();
	}
	
	public void test_speaker_complete(){
		try {
			Thread.sleep(7000);//7秒后启动mic
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(mp!=null){   
	        	test_mic();//喇叭完成之后进行mic测试
	     }else{
	        	sendData("AC0722");
	        	closeSerialPort();
	     }
	}
	
	
	/**录音测试*/
	File recAudioFile;
	MediaRecorder mMediaRecorder=null; 
 	public void test_mic(){
 		recAudioFile = new File("/mnt/sdcard", "new.amr"); 
 		mMediaRecorder = new MediaRecorder(); 

        if (recAudioFile.exists()) { 
            recAudioFile.delete(); 
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); 
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
        mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());         

        try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

        mMediaRecorder.start(); 
        Log.e(TAG, "mMediaRecorder start");
        
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//10秒之后停止录音
        
        
        /**停止播放源文件*/
		if(mp!=null){   
	        	mp.stop();
	        	mp.release();	
	        	mp=null;
		}
        
        
        //停止录音
	    if ((recAudioFile!=null)&&(mMediaRecorder!=null)) { 
	        mMediaRecorder.stop(); 
	        mMediaRecorder.release();
	        mMediaRecorder=null;
	    }     
	    Log.e(TAG, "mMediaRecorder stop");
	    
	    
	    //播放录音
	    if(recAudioFile!=null){
	    	if(mp == null && recAudioFile.exists()){			 
				 mp = MediaPlayer.create(this, Uri.fromFile(recAudioFile));//
			}
	    	mp.setLooping(true);  
	        mp.start();             
        }
	    
	    
 	    test_mic_complete();         
	}
 	
	public void test_mic_complete(){
		try {
			Thread.sleep(10000);//10秒后关闭音乐
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(mp!=null){   
	        	mp.stop();
	        	mp.release();	
	        	mp=null;
	        	sendData("AC0711");//录音测试成功
	     }else{
	        	sendData("AC0722");
	        	closeSerialPort();
	     }
	}
	
	
	/**触摸测试*/
	String keyDownValue = "";
	int touchnum;
	public void test_touch(){		
		final Timer timer = new Timer();
		touchnum=0;
 		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				touchnum = touchnum + 1;
				Log.i(TAG, "touchnum:"+touchnum);
 				if(touchnum==5){
 					Log.i(TAG, "touchnum:"+touchnum+"  //keyDownValue:"+keyDownValue);
 					if(keyDownValue.trim().length() == 0){
 						sendData("AC0822");
 						closeSerialPort();
 					}
					timer.cancel();
				}
			}
		}, 1000, 3000);
  	}
	

	boolean touche_complete_flag = false;
	public void test_touch_complete(String value){
		if(touche_complete_flag){
			if("suc".equals(value)){
				  sendData("AC0811");
			}else if("fail".equals(value)){
				  sendData("AC0822");
			}
		}		  
		touche_complete_flag = true;
	}
	
	
	/**短路测试*/
	String loopBackValue = "";
	int i=0;
	public void test_loopback(){
		
		final Timer timer = new Timer();
		i=0;
		loopBackValue = "";
 		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				i = i+1;
				Log.i(TAG, "loopBackValue："+loopBackValue+"      loopBackValue.length():"+loopBackValue.length());
				if(loopBackValue.length()>0){
					if(loopBackValue.length()>=10){						
						sendData("AC101110"+loopBackValue.substring(0,10));//最多取10位
					}else{
						sendData("AC1011"+loopBackValue.length()+loopBackValue);
					}
					timer.cancel();
				}else if(i == 5){
					sendData("AC101100");
					timer.cancel();
				}

			}
		}, 1000, 3000);
  	}
	
	public void test_loopback_complete(String value){
		  sendData(value);
	}
	
	
	/**所有测试结束*/
	public void all_test_complete(){
		  sendData("AD11");
 		  closeSerialPort();
 	}
	
	
	/**关闭串口*/
	public void closeSerialPort(){
		mApplication.closeSerialPort();
		stopSelf();
	}
	
 
//    protected static final int[] KEYCODES = { 25, 24, 82, 3, 4, 84 };//4返回键  82设置键  24音量加   25音量减
//	@Override
//	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
//	  {
//		    Log.i(TAG, "paramInt:"+paramInt);
//		    if("touch".equals(test_item)){		
//		    	keyDownValue = "touch";
//		    	if(paramInt == 4){//返回键
//		    		test_touch_complete("suc");
//	    			return true;
//		    	}
//		    	test_touch_complete("fail");
//		    	return false;
// 		    }
//		    
// 		    return true;
//	   }


 
	@Override
	public void onDestroy(){
 		Log.d(TAG, "onDestroy");
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		super.onDestroy();
	}

}

