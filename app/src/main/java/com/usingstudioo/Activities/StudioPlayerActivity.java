package com.usingstudioo.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.usingstudioo.ApplicationManager;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Broadcast.ReachabilityManager;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Models.BaseModel;
import com.usingstudioo.Models.CurrentUser;
import com.usingstudioo.Models.Midi;
import com.usingstudioo.Models.Notes;
import com.usingstudioo.Utils.CustomLoaderView;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;
import com.usingstudioo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.usingstudioo.Constants.Constants.ACTIVITY_TYPE_RESULT;
import static com.usingstudioo.Constants.Constants.kChangePassword;
import static com.usingstudioo.Constants.Constants.kCurrentUser;
import static com.usingstudioo.Constants.Constants.kData;
import static com.usingstudioo.Constants.Constants.kEditProfile;
import static com.usingstudioo.Constants.Constants.kGotoMenu;
import static com.usingstudioo.Constants.Constants.kId;
import static com.usingstudioo.Constants.Constants.kLogout;
import static com.usingstudioo.Constants.Constants.kMessageNetworkError;
import static com.usingstudioo.Constants.Constants.kMySubscription;
import static com.usingstudioo.Constants.Constants.kNewPassword;
import static com.usingstudioo.Constants.Constants.kOldPassword;
import static com.usingstudioo.Constants.Constants.kRange;
import static com.usingstudioo.Constants.Constants.kRangeName;
import static com.usingstudioo.Constants.Constants.kScale;
import static com.usingstudioo.Constants.Constants.kScaleName;
import static com.usingstudioo.Constants.Constants.kTitle;
import static com.usingstudioo.Constants.Constants.kType;
import static com.usingstudioo.Constants.Constants.kUserId;

public class StudioPlayerActivity extends AppCompatActivity{

    private final static String TAG = StudioPlayerActivity.class.getSimpleName();
    private CustomLoaderView loaderView;
    private BarVisualizer mVisualizer;
    private MediaPlayer mPlayer;
    private ImageView ivPlay;
    private TextView tvTimer;
    private CountDownTimer timer;
    private long milliLeft,min,sec;
    private int noteTimeCount=1;

    private TextView tvScale,tvRange,tvChord;
    private int scalePos=0,rangePos=0;
    private String rangeTxt,scaleTxt;
    private Midi midi;
    private ArrayList<Notes> notes;
    private HashMap<Integer,Object> noteMap;
    private int[] noteTimeArr = new int[400];

