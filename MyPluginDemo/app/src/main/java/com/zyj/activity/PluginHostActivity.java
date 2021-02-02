package com.zyj.activity;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zyj.model.PluginRuntimeEnv;
import com.zyj.plugin.PluginInstallUtils;
import com.zyj.pluginLib.BaseActivity;
import com.zyj.pluginLib.Constant;


/**
 * 用来装载fragment的activity，同时提供fragment所需要的上下文
 */
public class PluginHostActivity extends BaseActivity {
    private LinearLayout mViewRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
    }

    protected View getContentView() {
        mViewRoot = new LinearLayout(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mViewRoot.setFitsSystemWindows(true);
        }
        mViewRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mViewRoot.setId(android.R.id.primary);
        return mViewRoot;
    }


    /**
     * 装载插件的运行环境,这个函数需要在Activity中运行，不能移动到Application中去
     *
     * @param
     * @return
     */
    protected boolean installRunEnv() {
        PluginInstallUtils.getInstance(this).installAllEnv();
        return true;
    }

    private PluginRuntimeEnv getPluginRuntimeEnv(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        return PluginInstallUtils.mPackagesHolder.get(path);
    }

    @Override
    public AssetManager getAssets() {
        PluginRuntimeEnv env = getPluginRuntimeEnv(onGetPluginName());
        if (env != null) {
            return env.pluginAsset;
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        PluginRuntimeEnv env=getPluginRuntimeEnv(onGetPluginName());
        if(env!=null){
            return env.pluginRes;
        }
        return super.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        PluginRuntimeEnv env =getPluginRuntimeEnv(onGetPluginName());
        if(env!=null){
            return env.pluginTheme;
        }
        return super.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        PluginRuntimeEnv env =getPluginRuntimeEnv(onGetPluginName());
        if(env!=null){
            return env.pluginClassLoader;
        }
        return super.getClassLoader();
    }

    /**
     * 通过全类名反射创建fragment，通过fragmentManager把创建的fragment附加到Activity
     *
     * @param fragmentClass
     */
    protected void installPluginFragment(String fragmentClass) {
        if (isFinishing()) {
            return;
        }
        ClassLoader classLoader = getClassLoader();
        try {
            Fragment fragment = (Fragment) classLoader.loadClass(fragmentClass).newInstance();
            Bundle bundle = getIntent().getExtras();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.primary, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected String onGetPluginName() {
        return "";
    }
}
