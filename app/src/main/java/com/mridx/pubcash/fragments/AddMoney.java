package com.mridx.pubcash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mridx.pubcash.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddMoney extends Fragment {


    public AddMoney() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_addmoney, container, false);
    }

}
