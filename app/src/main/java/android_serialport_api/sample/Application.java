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

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class Application extends android.app.Application {

	public String TAG = "Application";
	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;//监听通信串口       1 串口通信  2.wifi  3.喇叭录音  4.触摸  
	private SerialPort mSerialPortLooPback = null;//监听回路串口

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
/*			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			Log.e(TAG, "path:"+path);
			Log.e(TAG, "baudrate:"+baudrate);
			 Check parameters 
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}*/

			/* Open the serial port */
			//设置串口路径和波特率
			String path = "/dev/ttyMT0";
			int baudrate = 115200;
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			Log.e(TAG, "mSerialPort:"+mSerialPort);
		}
		return mSerialPort;
	}
	
	
	public SerialPort getSerialPortLoopBack() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPortLooPback == null) {
			/* Open the serial port */
			//设置串口路径和波特率
			String path = "/dev/ttyMT0";
			int baudrate = 115200;
			mSerialPortLooPback = new SerialPort(new File(path), baudrate, 0);
			Log.e(TAG, "mSerialPortLooPback:"+mSerialPortLooPback);
		}
		return mSerialPortLooPback;
	}
	
 

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		
		if(mSerialPortLooPback!=null){
			mSerialPortLooPback.close();
			mSerialPortLooPback = null;
		}
	}
}
