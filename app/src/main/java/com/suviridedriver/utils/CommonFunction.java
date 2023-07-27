package com.suviridedriver.utils;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class CommonFunction {
    private static String keys = "";

    public CommonFunction() {
    }

    public static String getError(Response response) {
        String message = "";
        try {
            switch (response.code()) {
                case 500: {
                    message = "Code - 500 , Internal server error.";
                    break;
                }
                case 503: {
                    message = "Code - 503 , Service unavailable.";
                    break;
                }
                case 404: {
                   // message = "Code - 404 , Item not found.";
                    message = "Code - 404 , "+ response.message();
                    break;
                }
                case 400: {
                    message = "Code - 400 , "+ response.message();
                    break;
                }
                case 401: {
                    message = "Code - 401 , Access token not found.";
                    break;
                }
                default: {
                    message = "Code - " + response.code() + " , Something went wrong.";
                    break;
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
            message = "Exception - "+exception.getMessage();
        }
        return message;
    }

    public static void isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Toast.makeText(ctx, "Internet Connection Is not Required", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx, "Internet Connection Is Required", Toast.LENGTH_LONG).show();

        }
    }

    public static boolean isAppInForeground(Context context) {
       /* if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                    return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
                } else {
                    ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
                    ActivityManager.getMyMemoryState(appProcessInfo);
                    if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                        return true;
                    }

                    KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    // App is foreground, but screen is locked, so show notification
                    return km.inKeyguardRestrictedInputMode();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        //boolean foregroud = new ForegroundCheckTask().execute(context).get();
        return true;
    }

    public static boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

 /*   public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }*/

    public static void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    public static String[] splitcomma(String text) {
        // String str = "This is demo text, and demo line!";
        String[] res = text.split("[,]", 0);
        /*for (String myStr : res) {

        }*/
        return res;
    }

    public static String[] splitSlash(String text) {
        // String str = "This is demo text, and demo line!";
        String[] res = text.split("[/]", 0);
        /*for (String myStr : res) {

        }*/
        return res;
    }

    public static String[] splitcolon(String text) {
        // String str = "This is demo text, and demo line!";
        String[] res = text.split("[:]", 0);
        /*for (String myStr : res) {

        }*/
        return res;
    }

    public static String[] splitbr(String text) {
        // String str = "This is demo text, and demo line!";
        String[] res = text.split("<br>", 0);
        /*for (String myStr : res) {

        }*/
        return res;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static String gettime() {
        String time = "";
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }

    public static void shareApp(Context context) {
        String APP_PNAME = "com.autismtherepy";
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + APP_PNAME);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static String convertBitmapToString(Bitmap bm) {

        byte[] byte_arr = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte_arr = stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(byte_arr, Base64.DEFAULT);

    }

    // Cancel Progress dialog
    public static void cancelProgressDialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String replaceBrackets(String value) {
        value = value.replace("[", "");
        value = value.replace("]", "");

        return value;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static int getDevicewidth(Context ct) {
        DisplayMetrics displayMetrics = ct.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static void hideKeyBoard1(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

/*    public static String getEncryptString(String text) throws Exception {
        CryptLib cryptLib = new CryptLib();
        String encrypt_password = cryptLib.encryptPlainTextWithRandomIV(text, Constants.ENCRYPTION_KEY);
        return encrypt_password;
    }

    public static String getDecryptString(String text) throws Exception {

        CryptLib cryptLib = new CryptLib();
        String decrypt_name = cryptLib.decryptCipherTextWithRandomIV(text, Constants.ENCRYPTION_KEY);
        return decrypt_name;
    }*/

    public static int getDeviceHeight(Context ct) {
        DisplayMetrics displayMetrics = ct.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static String getdate() {
        String time = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    public static String getmmddyyyy_date() {
        String time = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    private static Calendar current;
    private static long miliSeconds;
    private static Date resultdate;

    public static Date getLocalTime(Date myDate) {

        Date resultdate1 = null;
        try {

            current = Calendar.getInstance();


            TimeZone tzCurrent = current.getTimeZone();
            int offset = tzCurrent.getRawOffset();
            if (tzCurrent.inDaylightTime(new Date())) {
                offset = offset + tzCurrent.getDSTSavings();
            }

            Calendar current1 = Calendar.getInstance();

            current1.setTime(myDate);
            miliSeconds = current1.getTimeInMillis();
            miliSeconds = miliSeconds + offset;
            resultdate = new Date(miliSeconds);

            TimeZone london = TimeZone.getTimeZone("America/New_York");
            long now = resultdate.getTime();
            resultdate = new Date(miliSeconds - london.getOffset(now));

        } catch (Exception e) {
            e.printStackTrace();
        }


        return resultdate;
    }

    public static Date StringTodateEditprofile(String dtStart) {
        Date date = null;

        String parsedate = dtStart.substring(0, 10);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = format.parse(parsedate);
            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }


    public static Date StringTodate(String dtStart) {
        Date date = null;

        String parsedate = dtStart.substring(0, 19);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = format.parse(parsedate);
            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }


    public static String getDateToString(Date date) {
        String time = "";
        try {
            // Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            time = df.format(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    public static String getCurrentdateTime() {
        String time = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1000;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String GetDDMM(String dd_mm_yyyy) {
        String date_time = "";

        String day = "", Month = "", year = "";
        String appint_array[] = dd_mm_yyyy.split(" ");
        if (appint_array != null && appint_array.length > 0) {
            String date_suggestby_counsel = appint_array[0];

            String datesplitarray[] = date_suggestby_counsel.split("/");

            if (datesplitarray != null && datesplitarray.length > 2) {
                day = datesplitarray[1];
                Month = datesplitarray[0];

            }
            date_time = day + "/" + Month;

        }
        return date_time;
    }


    public static String ChangeDateFormat(String mm_dd_yyyy) {
        String date_time = "";

        String day = "", Month = "", year = "";
        String appint_array[] = mm_dd_yyyy.split(" ");
        if (appint_array != null && appint_array.length > 0) {
            String date_suggestby_counsel = appint_array[0];

            String datesplitarray[] = date_suggestby_counsel.split("/");

            if (datesplitarray != null && datesplitarray.length > 2) {
                day = datesplitarray[1];
                Month = datesplitarray[0];
                year = datesplitarray[2];
            }
            date_time = day + "/" + Month + "/" + year + " " + appint_array[1];

        }
        return date_time;
    }


    public static String GetTimerData(String time, String CompareDate) {
        String day = "";
        try {
            String outputPattern = "MM/dd/yyyy HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(CompareDate);
            Date Date2 = format.parse(time);
            long mills = Date2.getTime() - Date1.getTime();
            long Day1 = mills / (1000);
            // long Day1 = mills / (1000 * 60 * 60);

            long remaing_sec = 86400 - Day1;
            if (remaing_sec > 0) {
                int hours = (int) remaing_sec / (60 * 60);

                int min = (int) (remaing_sec / (60)) % 60;
                int sec = (int) (remaing_sec) % 60;

                String hour_str = "" + hours;
                String min_str = "" + min;
                String sec_str = "" + sec;

                if (hours < 10)
                    hour_str = "0" + hours;
                if (min < 10)
                    min_str = "0" + min;
                if (sec < 10)
                    sec_str = "0" + sec;

                day = "" + hour_str + ":";
                day = day + "" + min_str + ":";
                day = day + "" + sec_str;
            }



           /* if (day < 0)
                day = 0;*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    public static int CheckPlanExpiry(String time, String CompareDate) {
        int day = 0;
        try {
            String outputPattern = "yyyy-MM-dd";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(CompareDate);
            Date Date2 = format.parse(time);
            long mills = Date2.getTime() - Date1.getTime();
            long Day1 = mills / (1000 * 60 * 60);

            day = (int) Day1 / 24;


           /* if (day < 0)
                day = 0;*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    public static int Getdatediff(String time, String CompareDate) {
        int day = 0;
        try {
            String outputPattern = "MM/dd/yyyy HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(CompareDate);
            Date Date2 = format.parse(time);
            long mills = Date2.getTime() - Date1.getTime();
            long Day1 = mills / (1000 * 60 * 60);

            day = (int) Day1 / 24;


           /* if (day < 0)
                day = 0;*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    public static boolean isSpecialChar(String word) {

        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(word);
        return hasSpecial.find();
    }

    public static boolean isUpperCase(String word) {

        Pattern letter = Pattern.compile("[A-Z]");
        Matcher hasLetter = letter.matcher(word);
        return hasLetter.find();
    }

    public static boolean isNumber(String word) {

        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasDigit = digit.matcher(word);
        return hasDigit.find();
    }

   /* public static void fetchTriggerWords()
    {

        ParseQuery<ParseObject> fetchTrigger = ParseQuery.getQuery("keywords");
        fetchTrigger.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null && objects.size() > 0) {
                    ParseObject mFetchObject = objects.get(0);
                    keys = mFetchObject.getString("keys");
                }
            }
        });
    }*/

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean is_numeric(String msg) {
        boolean is_num = false;
        for (int i = 0; i < 10; i++) {
            if (msg.contains("" + i)) {
                is_num = true;
                break;
            }
        }
        return is_num;
    }


    final static String TODAYS_STEPS = "todays_steps";

    public static int gettodaysstep(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TODAYS_STEPS, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void storertodaystep(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TODAYS_STEPS, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    final static String TODAYS_RECOMONDED_STEP = "todays_recomonded_steps";

    public static int gettodaysrecomndedstep(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TODAYS_RECOMONDED_STEP, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void settodaysrecomndedstep(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TODAYS_RECOMONDED_STEP, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*final static String LOCAL_LATLANG = "local_lat_lang";
    public static int GetLatLang(Context context)
    {
        int restoredText = 0;
        try
        {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(LOCAL_LATLANG, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void SetLatLang(Context context,int steps)
    {
        try
        {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(LOCAL_LATLANG,steps);
            editor.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }*/


    public static int Getdiffbettwodate(String newtime, String oldtime) {
        int day = 0;
        try {
            String outputPattern = "MM/dd/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(newtime);
            Date Date2 = format.parse(oldtime);
            long mills = Date1.getTime() - Date2.getTime();
            long Day1 = mills / (1000 * 60 * 60);

            day = (int) Day1 / 24;


            if (day < 0)
                day = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    final static String TARGET_PUSH_DATE = "target_push_date";

    public static int GetTargetPushDate(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TARGET_PUSH_DATE, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void SetTargetPushDate(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TARGET_PUSH_DATE, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getLocalTime(String datetime) {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datetime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            current = Calendar.getInstance();


            TimeZone tzCurrent = current.getTimeZone();
            int offset = tzCurrent.getRawOffset();
            if (tzCurrent.inDaylightTime(new Date())) {
                offset = offset + tzCurrent.getDSTSavings();
            }

            Calendar current1 = Calendar.getInstance();

            current1.setTime(myDate);
            miliSeconds = current1.getTimeInMillis();
            miliSeconds = miliSeconds + offset;
            resultdate = new Date(miliSeconds);
            SimpleDateFormat destFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            TimeZone london = TimeZone.getTimeZone("Europe/London");
            long now = resultdate.getTime();
            Date resultdate1 = new Date(miliSeconds - london.getOffset(now));
            result = destFormat.format(resultdate1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }


    public static String getGMTTime(String datetime) {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datetime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            current = Calendar.getInstance();
            current.setTime(myDate);
            miliSeconds = current.getTimeInMillis();
            TimeZone tzCurrent = current.getTimeZone();
            int offset = tzCurrent.getRawOffset();
            if (tzCurrent.inDaylightTime(new Date())) {
                offset = offset + tzCurrent.getDSTSavings();
            }
            miliSeconds = miliSeconds - offset;
            resultdate = new Date(miliSeconds);
            SimpleDateFormat destFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            TimeZone london = TimeZone.getTimeZone("Europe/London");
            long now = resultdate.getTime();
            Date resultdate1 = new Date(miliSeconds + london.getOffset(now));
            result = destFormat.format(resultdate1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public static double PI = 3.14159265;
    public static double TWOPI = 2 * PI;

    public static boolean coordinate_is_inside_polygon(
            double latitude, double longitude,
            ArrayList<Double> lat_array, ArrayList<Double> long_array) {
        int i;
        double angle = 0;
        double point1_lat;
        double point1_long;
        double point2_lat;
        double point2_long;
        int n = lat_array.size();

        for (i = 0; i < n; i++) {
            point1_lat = lat_array.get(i) - latitude;
            point1_long = long_array.get(i) - longitude;
            point2_lat = lat_array.get((i + 1) % n) - latitude;
            //you should have paid more attention in high school geometry.
            point2_long = long_array.get((i + 1) % n) - longitude;
            angle += Angle2D(point1_lat, point1_long, point2_lat, point2_long);
        }

        if (Math.abs(angle) < PI)
            return false;
        else
            return true;
    }


    public static double Angle2D(double y1, double x1, double y2, double x2) {
        double dtheta, theta1, theta2;

        theta1 = Math.atan2(y1, x1);
        theta2 = Math.atan2(y2, x2);
        dtheta = theta2 - theta1;
        while (dtheta > PI)
            dtheta -= TWOPI;
        while (dtheta < -PI)
            dtheta += TWOPI;

        return (dtheta);
    }


    public static ProgressDialog progressDialog = null;

    public static void cancelProgress() {

        try {
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isServiceRunning(Context mContext, Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())
                    && service.pid != 0) {
                //ShowLog("", "ser name "+serviceClass.getName());
                return true;
            }
        }
        return false;
    }

    public static void showProgressDialog(final Context context, final String title) {

        if (progressDialog != null) {
            try {
                /*if (progressDialog.isShowing()) {
                    cancelProgress();
                } else */
                {
                    try {
                        progressDialog.setTitle(title);
                        // progressDialog.setIcon(R.drawable.ic_launcher);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(true);
                        progressDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(title);
                //  progressDialog.setIcon(R.drawable.ic_launcher);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(true);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static String getReminingTime() {
        String delegate = "h:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    public static String getDate() {
        String delegate = "EEE dd MMMM";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    static Context context = null;

/*    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context)
    {
        String IMEI;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                IMEI = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (null != mTelephony && null != mTelephony.getDeviceId()) {
                    IMEI = mTelephony.getDeviceId();
                } else {
                    IMEI = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
            return IMEI;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }*/

    public static boolean isValidPassword(String password) {
        boolean isValid = true;
        if (password.length() > 15 || password.length() < 8) {
            System.out.println("Password must be less than 20 and more than 8 characters in length.");
            isValid = false;
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            System.out.println("Password must have atleast one uppercase character");
            isValid = false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            System.out.println("Password must have atleast one lowercase character");
            isValid = false;
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            System.out.println("Password must have atleast one number");
            isValid = false;
        }
        String specialChars = "(.*[!~&?><.;:*^_+=(){}@#$,%-].*$)";
        if (!password.matches(specialChars)) {
            System.out.println("Password must have atleast one special character among @#$%");
            isValid = false;
        }
        return isValid;
    }

    static class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
