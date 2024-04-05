package ordenese.rider.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;

public class ActivitySplashScreen extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadHome();

        // ATTENTION: This was auto-generated to handle app links.
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();

    }

    private void loadHome() {
        new Handler().postDelayed(() -> {
            if (!Constant.DataGetValue(ActivitySplashScreen.this, Constant.Driver_Token).equals("empty")) {
                startActivity(new Intent(ActivitySplashScreen.this, NavigationActivity.class));
                finish();
            } else {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityContainerWithOutTB.class));
                finish();
            }
        }, 3000);
    }
}
