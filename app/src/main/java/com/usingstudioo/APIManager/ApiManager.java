package com.usingstudioo.APIManager;

import android.util.Log;

import com.google.gson.JsonObject;
import com.usingstudioo.BaseManager.BaseManager;
import com.usingstudioo.Blocks.Block;
import com.usingstudioo.Blocks.GenericResponse;
import com.usingstudioo.Constants.Constants;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager extends BaseManager implements Constants {

    /**
     * API Base URL
     */
    //private static final String kAPIBaseURL  = "https://www.vibestest.com/wip_projects/development/deangola/api/";//staging release
    //private static final String kAPIBaseURL  = "http://vibestest.com/wip_projects/using/using_library/admin/service/";//staging release


    //private static final String kAPIBaseURL  ="http://usingstudio.com/beta/using_library/admin/service/";
    private static final String kAPIBaseURL  = "https://www.usingstudio.com/using_library/admin/service/";//staging release
    public static final String kGoogleMapsBaseURL = "https://maps.googleapis.com/";
    //API Request Content Types
    private static final String kContentType = "Content-Type";
    private static final String kContentTypeText = "text/html";
    private static final String kContentTypeRawJson = "application/json";
    private static final String kContentTypeJSON = "application/json; charset=utf-8";
    private static final String kContentTypeMultiPart = "multipart/form-data";
    private static final String kContentTypeFormData = "application/x-www-form-urlencoded";
    private static final String kContentTypeImage = "image/jpeg";
    private static final String kDefaultImageName = "photo.jpg";



    private static ApiManager _APIManager;
    private Retrofit _Retrofit;
    private ApiInterface _APIHelper;

    /**
     * a private constructor to prevent any other class from initiating
     */
    private ApiManager() { }

    /**
     * Singleton instance of {@link ApiManager}
     *
     * @return a thread safe singleton object of {@link ApiManager}
     */
    public static synchronized ApiManager ApiClient() {
        if (_APIManager == null) {
            _APIManager = new ApiManager();

            //TODO Need to remove in release mode
            //to enable logging api requests
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Interceptor iter = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("User-Agent", "Your-App-Name")
                            .header("Accept", "application/vnd.yourapi.v1.full+json")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();
            _APIManager._Retrofit = new Retrofit.Builder()
                    .baseUrl(kAPIBaseURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            _APIManager._APIHelper = _APIManager._Retrofit.create(ApiInterface.class);
        }
        return _APIManager;
    }

    /**
     * method converts parameter into  {@link retrofit2.http.Multipart} multipart/form-data
     *
     * @param parameter map which contains request body
     * @return {@link RequestBody} for multipart request : multipart/form-data
     */
    private RequestBody requestBody(String parameter) {
        return RequestBody.create(okhttp3.MediaType.parse(kContentTypeMultiPart), parameter);
    }

    /**
     * method convert parameter into json for application/json; charset=utf-8 type of request
     *
     * @param parameter map which contains request body
     * @return {@link RequestBody} for raw request e.g application/json; charset=utf-8
     */
    private RequestBody requestRawBody(Map<String, Object> parameter) {
        return RequestBody.create(okhttp3.MediaType.parse(kContentTypeJSON), new JSONObject(parameter).toString());
    }


    /**
     * Create API request with APIKey and corresponding parameters. This method will work for all
     * cases. news_head. To upload multiple file to server. news_detail. To get data from server in multiPart format.
     *
     * @param APIKey contains api key that to be called
     * @param parameters to be include as body to the request
     * @return return a {@link retrofit2.http.Multipart} request with the type of form-data
     */
    private Call<JsonObject> getAPIRequest(String APIKey, HashMap<String, Object> parameters) {
        //Process the parameters as per the File and details. If object is of File type then it create MultipartBody.Part and store it in fileList. Else object will be store in detailMap. detailMap and fileList will be use to create APIRequest.
        List<MultipartBody.Part> fileList = new ArrayList<>();
        HashMap<String, RequestBody> detailMap = new HashMap<>();
        for (String key : parameters.keySet()) {
            if (key != null && !key.equals(kEmptyString)) {
                Object value = parameters.get(key);
                assert value != null;
                if (value.getClass() == File.class) {
                    File file = new File(value.toString());
                    if (file.exists()) {
                        // create RequestBody instance from file
                        RequestBody requestFile = RequestBody
                                .create(okhttp3.MediaType.parse(kContentTypeImage), file);
                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body = MultipartBody.Part
                                .createFormData(key, kDefaultImageName, requestFile);
                        fileList.add(body);
                    }
                } else if (value.getClass() == HashMap.class) {
                    // Initialize Builder (not RequestBody)
                    FormBody.Builder builder = new FormBody.Builder();
                    // Add Params to Builder
                    for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) value).entrySet()) {
                        builder.add(entry.getKey(), entry.getValue().toString());
                    }
                    // Create RequestBody
                    RequestBody formBody = builder.build();
                    detailMap.put(key, formBody);
                } else {
                    detailMap.put(key, requestBody(Objects.requireNonNull(parameters.get(key)).toString()));
                }
            }
        }

        return _APIHelper.APIRequestWithFile(APIKey, detailMap, fileList);
    }

    /**
     * method to make convert the call into a raw one with type application/json
     *
     * @param APIKey that to be called
     * @param parameters to be include as body to the request
     * @return a retrofit {@link Call} with content type application/json; charset=utf-8
     */
    private Call<JsonObject> getAPIRawRequest(String APIKey, Map<String, Object> parameters) {
        RequestBody body = requestRawBody(parameters);
        return _APIHelper.APIRequestRaw(APIKey, body);
    }

    /**
     * method to make convert the call into a raw one with type application/json
     *
     * @param APIKey that to be called
     * @param parameters to be include as body to the request
     * @return a retrofit {@link Call} with content type application/json; charset=utf-8
     */
    private Call<JsonObject> getAPIGetRawRequest(String APIKey, Map<String, Object> parameters) {
        RequestBody body = requestRawBody(parameters);
        return _APIHelper.APIGetRequestRaw(APIKey, body);
    }

    private Call<JsonObject> getGetPublicationRequest(){
        return _APIHelper.getPublications();
    }

    private Call<JsonObject> getGetSubscriptionRequest(){
        return _APIHelper.getPlanListing();
    }


    /**
     * checkStatus is responsible to check whether api return the desired result or not by check
     * the api status. If status is success then it returns JSONObject other wise return a
     * suitable message.
     *
     * @param response json response result
     * @param success Block to be executed for success condition
     * @param failure Block to be executed for success condition
     */
    private void checkStatus(JsonObject response, Block.Success<JSONObject> success,
                             Block.Failure failure) {
        try {
            JSONObject jsonResposne = new JSONObject(response.toString());
            GenericResponse<JSONObject> genricResponse = new GenericResponse<>(jsonResposne);
            Log.v(this.getClass().getSimpleName(), "APIResponse :" + jsonResposne.toString(4));

            HTTPStatus status = HTTPStatus.getStatus(Integer.valueOf(jsonResposne.getString(kStatus)));
            String message = jsonResposne.getString(kResponseMessage);
            if (status == HTTPStatus.success) {
                success.iSuccess(Status.success, genricResponse);
            } else {
                failure.iFailure(Status.fail, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            failure.iFailure(Status.fail, kMessageInternalInconsistency);
        }
    }

    /**
     * base method to get data from server
     *
     * @param request a retrofit {@link Call} containing parameters
     * @param success Block to be executed for success condition
     * @param failure Block to be executed for failure condition
     */
    private void apiRequestWithAPI(Call<JsonObject> request, Block.Success<JSONObject> success,
                                   Block.Failure failure) {
        try {
            JsonObject response = request.execute().body();
            if (response != null) {
                checkStatus(response, (Status iStatus, GenericResponse<JSONObject> genricResponse) -> {
                    //if success, it return JSONObject if fail, it return message
                    if (iStatus == Status.success) {
                        success.iSuccess(Status.success, genricResponse);
                    }
                }, (Status iStatus, String message) -> {
                    //If failure occurred.
                    failure.iFailure(Status.fail, message);
                });
            } else {
                failure.iFailure(Status.fail, kMessageServerNotRespondingError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            failure.iFailure(Status.fail, kSocketTimeOut);
        }
    }


    /**
     * method to process raw request, should be used only when theres no file included with the
     * request parameters
     *
     * @param APIKey = api key to called
     * @param parameters = request parameters
     * @param success = success Block
     * @param failure = failure Block
     */
    public void processRawRequest(String APIKey, HashMap<String, Object> parameters,
                                  Block.Success<JSONObject> success, Block.Failure failure) {
        final Call<JsonObject> request = getAPIRawRequest(APIKey, parameters);
        apiRequestWithAPI(request, success, failure);
    }

    /**
     * method to process raw request, should be used only when theres no file included with the
     * request parameters
     *
     * @param APIKey = api key to called
     * @param parameters = request parameters
     * @param success = success Block
     * @param failure = failure Block
     */
    public void processRawGetRequest(String APIKey, HashMap<String, Object> parameters,
                                     Block.Success<JSONObject> success, Block.Failure failure) {
        final Call<JsonObject> request = getAPIGetRawRequest(APIKey, parameters);
        apiRequestWithAPI(request, success, failure);
    }

    /**
     * method to process request in form-data , should be used with requests with files
     *
     * @param APIKey = api key to called
     * @param parameters = request parameters
     * @param success = success Block
     * @param failure = failure Block
     */
    public void processFormRequest(String APIKey, HashMap<String, Object> parameters,
                                   Block.Success<JSONObject> success, Block.Failure failure) {

        final Call<JsonObject> request = getAPIRequest(APIKey, parameters);
        apiRequestWithAPI(request, success, failure);
    }

    public void processPublicationRequest(Block.Success<JSONObject> success, Block.Failure failure){
        final Call<JsonObject> request = getGetPublicationRequest();
        apiRequestWithAPI(request,success,failure);
    }

    public void processSubscriptionRequest(Block.Success<JSONObject> success, Block.Failure failure){
        final Call<JsonObject> request = getGetSubscriptionRequest();
        apiRequestWithAPI(request,success,failure);
    }

}
