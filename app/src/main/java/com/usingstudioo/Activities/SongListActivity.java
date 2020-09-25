package com.usingstudioo.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usingstudioo.Activities.Adapters.SongListAdapter;
import com.usingstudioo.ApplicationManager;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Broadcast.ReachabilityManager;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.ModelManager.ModelManager;
import com.usingstudioo.Models.BaseModel;
import com.usingstudioo.Models.CurrentUser;
import com.usingstudioo.Models.SongModel;
import com.usingstudioo.R;
import com.usingstudioo.Utils.CustomLoaderView;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.usingstudioo.Constants.Constants.kChangePassword;
import static com.usingstudioo.Constants.Constants.kCurrentUser;
import static com.usingstudioo.Constants.Constants.kData;
import static com.usingstudioo.Constants.Constants.kEditProfile;
import static com.usingstudioo.Constants.Constants.kGotoMenu;
import static com.usingstudioo.Constants.Constants.kId;
import static com.usingstudioo.Constants.Constants.kLimit;
import static com.usingstudioo.Constants.Constants.kLogout;
import static com.usingstudioo.Constants.Constants.kMessageNetworkError;
import static com.usingstudioo.Constants.Constants.kMySubscription;
import static com.usingstudioo.Constants.Constants.kNewPassword;
import static com.usingstudioo.Constants.Constants.kOldPassword;
import static com.usingstudioo.Constants.Constants.kPage;
import static com.usingstudioo.Constants.Constants.kRecordingList;
import static com.usingstudioo.Constants.Constants.kTitle;
import static com.usingstudioo.Constants.Constants.kType;
import static com.usingstudioo.Constants.Constants.kUserId;


public class SongListActivity extends AppCompatActivity {

    private static String TAG = SongListActivity.class.getSimpleName();
    private SongListAdapter songListAdapter;
    private LinearLayout emptyView;
    private CustomLoaderView loaderView;
    private CopyOnWriteArrayList<SongModel> songsModel;
    private CopyOnWriteArrayList<SongModel> filterModel;
    private int activityType;
    String currentDate, plan, plan_start_date, plan_end_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        loaderView = CustomLoaderView.initialize(this);
        activityType = getIntent().getIntExtra(kType, 1);
        getPlan();
        Log.d("activityType", activityType + "");

