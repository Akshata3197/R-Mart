package com.rmart.customerservice.dth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.rmart.R;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.customerservice.dth.adapter.OperatorAdapter;
import com.rmart.customerservice.mobile.listners.SlectOperator;
import com.rmart.customerservice.mobile.operators.model.Operator;
import com.rmart.customerservice.mobile.operators.model.OperatorResponse;
import com.rmart.customerservice.mobile.operators.repositories.OpratorsRepository;
import com.rmart.databinding.DthSelectOperatorBinding;

import java.util.ArrayList;


public class FragmentDTHRecharge extends BaseFragment {

    public FragmentDTHRecharge() {
        // Required empty public constructor
    }

    public static FragmentDTHRecharge newInstance(String param1, String param2) {
        FragmentDTHRecharge fragment = new FragmentDTHRecharge();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    SlectOperator slectOperator= new SlectOperator(){

        @Override
        public void onSelect(Operator operator) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, FragmentDTHServiceNumber.newInstance(operator)).addToBackStack(null)
                    .commit();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DthSelectOperatorBinding binding = DataBindingUtil.inflate(inflater, R.layout.dth_select_operator, container, false);
        OpratorsRepository.getOperators("DTH").observeForever(new Observer<OperatorResponse>() {
            @Override
            public void onChanged(OperatorResponse operatorResponse) {
             /*   ArrayList<Operator> preOperators = new ArrayList<>();
                if(type.equalsIgnoreCase("M_PRE")){
                    preOperators=operatorResponse.operatorData.preOperators;
                } else if(type.equalsIgnoreCase("M_POST")){
                    preOperators= operatorResponse.operatorData.postOperators;

                } else if(type.equalsIgnoreCase("DTH")){
                    preOperators= operatorResponse.operatorData.dthOperators;

                }*/
                ArrayList<Operator> preOperators = new ArrayList<>();
                    preOperators= operatorResponse.operatorData.dthOperators;

                OperatorAdapter operatorAdapter = new OperatorAdapter(getContext(), preOperators,slectOperator);
                binding.setOperatorAdapter(operatorAdapter);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==200 && data!=null ) {

        }

    }


}