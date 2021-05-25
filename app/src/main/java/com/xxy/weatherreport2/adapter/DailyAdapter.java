package com.xxy.weatherreport2.adapter;

import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxy.weatherreport2.R;
import com.xxy.weatherreport2.bean.DailyResponse;
import com.xxy.weatherreport2.utils.*;
import java.util.List;

/**
 * V7 API 天气预报数据列表适配器
 */
public class DailyAdapter extends BaseQuickAdapter<DailyResponse.DailyBean, BaseViewHolder> {
    public DailyAdapter(int layoutResId, @Nullable List<DailyResponse.DailyBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyResponse.DailyBean item) {
        //日期
        helper.setText(R.id.tv_date, DateUtils.dateSplitPlus(item.getFxDate()) + DateUtils.Week(item.getFxDate()))
                //最高温
                .setText(R.id.tv_temp_height, item.getTempMax() + "℃")
                //最低温
                .setText(R.id.tv_temp_low, " / " + item.getTempMin() + "℃");

        //天气状态图片
        ImageView weatherStateIcon = helper.getView(R.id.iv_weather_state);
        int code = Integer.parseInt(item.getIconDay());//获取天气状态码，根据状态码来显示图标
        WeatherUtil.changeIcon(weatherStateIcon,code);//调用工具类中写好的方法

    }
}

