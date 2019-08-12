package com.ics.ar.matri;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.ics.ar.matri.R;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.activity.subscription.MemberShipActivity;
import com.crashlytics.android.Crashlytics;
import com.ics.ar.matri.activity.LanguageSelection;
import com.ics.ar.matri.activity.dashboard.Dashboard;
import com.ics.ar.matri.interfaces.Consts;
import com.ics.ar.matri.network.NetworkManager;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;

import io.fabric.sdk.android.Fabric;

import java.util.Locale;


public class Splash extends AppCompatActivity {

    private Handler handler = new Handler();
    private Context mContext;
    LoginDTO loginDTO;
    private static int SPLASH_TIME_OUT = 3000;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1003;
    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CALL_PRIVILEGED, Manifest.permission.CALL_PHONE};
    private boolean cameraAccepted, storageAccepted, accessNetState, call_privilage, callPhone, fineLoc, corasLoc, readSMS, receiveSMS;
    public SharedPrefrence prefference;
    SharedPreferences languageDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_splash);
        mContext = Splash.this;

        prefference = SharedPrefrence.getInstance(mContext);
        prefference.setSkipped(true);
//        prefference.setPro_User_Account_Str("NotAnymore");
//        prefference.setSkipped(true);
        languageDetails = Splash.this.getSharedPreferences(Consts.LANGUAGE_PREF, MODE_PRIVATE);
        if (!hasPermissions(Splash.this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

        } else {
            handler.postDelayed(mTask, SPLASH_TIME_OUT);
        }
        Log.e("<---Splash TAG", "" + prefference.getBooleanValue(Consts.IS_REGISTERED));
    }

    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            if (languageDetails.getBoolean(Consts.LANGUAGE_SELECTION, false)) {
                language(languageDetails.getString(Consts.SELECTED_LANGUAGE, ""));
                Log.e("is skipped ","is "+prefference.isSkipped());
               // Toast.makeText(mContext, ""+prefference.isSkipped(), Toast.LENGTH_SHORT).show();
                if (NetworkManager.isConnectToInternet(mContext)) {

                    if (prefference.getBooleanValue(Consts.IS_REGISTERED) ) {
                        if(prefference.isSkipped())
                        {
                            startActivity(new Intent(mContext, Dashboard.class));
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            finish();

                        }else{
                            if(!prefference.getKEY_UID().isEmpty() &&prefference.isSkipped())
                            {
                                Log.e("is skipped ","is "+prefference.isSkipped());
                                startActivity(new Intent(mContext, Dashboard.class));
                                finish();

                            }else{
                                Log.e("is skipped ","is "+prefference.isSkipped());
                                startActivity(new Intent(mContext, MemberShipActivity.class));
                                finish();
                            }

                        }

                    } else {
                        startActivity(new Intent(mContext, AppIntro.class));
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        finish();

                    }


                } else {
                    ProjectUtils.InternetAlertDialog(Splash.this, getString(R.string.error_connecting), getString(R.string.internet_concation));

                }
            } else {
                startActivity(new Intent(mContext, LanguageSelection.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }


        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                try {

                    cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.CAMERA_ACCEPTED, cameraAccepted);

                    storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.STORAGE_ACCEPTED, storageAccepted);

                    call_privilage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.CALL_PRIVILAGE, call_privilage);

                    callPhone = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.CALL_PHONE, callPhone);
                    handler.postDelayed(mTask, 3000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void language(String language) {
        String languageToLoad = language; // your language

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config,
                mContext.getResources().getDisplayMetrics());


    }

}
