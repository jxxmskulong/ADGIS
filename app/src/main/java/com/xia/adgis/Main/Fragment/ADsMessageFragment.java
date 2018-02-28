package com.xia.adgis.Main.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xia.adgis.R;

import butterknife.ButterKnife;

public class ADsMessageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ads_message, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

}
