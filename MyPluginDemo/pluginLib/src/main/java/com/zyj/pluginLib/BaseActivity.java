package com.zyj.pluginLib;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
   public void startFragment(String fragmentName){
       startFragment(fragmentName,null);
   }
   public void startFragment(String fragmentName,Bundle bundle){
       Intent intent = new Intent();
       intent.putExtra(Constant.INTENT_KEY_FRAGMENT, fragmentName);
       intent.putExtra(Constant.INTENT_KEY_BUNDLE, bundle);
       startActivity(intent);
   }
}
