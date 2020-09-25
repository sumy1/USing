package com.usingstudioo.ModelManager;

import android.util.Log;

import com.usingstudioo.APIManager.ApiInterface;
import com.usingstudioo.APIManager.ApiManager;
import com.usingstudioo.BaseManager.BaseManager;
import com.usingstudioo.Blocks.Block;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Broadcast.ReachabilityManager;
import com.usingstudioo.Constants.Constants;
import com.usingstudioo.DispatchQueue.DispatchQueue;
import com.usingstudioo.Models.BaseModel;
import com.usingstudioo.Models.CurrentUser;
import com.usingstudioo.Models.SongModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.usingstudioo.APIManager.ApiInterface.kOtpVerify;

/**
 * Singleton class to manage all models in projects. this is basically provide data to view in the
 * form of models
 */

public class ModelManager extends BaseManager implements Constants {

    private final static String TAG = ModelManager.class.getSimpleName();
    //Static Properties
    private static ModelManager _ModelManager;

    //Instance variables
    private static CurrentUser mCurrentUser = null;
    private DispatchQueue dispatchQueue =
            new DispatchQueue("com.queue.serial.modelmanager", DispatchQueue.QoS.userInitiated);

    /**
     * private constructor make it to be Singleton class
     */
    private ModelManager() { }

    /**
     * method to create a threadsafe singleton class instance
     *
     * @return a thread safe singleton object of model manager
     */
    public static synchronized ModelManager modelManager() {
        if (_ModelManager == null) {
            _ModelManager = new ModelManager();
            mCurrentUser = getDataFromPreferences(kCurrentUser, CurrentUser.class);
            Log.e(TAG, mCurrentUser + " ");
        }
        return _ModelManager;
    }

    public DispatchQueue getDispatchQueue() {
        return dispatchQueue;
    }

    /**
     * to initialize the singleton object
     */
    public void initializeModelManager() {
        System.out.println("ModelManager object initialized.");
    }

    /**
     * getter and setter method for current user
     *
     * @return {@link CurrentUser} if user already logged in, null otherwise
     */
    public synchronized CurrentUser getCurrentUser() {
        return mCurrentUser;
    }

    public synchronized void setCurrentUser(CurrentUser o) {
        mCurrentUser = o;
        archiveCurrentUser();
    }

