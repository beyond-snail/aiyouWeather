package com.aiyouWeather.app.util;

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
}
