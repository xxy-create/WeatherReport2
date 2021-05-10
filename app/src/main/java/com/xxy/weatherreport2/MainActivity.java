package com.xxy.weatherreport2;

import android.Manifest;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xxy.mvplibrary.utils.LiWindow;
import com.xxy.weatherreport2.bean.*;
import com.xxy.weatherreport2.contract.WeatherContract;
import com.xxy.weatherreport2.utils.*;
import com.xxy.weatherreport2.adapter.*;
import com.xxy.mvplibrary.mvp.MvpActivity;
import com.xxy.mvplibrary.view.WhiteWindmills;
import com.xxy.mvplibrary.view.RoundProgressBar;
import com.xxy.mvplibrary.utils.ObjectUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;

import static com.xxy.mvplibrary.utils.RecyclerViewAnimation.runLayoutAnimationRight;
import static com.xxy.mvplibrary.utils.RecyclerViewAnimation.runLayoutAnimationRight;

public class MainActivity extends MvpActivity<WeatherContract.WeatherPresenter> implements WeatherContract.IWeatherView {

    @BindView(R.id.tv_info)
    TextView tvInfo;//天气状况
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;//温度
    @BindView(R.id.tv_low_height)
    TextView tvLowHeight;//最高温和最低温
    @BindView(R.id.tv_city)
    TextView tvCity;//城市
    @BindView(R.id.tv_old_time)
    TextView tvOldTime;//最近更新时间
    @BindView(R.id.rv)
    RecyclerView rv;//天气预报显示列表
    @BindView(R.id.ww_big)
    WhiteWindmills wwBig;//大风车
    @BindView(R.id.ww_small)
    WhiteWindmills wwSmall;//小风车
    @BindView(R.id.tv_wind_direction)
    TextView tvWindDirection;//风向
    @BindView(R.id.tv_wind_power)
    TextView tvWindPower;//风力
    @BindView(R.id.iv_city_select)
    ImageView ivcityselect; //城市图标ID
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;//刷新布局
    @BindView(R.id.iv_location)
    ImageView ivLocation;//定位图标
    @BindView(R.id.rv_hourly)
    RecyclerView rvHourly;//逐小时天气显示列表
    @BindView(R.id.rpb_aqi)
    RoundProgressBar rpbAqi;//污染指数圆环
    @BindView(R.id.tv_pm10)
    TextView tvPm10;//PM10
    @BindView(R.id.tv_pm25)
    TextView tvPm25;//PM2.5
    @BindView(R.id.tv_no2)
    TextView tvNo2;//二氧化氮
    @BindView(R.id.tv_so2)
    TextView tvSo2;//二氧化硫
    @BindView(R.id.tv_o3)
    TextView tvO3;//臭氧
    @BindView(R.id.tv_co)
    TextView tvCo;//一氧化碳

    private boolean flag = true;//图标显示标识,true显示，false不显示,只有定位的时候才为true,切换城市和常用城市都为false


    private RxPermissions rxPermissions;//权限请求框架
    //定位器
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    List<WeatherForecastResponse.HeWeather6Bean.DailyForecastBean> mList;//初始化数据源
    WeatherForecastAdapter mAdapter;//初始化适配器
    List<HourlyResponse.HeWeather6Bean.HourlyBean> mListHourly;//初始化数据源 -> 逐小时天气预报
    WeatherHourlyAdapter mAdapterHourly;//初始化适配器 逐小时天气预报

    //城市弹窗数据渲染
    private List<String> list;//字符串列表
    private List<CityResponse> provinceList;//省列表数据
    private List<CityResponse.CityBean> citylist;//市列表数据
    private List<CityResponse.CityBean.AreaBean> arealist;//区/县列表数据
    ProvinceAdapter provinceAdapter;//省数据适配器
    CityAdapter cityAdapter;//市数据适配器
    AreaAdapter areaAdapter;//县/区数据适配器
    String provinceTitle;//标题
    LiWindow liWindow;//自定义弹窗

