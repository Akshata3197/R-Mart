package com.rmart.inventory.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.rmart.R;
import com.rmart.inventory.adapters.ImageAdapter;
import com.rmart.inventory.adapters.ProductUnitAdapter;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.pojos.APIStockListResponse;
import com.rmart.utilits.pojos.BaseResponse;
import com.rmart.utilits.pojos.ProductResponse;
import com.rmart.utilits.services.VendorInventoryService;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowProductPreviewFragment extends BaseInventoryFragment {
    private static final String ARG_PRODUCT = "product";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM1 = "param1";
    private RecyclerView recyclerView;
    private ProductResponse product;
    private boolean isEdit;
    private ViewPager viewPager;
    //private APIStockListResponse apiStockListResponse;
    private AppCompatTextView tvProductName, tvProductDescription, tvProductRegionalName, tvProductExpiry, tvDeliveryInDays;
    public ShowProductPreviewFragment() {
        // Required empty public constructor
    }

    public static ShowProductPreviewFragment newInstance(ProductResponse product, boolean isEdit, APIStockListResponse apiStockListResponse) {
        ShowProductPreviewFragment fragment = new ShowProductPreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        args.putSerializable(ARG_PARAM1, apiStockListResponse);
        args.putBoolean(ARG_PARAM2, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (ProductResponse) getArguments().getSerializable(ARG_PRODUCT);
            //apiStockListResponse = (APIStockListResponse) getArguments().getSerializable(ARG_PARAM1);
            isEdit = getArguments().getBoolean(ARG_PARAM2);
        }
        /*product = Objects.requireNonNull(inventoryViewModel.getProductList().getValue()).get(inventoryViewModel.getSelectedProduct().getValue());
            inventoryViewModel.getSelectedProduct().observe(Objects.requireNonNull(getActivity()), index -> {
                product = Objects.requireNonNull(inventoryViewModel.getProductList().getValue()).get(index);
            } );*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory_product_preview, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(getString(R.string.product_details));
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvProductName = view.findViewById(R.id.product_name);
        recyclerView = view.findViewById(R.id.unit_base);
        tvProductDescription = view.findViewById(R.id.product_description);
        tvProductRegionalName = view.findViewById(R.id.product_regional_name);
        tvDeliveryInDays = view.findViewById(R.id.delivery);
        tvProductExpiry = view.findViewById(R.id.product_expiry);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewPager =  view.findViewById(R.id.view_pager);
        AppCompatButton delete = view.findViewById(R.id.delete);
        AppCompatButton edit = view.findViewById(R.id.edit);
        if(isEdit) {
            edit.setOnClickListener(view1 -> mListener.updateProduct(product, true));
            delete.setOnClickListener(view1 -> {
                progressDialog.show();
                VendorInventoryService vendorInventoryService = RetrofitClientInstance.getRetrofitInstance().create(VendorInventoryService.class);
                vendorInventoryService.deleteProduct(product.getProductID(), MyProfile.getInstance().getUserID()).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                        BaseResponse data = response.body();
                        if(data != null) {
                            if(data.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                                showDialog("", data.getMsg(), (dialogInterface, i) -> requireActivity().onBackPressed());
                            } else {
                                showDialog(getString(R.string.no_information_available));
                            }
                        } else {
                            showDialog(getString(R.string.no_information_available));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                        showDialog("", t.getMessage());
                        progressDialog.dismiss();
                    }
                });
            });
        } else {
            delete.setVisibility(View.GONE);
            edit.setText(getString(R.string.save));
        }

        updateUI();
    }

    private void updateUI() {
        ImageAdapter imageAdapter = new ImageAdapter(requireContext());
        viewPager.setAdapter(imageAdapter);
        tvProductName.setText(product.getName());
        tvDeliveryInDays.setText(String.format(getString(R.string.delivery_in_days), MyProfile.getInstance().getDeliveryInDays()));
        ProductUnitAdapter unitBaseAdapter = new ProductUnitAdapter(product.getUnitObjects(), view -> {
        }, false);
        recyclerView.setAdapter(unitBaseAdapter);

        tvProductDescription.setText(product.getDescription());
        tvProductRegionalName.setText(product.getRegionalName());
        tvProductExpiry.setText(product.getExpiry_date());
    }
}