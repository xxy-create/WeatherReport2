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
         * @param location 城市名
         */
        public void newSearchCity(String location) {//注意这里的4表示新的搜索城市地址接口
            ApiService service = ServiceGenerator.createService(ApiService.class, 4);//指明访问的地址
            service.newSearchCity(location,"exact").enqueue(new NetCallBack<NewSearchCityResponse>() {
                @Override
                public void onSuccess(Call<NewSearchCityResponse> call, Response<NewSearchCityResponse> response) {
                    if(getView() != null){
                        getView().getNewSearchCityResult(response);
                    }
                }

                @Override
                public void onFailed() {
                    if(getView() != null){
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
        //搜索城市返回城市id  通过id才能查下面的数据,否则会提示400  V7
        void getNewSearchCityResult(Response<NewSearchCityResponse> response);
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

