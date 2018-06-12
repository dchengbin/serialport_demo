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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android_serialport_api.LogUtil;
import android_serialport_api.SerialPort;

public abstract class SerialPortActivity extends Activity {

	private String TAG = "SerialPortActivity";
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;
	protected ReadThread mReadThread;
	
	
	
	protected SerialPort mSerialPortLoopBack;
	protected OutputStream mOutputStreamLoopBack;
	protected InputStream mInputStreamLoopBack;
	protected ReadThreadLoopBack mReadThreadLoopBack;

	class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();					
 			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
 					if (mInputStream == null) return;
 					LogUtil.LogI(TAG, "11111");
 					size = mInputStream.read(buffer);
 					
 					LogUtil.LogI(TAG, "2222 size:"+size+"  "+Arrays.toString(buffer));
  
					if (size > 0) {
						LogUtil.LogI(TAG, "333333");
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
//			super.run();
 			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
 					if (mInputStreamLoopBack == null) return;
 					LogUtil.LogI(TAG, "///1111 size:"+"///mInputStreamLoopBack:"+mInputStreamLoopBack);
 					size = mInputStreamLoopBack.read(buffer);
 					LogUtil.LogI(TAG, "///222222222 size:"+size+"  "+Arrays.toString(buffer));
 					 					
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

	
	/**发送数据*/
	public void sendData(String value){
		int i;
		CharSequence t = value;
		char[] text = new char[t.length()];
		for (i=0; i<t.length(); i++) {
			text[i] = t.charAt(i);
		}
		try {
			mOutputStream.write(new String(text).getBytes("utf-8"));
//			mOutputStream.write('\n');
		} catch (IOException e) {
			Log.i(TAG, "IOException");
			e.printStackTrace();
		}
	}
	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (Application) getApplication();
		try {
//			mSerialPort = mApplication.getSerialPort();
//			mOutputStream = mSerialPort.getOutputStream();
//			mInputStream = mSerialPort.getInputStream();
//
//			Log.e(TAG, "mSerialPort:"+mSerialPort);
//			Log.e(TAG, "mOutputStream:"+mOutputStream);
//			Log.e(TAG, "mInputStream:"+mInputStream);
//			/* Create a receiving thread */
//			mReadThread = new ReadThread();
//			mReadThread.start();
			
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
			Log.e(TAG, " SecurityException");
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
			Log.e(TAG, "IOException");
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
			Log.e(TAG, "InvalidParameterException");
		}
	}

 	protected abstract void onDataReceived(final byte[] buffer, final int size);
	
 	protected abstract void onDataReceivedLoopBack(final byte[] buffer, final int size);

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
//		if (mReadThread != null)
//			mReadThread.interrupt();
		if(mReadThreadLoopBack != null){
			mReadThreadLoopBack.interrupt();
		}
		mApplication.closeSerialPort();
		mSerialPort = null;
		mSerialPortLoopBack = null;
		super.onDestroy();
	}
}
