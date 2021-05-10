package com.xxy.weatherreport2.contract;

import android.content.Context;

import com.xxy.weatherreport2.api.ApiService;
import com.xxy.weatherreport2.bean.*;
import com.xxy.mvplibrary.base.BasePresenter;
import com.xxy.mvplibrary.base.BaseView;
import com.xxy.mvplibrary.net.NetCallBack;
import com.xxy.mvplibrary.net.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 天气订阅器
 */
public class WeatherContract {

    public static class WeatherPresenter extends BasePresenter<IWeatherView> {
        /**
         * 当日天气
         *
         * @param context
         * @param location 区/县
         */
        public void todayWeather(final Context context, String location) {
            //得到构建之后的网络请求服务，这里的地址已经拼接完成，只差一个location了
            ApiService service = ServiceGenerator.createService(ApiService.class, 0);
            //设置请求回调  NetCallBack是重写请求回调
            service.getTodayWeather(location).enqueue(new NetCallBack<TodayResponse>() {
                //成功回调
                @Override
                public void onSuccess(Call<TodayResponse> call, Response<TodayResponse> response) {
                    if (getView() != null) {//当视图不会空时返回请求数据
                        getView().getTodayWeatherResult(response);
                    }
                }

                //失败回调
                @Override
                public void onFailed() {
                    if (getView() != null) {//当视图不会空时获取错误信息
                        getView().getDataFailed();
                    }
                }
            });
        }

        /**
         * 天气预报  3-7天(白嫖的就只能看到3天)
         *
         * @param context
         * @param location
         */
        public void weatherForecast(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 0);
            service.getWeatherForecast(location).enqueue(new NetCallBack<WeatherForecastResponse>() {
                @Override
                public void onSuccess(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                    if (getView() != null) {
                        getView().getWeatherForecastResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if (getView() != null) {
                        getView().getDataFailed();
                    }
                }
            });
        }


        /**
         * 逐小时预报
         *
         * @param context
         * @param location
         */
        public void hourly(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 0);
            service.getHourly(location).enqueue(new NetCallBack<HourlyResponse>() {
                @Override
                public void onSuccess(Call<HourlyResponse> call, Response<HourlyResponse> response) {
                    if (getView() != null) {
                        getView().getHourlyResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if (getView() != null) {
                        getView().getDataFailed();
                    }
                }
            });
        }

        /**
         * 空气质量数据
         *
         * @param context
         * @param location
         */
        public void airNowCity(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 0);
            service.getAirNowCity(location).enqueue(new NetCallBack<AirNowCityResponse>() {
                @Override
                public void onSuccess(Call<AirNowCityResponse> call, Response<AirNowCityResponse> response) {
                    if (getView() != null) {
                        getView().getAirNowCityResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if (getView() != null) {
                        getView().getDataFailed();
                    }
                }
            });
        }
    }

    public interface IWeatherView extends BaseView {
        //查询当天天气的数据返回
        void getTodayWeatherResult(Response<TodayResponse> response);
        //查询天气预报的数据返回
        void getWeatherForecastResult(Response<WeatherForecastResponse> response);
        //查询逐小时天气的数据返回
        void getHourlyResult(Response<HourlyResponse> response);
        //查询空气质量的数据返回
        void getAirNowCityResult(Response<AirNowCityResponse> response);

        //错误返回
        void getDataFailed();
    }
}

