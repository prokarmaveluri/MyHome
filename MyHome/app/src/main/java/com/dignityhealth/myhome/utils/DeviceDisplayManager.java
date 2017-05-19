package com.dignityhealth.myhome.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Chandra on 15-03-2016.
 */
public class DeviceDisplayManager {
    private static DeviceDisplayManager ourInstance = new DeviceDisplayManager();
    private static Context mContext;
    private Display mDisplay;
    private static float density = 0;

    public static DeviceDisplayManager getInstance() {
        return ourInstance;
    }

    public void setContext(Context context){
        mContext = context;
        WindowManager wm =
                (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
    }

    private DeviceDisplayManager() {
    }

    public int getDeviceAspectRatio() {
        return (getDeviceHeight() / getDeviceWidth()) * 100;
    }

    public int getDeviceWidth() {
        int orientation = mDisplay.getRotation();
        switch (orientation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return portraitHeight();
            default:
                return portraitWidth();
        }
    }

    public int getDeviceHeight() {
        int orientation = mDisplay.getRotation();
        switch (orientation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return portraitWidth();
            default:
                return portraitHeight();
        }
    }

    private int portraitWidth() {
        if (null != mDisplay) {
//            if (android.os.Build.VERSION.SDK_INT >=
//                    android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            mDisplay.getSize(point);
            return point.x;
//            } else {
//                return mDisplay.getWidth();
//            }
        } else {
            return 0;
        }
    }

    private int portraitHeight() {
        if (null != mDisplay) {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            mDisplay.getSize(point);
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
    public float getDeviceDensity() {
        return density==0f? density = mContext.getResources().getDisplayMetrics().density :
                density;
    }

    public double getScreenSizeInInches() {
        DisplayMetrics dm = new DisplayMetrics();
        mDisplay.getMetrics(dm);
        double x = Math.pow(DeviceDisplayManager.getInstance().getDeviceWidth() / dm.xdpi, 2);
        double y = Math.pow(DeviceDisplayManager.getInstance().getDeviceHeight() / dm.ydpi, 2);
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