    /**
     * set response to @User
     *
     * @param genricResponse contains JSONObject with user information in it
     */
    private void setupCurrentUser(GenericResponse<JSONObject> genricResponse, int status) {
        try {
            mCurrentUser = new CurrentUser(genricResponse.getObject().getJSONObject(kResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        archiveCurrentUser();
    }

    /**
     * Stores {@link CurrentUser} to the share preferences and synchronize sharedpreferece
     */
    private synchronized void archiveCurrentUser() {
        saveDataIntoPreferences(mCurrentUser, BaseModel.kCurrentUser);
    }

    /**
     * method will be called when user login through system eg. with email and reset_password
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userLoginRequest(HashMap<String, Object> parameters, Block.Success<CurrentUser> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kUserLogin, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        try{
                            setupCurrentUser(genricResponse,0);
                            GenericResponse<CurrentUser> generic = new GenericResponse<>(mCurrentUser);
                            DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                        }catch (Exception e){
                            e.printStackTrace();
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, e.getMessage()));
                        }

                    }catch (Exception e){
                        e.printStackTrace();


                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));

                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Login",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, message));
                }));

    }

    /**
     * method will be called when user register through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userRegisterRequest(HashMap<String, Object> parameters, Block.Success<CurrentUser> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kUserReg, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        setupCurrentUser(genricResponse,1);
                        GenericResponse<CurrentUser> generic = new GenericResponse<>(mCurrentUser);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));

                    }
                }, (Status statusFail, String message) -> {
                    Log.e("SignUp",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, message));
                }));
    }

    /**
     * method will be called when user change reset_password through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userChangePassword(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kChangePassword, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String new_password = genricResponse.getObject().getString(kResponse);
                        mCurrentUser.setPassword(new_password);
                        archiveCurrentUser();
                        GenericResponse<String> generic = new GenericResponse<>(new_password);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Change Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    /**
     * method will be called when user forgot Password through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userForgotEmail(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kEmailForgot, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        String otp = obj.getString(kId);
                        GenericResponse<String> generic = new GenericResponse<>(otp);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }



    public void addPlan(HashMap<String, Object> parameters, Block.Success<JSONObject> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kAddPlan, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        GenericResponse<JSONObject> generic = new GenericResponse<>(obj);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void getpaln(HashMap<String, Object> parameters, Block.Success<JSONObject> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kgetPlan, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        GenericResponse<JSONObject> generic = new GenericResponse<>(obj);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    /**
     * method will be called when user forgot Password through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userForgotPassword(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kResetPassword, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String msg = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(msg);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    /**
     * method will be called when user mobile Verification through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     **/
    public void userVerificationOTP(HashMap<String, Object> parameters, int type, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kOtpVerify, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String object = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(object);
                        if(type==2){
                            mCurrentUser.setEmailVerified("1");
                            archiveCurrentUser();
                        }
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Email Verification",message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void userEditProfile(String name, Block.Success<String> success, Block.Failure failure) {
        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put(kId,mCurrentUser.getUserId());
        parameters.put(kFirstName,name);
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kEditProfile, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String msg = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(msg);
                        mCurrentUser.setUsername(name);
                        archiveCurrentUser();
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Edit Profile",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void SongList(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<SongModel>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kSongList,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SongModel> songs = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            if(object.getString(kSongStatus).equals("1"))
                                songs.add(new SongModel(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<SongModel>> generic = new GenericResponse<>(songs);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }


    public void SongListEmbed(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<SongModel>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.ksongListEmbed,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SongModel> songs = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            if(object.getString(kSongStatus).equals("1"))
                                songs.add(new SongModel(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<SongModel>> generic = new GenericResponse<>(songs);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }
/*
    //User API
    *//**
     * method will be called when user login through system eg. with email and reset_password
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition11
     *//*
    public void userLoginRequest(HashMap<String, Object> parameters, Block.Success<CurrentUser> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kUserLogin, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        try{
                            setupCurrentUser(genricResponse,0);
                            GenericResponse<CurrentUser> generic = new GenericResponse<>(mCurrentUser);
                            DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                        }catch (Exception e){
                            e.printStackTrace();
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, e.getMessage()));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Login",message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }


    *//**
     * method will be called when user register through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     *//*
    public void userResendOTP(HashMap<String, Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kResendOTP, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        Integer otp = genricResponse.getObject().getJSONObject(kResponse).getInt(kOtp);
                        GenericResponse<Integer> generic = new GenericResponse<>(otp);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("SignUp",message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    *//**
     * method will be called when user mobile Verification through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     *//*
    public void userVerificationOTP(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kOtpVerify, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String object = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(object);
                        mCurrentUser.setOTPVerified("1");
                        archiveCurrentUser();
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Email Verification",message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    *//**
     * method will be called when user change reset_password through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     *//*
    public void userChangePassword(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kChangePassword, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String new_password = genricResponse.getObject().getString(kResponse);
                        mCurrentUser.setPassword(new_password);
                        archiveCurrentUser();
                        GenericResponse<String> generic = new GenericResponse<>(new_password);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Change Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    *//**
     * method will be called when user forgot Password through system
     *
     * @param parameters include user info provided by user
     * @param success     Block passed as callback for success condition
     * @param failure    Block passed as callback for failure condition
     *//*
    public void userForgotPassword(HashMap<String, Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kForgotPassword, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        String msg = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(msg);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    *//**
     * method will be to logout user from application
     *
     * * @param status  Block passed as callback for success condition
     * @param failure Block passed as callback for failure condition
     *//*
    *//* public void getLogout(Block.Status status, Block.Failure failure) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(kAction, ApiInterface.kLogout);
        parameters.put(kUserId, getCurrentUser().getUserId());
        dispatchQueue.async(() -> {
            ApiManager.ApiClient()
                    .processFormRequest(ApiInterface.kBasePage, parameters,
                            (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                                try{
                                    DispatchQueue.main(() -> status.iStatus(iStatus));
                                } catch (Exception e){
                                    e.printStackTrace();
                                    if (!ReachabilityManager.getNetworkStatus())
                                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                                    else
                                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                                }
                            }, (Status statusFail, String message) -> {
                                Log.e("Logout",message);
                                DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                            });
        });
    }*//*

    public void userSocialLoginRequest(HashMap<String, Object> parameters, Block.Success<CurrentUser> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kSocialLogin, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        setupCurrentUser(genricResponse,0);
                        GenericResponse<CurrentUser> generic = new GenericResponse<>(mCurrentUser);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    }catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Login",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    *//*public void SubscriptionPlan(Block.Success<CopyOnWriteArrayList<SubscriptionPlan>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processSubscriptionRequest((Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SubscriptionPlan> list = new CopyOnWriteArrayList<>();
                        JSONArray array =  genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            list.add(new SubscriptionPlan(array.getJSONObject(i)));
                        }
                        GenericResponse<CopyOnWriteArrayList<SubscriptionPlan>> generic = new GenericResponse<>(list);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Subscription List", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }*//*

    public void SubscriptionPlans(HashMap<String,Object> parameters,Block.Success<CopyOnWriteArrayList<SubscriptionPlan>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kSubscriptionPlan,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SubscriptionPlan> plans = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            plans.add(new SubscriptionPlan(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<SubscriptionPlan>> generic = new GenericResponse<>(plans);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Plan List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void PublicationList(HashMap<String,Object> parameters,Block.Success<CopyOnWriteArrayList<Publications>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kPublicationList,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Publications> list = new CopyOnWriteArrayList<>();
                        JSONArray array =  genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            list.add(new Publications(array.getJSONObject(i),0));
                        }
                        GenericResponse<CopyOnWriteArrayList<Publications>> generic = new GenericResponse<>(list);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Publication List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    *//*public void Publications(Block.Success<CopyOnWriteArrayList<Publications>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processPublicationRequest((Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Publications> list = new CopyOnWriteArrayList<>();
                        JSONArray array =  genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            list.add(new Publications(array.getJSONObject(i),0));
                        }
                        GenericResponse<CopyOnWriteArrayList<Publications>> generic = new GenericResponse<>(list);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Publications List", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }*//*

    public void News(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<News>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kNews,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        int count=0;
                        String name="";
                        CopyOnWriteArrayList<News> news = new CopyOnWriteArrayList<>();
                        int releaseId = Integer.valueOf(genricResponse.getObject().getString(kReleaseId));
                        String releaseDate = Utils.changeDateTimeFormat(genricResponse.getObject().getString(kReleaseDate));
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            String pageName;
                            JSONObject object = array.getJSONObject(i);
                            count = count+1;
                            pageName = (count)+"-"+object.getString(kPageName);
                            *//*if(object.getString(kPageName).equals(name)){
                                count = count+1;
                                pageName = object.getString(kPageName)+"\n"+(count);
                            }else{
                                count=1;
                                name = object.getString(kPageName);
                                pageName = object.getString(kPageName)+"\n"+(count);
                            }*//*
                            news.add(new News(releaseId,releaseDate,pageName,object));
                        }
                        getCurrentUser().setNewsList(news);
                        archiveCurrentUser();
                        *//*CopyOnWriteArrayList<NewsCategory> categories = new CopyOnWriteArrayList<>();
                        JSONObject object = genricResponse.getObject().getJSONObject(kResponse);
                        Iterator<String> iter = object.keys();
                        int count=0;
                        while (iter.hasNext()) {
                            String key = iter.next();
                            count++;
                            CopyOnWriteArrayList<News> list = new CopyOnWriteArrayList<>();
                            try {
                                JSONArray array = (JSONArray) object.get(key);
                                for(int i=0;i<array.length();i++){
                                    list.add(new News(array.getJSONObject(i)));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            categories.add(new NewsCategory(count,key,list));
                        }*//*
                        GenericResponse<CopyOnWriteArrayList<News>> generic = new GenericResponse<>(news);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }


    public void PaymentConfirmation(HashMap<String,Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kOrder,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        Integer id =  genricResponse.getObject().getInt(kResponse);
                        GenericResponse<Integer> generic = new GenericResponse<>(id);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Payment Confirm", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void SubscriptionConfirmation(HashMap<String,Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kOrderPlace,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Publications> list = new CopyOnWriteArrayList<>();
                        JSONArray array =  genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            list.add(new Publications(array.getJSONObject(i),1));
                        }
                        mCurrentUser.setPublications(list);
                        archiveCurrentUser();
                        GenericResponse<String> generic = new GenericResponse<>("Confirmed");
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Payment Confirm", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void profileUpdate(HashMap<String,Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kUpdateProfile,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        JSONObject jsonResponse =  genricResponse.getObject().getJSONObject(kResponse);
                        if(jsonResponse.has(kDBMobile))
                            getCurrentUser().setPhone(jsonResponse.getString(kDBMobile));
                        if(jsonResponse.has(kDBUserName))
                            getCurrentUser().setUsername(jsonResponse.getString(kDBUserName));
                        archiveCurrentUser();
                        GenericResponse<String> generic = new GenericResponse<>("");
                        DispatchQueue.main(() -> success.iSuccess(iStatus,generic ));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!ReachabilityManager.getNetworkStatus())
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                        else
                            DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Profile Update", message);
                    DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }


    public void SectionCommentSection(HashMap<String, Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kComment, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        JSONObject object = genricResponse.getObject().getJSONObject(kResponse);
                        Integer totalComment = object.getInt(kCommentCount);
                        GenericResponse<Integer> generic = new GenericResponse<>(totalComment);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }


    public void SectionLikeStatus(HashMap<String, Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kLikeCount, parameters, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try{
                        JSONObject object = genricResponse.getObject().getJSONObject(kResponse);
                        Integer totalComment = object.getInt(kLikeCount);
                        GenericResponse<Integer> generic = new GenericResponse<>(totalComment);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e){
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("Forgot Password",message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, message));
                }));
    }

    public void SectionComments(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<Comment>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kCommentList,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Comment> comments = new CopyOnWriteArrayList<>();
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        JSONArray array = obj.getJSONArray(kComments);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            comments.add(new Comment(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<Comment>> generic = new GenericResponse<>(comments);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void ReleaseDates(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<ReleaseDates>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(kReleaseDates,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<ReleaseDates> dates = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            dates.add(new ReleaseDates(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<ReleaseDates>> generic = new GenericResponse<>(dates);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void Notifications(HashMap<String,Object> parameters, Block.Success<HashMap<String,Object>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kNotification,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        HashMap<String,Object> map = new HashMap<>();
                        CopyOnWriteArrayList<Advertisements> advertisements = new CopyOnWriteArrayList<>();
                        CopyOnWriteArrayList<Notifications> notifies = new CopyOnWriteArrayList<>();
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        JSONObject ob = obj.getJSONObject(kData);
                        JSONArray array = ob.getJSONArray(kNotifications);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            notifies.add(new Notifications(object));
                        }
                        JSONArray array1 = obj.getJSONArray(kAdvertisement);
                        for(int i=0;i<array1.length();i++){
                            JSONObject object = array1.getJSONObject(i);
                            advertisements.add(new Advertisements(object));
                        }
                        map.put(kNotification,notifies);
                        map.put(kAdvertisement,advertisements);
                        GenericResponse<HashMap<String,Object>> generic = new GenericResponse<>(map);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void PushNotification(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<Notifications>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kPushNotification,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Notifications> notifies = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            notifies.add(new Notifications(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<Notifications>> generic = new GenericResponse<>(notifies);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void NotificationUpdate(HashMap<String,Object> parameters, Block.Success<String> success, Block.Failure failure) {
        parameters.put(kUserId,mCurrentUser.getUserId());
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kNotificationUpdate,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        JSONObject object = genricResponse.getObject().getJSONObject(kResponse);
                        String plan = object.getString(kIsRead);
                        GenericResponse<String> generic = new GenericResponse<>(plan);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void NotificationCount(HashMap<String,Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        parameters.put(kUserId,mCurrentUser.getUserId());
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kNotificationCount,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        Integer count = genricResponse.getObject().getInt(kResponse);
                        GenericResponse<Integer> generic = new GenericResponse<>(count);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void Advertisement(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<Advertisements>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kAdvertisement,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<Advertisements> ads = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            ads.add(new Advertisements(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<Advertisements>> generic = new GenericResponse<>(ads);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void InactiveUser(HashMap<String,Object> parameters, Block.Success<Integer> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kInactiveUser,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        JSONObject object = genricResponse.getObject().getJSONObject(kResponse);
                        Integer type = Integer.valueOf(object.getString(kIsUserActive));
                        GenericResponse<Integer> generic = new GenericResponse<>(type);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void StaticPages(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<StaticPageModel>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kStaticPages,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<StaticPageModel> pages = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            pages.add(new StaticPageModel(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<StaticPageModel>> generic = new GenericResponse<>(pages);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void Contact(HashMap<String,Object> parameters, Block.Success<HashMap<String,Object>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kContactUs,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        HashMap<String,Object> map = new HashMap<>();
                        CopyOnWriteArrayList<Advertisements> advertisements = new CopyOnWriteArrayList<>();
                        CopyOnWriteArrayList<QueryModel> queryModels = new CopyOnWriteArrayList<>();
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        ContactModel model = new ContactModel(obj.getJSONObject(kContactDetail));
                        JSONArray array = obj.getJSONArray(kAdvertisement);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            advertisements.add(new Advertisements(object));
                        }
                        JSONArray arr = obj.getJSONArray(kQueries);
                        for(int i=0;i<arr.length();i++){
                            JSONObject object = arr.getJSONObject(i);
                            queryModels.add(new QueryModel(object));
                        }
                        map.put(kContactDetail,model);
                        map.put(kAdvertisement,advertisements);
                        map.put(kQueries,queryModels);
                        GenericResponse<HashMap<String,Object>> generic = new GenericResponse<>(map);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void ContactForm(HashMap<String,Object> parameters, Block.Success<String> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kContactForm,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        String msg = genricResponse.getObject().getString(kResponse);
                        GenericResponse<String> generic = new GenericResponse<>(msg);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void QueryForm(HashMap<String,Object> parameters, Block.Success<CopyOnWriteArrayList<QueryModel>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kQueryForm,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<QueryModel> queries = new CopyOnWriteArrayList<>();
                        JSONArray array = genricResponse.getObject().getJSONArray(kResponse);
                        //JSONArray arr = obj.getJSONArray(kQueries);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            queries.add(new QueryModel(object));
                        }
                        GenericResponse<CopyOnWriteArrayList<QueryModel>> generic = new GenericResponse<>(queries);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }


    public void SubscribedPlan(HashMap<String,Object> parameters, Block.Success<HashMap<String,Object>> success, Block.Failure failure) {
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kUserSubscription,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        HashMap<String,Object> map = new HashMap<>();
                        JSONObject jsonObject = genricResponse.getObject().getJSONObject(kResponse);
                        CopyOnWriteArrayList<SubscriptionType> types = new CopyOnWriteArrayList<>();
                        JSONArray array = jsonObject.getJSONArray(kSingle);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            types.add(new SubscriptionType(object));
                        }
                        map.put(kSingle,types);

                        CopyOnWriteArrayList<SubscriptionPackage> packages = new CopyOnWriteArrayList<>();
                        JSONArray arr = jsonObject.getJSONArray(kPackages);
                        for(int i=0;i<arr.length();i++){
                            JSONObject object = arr.getJSONObject(i);
                            packages.add(new SubscriptionPackage(object));
                        }
                        map.put(kPackage,packages);
                        GenericResponse<HashMap<String,Object>> generic = new GenericResponse<>(map);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void SubscriptionTypes(Block.Success<CopyOnWriteArrayList<SubscriptionAdsModel>> success, Block.Failure failure) {
        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put(kType,1);
        parameters.put(kUserId,mCurrentUser.getUserId());
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kSubscription,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SubscriptionAdsModel> modelList =  new CopyOnWriteArrayList<>();
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        JSONArray array = obj.getJSONArray(kTitles);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            modelList.add(new SubscriptionAdsModel(2,object));
                        }
                        JSONArray array1 = obj.getJSONArray(kAdvertisement);
                        int size = array.length();
                        for(int i=0;i<array1.length();i++){
                            JSONObject object = array1.getJSONObject(i);
                            modelList.add(new Random().nextInt(size),new SubscriptionAdsModel(1,object));
                        }
                        //Collections.shuffle(modelList);
                        GenericResponse<CopyOnWriteArrayList<SubscriptionAdsModel>> generic = new GenericResponse<>(modelList);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void SubscriptionPackages(Block.Success<CopyOnWriteArrayList<SubscriptionAdsModel>> success, Block.Failure failure) {
        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put(kType,2);
        parameters.put(kUserId,mCurrentUser.getUserId());
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kSubscription,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        CopyOnWriteArrayList<SubscriptionAdsModel> modelList =  new CopyOnWriteArrayList<>();
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        JSONArray array = obj.getJSONArray(kTitles);
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            modelList.add(new SubscriptionAdsModel(3,object));
                        }
                        JSONArray array1 = obj.getJSONArray(kAdvertisement);
                        int size = array.length();
                        for(int i=0;i<array1.length();i++){
                            JSONObject object = array1.getJSONObject(i);
                            modelList.add(new Random().nextInt(size),new SubscriptionAdsModel(1,object));
                        }
                        //Collections.shuffle(modelList);
                        GenericResponse<CopyOnWriteArrayList<SubscriptionAdsModel>> generic = new GenericResponse<>(modelList);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }

    public void SubscriptionSingleType(HashMap<String,Object> parameters,Block.Success<SubscriptionType> success, Block.Failure failure) {
        parameters.put(kType,1);
        parameters.put(kUserId,mCurrentUser.getUserId());
        dispatchQueue.async(() ->
                ApiManager.ApiClient().processFormRequest(ApiInterface.kSubscription,parameters,(Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    try {
                        JSONObject obj = genricResponse.getObject().getJSONObject(kResponse);
                        SubscriptionType type = new SubscriptionType(obj.getJSONObject(kTitles));
                        GenericResponse<SubscriptionType> generic = new GenericResponse<>(type);
                        DispatchQueue.main(() -> success.iSuccess(iStatus, generic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageInternalInconsistency));
                    }
                }, (Status statusFail, String message) -> {
                    Log.e("News List", message);
                    if (!ReachabilityManager.getNetworkStatus())
                        DispatchQueue.main(() -> failure.iFailure(Status.fail, kMessageNetworkError));
                    else
                        DispatchQueue.main(() -> failure.iFailure(statusFail, kMessageServerNotRespondingError));

                }));
    }*/


}
