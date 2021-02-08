package com.zyj.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main2Activity extends PluginHostActivity{
    private TextView mTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        installPluginFragment("com.zyj.plugina.MainFragment");
    }
    protected View getContentView() {
        Context context=getApplicationContext();
        FrameLayout viewRoot = new FrameLayout(this);
        viewRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mTextView = new TextView(this);
        viewRoot.addView(mTextView);
        Logger logger=LoggerFactory.getLogger(getClass());
        logger.debug("");
        viewRoot.setId(android.R.id.primary);
        return viewRoot;
    }
    @Override
    protected String onGetPluginName() {
        return "plugins/plugina-debug.apk";
    }
}
