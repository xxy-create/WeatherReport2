package com.xxy.weatherreport2.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxy.weatherreport2.R;
import com.xxy.weatherreport2.bean.SearchCityResponse;
import com.xxy.weatherreport2.bean.NewSearchCityResponse;

import java.util.List;

/**
 * 搜索城市结果列表适配器
 */
/*
public class  SearchCityAdapter extends BaseQuickAdapter<SearchCityResponse.HeWeather6Bean.BasicBean, BaseViewHolder> {
    public SearchCityAdapter(int layoutResId, @Nullable List<SearchCityResponse.HeWeather6Bean.BasicBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchCityResponse.HeWeather6Bean.BasicBean item) {
        helper.setText(R.id.tv_city_name, item.getLocation());
        helper.addOnClickListener(R.id.tv_city_name);//绑定点击事件

    }
}
*/

public class SearchCityAdapter extends BaseQuickAdapter<NewSearchCityResponse.LocationBean, BaseViewHolder> {
    public SearchCityAdapter(int layoutResId, @Nullable List<NewSearchCityResponse.LocationBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewSearchCityResponse.LocationBean item) {
        helper.setText(R.id.tv_city_name, item.getName());
        helper.addOnClickListener(R.id.tv_city_name);//绑定点击事件

    }
}
