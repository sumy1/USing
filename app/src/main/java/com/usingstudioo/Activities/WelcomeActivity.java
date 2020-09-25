package com.usingstudioo.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Utils.CustomLoaderView;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.usingstudioo.Constants.Constants.PERMISSIONS_REQUEST;
import static com.usingstudioo.Constants.Constants.REQUEST_PERMISSIONS_RECORD;
import static com.usingstudioo.Constants.Constants.kPlanStartDate;
import static com.usingstudioo.Constants.Constants.kPlanType;
import static com.usingstudioo.Constants.Constants.kType;
import static com.usingstudioo.Constants.Constants.kUserId;


public class WelcomeActivity extends AppCompatActivity {

    private Dialog dialog;
    private int btnState = 0;
    private CustomLoaderView loaderView;
    String currentDate,plan_trial_count,plan_end_date;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        loaderView = CustomLoaderView.initialize(WelcomeActivity.this);

        currentDate=getCurrentTime();
        findViewById(R.id.card_studio).setOnClickListener(v -> {
            btnState = 1;
            checkAndroidVersion();
        });

        findViewById(R.id.card_sing_music).setOnClickListener(v -> {
            btnState = 2;
            checkAndroidVersion();
        });

        findViewById(R.id.card_vocal_booth).setOnClickListener(v -> {
            btnState = 3;
            checkAndroidVersion();
        });
        getPlan();
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
        return (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        //Method of Fragment
        ActivityCompat.requestPermissions(WelcomeActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSIONS_RECORD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_RECORD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openActivityIntent(btnState);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.permissionTag),
                        Snackbar.LENGTH_SHORT).setAction(getString(R.string.enable_text),
                        v -> requestPermission()).show();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                showDialog();
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
        }
    }

    private void showDialog() {
        dialog = new Dialog(WelcomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_permission_setting);

        TextView text = dialog.findViewById(R.id.tv_dialog);
        text.setText(getResources().getString(R.string.permissionMsg));

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, PERMISSIONS_REQUEST);
        });
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void openActivityIntent(int state) {
        if (state == 1) {
            Intent in = new Intent(WelcomeActivity.this, StudioSplashActivity.class);
            startActivity(in);
            //finish();
        } else if (state == 2) {
            Intent in = new Intent(WelcomeActivity.this, SongListActivity.class);
            in.putExtra(kType, 1);
            startActivity(in);
            //finish();
        } else if (state == 3) {
            Intent in = new Intent(WelcomeActivity.this, SongListActivity.class);
            in.putExtra(kType, 2);
            startActivity(in);
            //finish();
        }
    }


    public void showDialogPurchage() {
        final Dialog dialog = new Dialog(WelcomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_free_subs);
        TextView btn_free_trial = dialog.findViewById(R.id.btn_free_trial);

        btn_free_trial.setOnClickListener(v -> {
            dialog.dismiss();
            addPlan();
            //finish();

        });
        TextView btn_get_subscription = dialog.findViewById(R.id.btn_get_subscription);

        btn_get_subscription.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, SubscriptionPlanActivity.class);
            startActivity(mainIntent);
            finish();
            dialog.dismiss();
        });
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private void getPlan() {
        loaderView.showLoader();
        HashMap<String, Object> map = new HashMap<>();
        map.put(kUserId, ModelManager.modelManager().getCurrentUser().getUserId());
        Log.e("Send", map.toString());
        ModelManager.modelManager().getpaln(map,
                (Constants.Status iStatus, GenericResponse<JSONObject> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        JSONObject jsonObject = genericResponse.getObject();

                        //JSONObject  jsonObject1=jsonObject.getJSONObject(kResponse);
                        String plan=jsonObject.getString("plan");

                        plan_trial_count=jsonObject.getString("plan_trial_count");
                        plan_end_date=jsonObject.getString("plan_end_date");

                        //plan_end_date="2020-08-12";

                        Log.d("Plan",plan+"/"+currentDate.compareTo(plan_end_date)+"/"+plan_trial_count+"?"+plan_end_date);
                        if(plan.equalsIgnoreCase("null") && plan_trial_count.equalsIgnoreCase("0")){
                            showDialogPurchage();
                        }
                        else if((plan.equalsIgnoreCase("1-month")&& currentDate.compareTo(plan_end_date)<0)||(plan.equalsIgnoreCase("1-year")&& currentDate.compareTo(plan_end_date)<0)||(plan.equalsIgnoreCase("weekly")&& currentDate.compareTo(plan_end_date)<0)){
                            dialog.dismiss();
                        }else  {
                            Intent mainIntent = new Intent(this, SubscriptionPlanActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            dialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }


    private void addPlan() {
        loaderView.showLoader();
        HashMap<String,Object> map = new HashMap<>();
        map.put(kUserId, ModelManager.modelManager().getCurrentUser().getUserId());
        map.put(kPlanType,"weekly");
        map.put(kPlanStartDate,currentDate);

        Log.e("send", map.toString());
        ModelManager.modelManager().addPlan(map,
                (Constants.Status iStatus, GenericResponse<JSONObject> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        JSONObject jsonObject = genericResponse.getObject();

                        String plan=jsonObject.getString("plan");


                        showDialogPurchage(plan);


                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    public void showDialogPurchage(String plan) {
        final Dialog dialog = new Dialog(WelcomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscription_sucessfull);
        TextView tv_thanks = dialog.findViewById(R.id.tv_thanks);
        //TextView tv_lets = dialog.findViewById(R.id.tv_lets);
        /*ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });*/

        if (plan.equalsIgnoreCase("weekly")) {
            tv_thanks.setText("Thanks For Choosing Free Trial");
            //tv_lets.setText("1 Month U Sing! Studio Vocal Booth, Sing2Music & W501 Music Library");
        } else if(plan.equalsIgnoreCase("1-month")){
            tv_thanks.setText("Thanks for choosing a monthly plan");
        } else{
            tv_thanks.setText("Thanks for choosing a annual plan");
        }

        //updateUi();
        Button dialogButton = dialog.findViewById(R.id.btn_continue);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
        });


        dialog.show();
    }

    public String getCurrentTime(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }

}
