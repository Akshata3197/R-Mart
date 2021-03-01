package com.rmart.customer.shops.home.repositories;

import com.rmart.customer.shops.home.api.Shops;
import com.rmart.customer.shops.home.model.ShopHomePageResponce;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.RetrofitClientInstance;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rmart.utilits.Utils.CLIENT_ID;

public class ShopRepository {

    public static MutableLiveData<ShopHomePageResponce> getShopHomePage( int vendorId, int shop_id){

        Shops shope = RetrofitClientInstance.getRetrofitInstance().create(Shops.class);
        final MutableLiveData<ShopHomePageResponce> resultMutableLiveData = new MutableLiveData<>();
        String type = MyProfile.getInstance().getRoleID();
        Call<ShopHomePageResponce> call = shope.getShopHomePage(CLIENT_ID,vendorId,shop_id, MyProfile.getInstance()!=null?MyProfile.getInstance().getUserID():"",type);
        final ShopHomePageResponce result = new ShopHomePageResponce();

        call.enqueue(new Callback<ShopHomePageResponce>() {
            @Override
            public void onResponse(Call<ShopHomePageResponce> call, Response<ShopHomePageResponce> response) {
                ShopHomePageResponce data = response.body();
                if(data!=null) {
                    resultMutableLiveData.setValue(data);

                } else {
                    result.setMsg("Somthing error");
                    result.setStatus(400);
                }
            }

            @Override
            public void onFailure(Call<ShopHomePageResponce> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \"hungryindia.co.in\": No address associated with hostname"))
                { result.setMsg("Please Check Enternet Connection");

                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);


            }
        });
        return resultMutableLiveData;

    }


}
