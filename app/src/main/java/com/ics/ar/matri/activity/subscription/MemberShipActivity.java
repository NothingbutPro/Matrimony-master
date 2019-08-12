package com.ics.ar.matri.activity.subscription;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ics.ar.matri.activity.dashboard.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.Models.PackagesDTO;
import com.ics.ar.matri.Models.SubscriptionDto;
import com.ics.ar.matri.R;
import com.ics.ar.matri.adapter.PackageslistAdapter;
import com.ics.ar.matri.https.HttpsRequest;
import com.ics.ar.matri.interfaces.Consts;
import com.ics.ar.matri.interfaces.Helper;
import com.ics.ar.matri.network.NetworkManager;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class MemberShipActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MemberShipActivity.class.getCanonicalName();
    private Context mContext;
    private ArrayList<PackagesDTO> packagesDTOlist;
    private PackageslistAdapter packageslistAdapter;
    private SharedPrefrence prefrence;
    Boolean is_already;
    private PackagesDTO packagesDTO;
    private DiscreteScrollView itemPicker;
    private LoginDTO loginDTO;
    IntentFilter filter;
    String payback;
    ProgressDialog post_amountProgressDialog;
    private ImageView ivBack;
    private HashMap<String, String> parms = new HashMap<>();
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    HashMap<String, String> parmsSubs = new HashMap<>();
    private BroadcastReceiver mHandleMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(this);
        setContentView(R.layout.activity_member_ship);
        payback = getIntent().getStringExtra("payback");
        ProjectUtils.statusbarBackgroundTrans(this, R.drawable.headergradient);
        mContext = MemberShipActivity.this;
        is_already = getIntent().getBooleanExtra("is_already" , false);
        prefrence = SharedPrefrence.getInstance(mContext);
        loginDTO = prefrence.getLoginResponse(Consts.LOGIN_DTO);
      //  parms.put(Consts.USER_ID, loginDTO.getData().getId());
        parms.put(Consts.USER_ID, prefrence.getKEY_UID());
      //  parmsSubs.put(Consts.USER_ID, loginDTO.getData().getId());
        parmsSubs.put(Consts.USER_ID, prefrence.getKEY_UID());
        init();
    }

    public void init() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);



        if (NetworkManager.isConnectToInternet(mContext)) {
            getAllPackege();

        } else {
            //  ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            ProjectUtils.InternetAlertDialog(MemberShipActivity.this, getString(R.string.internet_concation), getString(R.string.internet_concation));
        }
    }

    public void getAllPackege() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_ALL_PACKAGES_API, mContext).stringGet(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        packagesDTOlist = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<PackagesDTO>>() {
                        }.getType();
                        packagesDTOlist = (ArrayList<PackagesDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //  ProjectUtils.showToast(mContext, msg);

                }
            }
        });
    }

    public void showData() {

        packageslistAdapter = new PackageslistAdapter(this, packagesDTOlist);
        itemPicker.setAdapter(packageslistAdapter);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                if(!prefrence.isSkipped())
                {
                    finish();
                    moveTaskToBack(true);
                }

                finish();
                break;
        }
    }

    public void updateList(int pos) {
        for (int i = 0; i < packagesDTOlist.size(); i++) {
            if (i == pos) {
                packagesDTOlist.get(i).setSelected(true);
                packagesDTO = packagesDTOlist.get(i);
                Log.e("dto", "" + packagesDTO.getSubscription_type() + packagesDTO.getId());
            } else {
                packagesDTOlist.get(i).setSelected(false);
            }
        }
    }

    public void payment() {
        parms.put(Consts.PACKAGE_ID, packagesDTO.getId());
        parms.put(Consts.ORDER_ID, getOrderID());

        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.SUBSCRIPTION_API,parms, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                     ProjectUtils.showToast(mContext, msg);
                    getMySubscription();
                } else {
                     ProjectUtils.showToast(mContext, msg);

                }
            }
        });
    }

    public void getMySubscription() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_MY_SUBSCRIPTION_API, parmsSubs, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {

                        try {
                            SubscriptionDto subscriptionDto = new Gson().fromJson(response.getJSONObject("data").toString(), SubscriptionDto.class);
                            prefrence.setSubscription(subscriptionDto, Consts.SUBSCRIPTION_DTO);
                            prefrence.setBooleanValue(Consts.IS_SUBSCRIBE, true);

                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();


                } else {
                    prefrence.setBooleanValue(Consts.IS_SUBSCRIBE, false);
                }
            }
        });
    }


    public String getOrderID() {
        Random txnID = new Random();

        StringBuilder builder = new StringBuilder();
        for (int count = 0; count <= 5; count++) {
            builder.append(txnID.nextInt(10));
        }
        return builder.toString();
    }