        emptyView = findViewById(R.id.empty_view);
        RecyclerView rvList = findViewById(R.id.rv_list);
        songsModel = new CopyOnWriteArrayList<>();
        filterModel = new CopyOnWriteArrayList<>();
        rvList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        songListAdapter = new SongListAdapter(this, songsModel, new SongListAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(SongModel model, String embeddedlink) {

                if (activityType == 1) {
                    if (embeddedlink.equalsIgnoreCase("YES")) {
                        Intent in = new Intent(SongListActivity.this, YoutubePlayerActivity.class);
                        in.putExtra(kData, model);
                        startActivity(in);
                    } else {
                        Intent in = new Intent(SongListActivity.this, WebViewActivity.class);
                        //in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.putExtra(kData, model);
                        startActivity(in);
                        //finish();
                    }
                } else {

                    if (model.getAudioVideo().equalsIgnoreCase("1")) {
                        Intent in = new Intent(SongListActivity.this, YoutubeRecorderActivity.class);
                        //in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.putExtra(kData, model);
                        startActivity(in);
                    } else {
                        Intent in = new Intent(SongListActivity.this, WfiveRecorderActivity.class);
                        //in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.putExtra(kData, model);
                        startActivity(in);
                    }

                }


            }
        });

       /* songListAdapter = new SongListAdapter(this, songsModel, model -> {
            if(activityType==1){
                Intent in = new Intent(SongListActivity.this, YoutubePlayerActivity.class);
                in.putExtra(kData,model);
                startActivity(in);
            }else{
                Intent in = new Intent(SongListActivity.this, YoutubeRecorderActivity.class);
                //in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.putExtra(kData,model);
                startActivity(in);
                //finish();
            }

        });*/
        rvList.setAdapter(songListAdapter);
        checkEmptyView();

        if (activityType == 1) {
            getSongList();
        } else {
            getSongListEmbed();
        }


        EditText searchField = findViewById(R.id.et_search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

        LinearLayout sort = findViewById(R.id.filter_layout);
        sort.setOnClickListener(v -> showPopupMenu(sort));

        ImageButton btProfile = findViewById(R.id.bt_profile);
        btProfile.setOnClickListener(v -> showListMenu(btProfile));

        ImageButton btStudio = findViewById(R.id.bt_music);
        btStudio.setOnClickListener(v -> setMusicIntent());

        findViewById(R.id.iv_logo).setOnClickListener(v -> finish());
    }

    @SuppressLint("InflateParams")
    private void showListMenu(final View anchor) {

        String[] list;
        if (activityType == 1) {
            list = getResources().getStringArray(R.array.menu_array);
        } else {
            list = getResources().getStringArray(R.array.menu_record_array);
        }
        List<HashMap<String, Object>> data = new ArrayList<>();
        for (String s : list) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(kTitle, s);
            data.add(map);
        }

        // Create SimpleAdapter that will be used by short message list view.
        ListAdapter adapter = new SimpleAdapter(
                this, data, R.layout.popup_menu_layout,
                new String[]{kTitle}, // These are just the keys that the data uses (constant strings)
                new int[]{R.id.tv_select_text}); // The view ids to map the data to view
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
            switch ((String) Objects.requireNonNull(data.get(index).get(kTitle))) {
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
                    Intent in = new Intent(SongListActivity.this, WelcomeActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();
                    break;
                case kRecordingList:
                    RecordListIntent();
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
                        plan = jsonObject.getString("plan_display_text");

                        plan_start_date = jsonObject.getString("plan_start_date");
                        plan_end_date = jsonObject.getString("plan_end_date");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private String convertDate(String date1) {
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

    private void showPopupMenu(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.sort_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.all:
                    if (!songsModel.isEmpty())
                        sort("all");
                    return true;
                case R.id.guitar:
                    if (!songsModel.isEmpty())
                        sort("Guitar");
                    return true;
                case R.id.piano:
                    if (!songsModel.isEmpty())
                        sort("Piano");
                    return true;

                case R.id.wlibrary:
                    if (!songsModel.isEmpty())
                        sort("Band");
                    return true;
            }
            return false;
        });
        //displaying the popup
        popup.show();
    }

    /*private void showPopupMenu(View view){
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.sort_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.all:
                    if(!songsModel.isEmpty())
                        sort("all");
                    return true;
                case R.id.guitar:
                    if(!songsModel.isEmpty())
                        sort("Guitar");
                    return true;
                case R.id.piano:
                    if(!songsModel.isEmpty())
                        sort("Piano");
                    return true;

                case R.id.wlibrary:
                    if(!songsModel.isEmpty())
                        sort("Band");
                    return true;
            }
            return false;
        });
        //displaying the popup
        popup.show();
    }*/


    private void getSongList() {
        loaderView.showLoader();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(kPage, 0);
        parameters.put(kLimit, 10000);
        Log.d("songlist", "songlist");
        ModelManager.modelManager().SongList(parameters,
                (Constants.Status iStatus, GenericResponse<CopyOnWriteArrayList<SongModel>> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        songsModel = genericResponse.getObject();
                        filterModel = genericResponse.getObject();
                        Log.e(TAG, songsModel.toString());
                        songListAdapter.updateList(songsModel);
                        checkEmptyView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void getSongListEmbed() {
        loaderView.showLoader();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(kPage, 0);
        parameters.put(kLimit, 10000);

        Log.d("Embed", "Embedd");
        ModelManager.modelManager().SongListEmbed(parameters,
                (Constants.Status iStatus, GenericResponse<CopyOnWriteArrayList<SongModel>> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        songsModel = genericResponse.getObject();
                        filterModel = genericResponse.getObject();
                        Log.e(TAG, songsModel.toString());
                        songListAdapter.updateList(songsModel);
                        checkEmptyView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    void filter(String text) {
        if (text.isEmpty()) {
            songListAdapter.updateList(filterModel);
        } else {
            CopyOnWriteArrayList<SongModel> temp = new CopyOnWriteArrayList<>();
            for (SongModel d : filterModel) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getSongTitle().toLowerCase().contains(text.toLowerCase()) || text.equalsIgnoreCase(d.getSongTitle())) {
                    temp.add(d);
                }
            }
            songListAdapter.updateList(temp);
        }
    }

    void sort(String type) {
        if (type.equals("all")) {
            filterModel = songsModel;
            songListAdapter.updateList(songsModel);
        } else {
            filterModel = new CopyOnWriteArrayList<>();
            for (SongModel d : songsModel) {
                if (d.getSongType().equals(type)) {
                    filterModel.add(d);
                }
            }
            songListAdapter.updateList(filterModel);
        }
    }

    private void editProfileDialog() {
        // dialog
        Dialog dialog = new Dialog(Objects.requireNonNull(SongListActivity.this));
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
                setUpdateData(Utils.getProperText(etName), dialog);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setUpdateData(String name, Dialog dial) {
        loaderView.showLoader();
        ModelManager.modelManager().userEditProfile(name,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG, OTP);
                        congratsDialogg();
                        dial.dismiss();
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
        Dialog dialog = new Dialog(Objects.requireNonNull(SongListActivity.this));
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
            Log.e("Old reset_password user", user.getPassword());
            if (Utils.getProperText(etOld).isEmpty())
                Toaster.toastRangeen(getString(R.string.old_pass_msg));
            else if (!Utils.md5(etOld.getText().toString()).equals(user.getPassword()))
                Toaster.toastRangeen(getString(R.string.old_pass_invalid));
            else if (Utils.getProperText(etNew).isEmpty() || Utils.getProperText(etNew).length() < 6)
                Toaster.toastRangeen(getString(R.string.new_pass_msg));
            else if (Utils.getProperText(etCon).isEmpty())
                Toaster.toastRangeen(getString(R.string.renew_pass_msg));
            else if (!Utils.getProperText(etCon).equals(Utils.getProperText(etNew)))
                Toaster.toastRangeen(getString(R.string.renew_pass_invalid));
            else
                setChangePassword(Utils.getProperText(etOld), Utils.getProperText(etNew), dialog);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setChangePassword(String old, String pass, Dialog dial) {
        loaderView.showLoader();
        HashMap<String, Object> map = new HashMap<>();
        map.put(kId, ModelManager.modelManager().getCurrentUser().getUserId());
        map.put(kOldPassword, old);
        map.put(kNewPassword, pass);
        Log.e(TAG, map.toString());
        ModelManager.modelManager().userChangePassword(map,
                (Constants.Status iStatus, GenericResponse<String> genericResponse) -> {
                    loaderView.hideLoader();
                    try {
                        String OTP = genericResponse.getObject();
                        Log.e(TAG, OTP);
                        congratsDialog();
                        dial.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (Constants.Status iStatus, String message) -> {
                    loaderView.hideLoader();
                    Toaster.toastRangeen(message);
                });
    }

    private void congratsDialog() {
        final Dialog dialog = new Dialog(SongListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_password_sucessfull);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);
        btContinue.setOnClickListener(v -> onLogout());
        dialog.show();
    }

    private void congratsDialogg() {
        final Dialog dialog = new Dialog(SongListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_password_sucessfulll);
        dialog.setCancelable(false);

        Button btContinue = dialog.findViewById(R.id.btn_continue);
        btContinue.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void onLogout() {
        clearContent();
    }

    private void clearContent() {
        SharedPreferences preferences = ApplicationManager.getContext().getSharedPreferences(BaseModel.kAppPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(kCurrentUser);
        editor.apply();
        {
            ModelManager.modelManager().setCurrentUser(null);
        }
        setIntent();
    }

    private void setIntent() {
        Intent in = new Intent(SongListActivity.this, SignInActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }


    private void RecordListIntent() {
        Intent intent = new Intent(SongListActivity.this, RecorderListActivity.class);
        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        intent.putExtra("FROM", "3");
        intent.putExtra(kType, activityType);
        startActivity(intent);
        finish();
    }

    private void checkEmptyView() {
        if (songListAdapter.getItemCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }

    private void setMusicIntent() {
        Intent in = new Intent(SongListActivity.this, StudioPlayerActivity.class);
        startActivity(in);
        finish();
    }
}
