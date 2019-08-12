package com.ics.ar.matri.activity.loginsignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.R;
import com.ics.ar.matri.activity.dashboard.Dashboard;
import com.ics.ar.matri.https.HttpsRequest;
import com.ics.ar.matri.interfaces.Consts;
import com.ics.ar.matri.interfaces.Helper;
import com.ics.ar.matri.network.NetworkManager;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;
import com.ics.ar.matri.view.CustomButton;
import com.ics.ar.matri.view.CustomEditText;
import com.ics.ar.matri.view.CustomTextView;
import com.ics.ar.matri.view.ExtendedEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private ExtendedEditText etNumber;
    private CustomEditText etPassword;
    private CustomButton btnLogin, btnSearch;
    private CustomTextView tvForgotPass;
    private Button tvCreateNewAC;
    private Context mContext;
    private String TAG = Login.class.getSimpleName();
    private RelativeLayout RRsncbar;
    private SharedPrefrence prefrence;
    private LoginDTO loginDTO;
    private SharedPreferences firebase;
    Dialog priDlg = null;
    private Button Tarm;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mContext = Login.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.FIREBASE_TOKEN, ""));
        setUIAction();
        try{
            if(!prefrence.getKeyPrivacy().equals("null"))
            {
                Toast.makeText(this, "Privacy all set", Toast.LENGTH_SHORT).show();
            }else{
                PrivacyDlg();
            }

        }catch (Exception e)
        {

            e.printStackTrace();
        }
    }
    private void PrivacyDlg() {
        try {
            if (priDlg != null) {
                priDlg.cancel();
            }
            priDlg = new Dialog(Login.this, R.style.MyDialogTheme1);
            priDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            priDlg.setContentView(R.layout.dailog_privacy);
            priDlg.setCanceledOnTouchOutside(false);
            WebView app_pri = priDlg.findViewById(R.id.app_pri);
            app_pri.loadUrl("http://www.arshmuslimmarriage.com/PrivacyPolicy.html");
//            TextView tvPolicy = (TextView) priDlg.findViewById(R.id.tv_txt);
//            TextView tv_txt2 = (TextView) priDlg.findViewById(R.id.tv_txt2);
//            tvPolicy.setText(R.string.app_privacy);
//            tv_txt2.setText(R.string.app_privacy1);
            //  tvPolicy.setText(Html.fromHtml(String.valueOf(R.string.app_privacy)));

            Tarm = (Button) priDlg.findViewById(R.id.btn_tarm);
            checkbox = (CheckBox) priDlg.findViewById(R.id.checkbox);
            priDlg.show();
            Tarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkbox.isChecked()) {
                        //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        try {
//                            editor.putString("privace_flag", "1");
//                            editor.commit();
                            //    CallVersionApi();//do somethisng
                            if (NetworkManager.isConnectToInternet(mContext)) {

                                if (priDlg != null) {
                                    prefrence.setKeyPrivacy("yes");
                                    priDlg.dismiss();
                                }
                            }else{
                                Toast.makeText(Login.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Accept terms & conditions", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setUIAction() {
        RRsncbar = findViewById(R.id.RRsncbar);
        etPassword = findViewById(R.id.etPassword);
        etNumber = findViewById(R.id.etNumber);
        etNumber.setPrefix("+91 ");
        btnLogin = findViewById(R.id.btnLogin);
     //   btnSearch = findViewById(R.id.btnSearch);
        btnLogin.setOnClickListener(this);
        //btnSearch.setOnClickListener(this);

        tvCreateNewAC = findViewById(R.id.tvCreateNewAC);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvCreateNewAC.setOnClickListener(this);
        tvForgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                clickForSubmit();
                break;
            case R.id.tvCreateNewAC:
                startActivity(new Intent(mContext, Registration.class));
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                break;
//            case R.id.btnSearch:
//                startActivity(new Intent(mContext, Search.class));
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);
//                break;
            case R.id.tvForgotPass:
                Intent in = new Intent(mContext, ForgotPass.class);
                startActivity(in);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                break;
        }
    }

    public void clickForSubmit() {
        if (!ProjectUtils.isPhoneNumberValid(etNumber.getText().toString().trim())) {
            showSickbar(getString(R.string.val_mobile));
        } else if (!ProjectUtils.IsPasswordValidation(etPassword.getText().toString().trim())) {
            showSickbar(getString(R.string.val_password));
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                login();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }


    }

    public void login() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.LOGIN_API, getparm(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {


                    ProjectUtils.showToast(mContext, msg);

                    loginDTO = new Gson().fromJson(response.toString(), LoginDTO.class);

                    try {
                        prefrence.setKEY_UID(response.getString("user_id"));
                        response.getString("amount");
                      //  prefrence.setPro_User_Account_Str(response.getString("amount"));
                        prefrence.setLoginResponse(loginDTO, Consts.LOGIN_DTO);
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);
                        ProjectUtils.showToast(mContext, msg);

                        prefrence.setSkipped(true);
                        Intent in = new Intent(mContext, Dashboard.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                           // Toast.makeText(mContext, "Please buy a subscription", Toast.LENGTH_SHORT).show();
////                            prefrence.setSkipped(false);
//                          Intent to_membershi = new Intent(Login.this , MemberShipActivity.class);
//                          startActivity(to_membershi);



                } else {

                }


            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.MOBILE, ProjectUtils.getEditTextValue(etNumber));
        parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(etPassword));
        parms.put(Consts.FIREBASE_TOKEN, firebase.getString(Consts.FIREBASE_TOKEN, ""));
        Log.e(TAG + " Login", parms.toString());
        return parms;
    }


    public void showSickbar(String msg) {
        Snackbar snackbar = Snackbar.make(RRsncbar, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }



}
