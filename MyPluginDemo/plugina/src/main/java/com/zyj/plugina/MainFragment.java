package com.zyj.plugina;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plugina.R;
import com.zyj.pluginLib.fragment.BaseFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("");
        return inflater.inflate(R.layout.fragment_main,container,false);
    }
}
