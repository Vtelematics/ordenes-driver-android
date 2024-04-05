package ordenese.rider.activity;

import static android.content.ContentValues.TAG;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
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
        InstallStateUpdatedListener, OSSubscriptionObserver, OSPermissionObserver {

    LinearLayout mHomeBodyFullPageContainer;
    AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 4518;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_without_tb);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadCustomerLoginFragment();

        mHomeBodyFullPageContainer = findViewById(R.id.ll_home_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appUpdateManager = AppUpdateManagerFactory.create(this);
            appUpdateManager.registerListener(this);
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {

                        //UPDATE_AVAILABLE = 2
                        //DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS = 3
                        //UNKNOWN = 0
                        //UPDATE_NOT_AVAILABLE = 1

                        // Log.e("packageName", appUpdateInfo.packageName());
                        //  Log.e("availableVersionCode", "" + appUpdateInfo.availableVersionCode());
                        //  Log.e("installStatus", "" + appUpdateInfo.installStatus());
                        //  Log.e("isUpdateTypeAllowed", "" + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
                        //  Log.e("updateAvailability", "" + appUpdateInfo.updateAvailability());
                        // Log.e("UPDATE_AVAILABLE", "" + UpdateAvailability.UPDATE_AVAILABLE);

                        String s = "packageName=" + appUpdateInfo.packageName() + "\n" +
                                "availableVersionCode=" + appUpdateInfo.availableVersionCode() + "\n" +
                                "installStatus=" + appUpdateInfo.installStatus() + "\n" +
                                "isUpdateTypeAllowed" + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) + "\n" +
                                "updateAvailability" + appUpdateInfo.updateAvailability() + "\n" +
                                "UPDATE_AVAILABLE" + UpdateAvailability.UPDATE_AVAILABLE;

                        //   Constant.messageDialog(Activity_Home.this, "", s);

                        //  ...
                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {   //  check for the type of update flow you want
                            requestUpdate(appUpdateInfo);
                            // Log.e("Update", "Available");
                            //  Constant.showToast("Update-Available");
                        } else {
                            //  Log.e("Update", "NOt Available");
                            //Constant.showToast("Update-NOt Available");
                        }

                    });

        }

        OneSignal.addSubscriptionObserver(this);
        OneSignal.addPermissionObserver(this);
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

    }

    @Override
    public void loadDeliveryHistoryDetail(String delivery_id) {

    }

    @Override
    public void loadCustomerSignature(String delivery_id, String customer_name) {

    }

    @Override
    public void close() {
        onBackPressed();
    }

    @Override
    public void loadLocation() {

    }

    @Override
    public void earningHistory() {

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
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnCompleteListener(appUpdateInfo -> {
//                              AppFunctions.toastLong(AppHome.this,"Update Completed.");
                        });
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnFailureListener(appUpdateInfo -> {
                            // AppFunctions.toastLong(AppHome.this, "GPFailedMsg : " + appUpdateInfo.getMessage());
//                            Log.e("onResume: ",appUpdateInfo.getMessage());
                        });

                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                notifyUser();
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

                if (resultCode == RESULT_OK) {
                    // Log.e("RESULT_OK: ", "" + resultCode);
                    Constant.loadToastMessage(ActivityContainerWithOutTB.this, "Update Completed!");
                } else {
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    if (resultCode == RESULT_CANCELED) {
                        // Log.e("RESULT_CANCELED: ", "" + resultCode);
                        Constant.loadToastMessage(ActivityContainerWithOutTB.this, "Update Canceled!");
                    } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
                        // Log.e("RESULT_UPDATE_FAILED: ", "" + resultCode);
                        Constant.loadToastMessage(ActivityContainerWithOutTB.this, "Update Failed!");
                    } else {
                        // Log.e("Result code other: ", "" + resultCode);
                    }

                }
            }
        }
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUser();
            }
        }

    }

    private void notifyUser() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Snackbar snackbar =
                    Snackbar.make(
                            mHomeBodyFullPageContainer,
                            getResources().getString(R.string.an_update_dowmloaded),
                            Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getResources().getString(R.string.restart), view -> {

                        appUpdateManager.completeUpdate();
                        appUpdateManager.unregisterListener(this);

                    }

            );
            snackbar.setActionTextColor(
                    getResources().getColor(R.color.white));
            snackbar.show();
        }
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (appUpdateManager != null) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE, //  HERE specify the type of update flow you want
                            this,   //  the instance of an activity
                            MY_REQUEST_CODE
                    );
                } catch (Exception e) {
                    //  Log.e("requestUpdate excep", e.toString());
                    Constant.loadToastMessage(ActivityContainerWithOutTB.this, "RUException: " + e.toString());
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //   stopThread();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (appUpdateManager != null) {
                appUpdateManager.unregisterListener(this);
            }
        }

    }

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
