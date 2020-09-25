package com.usingstudioo.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.usingstudioo.Billing.IabBroadcastReceiver;
import com.usingstudioo.Billing.IabHelper;
import com.usingstudioo.Billing.IabResult;
import com.usingstudioo.Billing.Inventory;
import com.usingstudioo.Billing.Purchase;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.usingstudioo.Billing.Constants.SKU_DELAROY_ANNUAL;
import static com.usingstudioo.Billing.Constants.SKU_DELAROY_MONTHLY;
import static com.usingstudioo.Billing.Constants.base64EncodedPublicKey;
import static com.usingstudioo.Constants.Constants.KPurchageToken;
import static com.usingstudioo.Constants.Constants.PERMISSIONS_REQUEST;
import static com.usingstudioo.Constants.Constants.REQUEST_PERMISSIONS_RECORD;
import static com.usingstudioo.Constants.Constants.kAppPreferences;
import static com.usingstudioo.Constants.Constants.kPlanStartDate;
import static com.usingstudioo.Constants.Constants.kPlanType;
import static com.usingstudioo.Constants.Constants.kUserId;


public class SubscriptionPlanActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private Dialog dialog;
    private int btnState = 0;
    TextView tv_start_subs;

    String plan_type;
    //Subscription Plan..

    static final String TAG = SubscriptionPlanActivity.class.getSimpleName();

    // Does the user have an active subscription to the delaroy plan?
    boolean mSubscribedToDelaroy = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned subscription, and the options in the Manage dialog
    String mDelaroySku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";

    // Used to select between subscribing on a monthly, three month, six month or yearly basis
    String mSelectedSubscriptionPeriod = "";

    // SKU for our subscription

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;


    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    Context mContext;

    SharedPreferences sharedPreferences;
    String currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        mContext = this;
        currentDate=getCurrentTime();
        sharedPreferences=mContext.getSharedPreferences(kAppPreferences,Context.MODE_PRIVATE);
        tv_start_subs = findViewById(R.id.tv_start_subs);

        Spannable wordtoSpan = new SpannableString(getResources().getString(R.string.please_choose_subscription));
        wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.theme_color)), 1, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //wordtoSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)), 32, 62, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_start_subs.setText(wordtoSpan);
        createHelperBilling();
        /*findViewById(R.id.card_studio).setOnClickListener(v -> {
           btnState=1;
           checkAndroidVersion();
        });*/

        findViewById(R.id.card_sing_music).setOnClickListener(v -> {
            btnState = 1;
            checkAndroidVersion();
        });

        findViewById(R.id.card_vocal_booth).setOnClickListener(v -> {
            btnState = 2;
            checkAndroidVersion();
        });
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission())
                openActivityIntent(btnState);
            else
                requestPermission();
        } else {
            openActivityIntent(btnState);
        }
    }

    private boolean checkSelfPermission() {
        return (ActivityCompat.checkSelfPermission(SubscriptionPlanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SubscriptionPlanActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        //Method of Fragment
        ActivityCompat.requestPermissions(SubscriptionPlanActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSIONS_RECORD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_RECORD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openActivityIntent(btnState);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(SubscriptionPlanActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(SubscriptionPlanActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.permissionTag),
                        Snackbar.LENGTH_SHORT).setAction(getString(R.string.enable_text),
                        v -> requestPermission()).show();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(SubscriptionPlanActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(SubscriptionPlanActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                //showDialogPurchage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (checkSelfPermission()) {
                dialog.dismiss();
            }
        } else {

            if (mHelper == null) return;

            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...

                Log.d("data",data+"");
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }

        }
    }

    public void showDialogPurchage(String state) {
        dialog = new Dialog(SubscriptionPlanActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscription_sucessfull);
        TextView tv_thanks = dialog.findViewById(R.id.tv_thanks);
        //TextView tv_lets = dialog.findViewById(R.id.tv_lets);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });


        if (state.equalsIgnoreCase("1")) {
            tv_thanks.setText("Thanks For Choosing Monthly Subscription");
            //tv_lets.setText("1 Month U Sing! Studio Vocal Booth, Sing2Music & W501 Music Library");
        } else if (state.equalsIgnoreCase("2")) {
            tv_thanks.setText("Thanks For Choosing Annual Subscription");
            //tv_lets.setText("1 Year  U Sing! Studio Vocal Booth, Sing2Music & W501 Music Library");
        } else {

        }

        //updateUi();
        setWaitScreen(false);
        Button dialogButton = dialog.findViewById(R.id.btn_continue);
        dialogButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, SignUpActivity.class);
            startActivity(mainIntent);
            dialog.dismiss();
        });


        dialog.show();
    }


    public void showDialogPurchagee(String plan) {
        final Dialog dialog = new Dialog(SubscriptionPlanActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscription_sucessfull);
        TextView tv_thanks = dialog.findViewById(R.id.tv_thanks);
        //TextView tv_lets = dialog.findViewById(R.id.tv_lets);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (plan.equalsIgnoreCase("weekly")) {
            tv_thanks.setText("Thanks For Choosing Free Trial");
            //tv_lets.setText("1 Month U Sing! Studio Vocal Booth, Sing2Music & W501 Music Library");
        } else {

        }

        //updateUi();
        Button dialogButton = dialog.findViewById(R.id.btn_continue);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });


        dialog.show();
    }

    //...Subscription Plan..code here..

    private void createHelperBilling() {
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(SubscriptionPlanActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }


    private void getSubscriptionMonthly() {


        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }


        if (!mSubscribedToDelaroy || !mAutoRenewEnabled) {
            mFirstChoiceSku = SKU_DELAROY_MONTHLY;


        } else {
            // This is the subscription upgrade/downgrade path, so only one option is valid
            // options = new CharSequence[3];
            if (mDelaroySku.equals(SKU_DELAROY_MONTHLY)) {
                // Give the option to upgrade below
                //options[1] = getString(R.string.subscription_period_yearly);
                mFirstChoiceSku = SKU_DELAROY_MONTHLY;

            } else {
                // Give the option to upgrade or downgrade below
                /*options[0] = getString(R.string.subscription_period_monthly);
                options[1] = getString(R.string.subscription_period_yearly);*/
                mFirstChoiceSku = SKU_DELAROY_MONTHLY;

            }
        }


        String payload = "";

        if (TextUtils.isEmpty(mSelectedSubscriptionPeriod)) {
            // The user has not changed from the default selection
            mSelectedSubscriptionPeriod = mFirstChoiceSku;
        }

        List<String> oldSkus = null;
        if (!TextUtils.isEmpty(mDelaroySku)
                && !mDelaroySku.equals(mSelectedSubscriptionPeriod)) {
            // The user currently has a valid subscription, any purchase action is going to
            // replace that subscription
            oldSkus = new ArrayList<>();
            oldSkus.add(mDelaroySku);
        }

        setWaitScreen(true);
        try {
            mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
        // Reset the dialog options
        mSelectedSubscriptionPeriod = "";
        mFirstChoiceSku = "";

    }

    private void getSubscriptionAnnual() {


        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        if (!mSubscribedToDelaroy || !mAutoRenewEnabled) {
            mSecondChoiceSku = SKU_DELAROY_ANNUAL;


        } else {

            if (mDelaroySku.equals(SKU_DELAROY_ANNUAL)) {
                // Give the option to upgrade or downgrade below

                mSecondChoiceSku = SKU_DELAROY_ANNUAL;
            } else {


                mSecondChoiceSku = SKU_DELAROY_ANNUAL;

            }
        }


        String payload = "";

        if (TextUtils.isEmpty(mSelectedSubscriptionPeriod)) {
            // The user has not changed from the default selection
            mSelectedSubscriptionPeriod = mSecondChoiceSku;
        }

        List<String> oldSkus = null;
        if (!TextUtils.isEmpty(mDelaroySku)
                && !mDelaroySku.equals(mSelectedSubscriptionPeriod)) {
            // The user currently has a valid subscription, any purchase action is going to
            // replace that subscription
            oldSkus = new ArrayList<>();
            oldSkus.add(mDelaroySku);
        }

        setWaitScreen(true);
        try {
            mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
        // Reset the dialog options
        mSelectedSubscriptionPeriod = "";
        mSecondChoiceSku = "";
    }


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // First find out which subscription is auto renewing


            Purchase delaroyMonthly = inventory.getPurchase(SKU_DELAROY_MONTHLY);
            Purchase delaroyWeekly = inventory.getPurchase(SKU_DELAROY_ANNUAL);


            if (delaroyMonthly != null && delaroyMonthly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_MONTHLY;
                mAutoRenewEnabled = false;
            } else if (delaroyWeekly != null && delaroyWeekly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_ANNUAL;
                mAutoRenewEnabled = false;
            } else {
                mDelaroySku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToDelaroy = (delaroyMonthly != null && verifyDeveloperPayload(delaroyMonthly))
                    || (delaroyWeekly != null && verifyDeveloperPayload(delaroyWeekly));
            Log.d(TAG, "User " + (mSubscribedToDelaroy ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");

           // updateUi();
            //setWaitScreen(true);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if(purchase==null){

            }else{
                String purchaseToken=purchase.getToken();

                Log.d("PurchageToken",purchaseToken);

                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putString(KPurchageToken,purchaseToken);
                editor.commit();
            }




            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_DELAROY_MONTHLY)
                    || purchase.getSku().equals(SKU_DELAROY_ANNUAL)) {
                // bought the rasbita subscription
                Log.d(TAG, "Demo subscription purchased.");
                //alert("Thank you for subscribing to Demo!");
                mSubscribedToDelaroy = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mDelaroySku = purchase.getSku();

                Log.d("Sku", mDelaroySku);

                addPlan();
                //showDialogPurchage(String.valueOf(btnState));
                //updateUi();
                //setWaitScreen(false);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            addPlan();
            //showDialogPurchage(String.valueOf(btnState));
            //updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    // updates UI to reflect model
    public void updateUi() {

        if (mSubscribedToDelaroy) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // The user does not have rabista subscription"

        }


    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
    }

    void complain(String message) {
        Log.e(TAG, "**** Delaroy Error: " + message);
        // alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private void openActivityIntent(int state) {
        if (state == 1) {
            plan_type="1-month";
            getSubscriptionMonthly();
            Log.d("Click", "1");
            //showDialogPurchage(String.valueOf(1));
        } else if (state == 2) {
            plan_type="1-year";
            getSubscriptionAnnual();
            Log.d("Click", "2");
           // showDialogPurchage(String.valueOf(2));

        }
    }


    private void addPlan() {
        HashMap<String,Object> map = new HashMap<>();
        map.put(kUserId, ModelManager.modelManager().getCurrentUser().getUserId());
        map.put(kPlanType,plan_type);
        map.put(kPlanStartDate,currentDate);

        Log.e("send", map.toString());
        ModelManager.modelManager().addPlan(map,
                (Constants.Status iStatus, GenericResponse<JSONObject> genericResponse) -> {

                    try {
                        JSONObject jsonObject = genericResponse.getObject();

                        String plan=jsonObject.getString("plan");


                        showDialogPurchagee(plan);


                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {

                    Toaster.toastRangeen(message);
                });
    }

    public String getCurrentTime(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(mContext, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
