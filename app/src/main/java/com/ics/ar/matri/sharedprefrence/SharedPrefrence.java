package com.ics.ar.matri.sharedprefrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.Models.SubscriptionDto;
import com.ics.ar.matri.Models.UserDTO;
import com.ics.ar.matri.Models.UserFire;
import com.ics.ar.matri.utils.StaticConfig;

import java.lang.reflect.Type;

public class SharedPrefrence {
    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static SharedPrefrence myObj;

    private static String SHARE_KEY_NAME = "name";
    private static String IS_ProUser = "IS_ProUser";
    private static String Pro_User_Account_Str = "Pro_User_Account_Str";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";
    private static String KEY_UID = "usid";
    private static String KEY_PRIVACY = "KEY_PRIVACY";

    public  String getKeyPrivacy() {
        return myPrefs.getString(KEY_PRIVACY, "null");
    }

    public  void setKeyPrivacy(String keyPrivacy) {
        prefsEditor.putString(KEY_PRIVACY, keyPrivacy);
        prefsEditor.commit();
    }
    public SharedPrefrence() {
    }

    public  String getPro_User_Account_Str() {
        return myPrefs.getString(Pro_User_Account_Str, "nonnull");
    }

    public  void setPro_User_Account_Str(String pro_User_Account_Str) {
        prefsEditor.putString(Pro_User_Account_Str, pro_User_Account_Str);
        prefsEditor.commit();

    }

    //
//    public static Boolean getIsSubs() {
//        return IS_SUBS;
//    }
//
//    public static void setIsSubs(Boolean isSubs) {
//        IS_SUBS = isSubs;
//    }


    public static SharedPrefrence getInstance(Context ctx) {
        if (myObj == null) {
            myObj = new SharedPrefrence();
            myPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            prefsEditor = myPrefs.edit();
        }
        return myObj;
    }

    public void clearPreferences(String key) {
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public void clearAllPreference() {
        prefsEditor = myPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public long getLongValue(String Tag) {
        return myPrefs.getLong(Tag, 0L);

    }

    public void setLongValue(String Tag, long token) {
        prefsEditor.putLong(Tag, token);
        prefsEditor.commit();
    }

    public boolean getBooleanValue(String Tag) {
        return myPrefs.getBoolean(Tag, false);

    }
    public void setSkipped(boolean isLoggedIn) {
        prefsEditor.putBoolean("", isLoggedIn);
        prefsEditor.commit();
    }


    public void setProUser(boolean isProUser) {
        prefsEditor.putBoolean(IS_ProUser, isProUser);
        prefsEditor.commit();
    }
    // Get Skipped State
    public boolean isSkipped() {
        return myPrefs.getBoolean("", false);
    }

    public boolean isProUser() {
        return myPrefs.getBoolean(IS_ProUser, false);
    }
//    public boolean getIS_SUBS() {
//        return myPrefs.getBoolean(IS_SUBS_Str, false);
//
//    }

    public void setBooleanValue(String Tag, boolean token) {
        prefsEditor.putBoolean(Tag, token);
        prefsEditor.commit();
    }


    public int getIntValue(String Tag) {
        return myPrefs.getInt(Tag, 0);

    }

    public void setIntValue(String Tag, int value) {
        prefsEditor.putInt(Tag, value);
        prefsEditor.commit();
    }


    public String getValue(String Tag) {
        return myPrefs.getString(Tag, "default");
    }
    public String getKEY_UID() {
        return myPrefs.getString(KEY_UID, "default");
    }

    public void setValue(String Tag, String token) {
        prefsEditor.putString(Tag, token);
        prefsEditor.commit();
    }
    public void setKEY_UID(String id) {
        prefsEditor.putString(KEY_UID, id);
        prefsEditor.commit();
    }


    public void setLoginResponse(LoginDTO loginDTO, String tag) {

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(loginDTO);

        prefsEditor.putString(tag, hashMapString);
        prefsEditor.apply();
    }

    public LoginDTO getLoginResponse(String tag) {
        String obj = myPrefs.getString(tag, "defValue");
        if (obj.equals("defValue")) {
            return new LoginDTO();
        } else {

            Gson gson = new Gson();
            String storedHashMapString = myPrefs.getString(tag, "");
            Type type = new TypeToken<LoginDTO>() {
            }.getType();
            LoginDTO testHashMap = gson.fromJson(storedHashMapString, type);

            return testHashMap;
        }
    }

    public void setUserResponse(UserDTO userDTO, String tag) {

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(userDTO);

        prefsEditor.putString(tag, hashMapString);
        prefsEditor.apply();
    }

    public UserDTO getUserResponse(String tag) {
        String obj = myPrefs.getString(tag, "defValue");
        if (obj.equals("defValue")) {
            return new UserDTO();
        } else {

            Gson gson = new Gson();
            String storedHashMapString = myPrefs.getString(tag, "");
            Type type = new TypeToken<UserDTO>() {
            }.getType();
            UserDTO testHashMap = gson.fromJson(storedHashMapString, type);

            return testHashMap;
        }
    }
    public void setSubscription(SubscriptionDto subscriptionDto, String tag) {

        Gson gson = new Gson();
        String hashMapString = gson.toJson(subscriptionDto);

        prefsEditor.putString(tag, hashMapString);
        prefsEditor.apply();
    }

    public SubscriptionDto getSubscription(String tag) {
        String obj = myPrefs.getString(tag, "defValue");
        if (obj.equals("defValue")) {
            return new SubscriptionDto();
        } else {
            Gson gson = new Gson();
            String storedHashMapString = myPrefs.getString(tag, "");
            Type type = new TypeToken<SubscriptionDto>() {
            }.getType();
            SubscriptionDto testHashMap = gson.fromJson(storedHashMapString, type);
            return testHashMap;
        }
    }


    public void saveUserInfo(UserFire user) {
        prefsEditor.putString(SHARE_KEY_NAME, user.name);
        prefsEditor.putString(SHARE_KEY_EMAIL, user.email);
        prefsEditor.putString(SHARE_KEY_AVATA, user.avata);
        prefsEditor.putString(SHARE_KEY_UID, StaticConfig.UID);
        prefsEditor.apply();
    }

    public UserFire getUserInfo(){
        String userName = myPrefs.getString(SHARE_KEY_NAME, "");
        String email = myPrefs.getString(SHARE_KEY_EMAIL, "");
        String avatar = myPrefs.getString(SHARE_KEY_AVATA, "default");

        UserFire user = new UserFire();
        user.name = userName;
        user.email = email;
        user.avata = avatar;

        return user;
    }

    public String getUID(){
        return myPrefs.getString(SHARE_KEY_UID, "");
    }

}