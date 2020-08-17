package com.rmart.profile.views;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rmart.R;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.mapview.MapsFragment;
import com.rmart.profile.OnMyProfileClickedListener;

import java.util.Objects;

public class BaseMyProfileFragment extends BaseFragment {

    OnMyProfileClickedListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnMyProfileClickedListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMapView(boolean isEditable) {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance(isEditable, "");
        fragmentTransaction.add(R.id.map_view, mapsFragment, "MapsFragment");
        fragmentTransaction.commit();
    }
}