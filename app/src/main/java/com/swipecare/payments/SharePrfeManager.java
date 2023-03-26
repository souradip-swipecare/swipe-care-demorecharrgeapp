package com.swipecare.payments;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrfeManager {
    private static final String SHARE_PREFRERENCE = "user";
    private static SharePrfeManager mInstance;
    private static Context mContext;
    private static String apiUserId = "apiUserId";
    private static String apiPassword = "apiPassword";

    private SharePrfeManager(Context context) {
        mContext = context;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean shouldSetApiCredentials = sharedPreferences.getBoolean("shouldSetApiUserIdAndPassword", true);
        if (shouldSetApiCredentials) {
            setApiUserIdAndPassword();
            editor.putBoolean("shouldSetApiUserIdAndPassword", false);
            editor.apply();
        }
    }

    public static synchronized SharePrfeManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharePrfeManager(context);
        }
        return mInstance;
    }


    public boolean mCheckLogin() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String name = sharedPreferences.getString("name", "");

        if (!name.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void mSetBaseUrl(String baseurl) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("baseurl", baseurl);
        editor.apply();
    }

    public static String mGetBaseUrl() {
        return "https://rc.quickrc.in/";
    }

    public String mGetMainBalance() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String main_balance = sharedPreferences.getString("mainwallet", "");
        return main_balance;
    }

    public String mGetAEPSBalance() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String aeps_balance = sharedPreferences.getString("aepsbalance", "");
        return aeps_balance;
    }

    public void mSaveUserData(String username, String password, String id, String name, String email, String mobile, String mainwallet, String aepsbalance, String role_id, String parent_id,
                              String status, String company_id, String shopname, String apptoken,
                              String utiid, String utiidtxnid, String utiidstatus, String tokenamount, String account, String bank, String ifsc, String aepsid, String upi_status, String bharat_status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("mobile", mobile);
        editor.putString("mainwallet", mainwallet);
        editor.putString("aepsbalance", aepsbalance);
        editor.putString("role_id", role_id);
        editor.putString("parent_id", parent_id);
        editor.putString("status", status);
        editor.putString("company_id", company_id);
        editor.putString("shopname", shopname);
        editor.putString("apptoken", apptoken);
        editor.putString("utiid", utiid);
        editor.putString("utiidtxnid", utiidtxnid);
        editor.putString("utiidstatus", utiidstatus);
        editor.putString("tokenamount", tokenamount);
        editor.putString("account", account);
        editor.putString("bank", bank);
        editor.putString("ifsc", ifsc);
        editor.putString("aepsid", aepsid);
        editor.putString("upi_status", upi_status);
        editor.putString("bharat_status", bharat_status);
        editor.commit();

    }

    public void mLogout() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public String mGetName() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String name = sharedPreferences.getString("name", "");
        return name;
    }

    public String mGetMobile() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String mobile = sharedPreferences.getString("mobile", "");
        return mobile;
    }

    public String mGetUsername() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String password = sharedPreferences.getString("username", "");
        return password;
    }

    public String mGetPassword() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String password = sharedPreferences.getString("password", "");
        return password;
    }

    public String getEmail(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        return sharedPreferences.getString("email", "");
    }

    public  String mGetUserId() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String userid = sharedPreferences.getString("id", "");
        return userid;
    }

    public  String mGetappToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String apptoken = sharedPreferences.getString("apptoken", "");
        return apptoken;
    }

    public String mGetUtiid() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String utiid = sharedPreferences.getString("utiid", "");
        return utiid;
    }

    public void mSetUtiid(String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("utiid", status);
    }

    public String mGetUtiidTxnId() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String utiidtxnid = sharedPreferences.getString("utiidtxnid", "");
        return utiidtxnid;
    }

    public String mGetUttidStatus() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String utiidstatus = sharedPreferences.getString("utiidstatus", "");
        return utiidstatus;
    }

    public String mGetTokenAmount() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String tokenamount = sharedPreferences.getString("tokenamount", "");
        return tokenamount;
    }

    public String mGetAccount() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String account = sharedPreferences.getString("account", "");
        if (account.equalsIgnoreCase("null")) {
            return "";
        } else {
            return account;
        }
    }

    public String mGetBank() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String bank = sharedPreferences.getString("bank", "");
        if (bank.equalsIgnoreCase("null")) {
            return "";
        } else {
            return bank;
        }
    }

    public String mGetIFSC() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String ifsc = sharedPreferences.getString("ifsc", "");
        if (ifsc.equalsIgnoreCase("null")) {
            return "";
        } else {
            return ifsc;
        }
    }

    public String mGetAepsid() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String aepsid = sharedPreferences.getString("aepsid", "");
        if (aepsid.equalsIgnoreCase("null")) {
            return "";
        } else {
            return aepsid;
        }
    }

    private void setApiUserIdAndPassword() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(apiUserId, "4375");
        editor.putString(apiPassword, "Swipe@2020");
        editor.apply();
    }

    public String getApiUserId() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        return sharedPreferences.getString(apiUserId, "");
    }

    public String getApiPassword() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        return sharedPreferences.getString(apiPassword, "");
    }

    public String getPartnerId() {
        return "PS002096";
    }
    public String getPartnerApiKey() {
        return "UFMwMDIwOTZmYzRjZThmYjZkMjBmYWIyYjdhNDVkODIzMDM4MmFiZQ==";
    }
    public String mGetbharat() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String upi_status = sharedPreferences.getString("upi_status", "");
        return upi_status;
    }
    public String mGetupi() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFRERENCE, 0);
        String bharat_status = sharedPreferences.getString("bharat_status", "");
        return bharat_status;
    }
}
