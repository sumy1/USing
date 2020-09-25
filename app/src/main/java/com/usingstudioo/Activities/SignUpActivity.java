package com.usingstudioo.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mukesh.OtpView;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Models.CurrentUser;
import com.usingstudioo.Utils.CustomLoaderView;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;
import com.usingstudioo.Utils.Validations;
import com.usingstudioo.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.usingstudioo.Constants.Constants.kAgreeTC;
import static com.usingstudioo.Constants.Constants.kDeviceId;
import static com.usingstudioo.Constants.Constants.kDeviceToken;
import static com.usingstudioo.Constants.Constants.kEmail;
import static com.usingstudioo.Constants.Constants.kFullName;
import static com.usingstudioo.Constants.Constants.kId;
import static com.usingstudioo.Constants.Constants.kOtp;
import static com.usingstudioo.Constants.Constants.kPassword;
import static com.usingstudioo.Constants.Constants.kPlanStartDate;
import static com.usingstudioo.Constants.Constants.kPlanType;
import static com.usingstudioo.Constants.Constants.kUserId;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText etUser,etMail,etPass;
    private CheckBox chTerms;
    private CustomLoaderView loaderView;
    String currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        loaderView = CustomLoaderView.initialize(SignUpActivity.this);

        currentDate=getCurrentTime();

        etUser = findViewById(R.id.et_username);
        etMail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_password);

        chTerms = findViewById(R.id.ch_check);
        // Spannable Text for the SignUp Activity
        String firstWord = getResources().getString(R.string.sign_up_msg)+" ";
        String lastWord = getResources().getString(R.string.terms_n_condition);
        Spannable spannable = new SpannableString(firstWord + lastWord);
        spannable.setSpan(spanListener,firstWord.length(), firstWord.length() + lastWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        chTerms.setText(spannable);
        chTerms.setMovementMethod(LinkMovementMethod.getInstance());

        Button btSignIn = findViewById(R.id.btn_sign_up);
        btSignIn.setOnClickListener(v -> {
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
            btSignIn.startAnimation(buttonClick);
            if(validate()){
                setSignUp(getSignUpMap());
            }
        });

        findViewById(R.id.bt_back).setOnClickListener(v -> finish());
    }

    // Span Listener for SignUp Activity
    ClickableSpan spanListener = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            Intent intent=new Intent(SignUpActivity.this, TermsConditionActivity.class);
            startActivity(intent);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            //ds.setTextSize(30);
            ds.setTypeface(Typeface.DEFAULT_BOLD);
            ds.setColor(getResources().getColor(R.color.theme_color));
        }
    };

    private boolean validate() {
        boolean isOk = true;
        if (Utils.getProperText(etUser).isEmpty()) {
            etUser.setError(getString(R.string.user_error));
            etUser.requestFocus();
            isOk = false;
        }
        /*else if (!(Validations.isAlphaNumeric(Utils.getProperText(etUser)))) {
            etUser.setError(getString(R.string.error_invalid_credential));
            etUser.requestFocus();
            isOk = false;
        }*/
        else if (Utils.getProperText(etMail).isEmpty()) {
            etMail.setError(getString(R.string.email_error));
            etMail.requestFocus();
            isOk = false;
        }
        else if (!(Validations.isValidEmail(Utils.getProperText(etMail)))) {
            etMail.setError(getString(R.string.error_invalid_email));
            etMail.requestFocus();
            isOk = false;
        }
        else if (Utils.getProperText(etPass).isEmpty()) {
            etPass.setError(getString(R.string.pass_error));
            etPass.requestFocus();
            isOk = false;
        }
        else if (!Validations.isValidPassword(Utils.getProperText(etPass))) {
            etPass.setError(getString(R.string.error_invalid_password));
            etPass.requestFocus();
            isOk = false;
        }
        else if (!chTerms.isChecked()){
            Toaster.toastRangeen(getString(R.string.agree_toast));
            isOk = false;
        }

        return isOk;
    }

    @SuppressLint("HardwareIds")
    public HashMap<String, Object> getSignUpMap() {
        HashMap<String, Object> loginMap = new HashMap<>();
        loginMap.put(kDeviceId, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        loginMap.put(kFullName, Utils.getProperText(etUser));
        loginMap.put(kEmail, Utils.getProperText(etMail));
        loginMap.put(kPassword, Utils.getProperText(etPass));
        loginMap.put(kDeviceToken, "");
        loginMap.put(kAgreeTC, "1");

        return loginMap;
    }

    private void setSignUp(HashMap<String, Object> map) {
        loaderView.showLoader();
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userRegisterRequest(map,
                (Constants.Status iStatus, GenericResponse<CurrentUser> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        CurrentUser user = genericResponse.getObject();
                        Log.e(TAG,user.toString());
                        OtpDialog();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void OtpDialog(){
        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_otp_view);
        dialog.setCancelable(true);

        //TextView tvOTPTime = dialog.findViewById(R.id.tv_otp_time);
        Button btResend = dialog.findViewById(R.id.btn_resend);
        Button btContinue = dialog.findViewById(R.id.btn_continue);

        btResend.setOnClickListener(view -> resendOTP(ModelManager.modelManager().getCurrentUser().getEmail()));
        OtpView otpView = dialog.findViewById(R.id.otp_view);
        otpView.setOtpCompletionListener(s -> { });

        btContinue.setOnClickListener(v -> {
            /*congratsDialog();
            dialog.dismiss();*/
            if(Objects.requireNonNull(otpView.getText()).toString().isEmpty()){
                Toaster.toastRangeen(getString(R.string.code_msg));
            }else if(otpView.getText().toString().length()!=4){
                Toaster.toastRangeen(getString(R.string.code_invalid));
            }else{
                // set otp Data API
                setOTP(getOTP(otpView),dialog);
            }
        });

        dialog.show();
    }

    public HashMap<String, Object> getOTP(OtpView otpView) {
        HashMap<String, Object> loginMap = new HashMap<>();
        loginMap.put(kId, ModelManager.modelManager().getCurrentUser().getUserId());
        loginMap.put(kOtp, Objects.requireNonNull(otpView.getText()).toString());

        return loginMap;
    }

    private void setOTP(HashMap<String, Object> map,Dialog dialog) {
        loaderView.showLoader();
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userVerificationOTP(map,2,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG,OTP);
                        //congratsDialog();

                        showDialogPurchagee();
                        dialog.dismiss();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void resendOTP(String email) {
        loaderView.showLoader();
        HashMap<String,Object> map = new HashMap<>();
        map.put(kEmail,email);
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userForgotEmail(map,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG,OTP);
                    } catch (Exception e){
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

        Log.e(TAG, map.toString());
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

    private void congratsDialog(){
        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_congratulations);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);

        btContinue.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent=new Intent(SignUpActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }


    public void showDialogPurchagee() {
        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_free_subs);
        TextView btn_free_trial = dialog.findViewById(R.id.btn_free_trial);

        btn_free_trial.setOnClickListener(v -> {
            dialog.dismiss();

            addPlan();
        });
        TextView btn_get_subscription=dialog.findViewById(R.id.btn_get_subscription);

        btn_get_subscription.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, SubscriptionPlanActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            dialog.dismiss();
        });
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(v -> {
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


    public void showDialogPurchage(String plan) {
        final Dialog dialog = new Dialog(SignUpActivity.this);
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
            Intent intent=new Intent(SignUpActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        dialog.show();
    }
}