//
//    public void startPayment() {
//        /*
//          You need to pass current activity in order to let Razorpay create CheckoutActivity
//         */
//        final Activity activity = this;
//
//        final Checkout co = new Checkout();
//
//        try {
//            int mony = Integer.parseInt(packagesDTO.getPrice()) *100;
//            JSONObject options = new JSONObject();
//            options.put("name", loginDTO.getData().getName());
//            options.put("description", "Add Money to wallet");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("currency", "INR");
//            options.put("amount", mony+"");
//
//            JSONObject preFill = new JSONObject();
//            preFill.put("email", loginDTO.getData().getEmail());
//            preFill.put("contact", loginDTO.getData().getMobile());
//
//            options.put("prefill", preFill);
//
//            co.open(activity, options);
//        } catch (Exception e) {
//            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onPaymentSuccess(String razorpayPaymentID) {
//        try {
//            ProjectUtils.showToast(mContext, "Payment Successful");
//            parms.put(Consts.TXN_ID, razorpayPaymentID + "");
//            payment();
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in onPaymentSuccess", e);
//        }
//    }

//    @Override
//    public void onPaymentError(int code, String response) {
//        try {
//            ProjectUtils.showToast(mContext, "Payment failed");
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in onPaymentError", e);
//        }
//    }
    //insta mojo

    public void callInstamojoPay() {
        int mony = Integer.parseInt(packagesDTO.getPrice());
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
//            Intent intent = new Intent("ai.devsupport.instamojo");
//            sendBroadcast(intent);
//            mHandleMessageReceiver.onReceive(getApplicationContext() , intent);

//            unregisterForContextMenu();


        JSONObject pay = new JSONObject();
        mony = 10;
        try {
            pay.put("email", loginDTO.getData().getEmail());
           pay.put("phone", loginDTO.getData().getMobile());
            pay.put("purpose", "Add Money to wallet");
            pay.put("amount", mony+"");
            pay.put("name", loginDTO.getData().getName());
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                register();
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
                        .show();
                   // prefrence.setPro_User_Account_Str("NotAnymore");
                    prefrence.setSkipped(true);


                 new Give_That_To_Server(prefrence.getKEY_UID() ,Integer.parseInt(packagesDTO.getPrice()) ).execute();

            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
//                    prefrence.setPro_User_Account_Str("");
//                    prefrence.setSkipped(true);
//                if(prefrence.getPro_User_Account_Str().equals())
//                {
//                    prefrence.setPro_User_Account_Str("NotAnymore");
//                    prefrence.setSkipped(true);
//                }
//                else{
//                    prefrence.setPro_User_Account_Str("null");
//                    prefrence.setSkipped(false);
//                }

                Toast.makeText(mContext, "is skipped is "+prefrence.isSkipped(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void register() {
                try {
            ProjectUtils.showToast(mContext, "Payment Successful");
            parms.put(Consts.TXN_ID, loginDTO.getData().getName()+ ""+loginDTO.getData().getEmail());
           payment();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }
public class  Give_That_To_Server extends AsyncTask<Void , Void , String>{
        String key_ud ;
        int ammount;


    public Give_That_To_Server(String key_uid, int i) {
        this.key_ud = key_uid;
        this.ammount = i;

    }

    @Override
    protected void onPreExecute() {
        post_amountProgressDialog = new ProgressDialog(MemberShipActivity.this);
        post_amountProgressDialog.show();
        super.onPreExecute();
    }



    @Override
    protected String doInBackground(Void... voids) {
        try {

            URL url = new URL("https://www.arshmuslimmarriage.com/matrimony/api/payment-add");

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("user_id", key_ud);
            postDataParams.put("amount", ammount);
//
//        // Getting all registered Google Accounts;
//        // Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
//
//        // Getting all registered Accounts;
//        Account[] accounts = AccountManager.get(this).getAccounts();


            Log.e("postDataParams", postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000  /*milliseconds*/);
            conn.setConnectTimeout(15000  /*milliseconds*/);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    StringBuffer Ss = sb.append(line);
                    Log.e("Ss", Ss.toString());
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            //  dialog.dismiss();

            JSONObject jsonObject = null;
            Log.e("PostRegistration", result.toString());
            try {

                jsonObject = new JSONObject(result);
                String response = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                if (response.equalsIgnoreCase("true")) {
                    Toast.makeText(mContext, "Amount has been"+message+"Updated", Toast.LENGTH_SHORT).show();
                    Intent to_dash = new Intent(MemberShipActivity.this , Dashboard.class);
                    startActivity(to_dash);
                }else{
                    Toast.makeText(mContext, "Failed to post the amount on server", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        super.onPostExecute(result);
    }
}
    //


    @Override
    public void onBackPressed() {
        if(!prefrence.isSkipped())
        {
            finish();
            moveTaskToBack(true);

        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
//        unregisterReceiver();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
            if (post_amountProgressDialog.isShowing() && post_amountProgressDialog != null) {
                post_amountProgressDialog.dismiss();
            }
        }catch (Exception e)
        {

        }
//        try{
//            if(mHandleMessageReceiver!=null)
//                unregisterReceiver(mHandleMessageReceiver);
//
//        }catch(Exception e){}
        super.onDestroy();
    }
}
