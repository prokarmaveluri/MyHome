package com.dignityhealth.myhome.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Chandra on 15-03-2016.
 */
public class DeviceDisplayManager {
    private static DeviceDisplayManager ourInstance = new DeviceDisplayManager();
    private static float density = 0;
    public static final String W60H80 = "w60h80";
    public static final String W120H160 = "w120h160";

    public static DeviceDisplayManager getInstance() {
        return ourInstance;
    }

    private DeviceDisplayManager() {
    }

    public int getDeviceAspectRatio(Context context) {
        return (getDeviceHeight(context) / getDeviceWidth(context)) * 100;
    }

    @SuppressLint("SwitchIntDef")
    public int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int orientation = wm.getDefaultDisplay().getRotation();
        switch (orientation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return portraitHeight(context);
            default:
                return portraitWidth(context);
        }
    }

    @SuppressLint("SwitchIntDef")
    public int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int orientation = wm.getDefaultDisplay().getRotation();
        switch (orientation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return portraitWidth(context);
            default:
                return portraitHeight(context);
        }
    }

    private int portraitWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (null != wm.getDefaultDisplay()) {
//            if (android.os.Build.VERSION.SDK_INT >=
//                    android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            wm.getDefaultDisplay().getSize(point);
            return point.x;
//            } else {
//                return mDisplay.getWidth();
//            }
        } else {
            return 0;
        }
    }

    private int portraitHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (null != wm.getDefaultDisplay()) {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            wm.getDefaultDisplay().getSize(point);
            return point.y;
//            } else {
//                return mDisplay.getHeight();
//            }
        } else {
            return 0;
        }
    }

    // return 0.75 if it's LDPI
    // return 1.0 if it's MDPI
    // return 1.5 if it's HDPI
    // return 2.0 if it's XHDPI
    // return 3.0 if it's XXHDPI
    // return 4.0 if it's XXXHDPI
    public float getDeviceDensity(Context context) {
        return density == 0f ? density = context.getResources().getDisplayMetrics().density :
                density;
    }

    public double getScreenSizeInInches(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(DeviceDisplayManager.getInstance().getDeviceWidth(context) / dm.xdpi, 2);
        double y = Math.pow(DeviceDisplayManager.getInstance().getDeviceHeight(context) / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
