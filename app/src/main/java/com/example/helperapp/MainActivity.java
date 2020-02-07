package com.example.helperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.helperapp.fragments.AppCategoryFragment;
import com.example.helperapp.fragments.AppListFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_PHONE_STATE = 1001;
    private String TAG = "tag";
    private FirebaseAnalytics mFirebaseAnalytics;
    private String mPhoneNumber = "";
    private List<ApplicationInfo> packages;
    private PackageManager pm;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        viewPager = findViewById(R.id.viewPager);
//        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        pm = getPackageManager();
//        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//        } else {
//            //TODO
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
//                    && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
//                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            } else {
//                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//                mPhoneNumber = tMgr.getLine1Number();
//
//            }
//        }
//
//        sendEvent();


    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    private void sendEvent() {

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            if (packageInfo.packageName != null) {
                Bundle bundle = new Bundle();
                bundle.putString("phone_number", mPhoneNumber);
                if (String.valueOf(pm.getApplicationLabel(packageInfo)).contains("com.")) {
                    continue;
                }
                if (isSystemPackage(packageInfo)) {
                    continue;
                }
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.valueOf(pm.getApplicationLabel(packageInfo)));
                mFirebaseAnalytics.logEvent("user_app_data_" + mPhoneNumber.replace("+", ""), bundle);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                    final PackageManager pm = getPackageManager();
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                                && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        } else {
                            tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            mPhoneNumber = tMgr.getLine1Number();
                        }
                    }
                    sendEvent();
                }
                break;

            default:
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return AppListFragment.newInstance(null, null);
                case 1:
                    return AppCategoryFragment.newInstance(null, null);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
