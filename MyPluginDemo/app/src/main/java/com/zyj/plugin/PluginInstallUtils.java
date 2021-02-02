package com.zyj.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.example.myplugindemo.R;
import com.zyj.model.PluginRuntimeEnv;
import com.zyj.pluginLib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class PluginInstallUtils {
    private Context mContext;
    private static PluginInstallUtils sInstance;
    public static final String plugins_dir = "/plugins";
    public static final String unzip_dir = "/unzip";

    //加载插件的过程很耗时，通过一个Map把插件加载的数据缓存起来，下次遇到相同的插件就直接取出。key为apkpath，也可以是id
    public final static HashMap<String, PluginRuntimeEnv> mPackagesHolder = new HashMap<>();

    private PluginInstallUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public static PluginInstallUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PluginInstallUtils.class) {
                if (sInstance == null) {
                    sInstance = new PluginInstallUtils(context);
                }
            }
        }
        return sInstance;
    }

    public void installAllEnv() {
        try {
            FileUtils.copyAllAssertToCacheFolder(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file=new File(getPluginsDirFullPath(mContext));
        if(!file.exists()){
            file.mkdirs();
        }
        File[] files =file .listFiles();
        StringBuilder addApkDex = new StringBuilder();
        for (File curFile : files) {
            if (curFile.getName().contains("apk")) {
                addApkDex.append(curFile.getAbsolutePath()).append(curFile.pathSeparator);
            }
        }
        Log.d("zyj", addApkDex.toString());

        for (File curFile : files) {
            String fileName = curFile.getName();
            if (fileName.contains("apk")) {
                installRuntimeEnv(curFile.getPath());
            }
        }
    }

    public String getPluginsDirFullPath(Context context) {
        Log.d("getPluginsDirFullPath",context.getApplicationContext().getFilesDir().getAbsoluteFile() + plugins_dir);
        return context.getApplicationContext().getFilesDir().getAbsoluteFile() + plugins_dir;
    }

    /**
     * 通过apkname创建包含classloader、resources、assetManager、theme的插件化环境；如果map里有就直接返回
     *
     * @param apkPath
     * @return
     */
    public void installRuntimeEnv(String apkPath) {
        PluginRuntimeEnv pluginRuntimeEnv = mPackagesHolder.get(apkPath);
        if (pluginRuntimeEnv != null) {
            return;
        }
        DexClassLoader dexClassLoader = createDexClassLoader(apkPath, null);
        AssetManager assetManager = createAssetManager(apkPath);
        Resources resources = createResources(assetManager);
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(R.style.AppTheme, false);
        // create pluginPackage
        pluginRuntimeEnv = new PluginRuntimeEnv(apkPath, dexClassLoader, resources, assetManager, theme);
        apkPath="plugins"+apkPath.substring(apkPath.lastIndexOf("/"),apkPath.length());
        mPackagesHolder.put(apkPath, pluginRuntimeEnv);
    }

    /**
     * 创建classloader
     *
     * @param dexPath
     * @param nativeLibPath
     * @return
     */
    private DexClassLoader createDexClassLoader(String dexPath, String nativeLibPath) {
        DexClassLoader loader = new DexClassLoader(dexPath, getPluginsDirFullPath(mContext)+unzip_dir, nativeLibPath, mContext.getClassLoader());
        return loader;
        //zyj?第二个参数啥意思？
    }

    /**
     * 创建assetManager
     *
     * @param dexPath
     * @return
     */
    private AssetManager createAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssertPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssertPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过assetManager创建新的resources
     *
     * @param assetManager
     * @return
     */
    private Resources createResources(AssetManager assetManager) {
        Resources superRes = mContext.getResources();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        return resources;
    }


}
