package com.rmart.customer.views;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rmart.R;
import com.rmart.RMartApplication;
import com.rmart.baseclass.CallBackInterface;
import com.rmart.baseclass.Constants;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.customer.OnCustomerHomeInteractionListener;
import com.rmart.customer.adapters.VendorProductDetailsAdapter;
import com.rmart.customer.models.ContentModel;
import com.rmart.customer.models.CustomerProductsShopDetailsModel;
import com.rmart.customer.models.ProductBaseModel;
import com.rmart.customer.models.CustomerProductDetailsModel;
import com.rmart.customer.models.VendorProductDetailsResponse;
import com.rmart.customer.models.VendorProductShopDataResponse;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.HttpsTrustManager;
import com.rmart.utilits.LoggerInfo;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.services.CustomerProductsService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorProductDetailsFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";

    public VendorProductDetailsFragment() {
        // Required empty public constructor
    }

    private AppCompatEditText etProductsSearchField;
    private int currentPage = 0;
    private String searchProductName = "";
    private CustomerProductsShopDetailsModel productsShopDetailsModel;
    private int productCategoryId = -1;
    private TextView tvShopNameField;
    private TextView tvPhoneNoField;
    private TextView tvViewAddressField;
    private NetworkImageView ivShopImageField;
    private VendorProductDetailsAdapter vendorProductDetailsAdapter;
    private List<ProductBaseModel> vendorProductsList;
    private OnCustomerHomeInteractionListener onCustomerHomeInteractionListener;
    private TextView tvProductDiscountField;

    public static VendorProductDetailsFragment getInstance(CustomerProductsShopDetailsModel productsShopDetailsModel) {
        VendorProductDetailsFragment fragment = new VendorProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, productsShopDetailsModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productsShopDetailsModel = (CustomerProductsShopDetailsModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LoggerInfo.printLog("Fragment", "VendorProductDetailsFragment");
        return inflater.inflate(R.layout.fragment_vendor_product_details, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnCustomerHomeInteractionListener) {
            onCustomerHomeInteractionListener = (OnCustomerHomeInteractionListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIComponents(view);

        getVendorDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolBar();
    }

    public void updateToolBar() {
        requireActivity().setTitle(productsShopDetailsModel.getShopName());
        ((CustomerHomeActivity)(requireActivity())).showCartIcon();
    }

    private void loadUIComponents(View view) {
        RecyclerView productsListField = view.findViewById(R.id.products_list_field);
        productsListField.setHasFixedSize(false);

        tvProductDiscountField = view.findViewById(R.id.tv_product_discount_field);
        etProductsSearchField = view.findViewById(R.id.edt_product_search_field);
        ImageView ivSearchField = view.findViewById(R.id.iv_search_field);
        etProductsSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() != 0) {
                    performSearch();
                }
                ivSearchField.setImageResource(R.drawable.search);
            }
        });

        ivShopImageField = view.findViewById(R.id.iv_shop_image);
        tvShopNameField = view.findViewById(R.id.tv_shop_name_field);
        tvPhoneNoField = view.findViewById(R.id.tv_phone_no_field);
        tvViewAddressField = view.findViewById(R.id.tv_view_address_field);

        vendorProductsList = new ArrayList<>();
        vendorProductDetailsAdapter = new VendorProductDetailsAdapter(requireActivity(), vendorProductsList, callBackListener);
        productsListField.setAdapter(vendorProductDetailsAdapter);
    }

    private CallBackInterface callBackListener = pObject -> {
        if (pObject instanceof CustomerProductDetailsModel) {
            onCustomerHomeInteractionListener.gotoProductDescDetails((CustomerProductDetailsModel) pObject, productsShopDetailsModel);
        } else if (pObject instanceof ContentModel) {
            ContentModel contentModel = (ContentModel) pObject;
            String status = contentModel.getStatus();
            if (status.equalsIgnoreCase(Constants.TAG_VIEW_ALL)) {
                ProductBaseModel selectedProductCategoryDetails = (ProductBaseModel) contentModel.getValue();
                currentPage = 0;
                productCategoryId = selectedProductCategoryDetails.getProductCategoryId();
                onCustomerHomeInteractionListener.gotoVendorSameProductListScreen(selectedProductCategoryDetails, productsShopDetailsModel);
            }
        }
    };

    private void getVendorDetails() {
        if (Utils.isNetworkConnected(requireActivity())) {
            vendorProductsList.clear();
            progressDialog.show();
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            String customerId = MyProfile.getInstance().getUserID();
            Call<VendorProductDetailsResponse> call;
            if (TextUtils.isEmpty(searchProductName) && currentPage == 0 && productCategoryId == -1) {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(),
                        productsShopDetailsModel.getShopId(), customerId);
            } else if (TextUtils.isEmpty(searchProductName) && currentPage != 0 && productCategoryId != -1) {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(),
                        productsShopDetailsModel.getShopId(), productCategoryId, currentPage, searchProductName, customerId);
            } else if (!TextUtils.isEmpty(searchProductName) && currentPage != 0 && productCategoryId == -1) {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(), productsShopDetailsModel.getShopId(),
                        currentPage, searchProductName, customerId);
            } else {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(), productsShopDetailsModel.getShopId(), productCategoryId,
                        currentPage, searchProductName, customerId);
            }
            call.enqueue(new Callback<VendorProductDetailsResponse>() {
                @Override
                public void onResponse(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Response<VendorProductDetailsResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        VendorProductDetailsResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                List<CustomerProductDetailsModel> productDataList = body.getVendorProductDataResponse().getProductsListData();
                                VendorProductShopDataResponse shopDataResponse = body.getVendorProductDataResponse().getVendorShopDetails();
                                updateShopDetailsUI(shopDataResponse);
                                updateAdapter(productDataList);
                            } else {
                                showDialog(body.getMsg());
                            }
                        } else {
                            showDialog(getString(R.string.no_information_available));
                        }
                    }  else {
                        showDialog(getString(R.string.no_information_available));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    }

    private void updateAdapter(List<CustomerProductDetailsModel> productDataList) {
        if(productDataList != null && !productDataList.isEmpty()) {
            LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> linkedMapDetails = groupDataIntoHashMap(productDataList);
            for (Map.Entry<ProductBaseModel, List<CustomerProductDetailsModel>> entry : linkedMapDetails.entrySet()) {
                ProductBaseModel productBaseModel = entry.getKey();
                List<CustomerProductDetailsModel> listData = entry.getValue();
                if(listData != null && !listData.isEmpty()) {
                    productBaseModel.setProductsList(listData);
                    vendorProductsList.add(productBaseModel);
                }
            }
        }
        vendorProductDetailsAdapter.updateItems(vendorProductsList);
        vendorProductDetailsAdapter.notifyDataSetChanged();
    }

    private LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> groupDataIntoHashMap(List<CustomerProductDetailsModel> productDataList) {
        LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> groupedHashMap = new LinkedHashMap<>();
        for(CustomerProductDetailsModel element : productDataList) {
            ProductBaseModel productBaseModel = new ProductBaseModel();
            productBaseModel.setProductCategoryName(element.getParentCategoryName());
            productBaseModel.setProductCategoryId(element.getParentCategoryId());
            if(groupedHashMap.containsKey(productBaseModel)) {
                List<CustomerProductDetailsModel> listData = groupedHashMap.get(productBaseModel);
                if(listData != null) {
                    listData.add(element);
                }
            } else {
                List<CustomerProductDetailsModel> listData = new ArrayList<>();
                listData.add(element);
                groupedHashMap.put(productBaseModel, listData);
            }
        }
        return groupedHashMap;
    }

    private void updateShopDetailsUI(VendorProductShopDataResponse shopDataResponse) {
        requireActivity().setTitle(shopDataResponse.getShopName());
        List<String> shopImagesList = shopDataResponse.getShopImage();
        if(shopImagesList != null && !shopImagesList.isEmpty()) {
            String shopImageUrl = shopImagesList.get(0);
            if(!TextUtils.isEmpty(shopImageUrl)) {
                HttpsTrustManager.allowAllSSL();
                ImageLoader imageLoader = RMartApplication.getInstance().getImageLoader();
                imageLoader.get(shopImageUrl, ImageLoader.getImageListener(ivShopImageField,
                        R.mipmap.ic_launcher, android.R.drawable
                                .ic_dialog_alert));
                ivShopImageField.setImageUrl(shopImageUrl, imageLoader);
            }
        }
        tvShopNameField.setText(shopDataResponse.getShopName());
        tvViewAddressField.setText(shopDataResponse.getShopAddress());
        tvPhoneNoField.setText(shopDataResponse.getShopMobileNo());
    }

    private void performSearch() {
        searchProductName = Objects.requireNonNull(etProductsSearchField.getText()).toString().trim();
        /*if (newText.length() < 1) {
            customerProductsListAdapter.updateItems(new ArrayList<>());
            customerProductsListAdapter.notifyDataSetChanged();
        } else if (newText.length() == 3) {
            searchShopName = newText;
            currentPage = 0;
            getShopsList();
        } else {
            customerProductsListAdapter.getFilter().filter(newText);
        }*/
    }
}