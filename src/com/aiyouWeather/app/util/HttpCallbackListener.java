package com.aiyouWeather.app.util;

public interface HttpCallbackListener {
	void OnFinish(String response);
	void OnError(Exception e);
}