    private MediaObserver observer = null;
    private CircularProgressBar circularProgressBar;
    private SeekBar speedSeekBar;
    private float mediaSpeed = 1f;
    String currentDate,plan,plan_start_date,plan_end_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_screen);
        loaderView = CustomLoaderView.initialize(StudioPlayerActivity.this);
        rangeTxt = "Bass";
        scaleTxt = "Chromatic Scale";
        getPlan();
        //init music visualizer
        mVisualizer = findViewById(R.id.blast);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        ivPlay = findViewById(R.id.iv_play);
        tvScale = findViewById(R.id.tv_scale);
        tvRange = findViewById(R.id.tv_range);
        tvChord = findViewById(R.id.tv_chord);
        tvTimer = findViewById(R.id.tv_timer);

        //init Tempo SeekBar
        speedSeekBar = findViewById(R.id.audio_seek);
        mediaSpeedSeekBar();

        //init Listeners
        initListeners();

        //init MidiFile
        try {
            midi = new Midi(new JSONObject(Utils.loadJSONFromAsset(this,rangeTxt,scalePos)));
            notes = midi.getNotes();
            noteMap = midi.getNote();
            noteTimeArr = midi.getAccessNotes();
            tvChord.setText(notes.get(0).getNoteName());
            Log.i("times", Arrays.toString(noteTimeArr));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        findViewById(R.id.iv_logo).setOnClickListener(v -> finish());
    }




    private void initListeners(){
        ImageButton btProfile = findViewById(R.id.bt_profile);
        btProfile.setOnClickListener(v -> showListMenu(btProfile));
        findViewById(R.id.bt_song).setOnClickListener(v -> setMusicIntent());
        findViewById(R.id.bt_info).setOnClickListener(v -> setInfoIntent());
        findViewById(R.id.bt_choose).setOnClickListener(v -> setTypeIntent());

        //Play Btn Listener
        findViewById(R.id.btn_play).setOnClickListener(v -> {
            if(mPlayer==null) {
                startPlaying(mediaSpeed);
            } else if(mPlayer.isPlaying()){
                mPlayer.pause();
                ivPlay.setImageDrawable(getDrawable(R.drawable.play));
                timerPause();
                observer.stop();
            } else {
                mPlayer.start();
                ivPlay.setImageDrawable(getDrawable(R.drawable.pause));
                timerResume();
                observer = new MediaObserver();
                new Thread(observer).start();
                setVisualizerInit();
            }
        });

        //Reset Btn Listener
        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            stopPlaying();
            startPlaying(mediaSpeed);
        });

        //Stop Btn Listener
        findViewById(R.id.btn_stop).setOnClickListener(v -> stopPlaying());
    }

    //Method for play media file from start
    private void startPlaying(float speed){
        mPlayer = new MediaPlayer();
        try {
            String assetName = "audio/"+rangeTxt+scalePos+".mid";
            AssetFileDescriptor afd = getAssets().openFd(assetName);
            mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mPlayer.prepare();
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setPitch(1f));
            }
            mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));

            circularProgressBar.setProgressMax(mPlayer.getDuration());
            mPlayer.start();
            mPlayer.setOnCompletionListener(mp -> {
                stopPlaying();
                observer.stop();
                circularProgressBar.setProgress(0);
            });

            timerStart(mPlayer.getDuration(),0,mPlayer.getDuration());
            circularProgressBar.setProgress(0);
            observer = new MediaObserver();
            new Thread(observer).start();

            tvChord.setText(notes.get(0).getNoteName());
            ivPlay.setImageDrawable(getDrawable(R.drawable.pause));

            setVisualizerInit();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    //Init the visualizer with MediaPlayer session
    private void setVisualizerInit(){
        //get the AudioSessionId from your MediaPlayer and pass it to the visualizer
        int audioSessionId = mPlayer.getAudioSessionId();
        if(audioSessionId!=-1)
            mVisualizer.setAudioSessionId(audioSessionId);
        mVisualizer.show();
    }

    private void stopPlaying(){
        if(mPlayer!=null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            observer.stop();
        }else if(mPlayer!=null && !mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            observer.stop();
        }
        resetUI();
    }

    private void resetUI(){
        timerPause();
        tvTimer.setText(getString(R.string.clock_hint));
        circularProgressBar.setProgress(0);
        ivPlay.setImageDrawable(getDrawable(R.drawable.play));
        tvChord.setText(notes.get(0).getNoteName());
        noteTimeCount=1;

        if(mVisualizer!=null){
            mVisualizer.release();
            mVisualizer.hide();
        }
    }

    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                try {
                    Thread.sleep(10);
                    //circularProgressBar.setProgress(mPlayer.getCurrentPosition());
                    progressHandler.sendMessage(new Message());
                    //Log.e("Clock",mPlayer.getCurrentPosition()+",time:"+noteTimeArr[noteTimeCount] );
                    if((mPlayer.getCurrentPosition()-10)<noteTimeArr[noteTimeCount] &&
                            (mPlayer.getCurrentPosition()+10)>noteTimeArr[noteTimeCount] || (mPlayer.getCurrentPosition()+10)>noteTimeArr[noteTimeCount]){
                        //tvChord.setText((String)noteMap.get(noteTimeArr[noteTimeCount]));
                        String chord = (String)noteMap.get(noteTimeArr[noteTimeCount]);
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putString("Key", chord);
                        msg.setData(b);
                        noteHandler.sendMessage(msg);
                        Log.d(TAG, "chord " + chord);
                        noteTimeCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler noteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String key = b.getString("Key");
            tvChord.setText(key);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(mPlayer!=null)
                circularProgressBar.setProgress(mPlayer.getCurrentPosition());
        }
    };

    private void mediaSpeedSeekBar(){
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                if(progress<50)
                    mediaSpeed = (float)(progress/100)+0.5f;
                else
                    mediaSpeed = (float)(progress/100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stopPlaying();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void showListMenu(final View anchor) {

        String[] list = getResources().getStringArray(R.array.menu_array);
        List<HashMap<String, Object>> data = new ArrayList<>();
        for (String s : list) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(kTitle, s);
            data.add(map);
        }

        // Create SimpleAdapter that will be used by short message list view.
        ListAdapter adapter = new SimpleAdapter(
                this, data, R.layout.popup_menu_layout,
                new String[] {kTitle}, // These are just the keys that the data uses (constant strings)
                new int[] {R.id.tv_select_text}); // The view ids to map the data to view
        // Get list popup view.
        View popupView = getLayoutInflater().inflate(R.layout.popup_menu_design, null);
        ListView listView = popupView.findViewById(R.id.popupWindowSmsList);
        listView.setAdapter(adapter);


        // Create popup window.
        PopupWindow popupWindow = new PopupWindow(popupView, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(5);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        // Show popup window offset 1,1 .
        popupWindow.showAsDropDown(anchor, 5, 1);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        assert wm != null;
        wm.updateViewLayout(container, p);

        //ClickListener to ListItem
        listView.setOnItemClickListener((adapterView, view, index, l) -> {
            switch((String) Objects.requireNonNull(data.get(index).get(kTitle))) {
                case kEditProfile:
                    editProfileDialog();
                    break;
                case kMySubscription:
                    if (!ReachabilityManager.getNetworkStatus()) {
                        Toaster.customToast(kMessageNetworkError);
                    } else {
                        getPlan();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySubscription();
                            }
                        },2000);

                    }
                    break;

                case kGotoMenu:
                    Intent in = new Intent(StudioPlayerActivity.this, WelcomeActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();
                    break;
                case kChangePassword:
                    resetDialog();
                    break;
                case kLogout:
                    onLogout();
                    break;
            }
            /*facId = (int)data.get(index).get(kId);
              anchor.setText((String)data.get(index).get(kTitle));*/
            popupWindow.dismiss();
        });
    }

    private void editProfileDialog() {
        // dialog
        Dialog dialog = new Dialog(Objects.requireNonNull(StudioPlayerActivity.this));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_edit_profile);

        EditText etName = dialog.findViewById(R.id.et_username);
        etName.setText(ModelManager.modelManager().getCurrentUser().getUsername());

        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(view -> {
            if (Utils.getProperText(etName).isEmpty())
                Toaster.toastRangeen(getString(R.string.user_error));
            else if (Utils.getProperText(etName).isEmpty())
                Toaster.toastRangeen(getString(R.string.error_invalid_credential));
            else
                setUpdateData(Utils.getProperText(etName),dialog);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setUpdateData(String name,Dialog dial){
        loaderView.showLoader();
        ModelManager.modelManager().userEditProfile(name,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG,OTP);
                        congratsDialogg();
                        dial.dismiss();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
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
                        plan=jsonObject.getString("plan_display_text");

                        plan_start_date=jsonObject.getString("plan_start_date");
                        plan_end_date=jsonObject.getString("plan_end_date");



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void resetDialog() {
        // dialog
        CurrentUser user = ModelManager.modelManager().getCurrentUser();
        Dialog dialog = new Dialog(Objects.requireNonNull(StudioPlayerActivity.this));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_change_password);

        EditText etOld = dialog.findViewById(R.id.et_old_pass);
        EditText etNew = dialog.findViewById(R.id.et_new_pass);
        EditText etCon = dialog.findViewById(R.id.et_confirm_pass);

        /*visibilityBtn1 = dialog.findViewById(R.id.iv_visible);
        visibilityBtn1.setTag("InVisible");
        visibilityBtn1.setOnClickListener(v -> {
            if (visibilityBtn1.getTag().equals("InVisible")){
                etOld.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                visibilityBtn1.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility));
                visibilityBtn1.setTag("Visible");
            } else {
                etOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
                visibilityBtn1.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
                visibilityBtn1.setTag("InVisible");
            }
        });*/

        Button btnReset = dialog.findViewById(R.id.btn_submit);
        btnReset.setOnClickListener(view -> {
            Log.e("old Password md5", Utils.md5(etOld.getText().toString()));
            Log.e("Old reset_password user",user.getPassword());
            if (Utils.getProperText(etOld).isEmpty())
                Toaster.toastRangeen(getString(R.string.old_pass_msg));
            else if (!Utils.md5(etOld.getText().toString()).equals(user.getPassword()))
                Toaster.toastRangeen(getString(R.string.old_pass_invalid));
            else if (Utils.getProperText(etNew).isEmpty())
                Toaster.toastRangeen(getString(R.string.new_pass_msg));
            else if (Utils.getProperText(etCon).isEmpty())
                Toaster.toastRangeen(getString(R.string.renew_pass_msg));
            else if (!Utils.getProperText(etCon).equals(Utils.getProperText(etNew)))
                Toaster.toastRangeen(getString(R.string.renew_pass_invalid));
            else
                setChangePassword(Utils.getProperText(etOld), Utils.getProperText(etNew),dialog);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private String convertDate(String date1){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

    private void mySubscription() {
        // dialog
        CurrentUser user = ModelManager.modelManager().getCurrentUser();
        Dialog dialog = new Dialog(Objects.requireNonNull(this));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_mysubscription);

        TextView tv_subcription_paln = dialog.findViewById(R.id.tv_subcription_paln);
        TextView tv_subcription_start = dialog.findViewById(R.id.tv_subcription_start);
        TextView tv_subcription_end = dialog.findViewById(R.id.tv_subcription_end);

        try{
            if (plan.isEmpty()) {

            } else {
                if (plan.equalsIgnoreCase("7 Days Free Trial")) {
                    tv_subcription_paln.setText(plan);
                    tv_subcription_start.setText("Trial Started on" + " " + convertDate(plan_start_date));
                    tv_subcription_end.setText("Trial Expires on" + " " + convertDate(plan_end_date));
                } else {
                    tv_subcription_paln.setText(plan);//Montlysubscription and annual subscription
                    tv_subcription_start.setText("Plan Started on" + " " + convertDate(plan_start_date));

                    tv_subcription_end.setText("Plan Expires on" + " " + convertDate(plan_end_date));

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setChangePassword(String old,String pass,Dialog dial){
        loaderView.showLoader();
        HashMap<String,Object> map = new HashMap<>();
        map.put(kId, ModelManager.modelManager().getCurrentUser().getUserId());
        map.put(kOldPassword,old);
        map.put(kNewPassword,pass);
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userChangePassword(map,
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
        final Dialog dialog = new Dialog(StudioPlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_password_sucessfull);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);
        btContinue.setOnClickListener(v -> onLogout());
        dialog.show();
    }

    private void congratsDialogg(){
        final Dialog dialog = new Dialog(StudioPlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_password_sucessfulll);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);
        btContinue.setOnClickListener(v ->dialog.dismiss());
        dialog.show();
    }

    private void onLogout(){
        clearContent();
    }

    private void clearContent() {
        SharedPreferences preferences = ApplicationManager.getContext().getSharedPreferences(BaseModel.kAppPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(kCurrentUser);
        editor.apply();{
            ModelManager.modelManager().setCurrentUser(null);
        }
        setIntent();
    }

    private void setIntent(){
        Intent in = new Intent(StudioPlayerActivity.this, SignInActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    private void setMusicIntent(){
        Intent in = new Intent(StudioPlayerActivity.this, SongListActivity.class);
        in.putExtra(kType,1);
        startActivity(in);
        finish();
    }

    private void setInfoIntent(){
        Intent in = new Intent(StudioPlayerActivity.this, StudioHelpActivity.class);
        startActivity(in);
    }

    private void setTypeIntent(){
        Intent in = new Intent(StudioPlayerActivity.this, StudioSelectTypeActivity.class);
        in.putExtra(kScale,scalePos);
        in.putExtra(kRange,rangePos);
        in.putExtra(kRangeName,rangeTxt);
        in.putExtra(kScaleName,scaleTxt);
        startActivityForResult(in,ACTIVITY_TYPE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_TYPE_RESULT) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                rangeTxt = data.getStringExtra(kData);
                scaleTxt = data.getStringExtra(kType);
                rangePos = data.getIntExtra(kRange,0);
                scalePos = data.getIntExtra(kScale,0);
                tvRange.setText(rangeTxt);
                tvScale.setText(scaleTxt);
                try {
                    midi = new Midi(new JSONObject(Utils.loadJSONFromAsset(this,rangeTxt,scalePos)));
                    notes = midi.getNotes();
                    noteMap = midi.getNote();
                    noteTimeArr = midi.getAccessNotes();
                    tvChord.setText(notes.get(0).getNoteName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void timerStart(long timeLengthMilli,int resumeLong,long timeleft) {
        timer = new CountDownTimer(timeLengthMilli, 100) {

            @Override
            public void onTick(long milliTillFinish) {
                long time;
                if(resumeLong==0)
                    time = timeLengthMilli - milliTillFinish;
                else
                    time = timeLengthMilli - milliTillFinish + timeleft;
                milliLeft = time;
                min = (time/(1000*60));
                sec = ((time/1000)-min*60);
                tvTimer.setText(Utils.changeTimeFormatNew((min)+":"+(sec)));
                Log.i("Tick", "Tock:"+time);
            }

            @Override
            public void onFinish() { }
        };
        timer.start();
    }

    public void timerPause() {
        if(timer!=null)
            timer.cancel();
    }

    private void timerResume() {
        Log.i("min", Long.toString(min));
        Log.i("Sec", Long.toString(sec));
        timerStart(mPlayer.getDuration(),1,milliLeft);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }
}
