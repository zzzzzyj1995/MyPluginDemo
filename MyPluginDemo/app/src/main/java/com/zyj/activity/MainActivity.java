package com.zyj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myplugindemo.R;

import java.util.ArrayList;

public class MainActivity extends PluginHostActivity {
    private TextView btnPluginA;
    private TextView btnPluginB;
    private ArrayList<PluginItem> mPluginItems = new ArrayList<PluginItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        installRunEnv();
    }

    private void initView() {
        btnPluginA = findViewById(R.id.plugina_btn);
        btnPluginB = findViewById(R.id.pluginb_btn);
        btnPluginA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
//                PluginItem pluginItem = mPluginItems.get(0);
//                startFragment("com.zyj.plugina.MainFragment");
            }
        });
        btnPluginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PluginItem pluginItem = mPluginItems.get(1);
//                startFragment(pluginItem.rootFragment, pluginItem.pluginPath);
            }
        });
    }

    /**
     * 读取放plugin apk文件中的所有apk，将相关信息放入mPluginItems中
     */


    public static class PluginItem {
        public String rootFragment;
        public String pluginPath;
    }
}