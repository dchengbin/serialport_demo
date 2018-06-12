/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.LogUtil;
import android_serialport_api.Perference;
import android_serialport_api.WifiAdmin;

public class ConsoleActivity extends SerialPortActivity {

	private String TAG = "ConsoleActivity";
	private Context mcontext;	
    private String test_item;
	
    private boolean startSerialFlag = false;    
    private int testCompleteTime;

	private int sendcounts=0;
    
    private boolean changeingFlag = false;//���ɹ�״̬
      
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		mcontext = this;      
		Log.e(TAG, "start onCreate~~~         ");  
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.console);
					
		registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		//test_short();
		//test_speaker();

		shortTimer = new Timer();
		shortTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				test_speaker();
			}
		}, 500,30000);
	//test_mic();
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,AudioManager.FLAG_SHOW_UI);
		//new MyThread().start();


	}

	
	Timer shortTimer;
 	public void test_short(){		 
 			shortTimer = new Timer();
 			shortTimer.schedule(new TimerTask() {
 				
 				@Override
 				public void run() {
 					// TODO Auto-generated method stub
 					LogUtil.LogI(TAG, " ///+++++++++++++++++:");	
   					sendDataLoopback("....///+++++++++++++++++");
 					
 				}
 			}, 500,1000);
	}
	
	

    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e(TAG, " start onPause~~~  ");  
     }  
    @Override  
    protected void onStop() {  
        super.onStop();  
        Log.e(TAG, "start onStop~~~");  
     }  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mbatteryReceiver);
      } 
	
	
	
	
	
	/**�������*/
	public void sendData(String value){
		int i;
		CharSequence t = value;
		char[] text = new char[t.length()];
		for (i=0; i<t.length(); i++) {
			text[i] = t.charAt(i);
		}
		try {
			mOutputStream.write(new String(text).getBytes());
  		} catch (IOException e) {
			Log.i(TAG, "IOException");
			e.printStackTrace();
		}
	}
	
	
	/**��·���Է������*/
	public void sendDataLoopback(String value){
 		int i;
		CharSequence t = value;




		Log.e(TAG, "sendData!!!!!!!!!!!!"+sendcounts);
		 {
 			//mOutputStreamLoopBack.write(new String(text).getBytes());

				if(sendcounts==0) {
					//mOutputStreamLoopBack.write(array A0[i]);
					testmain1();
					//testmain_zddk();
					//fgslight();
					//String s="ff ff 01 11 fb ";
					//send_test(s);
				}
				if(sendcounts==1) {

					//mOutputStreamLoopBack.write(arrayA1[i]);
					testmain3();
					//testmain_zddk();
					//fgslight();
					//String s="ff ff 01 1A fb ";
					//send_test(s);

				}
				if(sendcounts==2) {
					//mOutputStreamLoopBack.write(arrayA2[i]);
					//testmain3();
					//String s="ff ff 03 1A fb ";
					//send_test(s);
					//fgslight();
					}


			sendcounts++;
			if(sendcounts>=2)
				sendcounts=0;


		}
	}
	
   


    
	/**�ر�com��0  ��Com��1*/
	public void closeCom0(){
		if(mSerialPortLoopBack!=null){
			mSerialPortLoopBack.close();
			mSerialPortLoopBack = null;
		}
		
		openCom1();
	}
	
	public void openCom1(){
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
			
			
			test_serial();
			
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
 	/**�������*/
	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		String receiveValue = new String(buffer, 0, size);

		LogUtil.LogI(TAG, "receiveValue:"+receiveValue);
  		if(receiveValue.contains("AT01")){//���ڲ���
 			test_item = "serial";
 			if("serial".equals(test_item)){
					serialTimer.cancel();
			}
  			test_serial_complete();
		}
 		
		if(receiveValue.contains("AE14")){//wifi����
			test_item = "wifi";
 			test_wifi();
		}
		
		if(receiveValue.contains("AE02")){//�����Բ���
			test_item = "changing";
 			test_changing();
		}
				
		if(receiveValue.contains("AC07")){//���Ȳ���
			test_item = "speaker";
			test_speaker();
		}
		
		if(receiveValue.contains("AE15")){//��������
			test_item = "touch";
		}
		
		if(receiveValue.contains("AC10")){//���ڶ�·����
			test_item = "loopback";
 			if("loopback".equals(test_item)){
 				Log.i(TAG, "loopBackValue.length():"+loopBackValue.length()+"  //loopBackValue:"+loopBackValue);
				if(loopBackValue.length()>0){
					if(loopBackValue.length()>=10){						
						sendData("AC101110"+loopBackValue.substring(0,10));//���ȡ10λ
					}else{
						sendData("AC10110"+loopBackValue.length()+loopBackValue);//С��10λ������ǰ�油0
					}
				}else{
					sendData("AC101100");
				}
			}
			
		}
		
		if(receiveValue.contains("AD00")){//���Խ���
			test_item = "all_complete";
			all_test_complete();
		}
		 
	}
	
	
	
	String loopBackValue = "";
	@Override
	protected void onDataReceivedLoopBack(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		String receiveLoopBackValue = new String(buffer, 0, size);
		//Log.e(TAG, "receiveLoopBackValue:%x"+receiveLoopBackValue);

		Log.d(TAG, "receiveData@@@@@@@@@@ length: "+receiveLoopBackValue.length());
		if(receiveLoopBackValue.length()>0)
		{
			String str2 =receiveLoopBackValue.replace("\r", "-");
			String str3 =str2.replace("\n", "-");
			Log.d(TAG, "receiveData@@@@@@@@@@: "+str3);
		}
		/*
		if(receiveLoopBackValue.length()>0){			
			loopBackValue = receiveLoopBackValue;	
			shortTimer.cancel();//�رն�ʱ��
			mInputStreamLoopBack = null;
			closeCom0();//��·������ֵ ���ر�com��0			 
		}*/
	}
	
	
	
