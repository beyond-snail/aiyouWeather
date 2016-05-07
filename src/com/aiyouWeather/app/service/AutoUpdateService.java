package com.aiyouWeather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.aiyouWeather.app.receiver.AutoUpdateReceiver;
import com.aiyouWeather.app.util.HttpCallbackListener;
import com.aiyouWeather.app.util.HttpUtil;
import com.aiyouWeather.app.util.Utility;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		//开启一个广播
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000; //这是8小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}
	protected void updateWeather() {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void OnFinish(String response) {
					// TODO Auto-generated method stub
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				}
				
				@Override
				public void OnError(Exception e) {
					// TODO Auto-generated method stub
					
				}
			});
	}

}
