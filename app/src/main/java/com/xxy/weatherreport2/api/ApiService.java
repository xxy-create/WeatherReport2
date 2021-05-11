package com.xxy.weatherreport2.api;

import com.xxy.weatherreport2.bean.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API服务接口
 */
public interface ApiService {

    /**
     * 空气质量数据 这个location要传入市的参数，不再是区县，否则会提示permission denied
     */
    @GET("/s6/air/now?key=8688f2b89a1c47ffa5ed6bce0559a73d")
    Call<AirNowCityResponse> getAirNowCity(@Query("location") String location);

    /**
     * 获取所有天气数据，在返回值中再做处理
     * @param location
     * @return
     */
    @GET("/s6/weather?key=8688f2b89a1c47ffa5ed6bce0559a73d")
    Call<WeatherResponse> weatherData(@Query("location") String location);

    /**
     * 搜索城市
     */
    @GET("/find?key=3086e91d66c04ce588a7f538f917c7f4&group=cn&number=10")
    Call<SearchCityResponse> searchCity(@Query("location") String location);

}

