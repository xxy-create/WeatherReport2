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
         * 搜索城市  V7版本中  需要把定位城市的id查询出来，然后通过这个id来查询详细的数据
         *
         * @param location 城市名
         */
        public void newSearchCity(String location) {//注意这里的4表示新的搜索城市地址接口
            ApiService service = ServiceGenerator.createService(ApiService.class, 3);//指明访问的地址
            service.newSearchCity(location, "exact").enqueue(new NetCallBack<NewSearchCityResponse>() {
                @Override
                public void onSuccess(Call<NewSearchCityResponse> call, Response<NewSearchCityResponse> response) {
                    if (getView() != null) {
                        getView().getNewSearchCityResult(response);
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
         * 实况天气  V7版本
         *
         * @param location 城市名
         */
        public void nowWeather(String location) {//这个3 表示使用新的V7API访问地址
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);
            service.nowWeather(location).enqueue(new NetCallBack<NowResponse>() {
                @Override
                public void onSuccess(Call<NowResponse> call, Response<NowResponse> response) {
                    if (getView() != null) {
                        getView().getNowResult(response);
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

        /**
         * 天气预报  V7版本   7d 表示天气的数据 为了和之前看上去差别小一些，这里先用七天的
         *
         * @param location 城市名
         */
        public void dailyWeather(String location) {//这个3 表示使用新的V7API访问地址
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);
            service.dailyWeather("7d", location).enqueue(new NetCallBack<DailyResponse>() {
                @Override
                public void onSuccess(Call<DailyResponse> call, Response<DailyResponse> response) {
                    if (getView() != null) {
                        getView().getDailyResult(response);
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

        /**
         * 逐小时预报（未来24小时）
         *
         * @param location 城市名
         */
        public void hourlyWeather(String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);
            service.hourlyWeather(location).enqueue(new NetCallBack<HourlyResponse>() {
                @Override
                public void onSuccess(Call<HourlyResponse> call, Response<HourlyResponse> response) {
                    if (getView() != null) {
                        getView().getHourlyResult(response);
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


        /**
         * 当天空气质量
         *
         * @param location 城市名
         */
        public void airNowWeather(String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 2);
            service.airNowWeather(location).enqueue(new NetCallBack<AirNowResponse>() {
                @Override
                public void onSuccess(Call<AirNowResponse> call, Response<AirNowResponse> response) {
                    if (getView() != null) {
                        getView().getAirNowResult(response);
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
        //搜索城市返回城市id  通过id才能查下面的数据,否则会提示400  V7
        void getNewSearchCityResult(Response<NewSearchCityResponse> response);

        //实况天气
        void getNowResult(Response<NowResponse> response);
        //天气预报  7天
        void getDailyResult(Response<DailyResponse> response);
        //逐小时天气预报
        void getHourlyResult(Response<HourlyResponse> response);
        //空气质量
        void getAirNowResult(Response<AirNowResponse> response);

        //天气数据获取错误返回
        void getWeatherDataFailed();

        //错误返回
        void getDataFailed();
    }
}

