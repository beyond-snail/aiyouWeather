package com.aiyouWeather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiyou.aiyouweather.R;
import com.aiyouWeather.app.db.AiyouWeatherDB;
import com.aiyouWeather.app.model.City;
import com.aiyouWeather.app.model.Country;
import com.aiyouWeather.app.model.Province;
import com.aiyouWeather.app.util.HttpCallbackListener;
import com.aiyouWeather.app.util.HttpUtil;
import com.aiyouWeather.app.util.Utility;

public class ChooseAreaActivity extends Activity{
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private AiyouWeatherDB aiyouWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	/**
	 * 省列表
	 */
	private List<Province> provincesList;
	
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<Country> countryList;
	
	/**
	 * 选中的省份
	 */
	private Province selectProvince;
	
	/**
	 * 选中的城市
	 */
	private City selectCity;
	
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose__area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		aiyouWeatherDB = AiyouWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE){
					selectProvince = provincesList.get(arg2);
					queryCities();
				}else if (currentLevel == LEVEL_CITY){
					selectCity = cityList.get(arg2);
					queryCountries();
				}
			}
		});
		queryProvinces();//加载省级数据
	}

	/**
	 * 查询全国所有省，优先从数据库中查询， 如果没有查询到再去服务器上查询
	 */
	private void queryProvinces() {
		provincesList = aiyouWeatherDB.loadProvince();
		if (provincesList.size() > 0){
			dataList.clear();
			for (Province province : provincesList){
				dataList.add(province.getProvinceName());
			}
			//刷新
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内所有的市， 优先从数据库中查询， 如果没有查到再去服务器上查询。
	 */
	protected void queryCities() {
		cityList = aiyouWeatherDB.loadCities(selectProvince.getId());
		if (cityList.size() > 0){
			dataList.clear();
			for (City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectProvince.getProvinceCode(), "province");
		}
	}

	
	/**
	 * 查询选中市内所有的县，优先从数据库查询， 如果没有查询到再去服务器上查询
	 */
	protected void queryCountries() {
		countryList = aiyouWeatherDB.loadCountries(selectCity.getId());
		if (countryList.size() > 0){
			dataList.clear();
			for (Country country : countryList){
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else{
			queryFromServer(selectCity.getCityCode(), "city");
		}
	}
	
	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * @param object
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void OnFinish(String response) {
				boolean result = false;
				if ("province".equals(type)){
					result = Utility.handleProvincesResponse(aiyouWeatherDB, response);
				}else if ("city".equals(type)){
					result = Utility.handleCityResponse(aiyouWeatherDB, response, selectProvince.getId());
				}else if ("country".equals(type)){
					result = Utility.handleCountriesResponse(aiyouWeatherDB, response, selectCity.getId());
				}
				
				if (result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//关闭对话框
							closeProgressDialog();
							if ("province".equals(type)){
								queryProvinces();
							}else if ("city".equals(type)){
								queryCities();
							}else if ("country".equals(type)){
								queryCountries();
							}
						}
					});
				}
			}
			
			@Override
			public void OnError(Exception e) {
				//通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//关闭对话框
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Bcak按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_CITY){
			queryProvinces();
		}else if (currentLevel == LEVEL_COUNTRY){
			queryCities();
		}else{
			finish();
		}
	}
}
