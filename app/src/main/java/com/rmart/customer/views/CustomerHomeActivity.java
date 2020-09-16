package com.rmart.customer.views;

import android.os.Bundle;
import android.view.View;

import com.rmart.R;
import com.rmart.baseclass.views.BaseNavigationDrawerActivity;
import com.rmart.customer.OnCustomerHomeInteractionListener;
import com.rmart.customer.models.CustomerProductsModel;
import com.rmart.customer.models.ShoppingCartResponseDetails;
import com.rmart.customer.models.VendorProductDataResponse;

public class CustomerHomeActivity extends BaseNavigationDrawerActivity implements OnCustomerHomeInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_authenticatin);

        replaceFragment(VendorListViewFragment.getInstance(), VendorListViewFragment.class.getName(),false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.shopping) {
            getToActivity(view.getId(), false);
        } else {
            getToActivity(view.getId(), true);
        }
    }

    @Override
    public void gotoChangeAddress() {
        addFragment(ChangeAddressFragment.getInstance(),ChangeAddressFragment.class.getName(),true);
    }

    @Override
    public void gotoVendorProductDetails(CustomerProductsModel customerProductsModel) {
        addFragment(VendorProductDetailsFragment.getInstance(customerProductsModel),VendorProductDetailsFragment.class.getName(),true);
    }

    @Override
    public void gotoProductDescDetails(VendorProductDataResponse vendorProductDetails, CustomerProductsModel vendorShopDetails) {
        addFragment(ProductCartDetailsFragment.getInstance(vendorProductDetails, vendorShopDetails), ProductCartDetailsFragment.class.getName(), true);
    }

    @Override
    public void gotoPaymentScreen() {
        addFragment(MakePaymentFragment.getInstance("", ""), MakePaymentFragment.class.getName(), true);
    }

    @Override
    public void gotoShoppingCartScreen() {
        addFragment(ShoppingCartFragment.getInstance(), ShoppingCartFragment.class.getName(), true);
    }

    @Override
    public void gotoSelectedShopDetails(ShoppingCartResponseDetails shoppingCartResponseDetails) {
        addFragment(ConfirmOrderFragment.getInstance(shoppingCartResponseDetails), ConfirmOrderFragment.class.getName(), true);
    }
}