//	/**���ڲ���*/
 	Timer serialTimer;
 	int i=0;
 	public void test_serial(){
 		
 		if(!startSerialFlag){
 			startSerialFlag = true;
 			serialTimer = new Timer();
 			i=0;
 			serialTimer.schedule(new TimerTask() {
 				
 				@Override
 				public void run() {
 					// TODO Auto-generated method stub
 					i = i+1;
 					LogUtil.LogI(TAG, i+"  AT01test_item:"+test_item);				
 					sendData("AT01");
 					
 				}
 			}, 500,500);
 		}
		
 
	}
 	
	/**���ڲ������*/
	public void test_serial_complete(){
		
   	}
	
	
	/**wifi����*/
	private WifiAdmin mWifiAdmin;
 	public void test_wifi(){
 		mWifiAdmin = new WifiAdmin(ConsoleActivity.this);  
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
		            	Log.d(TAG, "wifi���Գɹ�");
		            	test_wifi_complete("suc");
		            }else{
		            	Log.d(TAG, "wifi����ʧ��");
		            	test_wifi_complete("fail");
		            }
		            wifi_timer.cancel();
		                
		   	 	}
		   	 	else{
		   	 	   test_wifi_complete("fail");
		   	 	   wifi_timer.cancel();
				}
			}
		}, 500, 1000);

 		 
	}
 	/**wifi�������*/
	public void test_wifi_complete(String wifiResult){
		
 		if("suc".equals(wifiResult)){			
			sendData("AE1411");
		}else if("fail".equals(wifiResult)){
			sendData("AE1422");
			closeSerialPort();
		}
	}

	
	/**������*/
	public void test_changing(){
		if(changeingFlag){
			sendData("AE0211");
		}else{
			sendData("AE0222");
			closeSerialPort();
		}
	}
 	
 	
 	
	/**���Ȳ���*/
	MediaPlayer mp=null;
	public void test_speaker(){
		 if(mp == null){			 
			 mp = MediaPlayer.create(this, R.raw.lediledi);//tada
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
        mp.setLooping(false);
        mp.start();
        
        //test_speaker_complete();
	}
	
	public void test_speaker_complete(){
		try {
			Thread.sleep(1500);//1�������mic
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(mp!=null){   
	        	test_mic();//�������֮�����mic����
	     }else{
	        	sendData("AC0722");
	        	closeSerialPort();
	     }
	}
	
	
	/**¼������*/
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

//        try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}//1��֮��ֹͣ¼��
//

        /**ֹͣ����Դ�ļ�*/
//		if(mp!=null){
//	        	mp.stop();
//	        	mp.release();
//	        	mp=null;
//		}

	}
 	
	public void test_mic_complete(){
		 if(mp!=null){   
	        	sendData("AC0711");//¼�����Գɹ�
	     }else{
        	sendData("AC0722");
        	closeSerialPort();
	     }
	}
	
	

	

//	boolean touche_complete_flag = false;
	public void test_touch_complete(String value){
 			if("suc".equals(value)){
				  sendData("AE1511");
			}else if("fail".equals(value)){
				  sendData("AE1522");
				  closeSerialPort();
			}
		 		  
	}
	


	

 
	
	
	/**���в��Խ���*/
	public void all_test_complete(){
		  sendData("AD11");
		  if(mp!=null){   //�ص�¼��
	        	mp.stop();
	        	mp.release();	
	        	mp=null;
		  }
		  Perference.saveCompletePreference(this, testCompleteTime+1);
		  try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  closeSerialPort();
//		  this.finish();
		  
//		  Uri packageURI = Uri.parse("package:android_serialport_api.sample");        
//		  Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);        
//		  startActivity(uninstallIntent);  
		  clientUninstall("android_serialport_api.sample");
 	}
	
	
	/**
     * ��Ĭж��
     */
    private static boolean clientUninstall(String packageName){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value); 
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
	
    
    private static boolean returnResult(int value){
        // ���ɹ�  
        if (value == 0) {
            return true;
        } else if (value == 1) { // ʧ��
            return false;
        } else { // δ֪���
            return false;
        }  
    }
	
	
	/**�رմ���*/
	public void closeSerialPort(){
		 mApplication.closeSerialPort();
		 if(mp!=null){   //�ص�¼��
	        	mp.stop();
	        	mp.release();	
	        	mp=null;
		  }
		  this.finish();
	}
	
 
	
 
	
	
	

 
    protected static final int[] KEYCODES = { 25, 24, 82, 3, 4, 84 };//4���ؼ�  82���ü�  24������   25������
	@Override
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	  {
  		    LogUtil.LogI(TAG, "  paramInt:"+paramInt+"test_item:"+test_item);		
		    if("touch".equals(test_item)){		
 		    	if(paramInt == 4){//���ؼ�
		    		   test_touch_complete("suc");
	    			return true;
		    	}
 		    	test_touch_complete("fail");
		    	return false;
 		    }
		    
 		    return true;
	   }

	
    private BroadcastReceiver mbatteryReceiver=new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) 
        {
            String action =intent.getAction();
            if(Intent.ACTION_BATTERY_CHANGED.equals(action));
            {
                int status=intent.getIntExtra("status",BatteryManager.BATTERY_STATUS_UNKNOWN);
                LogUtil.LogI(TAG, "status:"+status);
                if(status==BatteryManager.BATTERY_STATUS_CHARGING)
                {
                 	changeingFlag = true;
                }
                else
                {
                 	changeingFlag = false;
                }
            }
        }
    };
	public void send_test(String s){

		//String s="FF FF A0 FF FF 0F 0F 01 4A F8";
		String[] s1=s.split(" ");
		byte[] bytes=hexStringToBytes(s);
		byte cheekBit=0;
		for(int i=2;i<4;i++){
			cheekBit+=bytes[i];
		}
		cheekBit=(byte) ~(cheekBit);
		System.out.printf("%x\n",cheekBit);
		byte a = (byte)0xaF;
		byte b=(byte)0x0F;
		byte c=(byte)(a&b);
		String tString = Integer.toBinaryString((c & 0xFF) + 0x100).substring(1);
		bytes[4]=cheekBit;
		printHexString(bytes);

	}

	int counts =0;
	public void fgslight(){

		if(counts==0)
		{
			String s="31";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==1)
		{
			String s="32";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==2)
		{
			String s="33";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==3)
		{
			String s="34";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==4)
		{
			String s="37";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==5)
		{
			String s="35";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==6)
		{
			String s="36";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		else if(counts==7)
		{
			String s="37";
			byte[] bytes=hexStringToBytes(s);
			printHexString(bytes);
		}
		counts++;
		if(counts>7)
			counts=0;

	}
	public void testmain_zddk(){

		String s="48 05 00 00 4D";
		byte[] bytes=hexStringToBytes(s);

		printHexString(bytes);

	}





	public void testmain1(){
		//String s="ff ff fe 03 ff 03 ff 03 35 fb ";//
		//String s="ff ff fe 03 ff 03 ff 03 ff fb ";
		String s="ff ff fe 00 00 00 00 00 00 01";

		//String s="FF FF A0 FF FF AB CD DC BA 53 ";
		//String s="ff ff fe 01 ff 03 ff 03 ff fb ";
		//String s="FF FF A0 FF FF 0F 0F 01 4A F8";
		String[] s1=s.split(" ");
		byte[] bytes=hexStringToBytes(s);
		byte cheekBit=0;
		for(int i=2;i<9;i++){
			cheekBit+=bytes[i];
		}
		cheekBit=(byte) ~(cheekBit);
		System.out.printf("%x\n",cheekBit);
		byte a = (byte)0xaF;
		byte b=(byte)0x0F;
		byte c=(byte)(a&b);
		String tString = Integer.toBinaryString((c & 0xFF) + 0x100).substring(1);
		bytes[9]=cheekBit;
		printHexString(bytes);

	}
	public void testmain2(){
		String s="ff ff fe 01 ff 01 ff 01 ff 01 ";

		String[] s1=s.split(" ");
		byte[] bytes=hexStringToBytes(s);
		byte cheekBit=0;
		for(int i=2;i<9;i++){
			cheekBit+=bytes[i];
		}
		cheekBit=(byte) ~(cheekBit);
		System.out.printf("%x\n",cheekBit);
		byte a = (byte)0xaF;
		byte b=(byte)0x0F;
		byte c=(byte)(a&b);
		String tString = Integer.toBinaryString((c & 0xFF) + 0x100).substring(1);
		bytes[9]=cheekBit;
		printHexString(bytes);

	}
	public void testmain3(){
		//String s="ff ff fe 00 00 00 00 00 00 01";
		String s="ff ff fe 03 ff 03 ff 03 36 fb ";
		String[] s1=s.split(" ");
		byte[] bytes=hexStringToBytes(s);
		byte cheekBit=0;
		for(int i=2;i<9;i++){
			cheekBit+=bytes[i];
		}
		cheekBit=(byte) ~(cheekBit);
		System.out.printf("%x\n",cheekBit);
		byte a = (byte)0xaF;
		byte b=(byte)0x0F;
		byte c=(byte)(a&b);
		String tString = Integer.toBinaryString((c & 0xFF) + 0x100).substring(1);
		bytes[9]=cheekBit;
		printHexString(bytes);

	}
	public byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString=hexString.replaceAll(" ", "");
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;

		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	private byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public  void printHexString( byte[] b) {
		for (int i = 0; i < b.length; i++) {
			try {
				mOutputStreamLoopBack.write(b[i]);
			}catch (IOException e) {
				Log.i(TAG, "IOException////");
				e.printStackTrace();
			}

			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase()+" ");
		}

	}
	public class MyThread extends Thread {

		//�̳�Thread�࣬����д��run����
		private final static String TAG = "My Thread ===> ";
		public void run(){
			Log.d(TAG, "run");
			for(int i = 0; i<100; i++)
			{
				Log.e(TAG, Thread.currentThread().getName() + "i =  " + i);
			}
		}
	}
}
