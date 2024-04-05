package ordenese.rider.activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Location.Live_Location;
import ordenese.rider.R;
import ordenese.rider.Transfer;
import ordenese.rider.firebase_chat.FirebaseChat;
import ordenese.rider.firebase_chat.UserListFragment;
import ordenese.rider.fragments.account.EarningHistory;
import ordenese.rider.fragments.FragmentLanguage;
import ordenese.rider.fragments.UnAssignOrderList;
import ordenese.rider.fragments.account.FragmentLogin;
import ordenese.rider.fragments.account.Fragment_custom_signature;
import ordenese.rider.fragments.account.Fragment_customer_detail;
import ordenese.rider.fragments.account.Fragment_delivery_history;
import ordenese.rider.fragments.static_screen.FragmentInfoScreen;

public class ActivityContainerWithTB extends AppCompatActivity implements Transfer
        , OSSubscriptionObserver, OSPermissionObserver {

    private int load_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_with_tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if (b != null) {
            String type = (String) b.get("type");
            if (type.equals("Setting")) {
                loadCustomerInfoFragment();
            }
            String filter;
            if (type.equals("My_delivery")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.ongoing_delivery));
                filter = (String) b.get("filter");
                loadDeliveryHistory(filter);
            }
            if (type.equals("Delivery_history")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.delivery_history));
                filter = (String) b.get("filter");
                loadDeliveryHistory(filter);
            }
            if (type.equals("Signature_type")) {
                load_type = 1;
                String delivery_id = b.getString("delivery_id");
                String signature = b.getString("customer_name");
                loadCustomerSignature(delivery_id, signature);
            }
            if (type.equals("Location")) {
                loadLocation();
            }
            if (type.equals("EarningHistory")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.earning_history));
                earningHistory();
            }
            if (type.equals("LoadChatList")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.chat));
                LoadChatList();
            }
            if (type.equals("LoadUsersList")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.chat));
                LoadUsersList();
            }
            if (type.equals("UnAssignOrderList")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.un_assign_orders));
                UnAssignOrderList();
            }
        }

        OneSignal.addSubscriptionObserver(this);
        OneSignal.addPermissionObserver(this);

    }

    private void LoadUsersList() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_loader, new UserListFragment(), "LoadUsersList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("LoadUsersList")
                .commitAllowingStateLoss();

    }

    private void UnAssignOrderList() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_loader, new UnAssignOrderList(), "UnAssignOrderList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("UnAssignOrderList")
                .commit();

    }

    private void LoadChatList() {

        FirebaseChat firebaseChat = new FirebaseChat();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", Constant.DataGetValue(getApplicationContext(), "admin_uid"));
        firebaseChat.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_loader, firebaseChat, "LoadChatList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("LoadChatList")
                .commit();
    }

    public void loadInfoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, new FragmentInfoScreen(), "FragmentInfoScreen")
                .addToBackStack("Info_Screen")
                .commit();
    }

    public void loadLanguageFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, new FragmentLanguage(), "FragmentLanguageScreen")
                .addToBackStack("Language")
                .commit();
    }

    public void loadCustomerInfoFragment() {
        getSupportActionBar().setTitle("Driver Info");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, new Fragment_customer_detail(), "FragmentCustomerInfoScreen")
                .addToBackStack("Customer_Info")
                .commit();
    }

    @Override
    public void loadForgetPassword() {

    }

    public void loadCustomerLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, new FragmentLogin(), "FragmentCustomerLogin")
                .addToBackStack("Customer_login")
                .commit();
    }

    @Override
    public void loadDeliveryHistory(String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter_id", filter);
        Fragment_delivery_history fragment_delivery_history = new Fragment_delivery_history();
        fragment_delivery_history.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, fragment_delivery_history, "FragmentDeliveryHistory")
                .addToBackStack("Delivery_History")
                .commit();
    }

    @Override
    public void loadDeliveryHistoryDetail(String delivery_id) {

    }

    @Override
    public void loadCustomerSignature(String delivery_id, String customer_name) {
        Bundle bundle = new Bundle();
        bundle.putString("delivery_id", delivery_id);
        bundle.putString("customer_name", customer_name);
        Fragment_custom_signature fragment_custom_signature = new Fragment_custom_signature();
        fragment_custom_signature.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, fragment_custom_signature, "fragment_custom_signature")
                .addToBackStack("Customer_signature")
                .commit();
    }

    @Override
    public void close() {

    }

    @Override
    public void loadLocation() {
        Live_Location live_location = new Live_Location();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, live_location, "live_location")
                .addToBackStack("live_location")
                .commit();
    }

    @Override
    public void earningHistory() {
        EarningHistory earningHistory = new EarningHistory();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.delivery_loader, earningHistory, "earningHistory")
                .addToBackStack("earningHistory")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (load_type == 1) {
                loadDialog();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            if (load_type == 1) {
                loadDialog();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.txt_order_conformation));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, arg1) -> toHome());
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void toHome() {
        Intent intent = new Intent(ActivityContainerWithTB.this, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                    "ar".equals(AppLanguageSupport.getLanguage(ActivityContainerWithTB.this)) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
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
        }
    }
}
