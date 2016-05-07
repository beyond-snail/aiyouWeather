package com.aiyouWeather.app.receiver;

import com.aiyouWeather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Intent intent = new Intent(arg0, AutoUpdateService.class);
		arg0.startService(intent);
	}

}
