package ordenese.rider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.LabeledSwitch;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.AppContext;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DataParser;
import ordenese.rider.R;
import ordenese.rider.Service.GoogleService;
import ordenese.rider.firebase_chat.Chat;
import ordenese.rider.firebase_chat.Users;
import ordenese.rider.fragments.static_screen.Dialog_language;
import ordenese.rider.model.AutoAssignDataSet;
import ordenese.rider.model.Delivery_Detail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AppContext Application;
    static ApiInterface apiInterface;
    RecyclerView recyclerView;
    Delivery_Detail_Adapter delivery_detail_adapter;
    ArrayList<Delivery_Detail> mHome_orders_list = new ArrayList<>();
    TextView tv_empty;
    ArrayList<AutoAssignDataSet> autoAssignDataSets = new ArrayList<>();
    boolean network;
    TextView shift_title;
    LabeledSwitch shift_switch;
    private int USER_LOCATION_PERMISSION_CODE = 41;
    DialogInterface dialogInterface;
    Menu menu_temp;
    NavigationView navigationView;
    View headerView;
    Boolean check = false;

    private DatabaseReference mFireBaseDatabase_Busy;
    private String mDriverId_Busy;
    private ProgressDialog mProgressDialog;

    DatabaseReference reference;
    Activity activity;
    ArrayList<Users> usersArrayList = new ArrayList<>();
    DatabaseReference mDatabase_loc;
    DataSnapshot mDataSnapshot_loc;

    public NavigationActivity(AppContext appContext) {
        this.Application = appContext;
    }

    public NavigationActivity() {

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
                    "sv".equals(AppLanguageSupport.getLanguage(NavigationActivity.this)) ?
                            View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppContext.getAppContext();
        activity = this;

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_please_wait));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Constant.DriverStatus_ = Constant.DataGetValue(this, Constant.DriverStatus);
        Constant.Driver_Token_ = Constant.DataGetValue(this, Constant.Driver_Token);

//        Button crashButton = findViewById(R.id.btn);
//        crashButton.setText("Test Crash");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });

        recyclerView = findViewById(R.id.recyclerView);
        tv_empty = findViewById(R.id.tv_empty);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
