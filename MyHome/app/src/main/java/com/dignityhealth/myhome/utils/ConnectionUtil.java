package com.dignityhealth.myhome.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * Created by kwelsh on 4/26/2017.
 * Utility class to check networking state
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ConnectionUtil {

    public static NetworkInfo activeNetwork(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo;
    }

    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected(@NonNull Context context) {
        NetworkInfo activeNetwork = activeNetwork(context);
        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellularConnected(@NonNull Context context) {
        NetworkInfo activeNetwork = activeNetwork(context);
        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellularRoaming(@NonNull Context context) {
        NetworkInfo activeNetwork = activeNetwork(context);
        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isRoaming()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * You need to add:
     * <p/>
     * <pre>
     *     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * </pre>
     * <p/>
     * in your AndroidManifest.xml.
     */
    public static String getNetworkType(@NonNull Context context) {
        TelephonyManager teleMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = teleMan.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            default:
                return "New type of network";
        }
    }

    public static String getIPAddress(@NonNull Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wm.getConnectionInfo().getIpAddress();
        return String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
}