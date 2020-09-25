package com.usingstudioo.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mukesh.OtpView;
import com.skyfishjy.library.RippleBackground;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Models.CurrentUser;
import com.usingstudioo.Utils.CustomLoaderView;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;
import com.usingstudioo.Utils.Validations;
import com.usingstudioo.R;

import java.util.HashMap;
import java.util.Objects;

import static com.usingstudioo.Constants.Constants.kEmail;
import static com.usingstudioo.Constants.Constants.kId;
import static com.usingstudioo.Constants.Constants.kNewPassword;
import static com.usingstudioo.Constants.Constants.kOtp;
import static com.usingstudioo.Constants.Constants.kPassword;
import static com.usingstudioo.Constants.Constants.kUserType;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();
    private EditText etMail,etPass;
    private Dialog dialog,otpDialog;
    private CustomLoaderView loaderView;
    private OtpView otpView;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_screen);
        loaderView = CustomLoaderView.initialize(SignInActivity.this);

        etMail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_password);

        //RippleView rvView = findViewById(R.id.ripple_view);
        //rvView.newRipple();
        RippleBackground ripple = findViewById(R.id.ripple);
        ripple.startRippleAnimation();
        ripple.setVisibility(View.VISIBLE);

        Button btSignIn = findViewById(R.id.btn_sign_in);
        btSignIn.setOnClickListener(v -> {
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
            btSignIn.startAnimation(buttonClick);
            if(validate())
                setLogin(Utils.getProperText(etMail), Utils.getProperText(etPass));
        });

        Button btSignUp = findViewById(R.id.btn_sign_up);
        btSignUp.setOnClickListener(v -> {
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
            btSignUp.startAnimation(buttonClick);
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> forgotDialog());
    }

    private boolean validate() {
        boolean isOk = true;

        if (Utils.getProperText(etMail).isEmpty()) {
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

        return isOk;
    }

    private void setLogin(String email, String password){
        loaderView.showLoader();
        Log.e(TAG,"email: " + email + ",reset_password: " +password);
        HashMap<String,Object> map = new HashMap<>();
        map.put(kEmail,email);
        map.put(kPassword,password);
        map.put(kUserType,"M");
        ModelManager.modelManager().userLoginRequest(map,
                (Constants.Status iStatus, GenericResponse<CurrentUser> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        CurrentUser user = genericResponse.getObject();
                        Log.e(TAG,user.toString());
                            Intent in = new Intent(SignInActivity.this, WelcomeActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void forgotDialog(){
        // dialog
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_forgot_password);

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        EditText editText = dialog.findViewById(R.id.et_email);
        dialog.findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if(Utils.getProperText(editText).isEmpty())
                Toaster.toastRangeen(getString(R.string.email_id_msg));
            else if(!Validations.isValidEmail(Utils.getProperText(editText)))
                Toaster.toastRangeen(getString(R.string.email_id_invalid));
            else
                setForgotPassword(Utils.getProperText(editText),1);
        });
        dialog.show();
    }

    private void setForgotPassword(String email,int type){
        loaderView.showLoader();
        HashMap<String,Object> map = new HashMap<>();
        map.put(kEmail,email);
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userForgotEmail(map,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        userId = genericResponse.getObject();
                        Log.e(TAG,userId);
                        dialog.dismiss();
                        if(type==1)
                            OtpDialog(email);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });

    }

    private void OtpDialog(String email){
        otpDialog = new Dialog(SignInActivity.this);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(otpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.setContentView(R.layout.dialog_otp_view);
        otpDialog.setCancelable(false);

        //TextView tvOTPTime = dialog.findViewById(R.id.tv_otp_time);
        Button btResend = otpDialog.findViewById(R.id.btn_resend);
        Button btContinue = otpDialog.findViewById(R.id.btn_continue);

        btResend.setOnClickListener(view -> setForgotPassword(email,2));
        otpView = otpDialog.findViewById(R.id.otp_view);
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
                setOTP(otpView);
            }
        });

        otpDialog.show();
    }

    private void setOTP(OtpView otpView) {
        loaderView.showLoader();
        HashMap<String, Object> map = new HashMap<>();
        map.put(kId,userId);
        map.put(kOtp, Objects.requireNonNull(otpView.getText()).toString());
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userVerificationOTP(map,1,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG,OTP);
                        otpDialog.dismiss();
                        resetDialog();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void resetDialog() {
        // dialog
        Dialog dialog = new Dialog(Objects.requireNonNull(SignInActivity.this));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_reset_password);

        EditText etNew = dialog.findViewById(R.id.et_new_pass);
        EditText etCon = dialog.findViewById(R.id.et_confirm_pass);

        Button btnReset = dialog.findViewById(R.id.btn_submit);
        btnReset.setOnClickListener(view -> {
            if (Utils.getProperText(etNew).isEmpty())
                Toaster.toastRangeen(getString(R.string.new_pass_msg));
            else if (Utils.getProperText(etCon).isEmpty())
                Toaster.toastRangeen(getString(R.string.renew_pass_msg));
            else if (!Utils.getProperText(etCon).equals(Utils.getProperText(etNew)))
                Toaster.toastRangeen(getString(R.string.renew_pass_invalid));
            else
                setChangePassword(Utils.getProperText(etNew),dialog);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setChangePassword(String pass,Dialog dial){
        loaderView.showLoader();
        HashMap<String,Object> map = new HashMap<>();
        map.put(kId,userId);
        map.put(kNewPassword,pass);
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userForgotPassword(map,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG,OTP);
                        congratsDialog();
                        dial.dismiss();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void congratsDialog(){
        final Dialog dialog = new Dialog(SignInActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_password_sucessfull);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);

        btContinue.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
