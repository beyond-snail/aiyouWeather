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
	 * ʡ�б�
	 */
	private List<Province> provincesList;
	
	/**
	 * ���б�
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<Country> countryList;
	
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectProvince;
	
	/**
	 * ѡ�еĳ���
	 */
	private City selectCity;
	
	/**
	 * ��ǰѡ�еļ���
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
		queryProvinces();//����ʡ������
	}

	/**
	 * ��ѯȫ������ʡ�����ȴ����ݿ��в�ѯ�� ���û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces() {
		provincesList = aiyouWeatherDB.loadProvince();
		if (provincesList.size() > 0){
			dataList.clear();
			for (Province province : provincesList){
				dataList.add(province.getProvinceName());
			}
			//ˢ��
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ��ʡ�����е��У� ���ȴ����ݿ��в�ѯ�� ���û�в鵽��ȥ�������ϲ�ѯ��
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
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�� ���û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
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
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//�رնԻ���
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
				//ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//�رնԻ���
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��...", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ�Ի���
	 */
	private void showProgressDialog() {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ�����...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رնԻ���
	 */
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Bcak���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
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
