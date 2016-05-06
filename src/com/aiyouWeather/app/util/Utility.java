package com.aiyouWeather.app.util;

import android.text.TextUtils;

import com.aiyouWeather.app.db.AiyouWeatherDB;
import com.aiyouWeather.app.model.City;
import com.aiyouWeather.app.model.Country;
import com.aiyouWeather.app.model.Province;

public class Utility {
	
	/**
	 * 解析和处理服务器返回的省数据
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
					//将解析出来的数据保存到Province表
					aiyouWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
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
					//将解析出来的数据保存到City表中
					aiyouWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
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
					//将解析出来的数据存储到Country表中
					aiyouWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		
		return false;
	}
}
