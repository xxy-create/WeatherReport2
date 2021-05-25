package com.xxy.weatherreport2.api;

import com.xxy.weatherreport2.bean.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API服务接口
 */
public interface ApiService {

    /**
     * 获取所有天气数据，在返回值中再做处理
     * @param location
     * @return
     */
    @GET("/s6/weather?key=7ae6424660264f0eb94c720bcc14588b")
    Call<WeatherResponse> weatherData(@Query("location") String location);


    /**
     * 空气质量5天预报
     *
     * @param location 城市id
     * @return 返回空气质量5天预报数据
     */
    @GET("/v7/air/5d?key=3086e91d66c04ce588a7f538f917c7f4")
    Call<MoreAirFiveResponse> airFiveWeather(@Query("location") String location);

    /**
     * 天气预报  因为是开发者所以最多可以获得15天的数据，但是如果你是普通用户，那么最多只能获得三天的数据
     * 分为 3天、7天、10天、15天 四种情况，这是时候就需要动态的改变请求的url
     *
     * @param type     天数类型  传入3d / 7d / 10d / 15d  通过Path拼接到请求的url里面
     * @param location 城市id
     * @return 返回天气预报数据 DailyResponse
     */
    @GET("/v7/weather/{type}?key=3086e91d66c04ce588a7f538f917c7f4")
    Call<DailyResponse> dailyWeather(@Path("type") String type, @Query("location") String location);

    /**
     * 当天空气质量
     *
     * @param location 城市id
     * @return 返回当天空气质量数据 MoreAirFiveResponse
     */
    @GET("/v7/air/now?key=7ae6424660264f0eb94c720bcc14588b")
    Call<AirNowResponse> airNowWeather(@Query("location") String location);

    /**
     * 搜索城市  V7版本  模糊搜索，国内范围 返回10条数据
     *
     * @param location 城市名
     * @param mode     exact 精准搜索  fuzzy 模糊搜索
     * @return NewSearchCityResponse 搜索城市数据返回
     */
    @GET("/v2/city/lookup?key=3086e91d66c04ce588a7f538f917c7f4&range=cn")
    Call<NewSearchCityResponse> newSearchCity(@Query("location") String location,
                                              @Query("mode") String mode);
}