    private String district;//改为全局的静态变量，方便更换城市之后也能进行下拉刷新
    private String city;//市 国控站点数据 用于请求空气质量
    //数据初始化  主线程，onCreate方法可以删除了，把里面的代码移动这个initData下面
    @Override
    public void initData(Bundle savedInstanceState) {
        //因为这个框架里面已经放入了绑定，所以这行代码可以注释掉了。
        //ButterKnife.bind(this);
        StatusBarUtil.transparencyBar(context);//透明状态栏
        initList();//天气预报列表初试化
        rxPermissions = new RxPermissions(this);//实例化这个权限请求框架，否则会报错
        permissionVersion();//权限判断
    }

    //绑定布局文件
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    //绑定Presenter ，这里不绑定会报错
    @Override
    protected WeatherContract.WeatherPresenter createPresent() {
        return new WeatherContract.WeatherPresenter();
    }

    //权限判断
    private void permissionVersion() {
        if (Build.VERSION.SDK_INT >= 23) {//6.0或6.0以上
            //动态权限申请
            permissionsRequest();
        } else {//6.0以下
            //发现只要权限在AndroidManifest.xml中注册过，均会认为该权限granted  提示一下即可
            ToastUtils.showShortToast(this, "你的版本在Android6.0以下，不需要动态申请权限。");
        }
    }

