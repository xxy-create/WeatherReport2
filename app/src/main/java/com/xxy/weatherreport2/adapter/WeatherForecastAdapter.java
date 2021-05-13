package com.xxy.weatherreport2.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxy.weatherreport2.R;
import com.xxy.weatherreport2.bean.WeatherResponse;
import com.xxy.weatherreport2.utils.WeatherUtil;
import com.xxy.weatherreport2.utils.DateUtils;

import java.util.List;

/**
 * 天气预报列表展示适配器
 */
public class WeatherForecastAdapter extends BaseQuickAdapter<WeatherResponse.HeWeather6Bean.DailyForecastBean, BaseViewHolder> {

    public WeatherForecastAdapter(int layoutResId, @Nullable List<WeatherResponse.HeWeather6Bean.DailyForecastBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WeatherResponse.HeWeather6Bean.DailyForecastBean item) {
        helper.setText(R.id.tv_date, DateUtils.dateSplitPlus(item.getDate())+ DateUtils.Week(item.getDate()))//日期
//                .setText(R.id.tv_info, item.getCond_txt_d())//天气
                .setText(R.id.tv_temp_height, item.getTmp_max() + "℃")//最低温和最高温
                .setText(R.id.tv_temp_low,"/" + item.getTmp_min() + "℃");
        //天气状态图片
        ImageView weatherStateIcon = helper.getView(R.id.iv_weather_state);
        int code = Integer.parseInt(item.getCond_code_d());//获取天气状态码，根据状态码来显示图标
        WeatherUtil.changeIcon(weatherStateIcon,code);//调用工具类中写好的方法
    }
}



