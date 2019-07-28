package com.scy.android.wifidemotest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WifiManager mWifiManager;
    private WifiBroadCastReceiver mWifiBroadCastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //wifi统一管理类，进行各种wifi操作
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        getExitslianjieWifi();
        requestPermission();
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            //do someThing;
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            //do someThing;
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            //do someThing;
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //do someThing;
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_UNKNOWN) {
            //do someThing;
        }

        mWifiBroadCastReceiver = new WifiBroadCastReceiver();
        IntentFilter intentFilter1 = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiBroadCastReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiBroadCastReceiver, intentFilter2);

    }

    private void getExitslianjieWifi() {
        //它所存储的就是设备之前连接过的所有wifi热点的信息
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        StringBuilder stringBuilder = new StringBuilder();
        //获取当前wifi的连接信息
        stringBuilder.append("设备保存的wifi:"+"\n");
        WifiInfo info = mWifiManager.getConnectionInfo();
        for(WifiConfiguration c: configurations) {
            stringBuilder.append(c.SSID+"\n");
        }
        stringBuilder.append("当前wifi:" + info.getSSID() +"物理地址"+info.getMacAddress()+"信号强度"+info.getRssi() +"\n");
        ((TextView)findViewById(R.id.tv1)).setText(stringBuilder.toString());
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 6);
        } else {
            //打开wifi
            Toast.makeText(this, "权限允许", Toast.LENGTH_SHORT).show();
            openGPSSEtting();
        }
    }

    private void startScanwf() {
        //请求开启wifi
        boolean isOpen = mWifiManager.setWifiEnabled(true);
        Log.d(TAG, "startScanwf: " + mWifiManager.getWifiState() + isOpen);
        if (isOpen || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            mWifiManager.startScan();
            List<ScanResult> Results = mWifiManager.getScanResults();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("附近的wifi" + "\n");
            for (ScanResult Result : Results) {
                stringBuilder.append("wifi结果: " + Result.SSID + "  RSSI:" + Result.level + "\n");
                Log.d(TAG, "wifi结果: " + Result.SSID + "  RSSI:" + Result.level + "  Time：" + System.currentTimeMillis());
            }
            ((TextView)findViewById(R.id.tv2)).setText(stringBuilder.toString());
        }
    }

    private void lianjie() {
        AccessPoint ap = new AccessPoint();
        WifiConfiguration config = createConfiguration(ap);
        //如果你设置的wifi是设备已经存储过的，那么这个networkId会返回小于0的值。
        int networkId = networkId = mWifiManager.addNetwork(config);
        mWifiManager.enableNetwork(networkId, true);
        mWifiManager.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 6) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限允许", Toast.LENGTH_SHORT).show();
                    openGPSSEtting();
                } else {
                    Toast.makeText(this, "你拒绝了权限请求", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkGpsIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private void openGPSSEtting() {
        if (checkGpsIsOpen()) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
            startScanwf();
        } else {
            new AlertDialog.Builder(this).setTitle("open GPS")
                    .setMessage("go to open")
                    //  取消选项
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
                            // 关闭dialog
                            dialogInterface.dismiss();
                        }
                    })
                    //  确认选项
                    .setPositiveButton("setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //跳转到手机原生设置页面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 5);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public WifiConfiguration createConfiguration(AccessPoint ap) {
        String SSID = ap.getSsid();
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + SSID + "\"";

        String encryptionType = ap.getEncryptionType();
        String password = ap.getPassword();
        if (encryptionType.contains("wep")) {
            /**
             * special handling according to password length is a must for wep
             */
            int i = password.length();
            if (((i == 10 || (i == 26) || (i == 58))) && (password.matches("[0-9A-Fa-f]*"))) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (encryptionType.contains("wpa")) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    class WifiBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //wifi开关变化通知
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_DISABLED);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            //doSomething();
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            //doSomething();
                            break;
                    }
                    break;
                //wifi扫描结果通知
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiBroadCastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            startScanwf();
        }
    }
}