    //动态权限申请
    private void permissionsRequest() {//使用这个框架需要制定JDK版本，建议用1.8
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {//申请成功
                        //得到权限之后开始定位
                        startLocation();
                    } else {//申请失败
                        ToastUtils.showShortToast(this, "权限未开启");
                    }
                });
    }

    //定位
    private void startLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();

        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
        option.setNeedNewVersionRgc(true);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(option);
        //启动定位
        mLocationClient.start();

    }


    /**
     * 定位结果返回
     */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取区/县
            district = location.getDistrict();
            //获取市
            city = location.getCity();

            //在数据请求之前放在加载等待弹窗，返回结果后关闭弹窗
            showLoadingDialog();
            //获取今天的天气数据
            mPresent.todayWeather(context, district);
            //获取天气预报数据
            mPresent.weatherForecast(context, district);
            //获取逐小时天气预报
            mPresent.hourly(context,district);
            //获取空气质量数据
            mPresent.airNowCity(context,city);

            //下拉刷新
            refresh.setOnRefreshListener(refreshLayout -> {
                //获取今天的天气数据
                mPresent.todayWeather(context,district);
                //获取天气预报数据
                mPresent.weatherForecast(context,district);
                //获取逐小时天气预报
                mPresent.hourly(context,district);
                //获取空气质量数据
                mPresent.airNowCity(context,city);
            });
        }
    }

    //查询当天天气，请求成功后的数据返回
    @Override
    public void getTodayWeatherResult(Response<TodayResponse> response) {
        dismissLoadingDialog();//关闭弹窗
        //数据返回后关闭定位
        mLocationClient.stop();
        if (response.body().getHeWeather6().get(0).getBasic() != null) {//得到数据不为空则进行数据显示
            //数据渲染显示出来
            tvTemperature.setText(response.body().getHeWeather6().get(0).getNow().getTmp());//温度

            if(flag){
                ivLocation.setVisibility(View.VISIBLE);//显示定位图标
            }else{
                ivLocation.setVisibility(View.GONE);//不显示定位图标
            }
            tvCity.setText(response.body().getHeWeather6().get(0).getBasic().getLocation());//城市
            tvInfo.setText(response.body().getHeWeather6().get(0).getNow().getCond_txt());//天气状况
            //修改上次更新时间的结果显示 更加人性化
            String datatime = response.body().getHeWeather6().get(0).getUpdate().getLoc();//赋值
            String time = datatime.substring(11);//去掉前面的字符，保留后面所有的字符 剩下22:00
            tvOldTime.setText("上次更新时间：" + WeatherUtil.showTimeInfo(time)+time);

            tvWindDirection.setText("风向     " + response.body().getHeWeather6().get(0).getNow().getWind_dir());//风向
            tvWindPower.setText("风力     " + response.body().getHeWeather6().get(0).getNow().getWind_sc() + "级");//风力
            wwBig.startRotate();//大风车开始转动
            wwSmall.startRotate();//小风车开始转动
        } else {
            ToastUtils.showShortToast(context, response.body().getHeWeather6().get(0).getStatus());
        }
    }

    //查询天气预报，请求成功后的数据返回
    @Override
    public void getWeatherForecastResult(Response<WeatherForecastResponse> response) {
        if (("ok").equals(response.body().getHeWeather6().get(0).getStatus())) {
            //最低温和最高温
            tvLowHeight.setText(response.body().getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min() + " / " +
                    response.body().getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max() + "℃");

            if (response.body().getHeWeather6().get(0).getDaily_forecast() != null) {
                List<WeatherForecastResponse.HeWeather6Bean.DailyForecastBean> data
                        = response.body().getHeWeather6().get(0).getDaily_forecast();
                mList.clear();//添加数据之前先清除
                mList.addAll(data);//添加数据
                mAdapter.notifyDataSetChanged();//刷新列表
            } else {
                ToastUtils.showShortToast(context, "天气预报数据为空");
            }
        } else {
            ToastUtils.showShortToast(context, response.body().getHeWeather6().get(0).getStatus());
        }
    }

    //逐小时天气预报返回
    @Override
    public void getHourlyResult(Response<HourlyResponse> response) {
        dismissLoadingDialog();//关闭弹窗
        if (("ok").equals(response.body().getHeWeather6().get(0).getStatus())) {
            if(response.body().getHeWeather6().get(0).getHourly()!=null){
                List<HourlyResponse.HeWeather6Bean.HourlyBean> data
                        = response.body().getHeWeather6().get(0).getHourly();
                mListHourly.clear();;//添加数据之前先清除
                mListHourly.addAll(data);//添加数据
                mAdapterHourly.notifyDataSetChanged();//刷新列表
            }else{
                ToastUtils.showShortToast(context,"逐小时预报数据为空");
            }
        } else {
            ToastUtils.showShortToast(context, response.body().getHeWeather6().get(0).getStatus());
        }
    }

    //空气质量数据返回
    @Override
    public void getAirNowCityResult(Response<AirNowCityResponse> response) {
        dismissLoadingDialog();//关闭弹窗
        if (("ok").equals(response.body().getHeWeather6().get(0).getStatus())) {
            //UI显示
            AirNowCityResponse.HeWeather6Bean.AirNowCityBean data = response.body().getHeWeather6().get(0).getAir_now_city();
            if (!ObjectUtils.isEmpty(data) && data != null) {
                //污染指数
                rpbAqi.setMaxProgress(500);//最大进度，用于计算
                rpbAqi.setMinText("0");//设置显示最小值
                rpbAqi.setMinTextSize(32f);
                rpbAqi.setMaxText("500");//设置显示最大值
                rpbAqi.setMaxTextSize(32f);
                rpbAqi.setProgress(Float.valueOf(data.getAqi()));//当前进度
                rpbAqi.setArcBgColor(getResources().getColor(R.color.arc_bg_color));//圆弧的颜色
                rpbAqi.setProgressColor(getResources().getColor(R.color.arc_progress_color));//进度圆弧的颜色
                rpbAqi.setFirstText(data.getQlty());//空气质量描述  取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                rpbAqi.setFirstTextSize(44f);
                rpbAqi.setSecondText(data.getAqi());//空气质量值
                rpbAqi.setSecondTextSize(64f);
                rpbAqi.setMinText("0");
                rpbAqi.setMinTextColor(getResources().getColor(R.color.arc_progress_color));

                tvPm10.setText(data.getPm10());//PM10
                tvPm25.setText(data.getPm25());//PM2.5
                tvNo2.setText(data.getNo2());//二氧化氮
                tvSo2.setText(data.getSo2());//二氧化硫
                tvO3.setText(data.getO3());//臭氧
                tvCo.setText(data.getCo());//一氧化碳
            }
        } else {
            ToastUtils.showShortToast(context, response.body().getHeWeather6().get(0).getStatus());
        }
    }

    /**
     * 页面销毁时
     */
    @Override
    public void onDestroy() {
        wwBig.stop();//停止大风车
        wwSmall.stop();//停止小风车
        super.onDestroy();
    }


    /**
     * 初始化天气预报数据列表
     */
    private void initList() {
        mList = new ArrayList<>();//声明为ArrayList
        mAdapter = new WeatherForecastAdapter(R.layout.item_weather_forecast_list, mList);//为适配器设置布局和数据源
        LinearLayoutManager manager = new LinearLayoutManager(context);//布局管理,默认是纵向
        rv.setLayoutManager(manager);//为列表配置管理器
        rv.setAdapter(mAdapter);//为列表配置适配器

        //逐小时天气预报
        mListHourly = new ArrayList<>();
        mAdapterHourly = new WeatherHourlyAdapter(R.layout.item_weather_hourly_list,mListHourly);
        LinearLayoutManager managerHourly = new LinearLayoutManager(context);
        managerHourly.setOrientation(RecyclerView.HORIZONTAL);//设置列表为横向
        rvHourly.setLayoutManager(managerHourly);
        rvHourly.setAdapter(mAdapterHourly);
    }

    /**
     * 城市弹窗
     */
    private void showCityWindow() {
        provinceList = new ArrayList<>();
        citylist = new ArrayList<>();
        arealist = new ArrayList<>();
        list = new ArrayList<>();
        liWindow = new LiWindow(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.window_city_list, null);
        ImageView areaBack = (ImageView) view.findViewById(R.id.iv_back_area);
        ImageView cityBack = (ImageView) view.findViewById(R.id.iv_back_city);
        TextView windowTitle = (TextView) view.findViewById(R.id.tv_title);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        liWindow.showRightPopupWindow(view);

        initCityData(recyclerView,areaBack,cityBack,windowTitle);//加载城市列表数据
    }

    /**
     * 省市县数据渲染
     * @param recyclerView  列表
     * @param areaBack 区县返回
     * @param cityBack 市返回
     * @param windowTitle  窗口标题
     */
    private void initCityData(RecyclerView recyclerView,ImageView areaBack,ImageView cityBack,TextView windowTitle) {
        //初始化省数据 读取省数据并显示到列表中
        try {
            InputStream inputStream = getResources().getAssets().open("City.txt");//读取数据
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String lines = bufferedReader.readLine();
            while (lines != null) {
                stringBuffer.append(lines);
                lines = bufferedReader.readLine();
            }

            final JSONArray Data = new JSONArray(stringBuffer.toString());
            //循环这个文件数组、获取数组中每个省对象的名字
            for (int i = 0; i < Data.length(); i++) {
                JSONObject provinceJsonObject = Data.getJSONObject(i);
                String provinceName = provinceJsonObject.getString("name");
                CityResponse response = new CityResponse();
                response.setName(provinceName);
                provinceList.add(response);
            }

            //定义省份显示适配器
            provinceAdapter = new ProvinceAdapter(R.layout.item_city_list, provinceList);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(provinceAdapter);
            provinceAdapter.notifyDataSetChanged();
            runLayoutAnimationRight(recyclerView);//动画展示
            provinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    try {
                        //返回上一级数据
                        cityBack.setVisibility(View.VISIBLE);
                        cityBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recyclerView.setAdapter(provinceAdapter);
                                provinceAdapter.notifyDataSetChanged();
                                cityBack.setVisibility(View.GONE);
                                windowTitle.setText("中国");
                            }
                        });

                        //根据当前位置的省份所在的数组位置、获取城市的数组
                        JSONObject provinceObject = Data.getJSONObject(position);
                        windowTitle.setText(provinceList.get(position).getName());
                        provinceTitle = provinceList.get(position).getName();
                        final JSONArray cityArray = provinceObject.getJSONArray("city");

                        //更新列表数据
                        if (citylist != null) {
                            citylist.clear();
                        }

                        for (int i = 0; i < cityArray.length(); i++) {
                            JSONObject cityObj = cityArray.getJSONObject(i);
                            String cityName = cityObj.getString("name");
                            CityResponse.CityBean response = new CityResponse.CityBean();
                            response.setName(cityName);
                            citylist.add(response);
                        }

                        cityAdapter = new CityAdapter(R.layout.item_city_list, citylist);
                        LinearLayoutManager manager1 = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(manager1);
                        recyclerView.setAdapter(cityAdapter);
                        cityAdapter.notifyDataSetChanged();
                        runLayoutAnimationRight(recyclerView);

                        cityAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                try {
                                    //返回上一级数据
                                    areaBack.setVisibility(View.VISIBLE);
                                    areaBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            recyclerView.setAdapter(cityAdapter);
                                            cityAdapter.notifyDataSetChanged();
                                            areaBack.setVisibility(View.GONE);
                                            windowTitle.setText(provinceTitle);
                                            arealist.clear();
                                        }
                                    });
                                    //获取区县的上级市，用于请求空气质量数据API接口
                                    city = citylist.get(position).getName();
                                    //根据当前城市数组位置 获取地区数据
                                    windowTitle.setText(citylist.get(position).getName());
                                    JSONObject cityJsonObj = cityArray.getJSONObject(position);
                                    JSONArray areaJsonArray = cityJsonObj.getJSONArray("area");
                                    if (arealist != null) {
                                        arealist.clear();
                                    }
                                    if(list != null){
                                        list.clear();
                                    }
                                    for (int i = 0; i < areaJsonArray.length(); i++) {
                                        list.add(areaJsonArray.getString(i));
                                    }
                                    Log.i("list", list.toString());
                                    for (int j = 0; j < list.size(); j++) {
                                        CityResponse.CityBean.AreaBean response = new CityResponse.CityBean.AreaBean();
                                        response.setName(list.get(j).toString());
                                        arealist.add(response);
                                    }
                                    areaAdapter = new AreaAdapter(R.layout.item_city_list, arealist);
                                    LinearLayoutManager manager2 = new LinearLayoutManager(context);

                                    recyclerView.setLayoutManager(manager2);
                                    recyclerView.setAdapter(areaAdapter);
                                    areaAdapter.notifyDataSetChanged();
                                    runLayoutAnimationRight(recyclerView);

                                    areaAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                                        @Override
                                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                            showLoadingDialog();
                                            district = arealist.get(position).getName();//选中的区县赋值给这个全局变量
                                            mPresent.todayWeather(context, district);//今日天气
                                            mPresent.weatherForecast(context, district);//天气预报
                                            mPresent.hourly(context,district);//逐小时天气
                                            mPresent.airNowCity(context,city);//空气质量数据
                                            flag=false;//切换的城市不属于定位，隐藏定位图标
                                            liWindow.closePopupWindow();

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //点击事件
    @OnClick(R.id.iv_city_select)
    public void onViewClicked() {//显示城市弹窗
        showCityWindow();
    }

    //数据请求失败返回
    @Override
    public void getDataFailed() {
        refresh.finishRefresh();//关闭刷新
        dismissLoadingDialog();//关闭弹窗
        ToastUtils.showShortToast(context,"网络异常");//这里的context是框架中封装好的，等同于this
    }
}

