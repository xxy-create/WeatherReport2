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

        /**
         * 天气所有数据
         *
         * @param context
         * @param location
         */
        public void weatherData(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 0);
            service.weatherData(location).enqueue(new NetCallBack<WeatherResponse>() {
                @Override
                public void onSuccess(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (getView() != null) {
                        getView().getWeatherDataResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if (getView() != null) {
                        getView().getWeatherDataFailed();
                    }
                }
            });
        }
    }

    public interface IWeatherView extends BaseView {
        //查询空气质量的数据返回
        void getAirNowCityResult(Response<AirNowCityResponse> response);

        //查询天气所有数据
        void getWeatherDataResult(Response<WeatherResponse> response);
        //天气数据获取错误返回
        void getWeatherDataFailed();

        //错误返回
        void getDataFailed();
    }
}

