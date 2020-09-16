package com.rmart.customer_order.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.customer_order.adapters.OrdersListAdapter;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.pojos.orders.Order;
import com.rmart.utilits.pojos.orders.OrdersByStatus;
import com.rmart.utilits.pojos.orders.StateOfOrders;
import com.rmart.utilits.services.CustomerOrderService;
import com.rmart.utilits.services.OrderService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrderListFragment extends BaseOrderFragment implements View.OnClickListener {

    private static final String ARG_ORDER_OBJECT = "param1";
    private static final String ARG_PARAM2 = "param2";

    // SelectedOrderGroup mSelectedOrderGroup;
    // private MyOrdersViewModel myOrdersViewModel;
    private OrdersListAdapter ordersListAdapter;

    private AppCompatTextView tvTotalOrder;
    private RecyclerView orderList;
    StateOfOrders stateOfOrders;
    private String startIndex = "0";
    private ArrayList<Order> orders = new ArrayList<>();
    private OrdersByStatus data;

    public CustomerOrderListFragment() {
        // Required empty public constructor
    }

    public static CustomerOrderListFragment newInstance(String param2) {
        CustomerOrderListFragment fragment = new CustomerOrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // stateOfOrders = (StateOfOrders) getArguments().getSerializable(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Objects.requireNonNull(getActivity()).setTitle(mSelectedOrderGroup.getOrderType());
        startIndex = "0";
        getOrdersOfStatesFromServer();
        requireActivity().setTitle(R.string.my_orders);
    }

    private void getOrdersOfStatesFromServer() {
        progressDialog.show();
        CustomerOrderService customerOrderService = RetrofitClientInstance.getRetrofitInstance().create(CustomerOrderService.class);
        customerOrderService.getStateOfOrder(startIndex, "9000000000" /*MyProfile.getInstance().getMobileNumber()*/).enqueue(new Callback<OrdersByStatus>() {
            @Override
            public void onResponse(Call<OrdersByStatus> call, Response<OrdersByStatus> response) {
                if(response.isSuccessful()) {
                    data = response.body();
                    orders = data.getOrders();
                    if(orders.size() > 0) {
                        assert data != null;
                        if(data.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            startIndex = data.getEndIndex();
                            updateUI();
                        } else {
                            showDialog(data.getMsg());
                        }
                    } else {
                        showDialog("",data.getMsg(), (dialog, i) -> {
                            requireActivity().onBackPressed();
                        });
                    }

                } else {
                    showDialog(response.message());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<OrdersByStatus> call, Throwable t) {
                showDialog(t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void updateUI() {
        ordersListAdapter = new OrdersListAdapter(orders, this);
        String count  = data.getOrdersCount();
        if (null == count || count.length()<=0) {
            count = "0";
        }
        if (null != stateOfOrders) {
            tvTotalOrder.setText(String.format(getResources().getString(R.string.total_orders), count, stateOfOrders.getStatusName()));
        } else {
            tvTotalOrder.setVisibility(View.GONE);
        }
        orderList.setAdapter(ordersListAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotalOrder = view.findViewById(R.id.total_order);
        orderList = view.findViewById(R.id.order_list);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // myOrdersViewModel = null;
        mListener =null;
    }

    @Override
    public void onClick(View view) {
        Order orderObject = (Order) view.getTag();
        // orderObject.setOrderStatus(stateOfOrders.getStatusName());
        //orderObject.setOrderStatusID(stateOfOrders.getStatus());
        mListener.goToViewFullOrder(orderObject);
    }
}