package com.usingstudioo.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.usingstudioo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.usingstudioo.Constants.Constants.kU8Format;


public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    static ProgressDialog mProgressDialog;

    /**
     * Checks if the input parameter is a valid email.
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher;
        Pattern pattern = Pattern.compile(emailPattern);

        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * Generate top layer progress indicator.
     * @param context    activity context
     * @param cancelable can be progress layer canceled
     * @return dialog
     */
    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog =null ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            progressDialog = new ProgressDialog(context, R.style.ProgressTheme);
        else
            progressDialog = new ProgressDialog(context, R.style.AlertDialog_Holo);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        return progressDialog;
    }



    public static boolean isValidPasswordd(String testString) {
        return (testString.length()>=6 && testString.length()<=16);
    }

    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context
     */
    public static void hideKeyboard(Context context) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even hide keyboard " + e.getMessage());
        }
    }

    /**
     * Force the keyboard for the view.
     * @param context
     */
    public static void showKeyboard(Context context,View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant show keyboard " + e.getMessage());
        }
    }

    /**
     *
     * @param textView = textview / editext from which value to be extracted
     * @return proper String value
     */
    public static String getProperText(TextView textView){
        return textView.getText().toString().trim();
    }

    @Nullable
    /**
     * Partially capitalizes the string from paramter start and offset.
     *
     * @param string String to be formatted
     * @param start  Starting position of the substring to be capitalized
     * @param offset Offset of characters to be capitalized
     * @return
     */
    public static String capitalizeString(String string, int start, int offset) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string.substring(start, offset).toUpperCase() + string.substring(offset);
    }

    public static String  getFirstLetterInUpperCase(String str) {
        if(str.isEmpty())
            return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    public static String changeDateTimeFormat(String date){
        if(!date.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.UK);
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy",Locale.UK);
            assert sourceDate != null;
            return targetFormat.format(sourceDate);
        }else
            return "";
    }

    public static String changeDateTimeNewFormat(String date){
        if(!date.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.UK);
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yy",Locale.UK);
            assert sourceDate != null;
            return targetFormat.format(sourceDate);
        }else
            return "";
    }

    public static String changeDateTimeModel(String date){
        if(!date.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.UK);
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
            assert sourceDate != null;
            return targetFormat.format(sourceDate);
        }else
            return "";
    }

    public static String changeDateTimeFormatNew(String date){
        if(!date.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.UK);
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss",Locale.UK);
            assert sourceDate != null;
            return targetFormat.format(sourceDate);
        }else
            return "";
    }

    public static String changeTimeFormatNew(String time){
        if(!time.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("mm:ss",Locale.UK);
            assert sourceDate != null;
            return targetFormat.format(sourceDate);
        }else
            return "";
    }

    public static String getTimeStampDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static String getTimeStampTime(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.UK);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static Calendar getTime(Calendar c,String sTime){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(sTime);
        } catch (ParseException e) {
            e.getErrorOffset();
        }
        assert date != null;
        c.setTime(date);
        return c;
    }

    public static String scheduleTimeStamp(String str_date){
        DateFormat formatter = new SimpleDateFormat("MMM, dd, yyyy hh:mm", Locale.UK);
        Date date;
        String timeStamp="";
        try {
            date = formatter.parse(str_date);
            timeStamp = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static String getFileTimeStamp(long time){
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
        Date date = new Date(time);
        return formatter.format(date);
    }

    public static Date scheduleDate(String str_date){
        DateFormat formatter = new SimpleDateFormat("MMM, dd, yyyy hh:mm", Locale.UK);
        Date date = null;
        try {
            date = formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



    public static int getLastFirst(String str_date){
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        Date date = null;
        try {
            date = formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cld = Calendar.getInstance();
        Date current = cld.getTime();
        long time = date.getTime()-(5 * 24 * 60 * 60 * 1000);
        if(current.getTime()==time || (current.getTime()>time && current.getTime()<date.getTime()))
            return 1;
        else if(current.getTime()>date.getTime())
            return 2;
        else
            return 0;
    }


    public static String loadJSONFromAsset(Context context,String rangeTxt,int scalePos) {
        String json;
        try {
            String assetName = "json/"+rangeTxt+scalePos+".json";
            InputStream is = context.getAssets().open(assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, kU8Format);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
        return json;
    }



    /**
     * Converting dp to pixel
     */
    public static int dpToPx(Context context,int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    /**
     * Shows an alert dialog with the OK button. When the user presses OK button, the dialog
     * dismisses.
     **/
    public static void showAlertDialog(Context context, @StringRes int titleResId, @StringRes int bodyResId) {
        showAlertDialog(context, context.getString(titleResId),
                context.getString(bodyResId), null);
    }

    /**
     * Shows an alert dialog with the OK button. When the user presses OK button, the dialog
     * dismisses.
     **/
    public static void showAlertDialog(Context context, String title, String body) {
        showAlertDialog(context, title, body, null);
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body, DialogInterface.OnClickListener okListener) {

        if (okListener == null) {
            okListener = (dialog, which) -> dialog.cancel();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(body).setPositiveButton("OK", okListener);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        builder.show();
    }

    private static final String DATE_STRING="yyyy-MM-dd-HH_mm_ss";
    public static String createPath(Context ctx,String fileType,String suffix)
    {
        long dateTaken = System.currentTimeMillis();
        File filesDir = getStorageDir(ctx);
        String filesDirPath = filesDir.getAbsolutePath();
        filesDir.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING,Locale.UK);
        Date date = new Date(dateTaken);
        String filepart = dateFormat.format(date);
        return filesDirPath + "/" + fileType + filepart + suffix;
    }

    private static File getStorageDir(Context ctx) {
        /*String filesDirPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC).toString() +File.separator + "USing";*/
        String filesDirPath = ctx.getCacheDir()+File.separator + "USing";
        //String filesDirPath = Environment.getDataDirectory()+File.separator + "USing";

        File ret = new File(filesDirPath);
        if(!ret.exists()) {
            ret.mkdirs();
        }
        return ret;
    }

    public static ArrayList<File> getFiles(Context ctx){
        ArrayList<File> list = new ArrayList<>();
        File directory = getStorageDir(ctx);
        String filesDirPath = directory.getAbsolutePath();
        directory.mkdirs();
        File[] files = directory.listFiles();
        assert files != null;
        if(files.length!=0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }
        Log.d("Files", "Size: "+ files.length);
        for (File file : files) {
            list.add(file);

            Log.d("Files", "FileName:" + file.getName());
        }
        return list;
    }




    /**
     * Serializes the Bitmap to Base64
     *
     * @return Base64 string value of a {@linkplain Bitmap} passed in as a parameter
     //* @throws NullPointerException If the parameter bitmap is null.
     **/
    public static String toBase64(Bitmap bitmap) {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        String base64Bitmap = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBitmap = stream.toByteArray();
        base64Bitmap = Base64.encodeToString(imageBitmap, Base64.DEFAULT);

        return base64Bitmap;
    }

    /**
     * Converts the passed in drawable to Bitmap representation
     *
     //* @throws NullPointerException If the parameter drawable is null.
     **/
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            throw new NullPointerException("Drawable to convert should NOT be null");
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Converts the given bitmap to {@linkplain InputStream}.
     *
     //* @throws NullPointerException If the parameter bitmap is null.
     **/
    public static InputStream bitmapToInputStream(Bitmap bitmap) throws NullPointerException {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream inputstream = new ByteArrayInputStream(baos.toByteArray());

        return inputstream;
    }

    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx           Activity context
     * @param title         Title of the progress dialog
     * @param body          Body/Message to be shown in the progress dialog
     * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
     **/
    public static void showProgressDialog(Context ctx, String title, String body, boolean isCancellable) {
        showProgressDialog(ctx, title, body, null, isCancellable);
    }

    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx           Activity context
     * @param title         Title of the progress dialog
     * @param body          Body/Message to be shown in the progress dialog
     * @param icon          Icon to show in the progress dialog. It can be null.
     * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
     **/
    public static void showProgressDialog(Context ctx, String title, String body, Drawable icon, boolean isCancellable) {

        if (ctx instanceof Activity) {
            if (!((Activity) ctx).isFinishing()) {
                mProgressDialog = ProgressDialog.show(ctx, title, body, true);
                mProgressDialog.setIcon(icon);
                mProgressDialog.setCancelable(isCancellable);
            }
        }
    }

    /**
     * Check if the {@link ProgressDialog} is visible in the UI.
     **/
    public static boolean isProgressDialogVisible() {
        return (mProgressDialog != null);
    }

    /**
     * Dismiss the progress dialog if it is visible.
     **/
    public static void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = null;
    }

    /**
     * Gives the device independent constant which can be used for scaling images, manipulating view
     * sizes and changing dimension and display pixels etc.
     **/
    public static float getDensityMultiplier(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    public static int getDip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    /**
     * Creates a confirmation dialog with Yes-No Button. By default the buttons just dismiss the
     * dialog.
     *
     * @param ctx
     * @param message     Message to be shown in the dialog.
     * @param yesListener Yes click handler
     * @param noListener
     **/
    public static void showConfirmDialog(Context ctx, String message, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
        showConfirmDialog(ctx, message, yesListener, noListener, "Yes", "No");
    }

    /**
     * Creates a confirmation dialog with Yes-No Button. By default the buttons just dismiss the
     * dialog.
     *
     * @param ctx
     * @param message     Message to be shown in the dialog.
     * @param yesListener Yes click handler
     * @param noListener
     * @param yesLabel    Label for yes button
     * @param noLabel     Label for no button
     **/
    public static void showConfirmDialog(Context ctx, String message, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener, String yesLabel, String noLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        if (yesListener == null) {
            yesListener = (dialog, which) -> dialog.dismiss();
        }

        if (noListener == null) {
            noListener = (dialog, which) -> dialog.dismiss();
        }

        builder.setMessage(message).setPositiveButton(yesLabel, yesListener).setNegativeButton(noLabel, noListener).show();
    }

    public static String md5(String s) {
        /*try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";*/

        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Creates a confirmation dialog that show a pop-up with button labeled as parameters labels.
     *
     * @param ctx                 {@link Activity} {@link Context}
     * @param message             Message to be shown in the dialog.
     * @param dialogClickListener
     * @param positiveBtnLabel    For e.g. "Yes"
     * @param negativeBtnLabel    For e.g. "No"
     **/
    public static void showDialog(Context ctx, String message, String positiveBtnLabel, String negativeBtnLabel, DialogInterface.OnClickListener dialogClickListener) {

        if (dialogClickListener == null) {
            throw new NullPointerException("Action listener cannot be null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setMessage(message).setPositiveButton(positiveBtnLabel, dialogClickListener).setNegativeButton(negativeBtnLabel, dialogClickListener).show();
    }

}
