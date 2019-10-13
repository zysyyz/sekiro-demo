package com.virjar.injecttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {
    public static final String TAG = "DM_HOOK";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                new AlertDialog.Builder((Context) param.thisObject)
                        .setTitle("注入测试")
                        .setMessage("app已被控制")
                        .setNeutralButton("我已知晓！",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                        .create().show();
            }
        });

//        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                Log.i(TAG, "now activity: " + param.thisObject.getClass().getName());
//                if (param.thisObject.getClass().getName().equalsIgnoreCase("cn.damai.trade.newtradeorder.ui.projectdetail.ui.activity.ProjectDetailActivity")) {
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            List<View> allViews = ViewUtil.getAllViews(((Activity) param.thisObject).getWindow().getDecorView());
//                            for (View view : allViews) {
//                                String text = view.getClass().getName();
//                                if (view instanceof TextView) {
//                                    text = text + " " + ((TextView) view).getText();
//                                }
//                                Log.i(TAG, "view: " + text);
//                            }
//
//                        }
//                    }, 10000);
//                }
//            }
//        });

    }
}