//        toolbar.setLogo(activity.getDrawable(R.drawable.ordenes_partner_logo));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        shift_switch = headerView.findViewById(R.id.shift_switch);
        shift_title = headerView.findViewById(R.id.shift_title);

        if (Constant.DataGetValue(NavigationActivity.this, Constant.CustomerDetails) != null) {
            shift_switch.setOnToggledListener((buttonView, isChecked) -> {
                if (isChecked) {
                    //shift on :-
                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    JSONObject object = new JSONObject();

                    try {
                        object.put("shift_status", "1");
                        object.put("language_id", String.valueOf(Constant.current_language_id()));

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                        Call<String> On = apiInterface.shift_change(Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Token), body);
                        On.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful()) {
                                    startLocationUpdates();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                        Constant.loadToastMessage(NavigationActivity.this, jsonObject1.getString("message"));
                                        Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, jsonObject.getString("shift_status"));

                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("drivers");
                                        String Id = Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Id);
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(Id)) {
                                                    mDatabase.child(Id).child("shift").setValue("1");
                                                    //  LoadDeliveryMapDetails();
                                                } else {
                                                    mDatabase.child(Id).child("shift").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //shift off :-
                    apiInterface = ApiClient.getClient().create(ApiInterface.class);

                    JSONObject object = new JSONObject();
                    try {
                        object.put("shift_status", "0");
                        object.put("language_id", String.valueOf(Constant.current_language_id()));
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                        Call<String> On = apiInterface.shift_change(Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Token), body);
                        On.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful()) {
                                    stopLocationUpdates();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                        Constant.loadToastMessage(NavigationActivity.this, jsonObject1.getString("message"));
                                        Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, jsonObject.getString("shift_status"));

                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("drivers");
                                        String Id = Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Id);
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getKey().equals(Id)) {
                                                    mDatabase.child(Id).child("shift").setValue("0");
                                                    //  LoadDeliveryMapDetails();
                                                } else {
                                                    mDatabase.child(Id).child("shift").setValue("0");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(NavigationActivity.this));

        statusCheck();
        LoadDriverStatus();
    }


    private void autoAssignApiCall(Menu menu) {

        network = Constant.isNetworkOnline(activity);
        if (!Constant.DataGetValue(activity, Constant.DriverStatus).equals("empty") &&
                !Constant.DataGetValue(activity, Constant.DriverStatus).equals("0")) {
            if (network) {
                JSONObject object = new JSONObject();
                try {
                    object.put("page", "1");
                    object.put("page_per_unit", "5");

                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                    Call<String> call = apiInterface.order_unassigned(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful()) {
                                autoAssignDataSets = DataParser.autoAssignDataSets(response.body());
                                if (autoAssignDataSets != null && autoAssignDataSets.size() != 0) {
                                    menu.findItem(R.id.notification_layout).setVisible(true);
                                    menu.findItem(R.id.notification).setVisible(false);
                                } else {
                                    menu.findItem(R.id.notification_layout).setVisible(false);
                                    menu.findItem(R.id.notification).setVisible(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Constant.loadInternetAlert(getSupportFragmentManager());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu_temp = menu;

        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_item).setVisible(false);
//        update(menu);
//        getNotification(menu);

        unassigned(menu);
        autoAssignApiCall(menu);

        return true;
    }

    private void unassigned(Menu menu) {
        View view = menu.findItem(R.id.notification_layout).getActionView();
        ImageView t1 = view.findViewById(R.id.image_view);
        TextView t2 = view.findViewById(R.id.count_value);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(activity, Constant.DriverStatus).equals("empty") &&
                        !Constant.DataGetValue(activity, Constant.DriverStatus).equals("0")) {
                    Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
                    intent.putExtra("type", "UnAssignOrderList");
                    startActivity(intent);
                } else {
                    Constant.loadToastMessage(activity, getResources().getString(R.string.please_shift_login_to_proceed_further));
                }
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(activity, Constant.DriverStatus).equals("empty") &&
                        !Constant.DataGetValue(activity, Constant.DriverStatus).equals("0")) {
                    Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
                    intent.putExtra("type", "UnAssignOrderList");
                    startActivity(intent);
                } else {
                    Constant.loadToastMessage(activity, getResources().getString(R.string.please_shift_login_to_proceed_further));
                }
            }
        });
    }

    private void update(Menu menu) {
        View view = menu.findItem(R.id.with_notify).getActionView();
        ImageView t1 = view.findViewById(R.id.cart_image_view);
        TextView t2 = view.findViewById(R.id.cart_count_value);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("") ||
                        !Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("empty")) {
                    Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
                    intent.putExtra("type", "LoadUsersList");
                    startActivity(intent);
                }
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("") ||
                        !Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("empty")) {
                    Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
                    intent.putExtra("type", "LoadUsersList");
                    startActivity(intent);
                }
            }
        });
    }

    private void getNotification(Menu menu) {
        menu.findItem(R.id.menu_item).setVisible(false);
        reference = FirebaseDatabase.getInstance().getReference("messages").child(Constant.DataGetValue(getApplicationContext(), "rider_uid"));
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey() != null) {
                            reference = FirebaseDatabase.getInstance().getReference("users_list").child(dataSnapshot.getKey());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (!Objects.equals(dataSnapshot.getKey(), Constant.DataGetValue(getApplicationContext(), "rider_uid"))) {
                                            Users users = snapshot.getValue(Users.class);
                                            usersArrayList.add(users);
                                            for (int i = 0; i < usersArrayList.size(); i++) {
                                                if (usersArrayList.get(i).getuid() != null) {
                                                    DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("messages").child(Constant.DataGetValue(activity, "rider_uid")).child(usersArrayList.get(i).getuid());
                                                    referenceChat.addValueEventListener(new ValueEventListener() {
                                                        @SuppressLint("NotifyDataSetChanged")
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            int count = 0;
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                if (snapshot.getValue() != null) {
                                                                    Chat chat = snapshot.getValue(Chat.class);
                                                                    if (chat != null) {
                                                                        if (chat.getSeen() != null) {
                                                                            if (chat.getSeen().equals("false")) {
                                                                                count = count + 1;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (count == 0) {
                                                                        menu.findItem(R.id.menu_item).setVisible(true);
                                                                        menu.findItem(R.id.with_notify).setVisible(false);
                                                                    } else {
                                                                        menu.findItem(R.id.menu_item).setVisible(false);
                                                                        menu.findItem(R.id.with_notify).setVisible(true);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item) {
            if (!Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("") ||
                    !Constant.DataGetValue(getApplicationContext(), "rider_uid").equals("empty")) {
                Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
                intent.putExtra("type", "LoadUsersList");
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.notification) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "UnAssignOrderList");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public void statusCheck() {
        if (ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            askPermission();
        } else {

//            if (ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                askBGPermission();
//            }

            if (dialogInterface != null)
                dialogInterface.cancel();
        }
    }

    private void askPermission() {

        if (ContextCompat.checkSelfPermission(NavigationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NavigationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.please_allow_the_device_location_access)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(NavigationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    USER_LOCATION_PERMISSION_CODE);

                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NavigationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        USER_LOCATION_PERMISSION_CODE);
            }
        }

    }

    private void askBGPermission() {

        if (ContextCompat.checkSelfPermission(NavigationActivity.this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NavigationActivity.this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.please_allow_the_device_background_location_access)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(NavigationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    42);

                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NavigationActivity.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        42);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 41:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    askPermission();
                }
                break;
//            case 42:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    askBGPermission();
//                }
//                break;
            case 100:
        }
    }

    private void LoadDriverStatus() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Query DriverDetailQuery = mDatabase.child("drivers");
        String Id = Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Id);
        DriverDetailQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(Id)) {
                    String Shift_Status = dataSnapshot.child("shift").getValue(String.class);
                    if (Shift_Status != null) {
                        if (Shift_Status.length() > 0) {
                            if (Shift_Status.equals("1")) {
                                startLocationUpdates();
                                shift_switch.setOn(true);
                                //scheduleDriverLocation();
                                Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, "1");
//                                AppContext.driver_location();
                            } else {
                                stopLocationUpdates();
                                shift_switch.setOn(false);
                                Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, "0");
//                                AppContext.stop_update();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(Id)) {
                    String Shift_Status = dataSnapshot.child("shift").getValue(String.class);
                    if (Shift_Status != null) {
                        if (Shift_Status.length() > 0) {
                            if (Shift_Status.equals("1")) {
                                startLocationUpdates();
                                shift_switch.setOn(true);
                                //scheduleDriverLocation();
                                Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, "1");
//                                AppContext.driver_location();
                            } else {
                                stopLocationUpdates();
                                shift_switch.setOn(false);
                                Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, "0");
//                                AppContext.stop_update();
                            }
                        }

                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startLocationUpdates() {
        check = true;
        Intent intent = new Intent(NavigationActivity.this, GoogleService.class);
        startService(intent);
    }

    private void stopLocationUpdates() {
        if (check) {
            check = false;
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        }
    }

    public void current_loc_update(Double latitude, Double longitude) {

        if (Constant.DataGetValue(activity, Constant.DriverStatus).equals("1")) {
            if (FirebaseDatabase.getInstance().getReference("drivers") != null) {
                String Id = Constant.DataGetValue(activity, Constant.Driver_Id);
                mDatabase_loc = FirebaseDatabase.getInstance().getReference("drivers");
                mDatabase_loc.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDataSnapshot_loc = dataSnapshot;
                        if (mDatabase_loc != null) {
                            if (latitude != 0.0 && longitude != 0.0) {

                                Log.e("onDataChange:3 ", latitude + "");
                                Log.e("onDataChange:4 ", longitude + "");

                                if (mDataSnapshot_loc.hasChild(Id)) {
                                    mDatabase_loc.child(Id).child("latitude").setValue(String.valueOf(latitude));
                                    mDatabase_loc.child(Id).child("longitude").setValue(String.valueOf(longitude));
                                    mDatabase_loc.child(Id).child("name").setValue(Constant.DataGetValue(activity, Constant.DriverName));
                                    // mDatabase_loc.child(Id).child("busy").setValue("1");
                                    mDatabase_loc.child(Id).child("telephone").setValue(Constant.DataGetValue(activity, Constant.DriverMobile));
                                } else {
                                    mDatabase_loc.child(Id).child("latitude").setValue(latitude);
                                    mDatabase_loc.child(Id).child("longitude").setValue(longitude);
                                    mDatabase_loc.child(Id).child("name").setValue(Constant.DataGetValue(activity, Constant.DriverName));
                                    //  mDatabase_loc.child(Id).child("busy").setValue("1");
                                    mDatabase_loc.child(Id).child("telephone").setValue(Constant.DataGetValue(activity, Constant.DriverMobile));
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("onCancelled: ", databaseError.getMessage() + "");
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void checkGpsLocationEnable() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(NavigationActivity.this, 100);
                        } catch (IntentSender.SendIntentException e) {
                            //   Log.d("test", e.getMessage());
                        } catch (ClassCastException e) {
                            //   Log.d("test", e.getMessage());
                        }
                        break;
                }
            }
        });
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double latitude = Double.valueOf(intent.getStringExtra("latitude"));
            Double longitude = Double.valueOf(intent.getStringExtra("longitude"));
            current_loc_update(latitude, longitude);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LoadData();

        //If AutoAssignApiCallDB has entry means, the autoAssignApiCall() method call already tried for call but at
        // the time the app  is closed/added in background tray so that cant able to call autoAssignApiCall() to show
        // accept / reject dialogue box.So now we try to continue the autoAssignApiCall() method call.

        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

        checkGpsLocationEnable();

        if (Constant.DataGetValue(NavigationActivity.this, Constant.DriverDataUpdated) != null &&
                !Constant.DataGetValue(NavigationActivity.this, Constant.DriverDataUpdated).isEmpty() &&
                !Constant.DataGetValue(NavigationActivity.this, Constant.DriverDataUpdated).equals("empty")) {

            TextView drawerUsername = headerView.findViewById(R.id.name);
            TextView drawerAccount = headerView.findViewById(R.id.phone);

            CircleImageView drewerImage = headerView.findViewById(R.id.image_category);

            drawerUsername.setText(Constant.DataGetValue(NavigationActivity.this, Constant.DriverName));
            drawerAccount.setText(Constant.DataGetValue(NavigationActivity.this, Constant.DriverMobile));

            if (Constant.DataGetValue(NavigationActivity.this, Constant.DriverImgUrl) != null && !Constant.DataGetValue(NavigationActivity.this, Constant.DriverImgUrl).isEmpty()) {
                Constant.glide_image_loader(Constant.DataGetValue(NavigationActivity.this, Constant.DriverImgUrl), drewerImage);
            } else {
                Picasso.with(NavigationActivity.this).load(R.drawable.no_image).into(drewerImage);
            }
        }

        if (mHome_orders_list != null) {
            if (mHome_orders_list.size() < 2) {
                updateDriverBusyStatus("0");
            }
        } else {
            updateDriverBusyStatus("0");
        }

        if (menu_temp != null) {
            autoAssignApiCall(menu_temp);
        }

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }

    private void updateDriverBusyStatus(String status) {

        mFireBaseDatabase_Busy = FirebaseDatabase.getInstance().getReference("drivers");
        mDriverId_Busy = Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Id);
        mFireBaseDatabase_Busy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mDriverId_Busy)) {
                    mFireBaseDatabase_Busy.child(mDriverId_Busy).child("busy").setValue(status);
                } else {
                    mFireBaseDatabase_Busy.child(mDriverId_Busy).child("busy").setValue(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void LoadData() {

        mProgressDialog.show();
        mHome_orders_list.clear();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Query myDeliveryPostsQuery = mDatabase.child("tasks");
        String Id = Constant.DataGetValue(NavigationActivity.this, Constant.Driver_Id);

       /*  myDeliveryPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null) {
                        if (mHome_orders_list != null) {
                            mHome_orders_list.clear();
                        }
                        DatabaseReference myDeliveryPosts = FirebaseDatabase.getInstance().getReference().child("tasks").child(dataSnapshot.getKey());
                        myDeliveryPosts.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    HashMap<String, Object> stringStringHashMap = (HashMap<String, Object>) snapshot.getValue();
                                    if (stringStringHashMap.get("driver_id") != null) {
                                        if (Objects.equals(stringStringHashMap.get("driver_id"), Id)) {
                                            if (stringStringHashMap.get("status") != null) {
                                                if (Objects.equals(stringStringHashMap.get("status"), "0")) {
                                                    if (stringStringHashMap.get("order_status_id") != null) {
                                                        if (!Objects.equals(stringStringHashMap.get("order_status_id"), "9")) {
                                                            Delivery_Detail delivery_detail = new Delivery_Detail();
                                                            delivery_detail.setOrder_id(snapshot.getKey());
                                                            for (Map.Entry m : stringStringHashMap.entrySet()) {
                                                                if (m.getKey() != null && m.getValue() != null) {
                                                                    if (m.getKey().equals("marker_d_address")) {
                                                                        delivery_detail.setDelivery_address(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("marker_p_address")) {
                                                                        delivery_detail.setPickup_address(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("task_date_added")) {
                                                                        delivery_detail.setDelivery_date(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("contactless_delivery")) {
                                                                        delivery_detail.setDelivery_type(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("total")) {
                                                                        delivery_detail.setOrder_total(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("order_status")) {
                                                                        delivery_detail.setStatus(String.valueOf(m.getValue()));
                                                                    } else if (m.getKey().equals("marker_d_flat")) {
                                                                        delivery_detail.setFlatNo(String.valueOf(m.getValue()));
                                                                    }
                                                                }
                                                            }
                                                            mHome_orders_list.add(delivery_detail);
                                                        }
                                                    } else {
                                                        Delivery_Detail delivery_detail = new Delivery_Detail();
                                                        delivery_detail.setOrder_id(snapshot.getKey());
                                                        for (Map.Entry m : stringStringHashMap.entrySet()) {
                                                            if (m.getKey() != null && m.getValue() != null) {
                                                                if (m.getKey().equals("marker_d_address")) {
                                                                    delivery_detail.setDelivery_address(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("marker_p_address")) {
                                                                    delivery_detail.setPickup_address(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("task_date_added")) {
                                                                    delivery_detail.setDelivery_date(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("contactless_delivery")) {
                                                                    delivery_detail.setDelivery_type(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("total")) {
                                                                    delivery_detail.setOrder_total(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("order_status")) {
                                                                    delivery_detail.setStatus(String.valueOf(m.getValue()));
                                                                } else if (m.getKey().equals("marker_d_flat")) {
                                                                    delivery_detail.setFlatNo(String.valueOf(m.getValue()));
                                                                }
                                                            }
                                                        }
                                                        mHome_orders_list.add(delivery_detail);
                                                    }
                                                }
                                                delivery_detail_adapter = new Delivery_Detail_Adapter();
                                                recyclerView.setAdapter(delivery_detail_adapter);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        myDeliveryPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String driver_id = String.valueOf(dataSnapshot.child("driver_id").getValue());
                if (dataSnapshot.child("status").getValue() != null) {
                    if (driver_id.equals(Id)) {
                        if (dataSnapshot.child("status").getValue().equals("1")) {
                            if (dataSnapshot.child("order_status_id").getValue() != null) {
                                if (!Objects.equals(dataSnapshot.child("order_status_id").getValue(), "9")) {
                                    Delivery_Detail delivery_detail = new Delivery_Detail();
                                    delivery_detail.setOrder_id(dataSnapshot.getKey());
                                    delivery_detail.setDelivery_address(dataSnapshot.child("marker_d_address").getValue(String.class));
                                    delivery_detail.setPickup_address(dataSnapshot.child("marker_p_address").getValue(String.class));
                                    delivery_detail.setDelivery_date(dataSnapshot.child("task_date_added").getValue(String.class));
                                    delivery_detail.setDelivery_type(dataSnapshot.child("contactless_delivery").getValue(String.class));

                                    delivery_detail.setOrder_total(dataSnapshot.child("total").getValue(String.class));
                                    delivery_detail.setStatus(dataSnapshot.child("order_status").getValue(String.class));

                                    if (dataSnapshot.child("marker_d_flat").getValue(String.class) != null) {
                                        delivery_detail.setFlatNo(dataSnapshot.child("marker_d_flat").getValue(String.class));
                                    }
                                    mHome_orders_list.add(delivery_detail);
                                }
                            } else {
                                Delivery_Detail delivery_detail = new Delivery_Detail();
                                delivery_detail.setOrder_id(dataSnapshot.getKey());
                                delivery_detail.setDelivery_address(dataSnapshot.child("marker_d_address").getValue(String.class));
                                delivery_detail.setPickup_address(dataSnapshot.child("marker_p_address").getValue(String.class));
                                delivery_detail.setDelivery_date(dataSnapshot.child("task_date_added").getValue(String.class));
                                delivery_detail.setDelivery_type(dataSnapshot.child("contactless_delivery").getValue(String.class));

                                delivery_detail.setOrder_total(dataSnapshot.child("total").getValue(String.class));
                                delivery_detail.setStatus(dataSnapshot.child("order_status").getValue(String.class));

                                if (dataSnapshot.child("marker_d_flat").getValue(String.class) != null) {
                                    delivery_detail.setFlatNo(dataSnapshot.child("marker_d_flat").getValue(String.class));
                                }
                                mHome_orders_list.add(delivery_detail);
                            }
                            delivery_detail_adapter = new Delivery_Detail_Adapter();
                            recyclerView.setAdapter(delivery_detail_adapter);
                        }
                    } else {
                        mProgressDialog.cancel();
                    }
                } else {
                    mProgressDialog.cancel();
                }
//                mCount++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //  Log.e("onChildChanged: ", "yes1");

                if (mHome_orders_list != null) {
                    if (mHome_orders_list.size() > 0) {
                        for (int i = 0; i < mHome_orders_list.size(); i++) {
                            if (mHome_orders_list.get(i).getOrder_id().equalsIgnoreCase(dataSnapshot.getKey())) {
                                mHome_orders_list.remove(i);
                                delivery_detail_adapter = new Delivery_Detail_Adapter();
                                recyclerView.setAdapter(delivery_detail_adapter);
                            }
                        }
                    }
                }

                String driver_id = String.valueOf(dataSnapshot.child("driver_id").getValue());

                if (dataSnapshot.child("status").getValue() != null) {
                    if (driver_id.equals(Id)) {
                        if (dataSnapshot.child("status").getValue().equals("1")) {
                            if (dataSnapshot.child("order_status_id").getValue() != null) {
                                if (!Objects.equals(dataSnapshot.child("order_status_id").getValue(), "9")) {
                                    Delivery_Detail delivery_detail = new Delivery_Detail();
                                    delivery_detail.setOrder_id(dataSnapshot.getKey());
                                    delivery_detail.setDelivery_address(dataSnapshot.child("marker_d_address").getValue(String.class));
                                    delivery_detail.setPickup_address(dataSnapshot.child("marker_p_address").getValue(String.class));
                                    delivery_detail.setDelivery_date(dataSnapshot.child("task_date_added").getValue(String.class));
                                    delivery_detail.setDelivery_type(dataSnapshot.child("contactless_delivery").getValue(String.class));

                                    delivery_detail.setOrder_total(dataSnapshot.child("total").getValue(String.class));
                                    delivery_detail.setStatus(dataSnapshot.child("order_status").getValue(String.class));

//                                    if (dataSnapshot.child("marker_d_flat").getValue(String.class) != null) {
//                                        delivery_detail.setFlatNo(dataSnapshot.child("marker_d_flat").getValue(String.class));
//                                    }
                                    mHome_orders_list.add(delivery_detail);
                                }
                            } else {
                                Delivery_Detail delivery_detail = new Delivery_Detail();
                                delivery_detail.setOrder_id(dataSnapshot.getKey());
                                delivery_detail.setDelivery_address(dataSnapshot.child("marker_d_address").getValue(String.class));
                                delivery_detail.setPickup_address(dataSnapshot.child("marker_p_address").getValue(String.class));
                                delivery_detail.setDelivery_date(dataSnapshot.child("task_date_added").getValue(String.class));
                                delivery_detail.setDelivery_type(dataSnapshot.child("contactless_delivery").getValue(String.class));

                                delivery_detail.setOrder_total(dataSnapshot.child("total").getValue(String.class));
                                delivery_detail.setStatus(dataSnapshot.child("order_status").getValue(String.class));

//                                if (dataSnapshot.child("marker_d_flat").getValue(String.class) != null) {
//                                    delivery_detail.setFlatNo(dataSnapshot.child("marker_d_flat").getValue(String.class));
//                                }
                                mHome_orders_list.add(delivery_detail);
                            }
                            delivery_detail_adapter = new Delivery_Detail_Adapter();
                            recyclerView.setAdapter(delivery_detail_adapter);
                        }
                    } else {
                        mProgressDialog.cancel();
                    }
                } else {
                    mProgressDialog.cancel();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //  Log.e("onChildRemoved: ", "yes3");
//                Log.e("onChildRemoved: ", "" + dataSnapshot);
                if (mHome_orders_list != null) {
                    if (mHome_orders_list.size() > 0) {
                        for (int i = 0; i < mHome_orders_list.size(); i++) {
                            if (mHome_orders_list.get(i).getOrder_id().equalsIgnoreCase(dataSnapshot.getKey())) {
                                mHome_orders_list.remove(i);
                                delivery_detail_adapter = new Delivery_Detail_Adapter();
                                recyclerView.setAdapter(delivery_detail_adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.cancel();
            }
        });

        mProgressDialog.cancel();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_delivery_history) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "Delivery_history");
            intent.putExtra("filter", "3");
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "Setting");
            startActivity(intent);
        } else if (id == R.id.nav_support) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "LoadChatList");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {

            logout();

        } else if (id == R.id.nav_language) {
            Dialog_language fragment = new Dialog_language();
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            fragment.show(getSupportFragmentManager(), "forgot");
        } else if (id == R.id.nav_live_location) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "Location");
            startActivity(intent);
        } else if (id == R.id.earning_history) {
            Intent intent = new Intent(NavigationActivity.this, ActivityContainerWithTB.class);
            intent.putExtra("type", "EarningHistory");
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {

        JSONObject object = new JSONObject();
        try {
            if (OneSignal.getDeviceState() != null && OneSignal.getDeviceState().getUserId() != null) {
                object.put("push_id", OneSignal.getDeviceState().getUserId());
            } else {
                object.put("push_id", "");
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<String> call = apiInterface.log_out(Constant.DataGetValue(activity, Constant.Driver_Token), body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (!jsonObject.isNull("success")) {
                                stopLocationUpdates();

                                JSONObject object1 = jsonObject.getJSONObject("success");
                                Constant.loadToastMessage(activity, object1.getString("message"));

                                Intent intent = new Intent(NavigationActivity.this, ActivitySplashScreen.class);
                                Constant.DataStoreValue(NavigationActivity.this, Constant.DriverStatus, "0");
                                Constant.DataRemoveValue(NavigationActivity.this, Constant.Driver_Token);
                                Constant.DataRemoveValue(NavigationActivity.this, Constant.CustomerDetails);

                                Constant.DataRemoveValue(NavigationActivity.this, "rider_uid");
                                Constant.DataRemoveValue(NavigationActivity.this, "admin_uid");
                                Constant.DataRemoveValue(NavigationActivity.this, "token_notify");

                                Constant.login = false;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else if (!jsonObject.isNull("error")) {
                                JSONObject object1 = jsonObject.getJSONObject("error");
                                Constant.loadToastMessage(activity, object1.getString("message"));
                            }
                        } catch (JSONException e) {
                            Constant.loadToastMessage(activity, getResources().getString(R.string.process_failed_please_try_again));
                        }
                    }
                    mProgressDialog.cancel();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Constant.loadToastMessage(activity, getResources().getString(R.string.process_failed_please_try_again));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Constant.loadToastMessage(activity, getResources().getString(R.string.process_failed_please_try_again));
        }

    }

    private void mLogE(String title, String msg) {
//        Log.e(title, msg);
    }

    private void mLogD(String title, String msg) {
        // Log.d("*******************","************************");
        // Log.d(title,msg);
        //Log.d("*******************","************************");
    }

    class Delivery_Detail_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Delivery_Detail> Delivery_detail;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;

        Delivery_Detail_Adapter() {
            this.Delivery_detail = mHome_orders_list;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }

                }
            });

            //For this scenario, If the driver has 2 orders then the busy status is 1.That means driver busy currently.
            //If the driver has 0 or 1 order then the driver ready to receive the order up to maximum of TWO orders.
            if (Delivery_detail != null) {
                if (Delivery_detail.size() == 2) {
                    updateDriverBusyStatus("1");
                } else if (Delivery_detail.size() < 2) {
                    updateDriverBusyStatus("0");
                }
            } else {
                updateDriverBusyStatus("0");
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 2) {
                view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.fragment_home_page, parent, false);
                return new ViewHolderHome(view);
            } else {
                view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.rc_empty_row, parent, false);
                return new ViewHolderEmpty(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                ViewHolderHome holder1 = (ViewHolderHome) holder;
                holder1.order_id.setText(getString(R.string.txt_order_id) + Delivery_detail.get(position).getOrder_id());
                holder1.order_total.setText(getString(R.string.txt_order_total) + Delivery_detail.get(position).getOrder_total());
                holder1.delivery_date.setText(getString(R.string.text_order_date) + Delivery_detail.get(position).getDelivery_date());

                if (Delivery_detail.get(position).getDelivery_type().equals("1")) {
                    holder1.delivery_type_linear.setVisibility(View.VISIBLE);
                    holder1.delivery_type.setText(getString(R.string.contactless_delivery));
                } else {
                    holder1.delivery_type_linear.setVisibility(View.GONE);
                }

                if (Delivery_detail.get(position).getStatus().isEmpty() || Delivery_detail.get(position).getStatus().equals("1") ||
                        Delivery_detail.get(position).getStatus().equals("0")) {
                    holder1.staus.setVisibility(View.GONE);
                } else {
                    holder1.staus.setVisibility(View.VISIBLE);
                    holder1.staus.setText(Delivery_detail.get(position).getStatus());
                }

                holder1.pickup_address.setText(Delivery_detail.get(position).getPickup_address());
                holder1.delivery_address.setText(Delivery_detail.get(position).getDelivery_address());
                holder1.flatNo.setVisibility(View.GONE);
//                holder1.flatNo.setText(Delivery_detail.get(position).getFlatNo());
//                holder1.mlinearLayout.setOnClickListener(v -> LoadMapActivity(Delivery_detail.get(position).getOrder_id()));
                holder1.view_details.setOnClickListener(v -> LoadMapActivity(Delivery_detail.get(position).getOrder_id()));
            } else {
                ViewHolderEmpty viewHolderEmpty = (ViewHolderEmpty) holder;
                viewHolderEmpty.tv_MenuListEmpty.setText(getString(R.string.empty));
            }
            mProgressDialog.cancel();
        }

        private void LoadMapActivity(String delivery_id) {
            Intent ii = new Intent(NavigationActivity.this, GoogleMapsActivity.class);
            ii.putExtra("Delivery_id", delivery_id);
            startActivity(ii);
        }

        @Override
        public int getItemCount() {
            if (Delivery_detail != null) {
                if (Delivery_detail.size() > 0) {
                    return Delivery_detail.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (Delivery_detail != null) {
                if (Delivery_detail.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        void setLoaded() {
            isLoading = false;
        }

        void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }

        private class ViewHolderHome extends RecyclerView.ViewHolder {
            TextView order_id, order_total, delivery_date, staus,
                    pickup_address, delivery_address, delivery_type, flatNo, view_details;
            LinearLayout mlinearLayout, delivery_type_linear;

            ViewHolderHome(View itemView) {
                super(itemView);
                order_id = itemView.findViewById(R.id.m_order_id);
                order_total = itemView.findViewById(R.id.m_order_total);
                delivery_date = itemView.findViewById(R.id.m_delivery_date);
                delivery_type = itemView.findViewById(R.id.delivery_type);
                delivery_type_linear = itemView.findViewById(R.id.delivery_type_linear);
                staus = itemView.findViewById(R.id.m_status);
                pickup_address = itemView.findViewById(R.id.m_pickup_address);
                delivery_address = itemView.findViewById(R.id.m_delivery_address);
                flatNo = itemView.findViewById(R.id.flat_no);

                mlinearLayout = itemView.findViewById(R.id.linearLayout);
                view_details = itemView.findViewById(R.id.view_details);
            }
        }

        private class ViewHolderEmpty extends RecyclerView.ViewHolder {
            TextView tv_MenuListEmpty;

            ViewHolderEmpty(View view) {
                super(view);
                tv_MenuListEmpty = view.findViewById(R.id.tv_empty);
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
