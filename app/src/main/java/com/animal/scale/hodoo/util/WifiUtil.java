package com.animal.scale.hodoo.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiUtil {

    private static final String TAG = "HJLEE";

    Context context;

    boolean isEnabled;

    boolean reconnect;

    public WifiUtil(Context context) {
        this.context = context;
    }

    public boolean connectWiFi(String SSID, String password, String Security) {
        try {
            Security = "WPA";
            Log.e(TAG, "Item clicked, SSID " + SSID + " Security : " + Security);
            String networkSSID = SSID;
            String networkPass = password;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;
// Check if security type is WEP
            if (Security.toUpperCase().contains("WEP")) {
                Log.e("HJLEE", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }
                conf.wepTxKeyIndex = 0;
            // Check if security type is WPA
            } else if (Security.toUpperCase().contains("WPA")) {
                Log.e(TAG, "Configuring WPA");
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";
            // Check if network is open network
            } else {
                Log.e(TAG, "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }
            //Connect to the network
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);
            Log.e(TAG, "Add result " + networkId);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                   // Log.e(TAG, "WifiConfiguration SSID " + i.SSID);
                    boolean isDisconnected = wifiManager.disconnect();
                   // Log.e(TAG, "isDisconnected : " + isDisconnected);
                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                  //  Log.e(TAG, "isEnabled : " + isEnabled);
                    reconnect = wifiManager.reconnect();
                    //Log.e(TAG, "isReconnected : " + isReconnected);
                    Log.e(TAG, "reassociate : " + wifiManager.reassociate());
                    return reconnect;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reconnect;
    }
}
