package com.xxy.weatherreport2.contract;

import android.content.Context;

import com.xxy.weatherreport2.api.ApiService;
import com.xxy.weatherreport2.bean.NewSearchCityResponse;
import com.xxy.weatherreport2.bean.SearchCityResponse;
import com.xxy.mvplibrary.base.BasePresenter;
import com.xxy.mvplibrary.base.BaseView;
import com.xxy.mvplibrary.net.NetCallBack;
import com.xxy.mvplibrary.net.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Response;

public class SearchCityContract {

    public static class SearchCityPresenter extends BasePresenter<ISearchCityView> {

        /**
         * 搜索城市
         *
         * @param context
         * @param location
         */
        public void searchCity(final Context context, String location) {
            ApiService service = ServiceGenerator.createService(ApiService.class, 1);//指明访问的地址
            service.searchCity(location).enqueue(new NetCallBack<SearchCityResponse>() {
                @Override
                public void onSuccess(Call<SearchCityResponse> call, Response<SearchCityResponse> response) {
                    if (getView() != null) {
                        getView().getSearchCityResult(response);
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


    public interface ISearchCityView extends BaseView {
        //查询城市返回数据
        void getSearchCityResult(Response<SearchCityResponse> response);

        //错误返回
        void getDataFailed();
    }
}

