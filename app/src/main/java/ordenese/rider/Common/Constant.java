package ordenese.rider.Common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ordenese.rider.AppContext;
import ordenese.rider.R;
import ordenese.rider.fragments.static_screen.No_internet_connection;

import java.io.ByteArrayOutputStream;

public class Constant {

    public static int milliSeconds = 15000;
    public static int milliSecondsForAutoAssignApiCallDB = 2000 ;

    public static String visible = "visible";
    public static String disable = "disable";
    public static String Required = "Required";

    public static String Order_id = "";
    public static String Order_status_id = "";
    public static String Driver_Token = "Driver_Token";
    public static String Driver_Token_ = "";
    public static String CustomerDetails = "Customer_details";
    public static String DriverStatus_ = "";
    public static String DriverStatus = "DriverStatus";
    public static String DriverImg = "DriverImg";
    public static String Driver_Id = "Driver_Id";
    public static String DriverImgUrl = "DriverImgUrl";
    public static String DriverName = "DriverName";
    public static String DriverMobile = "DriverMobile";
    public static String DriverDataUpdated = "DriverDataUpdated";
    public static String DirectionsApiKey = "AIzaSyAC429H1tsagehvTHTVe0HA6F8sbgXSAks";
    public static String CurrentLanguage = "CurrentLanguage";
    public static String CurrentLanguageCode = "CurrentLanguageCode";
    public static String CurrentLanguageId = "CurrentLanguageId";
    public static String LanguageSelection = "IsLanguageOpened";
    public static String CurrentLocation = "CurrentLocation";
    public static String CurrentLatitude = "CurrentLatitude";
    public static String CurrentLongitude = "CurrentLongitude";
    private static Resources mResources;
    public static Boolean tracking = false;
    public static Boolean login = true;

    public static void glide_image_loader(String url, final ImageView imageView) {
        Glide.with(AppContext.getInstance()).load(url).apply(getOption("Default")).into(imageView);
    }

    private static RequestOptions getOption(String which) {

        RequestOptions options;
        switch (which) {
            case "fixed":
                options = new RequestOptions()
                        .error(R.drawable.app_logo_2)
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            case "Category":
                options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            default:
                options = new RequestOptions()
                        .error(R.drawable.app_logo_2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
        }
        return options;

    }

    public static void DataStoreValue(Context context, String Key, String Value){
        DataRemoveValue(context,Key);
        SharedPreferences sharedPreferences =  context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Key,Value);
        editor.apply();
    }

    public static String DataGetValue(Context context, String Key){
        SharedPreferences preferences = context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        String Results = preferences.getString(Key,"empty");
        return Results;
    }

    public static void DataRemoveValue(Context context, String Key){
        SharedPreferences sharedPreferences =  context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Key);
        editor.apply();
    }

    public static RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap bitmap) {

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int borderWidthHalf = 10; // In pixels

        int bitmapRadius = Math.min(bitmapWidth, bitmapHeight) / 2;

        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);

        int newBitmapSquareWidth = bitmapSquareWidth + borderWidthHalf;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(roundedBitmap);


        canvas.drawColor(Color.RED);

        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;


        canvas.drawBitmap(bitmap, x, y, null);


        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf * 2);
        borderPaint.setColor(Color.GRAY);


        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, newBitmapSquareWidth / 2, borderPaint);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources, roundedBitmap);


        roundedBitmapDrawable.setCornerRadius(bitmapRadius);


        roundedBitmapDrawable.setAntiAlias(true);

        return roundedBitmapDrawable;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static boolean isNetworkOnline(Context con)
    {
        boolean status = false;
        try
        {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm != null ? cm.getNetworkInfo(0) : null;

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm != null ? cm.getNetworkInfo(1) : null;

                status = netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return status;
    }

    public  static  void loadToastMessage(Activity activity, String Message){
        Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
    }

    public static void loadInternetAlert(FragmentManager fragmentManager){
        No_internet_connection fragment = new No_internet_connection();
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment.show(fragmentManager, "no internet");
    }

    public static int current_language_id() {
//        if (!Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("empty")) {
//            if (Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("true")) {
//                return 1;
//            } else {
//                return 2;
//            }
//        } else {
//            return 1;
//        }
        return !Constant.DataGetValue(AppContext.getInstance(), Constant.CurrentLanguageId).equals("empty") ?
                Integer.parseInt(Constant.DataGetValue(AppContext.getInstance(), Constant.CurrentLanguageId)) : 1;

    }

    public static String current_language_code() {
//        if (!Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("empty")) {
//            if (Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("true")) {
//                return "en";
//            } else {
//                return "ar-AE";
//            }
//        } else {
//            return "en";
//        }
        return !Constant.DataGetValue(AppContext.getInstance(), Constant.CurrentLanguageCode).equals("empty") ?
                Constant.DataGetValue(AppContext.getInstance(), Constant.CurrentLanguageCode) : "en";
    }

}
