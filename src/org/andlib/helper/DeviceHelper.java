/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package org.andlib.helper;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 
 * @author meinside@gmail.com
 * @since 10.01.07.
 * 
 * last update 10.01.07.
 *
 */
public class DeviceHelper
{
	public static final int FLAG_INIT_SENSOR_MANAGER = 0x0001;
	public static final int FLAG_INIT_CONNECTIVITY_MANAGER = 0x0002;
	public static final int FLAG_INIT_LOCATION_MANAGER = 0x0004;
	public static final int FLAG_INIT_WIFI_MANAGER = 0x0008;
	public static final int FLAG_INIT_TELEPHONY_MANAGER = 0x0010;
	
	private SensorManager sensorManager = null;
	private ConnectivityManager connectManager = null;
	private LocationManager locationManager = null;
	private WifiManager wifiManager = null;
	private TelephonyManager telManager = null;
	
	/**
	 * 
	 * @param context
	 * @param initFlag flag that indicates which manager to initialize
	 */
	public DeviceHelper(Context context, int initFlag)
	{
		if((initFlag & FLAG_INIT_SENSOR_MANAGER) > 0)
			sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		if((initFlag & FLAG_INIT_CONNECTIVITY_MANAGER) > 0)
			connectManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if((initFlag & FLAG_INIT_LOCATION_MANAGER) > 0)
			locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if((initFlag & FLAG_INIT_WIFI_MANAGER) > 0)
			wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if((initFlag & FLAG_INIT_TELEPHONY_MANAGER) > 0)
			telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	/**
	 * initialize all managers
	 * @param context
	 */
	public DeviceHelper(Context context)
	{
		this(context,
				FLAG_INIT_SENSOR_MANAGER | 
				FLAG_INIT_CONNECTIVITY_MANAGER |  
				FLAG_INIT_LOCATION_MANAGER | 
				FLAG_INIT_WIFI_MANAGER | 
				FLAG_INIT_TELEPHONY_MANAGER);
	}
	
	/**
	 * get all available sensors
	 * @return
	 */
	public List<Sensor> getAllAvailableSensors()
	{
		if(sensorManager == null)
			return null;
		return sensorManager.getSensorList(Sensor.TYPE_ALL);
	}

	/**
	 * 
	 * @param type sensor type (Sensor.TYPE_*)
	 * @return
	 */
	public List<Sensor> getAvailableSensors(int type)
	{
		if(sensorManager == null)
			return null;
		return sensorManager.getSensorList(type);
	}
	
	/**
	 * get a default sensor for given type
	 * @param type sensor type (Sensor.TYPE_*)
	 * @return
	 */
	public Sensor getDefaultSensor(int type)
	{
		if(sensorManager == null)
			return null;
		return sensorManager.getDefaultSensor(type);
	}
	
	/**
	 * check if accelerometer sensor is available on this device
	 * @return
	 */
	public boolean isAccelerometerSensorAvaiable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_ACCELEROMETER);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if gyroscope sensor is available on this device
	 * @return
	 */
	public boolean isGyroscopeSensorAvaiablle()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_GYROSCOPE);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if light sensor is available on this device
	 * @return
	 */
	public boolean isLightSensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_LIGHT);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if magnetic field sensor is available on this device
	 * @return
	 */
	public boolean isMagneticFieldSensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_MAGNETIC_FIELD);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if orientation sensor is available on this device
	 * @return
	 */
	public boolean isOrientationSensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_ORIENTATION);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if pressure sensor is available on this device
	 * @return
	 */
	public boolean isPressureSensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_PRESSURE);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if proximity sensor is available on this device
	 * @return
	 */
	public boolean isProximitySensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_PROXIMITY);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * check if temperature sensor is available on this device
	 * @return
	 */
	public boolean isTemperatureSensorAvailable()
	{
		List<Sensor> sensors = getAvailableSensors(Sensor.TYPE_TEMPERATURE);
		if(sensors != null && sensors.size() > 0)
			return true;
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public NetworkInfo getActiveNetwork()
	{
		if(connectManager == null)
			return null;
		return connectManager.getActiveNetworkInfo();
	}
	
	/**
	 * 
	 * @return active network type (ConnectivityManager.TYPE_*)
	 */
	public int getActiveNetworkType()
	{
		NetworkInfo netInfo = getActiveNetwork();
		if(netInfo != null)
		{
			return netInfo.getType();
		}
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isActiveNetworkConnected()
	{
		NetworkInfo netInfo = getActiveNetwork();
		if(netInfo != null)
		{
			return netInfo.isConnected();
		}
		return false;
	}
	
	/**
	 * check if gps location provider is installed on the device 
	 * @return
	 */
	public boolean isFineLocationAvailable()
	{
		try
		{
			return (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return false;
	}
	
	/**
	 * check if gps location provider is enabled
	 * @return
	 */
	public boolean isFineLocationEnabled()
	{
		try
		{
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return false;
	}
	
	/**
	 * check if cell tower / wi-fi location provider is installed on the device
	 * @return
	 */
	public boolean isCoarseLocationAvailable()
	{
		try
		{
			return (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return false;
	}
	
	/**
	 * check if cell tower / wi-fi location provider is enabled
	 * @return
	 */
	public boolean isCoarseLocationEnabled()
	{
		try
		{
			return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch(Exception e)
		{
			Log.e(LogHelper.where(), e.toString());
		}
		return false;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getDeviceId()
	{
		if(telManager == null)
			return null;
		return telManager.getDeviceId();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPhoneNumber()
	{
		if(telManager == null)
			return null;
		return telManager.getLine1Number();
	}


	/**
	 * 
	 * @return
	 */
	public SensorManager getSensorManager()
	{
		return sensorManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConnectivityManager getConnectivityManager()
	{
		return connectManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public LocationManager getLocationManager()
	{
		return locationManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public WifiManager getWifiManager()
	{
		return wifiManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public TelephonyManager getTelephonyManager()
	{
		return telManager;
	}
}
