package com.aiyouWeather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.aiyouWeather.app.db.AiyouWeatherDB;
import com.aiyouWeather.app.model.City;
import com.aiyouWeather.app.model.Country;
import com.aiyouWeather.app.model.Province;

public class Utility {
	
	/**
	 * �����ʹ�����������ص�ʡ����
	 */
	public static boolean handleProvincesResponse(AiyouWeatherDB aiyouWeatherDB, 
			String response){
		
		if (!TextUtils.isEmpty(response)){
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0){
				for (String p : allProvince){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���������������ݱ��浽Province��
					aiyouWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCityResponse(AiyouWeatherDB aiyouWeatherDB,
			String response, int provinceId){
		if (!TextUtils.isEmpty(response)){
			String[] allCites = response.split(",");
			
			if (allCites != null && allCites.length > 0){
				for (String c : allCites){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//���������������ݱ��浽City����
					aiyouWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountriesResponse(AiyouWeatherDB aiyouWeatherDB,
			String response, int cityId){
		
		if (!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0){
				for (String c : allCounties){
					String[] array = c.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					//���������������ݴ洢��Country����
					aiyouWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			//������Ϣ
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �����������ص�����������Ϣ�洢��sharePreferences�ļ�
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}
