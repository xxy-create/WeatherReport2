package com.xxy.weatherreport2.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxy.weatherreport2.R;
import com.xxy.weatherreport2.bean.CityResponse;

import java.util.List;

/**
 * 省列表适配器
 */
public class ProvinceAdapter extends BaseQuickAdapter<CityResponse, BaseViewHolder> {
    public ProvinceAdapter(int layoutResId, @Nullable List<CityResponse> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CityResponse item) {
        helper.setText(R.id.tv_city,item.getName());//省名称
        helper.addOnClickListener(R.id.item_city);//点击之后进入市级列表
    }
}

