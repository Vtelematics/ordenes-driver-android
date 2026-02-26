package ordenese.rider.activity;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.Transfer;
import ordenese.rider.fragments.FragmentLanguage;
import ordenese.rider.fragments.account.FragmentForgetPassword;
import ordenese.rider.fragments.account.FragmentLogin;
import ordenese.rider.fragments.account.Fragment_customer_detail;
import ordenese.rider.fragments.static_screen.FragmentInfoScreen;


public class ActivityContainerWithOutTB extends AppCompatActivity implements Transfer,
        OSSubscriptionObserver, OSPermissionObserver {

    LinearLayout mHomeBodyFullPageContainer;
    AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 4518;
    private static final String TAG = "ImmediateUpdateExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_without_tb);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadCustomerLoginFragment();

        mHomeBodyFullPageContainer = findViewById(R.id.ll_home_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appUpdateManager = AppUpdateManagerFactory.create(this);

            // Check for immediate updates when the app starts.
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

                // Check if an immediate update is available.
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Start the immediate update flow.
                    Log.d(TAG, "Immediate update available. Starting update flow.");
                    startImmediateUpdate(appUpdateInfo);
                } else {
                    Log.d(TAG, "No immediate update available or update not allowed.");
                }
            });

        }

        OneSignal.addSubscriptionObserver(this);
        OneSignal.addPermissionObserver(this);
    }

    private void startImmediateUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_REQUEST_CODE
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed to start immediate update flow: " + e.getMessage());
            Constant.loadToastMessage(ActivityContainerWithOutTB.this, "RUException: " + e.toString());
        }
    }

    public void loadInfoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_home_container, new FragmentInfoScreen(), "FragmentInfoScreen")
                .addToBackStack("customer_info")
                .commit();
    }

    public void loadLanguageFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_home_container, new FragmentLanguage(), "FragmentLanguageScreen")
                .addToBackStack("language")
                .commit();
    }

    public void loadCustomerInfoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_home_container, new Fragment_customer_detail(), "FragmentCustomerInfoScreen")
                .addToBackStack("info_customer")
                .commit();
    }

    @Override
    public void loadForgetPassword() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_home_container, new FragmentForgetPassword(), "FragmentForgetPassword")
                .addToBackStack("forget_password")
                .commit();
    }

    public void loadCustomerLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_home_container, new FragmentLogin(), "FragmentCustomerLogin")
                .addToBackStack("login")
                .commit();
    }

    @Override
    public void loadDeliveryHistory(String filter) {
        // Not implemented in this example
    }

    @Override
    public void loadDeliveryHistoryDetail(String delivery_id) {
        // Not implemented in this example
    }

    @Override
    public void loadCustomerSignature(String delivery_id, String customer_name) {
        // Not implemented in this example
    }

    @Override
    public void close() {
        onBackPressed();
    }

    @Override
    public void loadLocation() {
        // Not implemented in this example
    }

    @Override
    public void earningHistory() {
        // Not implemented in this example
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(
                    "ar".equals(AppLanguageSupport.getLanguage(ActivityContainerWithOutTB.this)) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (appUpdateManager != null) {
                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                    // If an immediate update is already in progress, start it again.
                    if (appUpdateInfo.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        Log.d(TAG, "Resuming immediate update flow.");
                        startImmediateUpdate(appUpdateInfo);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (resultCode != RESULT_OK) {
                    // If the user cancels the immediate update or it fails, your app
                    // should not be usable. You may want to close the app or
                    // display a message.
                    Log.e(TAG, "Immediate update flow failed or was cancelled. Result code: " + resultCode);
                    Constant.loadToastMessage(this, "Update failed or canceled. App must close.");
                    finish(); // Example of closing the app
                } else {
                    Log.d(TAG, "Immediate update flow successful.");
                }
            }
        }
    }

    // The InstallStateUpdatedListener and onStateUpdate are for flexible updates,
    // which are not needed for an immediate update flow.
    // They are removed from the class and its implementation.

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (stateChanges.getTo().isSubscribed()) {
            String playerId = stateChanges.getTo().getUserId();
            if (playerId != null) {
                Log.e(TAG, "OneSignal Player ID : " + playerId);
            }
        }
    }

    @Override
    public void onOSPermissionChanged(OSPermissionStateChanges stateChanges) {
        if (stateChanges.getFrom().areNotificationsEnabled() &&
                !stateChanges.getTo().areNotificationsEnabled()) {
//            new AlertDialog.Builder(this)
//                    .setMessage("Notifications Disabled!")
//                    .show();
        }
    }

}
