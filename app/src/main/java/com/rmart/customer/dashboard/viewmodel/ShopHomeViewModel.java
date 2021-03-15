package com.rmart.customer.dashboard.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rmart.customer.shops.list.models.ShopDetailsModel;
import com.rmart.customer.dashboard.model.ShopHomePageResponce;
import com.rmart.customer.dashboard.repositories.ShopRepository;

public class ShopHomeViewModel extends ViewModel {

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<ShopHomePageResponce> shopHomePageResponceMutableLiveData = new MutableLiveData<>();


    public void loadShopHomePage(ShopDetailsModel productsShopDetailsModel){
        isLoading.setValue(true);
        ShopRepository.getShopHomePageNEW().observeForever(homeResult -> {
            shopHomePageResponceMutableLiveData.setValue(homeResult);
            isLoading.postValue(false);
        });




    }

}