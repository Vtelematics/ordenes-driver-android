package ordenese.rider.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;

public class ActivitySplashScreen extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 200;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Request notification permission first on Android 13+
        // This prevents the OneSignal dialog from interrupting the login screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
                // loadHome() will be called from onRequestPermissionsResult
                return;
            }
        }

        // Android 12 and below, or permission already granted — proceed normally
        loadHome();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // Whether granted or denied, proceed to the app
            // We don't block the user from using the app just because they denied notifications
            loadHome();
        }
    }

    private void loadHome() {
        new Handler().postDelayed(() -> {
            if (!Constant.DataGetValue(ActivitySplashScreen.this, Constant.Driver_Token).equals("empty")) {
                startActivity(new Intent(ActivitySplashScreen.this, NavigationActivity.class));
            } else {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityContainerWithOutTB.class));
            }
            finish();
        }, 3000);
    }
}