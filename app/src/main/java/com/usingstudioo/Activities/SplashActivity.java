package com.usingstudioo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.R;

import static com.usingstudioo.Constants.Constants.KPurchageToken;
import static com.usingstudioo.Constants.Constants.kAppPreferences;

public class SplashActivity extends AppCompatActivity {

    //private static int SPLASH_TIME_OUT = 1000;


    SharedPreferences sharedPreferences;
    Context mContext;
    String purchageToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext=this;
        sharedPreferences=mContext.getSharedPreferences(kAppPreferences, Context.MODE_PRIVATE);
        purchageToken=sharedPreferences.getString(KPurchageToken,"");

        Log.d("PurchageToken",purchageToken);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());

        ImageView image= findViewById(R.id.img_view);
        image.startAnimation(rotate);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 1000ms
            if(ModelManager.modelManager().getCurrentUser()!=null){
                if(ModelManager.modelManager().getCurrentUser().getEmailVerified().equals("1")){
                    Intent mainIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }else{
                Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000);


    }
}
