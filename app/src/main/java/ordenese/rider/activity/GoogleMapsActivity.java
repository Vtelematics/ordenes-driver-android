package ordenese.rider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.LabeledSwitch;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.AppContext;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DirectionsJSONParser;
import ordenese.rider.Common.GpsLocationTracker;
import ordenese.rider.R;
import ordenese.rider.fragments.account.Fragment_cancel_order;
import ordenese.rider.fragments.account.ScrollMaps;
import ordenese.rider.model.Delivery_Detail;
import ordenese.rider.model.Delivery_product_details;
import ordenese.rider.model.Delivery_product_total;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String mOrderId;
    ApiInterface apiInterface;
    ImageView restaurant_contact, customer_contact, restaurant_whatsapp, customer_whatsapp;
    TextView order_id, delivery_date, restaurant_mobile, customer_mobile, staus, pickup_address, contactless_delivery,
            delivery_address, restaurant_name, customer_name, payment, tv_payment_method, mTrack, flatNo, customer_comment;
    RecyclerView recyclerViewProduct, recyclerViewTotal;
    ArrayList<Delivery_Detail> delivery_details = new ArrayList<>();
    LabeledSwitch btn_switch, btn_out_of_delivery, mBtn_delivered;
    LinearLayout progress_bar_loader;
    ScrollView mscrollView;
    ImageView btn_change_view_sv, btn_change_view_nv, btn_current_location, btn_collapse, btn_expand;
    private String Customer_Mobile_number, Restaurant_Mobile_number, order_status_id = "";
    boolean network;
    private static final int PERMISSION_REQUEST_CODE = 1;
    FrameLayout frameLayout;
//    private String Delivery_geo, Pickup_geo;

    String pickup_latitude, pickup_longitude, delivery_latitude, delivery_longitude;
    private boolean isActive = false;
    private int USER_LOCATION_PERMISSION_CODE = 41;
    ArrayList<Location> mMovementLocationList;
    private double mBearing;
    LatLng mMapCenter;
    private double mDistanceTraveled;
    private Marker mMarker;
    Location mDriverLocation;
    AlertDialog.Builder mAlertDialog;
    DialogInterface dialogInterface;
    AlertDialog mcreateDialog;

    CameraUpdate cu;
    LatLngBounds bounds;

    TextView mPreparingTime;
    Activity activity;

    public GoogleMapsActivity() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        askPermission();
        mPreparingTime = findViewById(R.id.preparing_time);

//        image_customer = findViewById(R.id.image_customer);
//        image_restaurant = findViewById(R.id.image_restaurant);
        order_id = findViewById(R.id.order_id);
//        order_total = findViewById(R.id.m_order_total);
        delivery_date = findViewById(R.id.pickup_time);
        restaurant_mobile = findViewById(R.id.restaurant_phone);
        customer_mobile = findViewById(R.id.customer_phone);
        restaurant_name = findViewById(R.id.restaurant_name);
        customer_name = findViewById(R.id.customer_name);
//        speed = findViewById(R.id.m_speed);
        staus = findViewById(R.id.status);
        pickup_address = findViewById(R.id.pickup_address);
        delivery_address = findViewById(R.id.delivery_address);
        payment = findViewById(R.id.tv_payment);

        btn_switch = findViewById(R.id.btn_pickup);
        btn_out_of_delivery = findViewById(R.id.btn_out_of_delivery);
        mBtn_delivered = findViewById(R.id.btn_delivered_the_order);

        flatNo = findViewById(R.id.customer_flat_no);

        recyclerViewProduct = findViewById(R.id.recyclerViewProduct);
        recyclerViewTotal = findViewById(R.id.recyclerViewTotal);
        progress_bar_loader = findViewById(R.id.progress_bar_loader);
        btn_change_view_sv = findViewById(R.id.btn_change_view_sv);
        btn_change_view_nv = findViewById(R.id.btn_change_view_nv);
        btn_current_location = findViewById(R.id.btn_current_location);
        btn_expand = findViewById(R.id.btn_expand);
        btn_collapse = findViewById(R.id.btn_collapse);
        frameLayout = findViewById(R.id.frame_layout_map);
        restaurant_contact = findViewById(R.id.restaurant_contact);
        customer_comment = findViewById(R.id.customer_comment);
        customer_contact = findViewById(R.id.customer_contact);
        restaurant_whatsapp = findViewById(R.id.restaurant_whatsapp);
        customer_whatsapp = findViewById(R.id.customer_whatsapp);
        tv_payment_method = findViewById(R.id.tv_payment_method);

        contactless_delivery = findViewById(R.id.contactless_delivery);
        contactless_delivery.setVisibility(View.GONE);

        mTrack = findViewById(R.id.track_location);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        askPermission();
        RunTimeStatusCheck();
        if (b != null) {
            mOrderId = b.getString("Delivery_id");
        }
        mscrollView = findViewById(R.id.scrollView);
        ScrollMaps mapFragment = (ScrollMaps) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LoadDeliveryMapDetails();
            ((ScrollMaps) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(() -> mscrollView.requestDisallowInterceptTouchEvent(true));
        });

        if (Constant.DataGetValue(GoogleMapsActivity.this, Constant.DriverStatus).equals("1")) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mscrollView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 130);
            mscrollView.setLayoutParams(layoutParams);
            btn_switch.setVisibility(View.VISIBLE);
            btn_switch.setOnToggledListener((labeledSwitch, isOn) -> {
                UpdateStatus("8");
            });
        } else {
            btn_switch.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.please_shift_login_to_proceed_further), Toast.LENGTH_SHORT).show();
        }

        btn_out_of_delivery.setOnToggledListener((labeledSwitch, isOn) -> {
            if (Constant.DataGetValue(GoogleMapsActivity.this, Constant.DriverStatus).equals("1")) {
                UpdateStatus("9");
            } else {
                btn_switch.setVisibility(View.GONE);
                Toast.makeText(this, getResources().getString(R.string.please_shift_login_to_proceed_further), Toast.LENGTH_SHORT).show();
            }
        });

        mBtn_delivered.setOnToggledListener((labeledSwitch, isOn) -> {
            if (Constant.DataGetValue(GoogleMapsActivity.this, Constant.DriverStatus).equals("1")) {
                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(GoogleMapsActivity.this);
                alertDialogBuilder1
                        .setMessage(AppContext.getAppContext().getResources().getString(R.string.delivery_complete))
                        //.setTitle(mContext.getString(R.string.))
                        .setCancelable(false)
                        .setPositiveButton(AppContext.getAppContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UpdateStatus("9");
                            }
                        })
                        .setNegativeButton(AppContext.getAppContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //contactless_delivery.set
                                labeledSwitch.setOn(false);
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                alertDialog1.show();
            } else {
                btn_switch.setVisibility(View.GONE);
                Toast.makeText(this, getResources().getString(R.string.please_shift_login_to_proceed_further), Toast.LENGTH_SHORT).show();
            }
        });

        btn_change_view_sv.setOnClickListener(v -> {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            btn_change_view_nv.setVisibility(View.VISIBLE);
            btn_change_view_sv.setVisibility(View.GONE);
        });

        btn_change_view_nv.setOnClickListener(v -> {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            btn_change_view_nv.setVisibility(View.GONE);
            btn_change_view_sv.setVisibility(View.VISIBLE);
        });
        btn_current_location.setOnClickListener(v -> scheduleSendLocation());
        btn_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pixels = 440 * GoogleMapsActivity.this.getResources().getDisplayMetrics().density;
                frameLayout.getLayoutParams().height = (int) pixels;
                btn_expand.setVisibility(View.VISIBLE);
                btn_collapse.setVisibility(View.GONE);
            }
        });
        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pixels = 240 * GoogleMapsActivity.this.getResources().getDisplayMetrics().density;
                frameLayout.getLayoutParams().height = (int) pixels;
                btn_expand.setVisibility(View.GONE);
                btn_collapse.setVisibility(View.VISIBLE);
            }
        });
        restaurant_mobile.setOnClickListener(v -> {
            if (checkPermission()) {
                loadCallIntent(Restaurant_Mobile_number);
            } else {
                requestPermission();
            }

        });

        customer_mobile.setOnClickListener(v -> {
            if (checkPermission()) {
                loadCallIntent(Customer_Mobile_number);
            } else {
                requestPermission();
            }

        });

        restaurant_whatsapp.setOnClickListener(v -> {
            loadWhatsappIntent(Restaurant_Mobile_number);
        });
        customer_whatsapp.setOnClickListener(v -> {
            loadWhatsappIntent(Customer_Mobile_number);
        });
        mTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + delivery_address.getText().toString()));
                    Intent intent;
                    if (order_status_id.equals("8")) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + delivery_latitude + "," + delivery_longitude));
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + pickup_latitude + "," + pickup_longitude));
                    }
                    startActivity(intent);
                } catch (ActivityNotFoundException ane) {
                    Toast.makeText(GoogleMapsActivity.this, "Please Install Google Maps ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void loadWhatsappIntent(String Mobile_number) {

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone=91" + Mobile_number + "&text=" + URLEncoder.encode("", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCallIntent(String Mobile_number) {
        String phone = Mobile_number;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.m_cancel_order) {

            Bundle bundle = new Bundle();
            bundle.putString("delivery_id", mOrderId);
            bundle.putString("order_id", mOrderId);
            Fragment_cancel_order fragment = new Fragment_cancel_order();
            fragment.setArguments(bundle);
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            fragment.show(getSupportFragmentManager(), "cancel_order");
            return true;

        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LoadDeliveryMapDetails() {

        progress_bar_loader.setVisibility(View.GONE);
        network = Constant.isNetworkOnline(GoogleMapsActivity.this);
        if (network) {
            progress_bar_loader.setVisibility(View.VISIBLE);

            try {
                JSONObject object = new JSONObject();
                object.put("order_id", mOrderId);
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<String> call = apiInterface.order_info(Constant.DataGetValue(GoogleMapsActivity.this, Constant.Driver_Token), body);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                        // Log.e("onResponse: ", response.body() + "");
                        progress_bar_loader.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                if (delivery_details != null) {
                                    if (delivery_details.size() > 0) {
                                        delivery_details.clear();
                                    }
                                }
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONObject jsonObject1 = jsonObject.getJSONObject("order");
                                JSONArray products = jsonObject1.getJSONArray("product");
                                ArrayList<Delivery_product_details> deliveryProductDetails = new ArrayList<>();
                                for (int j = 0; j < products.length(); j++) {
                                    JSONObject product_detail = products.getJSONObject(j);
                                    Delivery_product_details delivery_product_details = new Delivery_product_details();
                                    delivery_product_details.setImage(product_detail.getString("logo"));
                                    delivery_product_details.setName(product_detail.getString("name"));
                                    delivery_product_details.setQuantity(product_detail.getString("quantity"));
                                    delivery_product_details.setTotal(product_detail.getString("price"));
                                    deliveryProductDetails.add(delivery_product_details);
                                }
                                recyclerViewProduct.setLayoutManager(new LinearLayoutManager(GoogleMapsActivity.this));
                                RecyclerViewDeliveryProduct recyclerViewDeliveryProduct = new RecyclerViewDeliveryProduct(deliveryProductDetails);
                                recyclerViewProduct.setAdapter(recyclerViewDeliveryProduct);

                                Delivery_Detail delivery_detail = new Delivery_Detail();
                                delivery_detail.setOrder_id(jsonObject1.getString("order_id"));
                                delivery_detail.setOrder_status_id(jsonObject1.getString("order_status_id"));
                                delivery_detail.setOrder_total(jsonObject1.getString("total"));
                                delivery_detail.setCustomer_mobile(jsonObject1.getString("customer_mobile"));
                                delivery_detail.setDelivery_address(jsonObject1.getString("delivery_address"));
                                delivery_detail.setPreparing_time(jsonObject1.getString("preparing_time"));
                                delivery_detail.setDelivery_type(jsonObject1.getString("contactless_delivery"));
                                delivery_detail.setDelivery_date(jsonObject1.getString("date_delivery"));
                                delivery_detail.setDelivery_status(jsonObject1.getString("delivery_status"));
                                delivery_detail.setPickup_address(jsonObject1.getString("pickup_address"));
                                delivery_detail.setRestaurant_mobile(jsonObject1.getString("vendor_mobile"));
                                delivery_detail.setPayment_method(jsonObject1.getString("payment_method"));
                                delivery_detail.setStatus(jsonObject1.getString("delivery_status"));

                                if (!jsonObject1.isNull("delivery_latitude")) {
                                    delivery_detail.setDelivery_latitude(jsonObject1.getString("delivery_latitude"));
                                } else {
                                    delivery_detail.setDelivery_latitude("");
                                }
                                if (!jsonObject1.isNull("delivery_longitude")) {
                                    delivery_detail.setDelivery_longitude(jsonObject1.getString("delivery_longitude"));
                                } else {
                                    delivery_detail.setDelivery_longitude("");
                                }
                                if (!jsonObject1.isNull("pickup_latitude")) {
                                    delivery_detail.setPickup_latitude(jsonObject1.getString("pickup_latitude"));
                                } else {
                                    delivery_detail.setPickup_latitude("");
                                }
                                if (!jsonObject1.isNull("pickup_longitude")) {
                                    delivery_detail.setPickup_longitude(jsonObject1.getString("pickup_longitude"));
                                } else {
                                    delivery_detail.setPickup_longitude("");
                                }
                                delivery_detail.setRestaurant_image(jsonObject1.getString("logo"));
                                delivery_detail.setRestaurant_name(jsonObject1.getString("vendor"));
                                delivery_detail.setCustomer_name(jsonObject1.getString("customer_first_name"));
                                delivery_details.add(delivery_detail);

                                if (delivery_details != null) {
                                    if (delivery_details.size() > 0) {
                                        for (int i = 0; i < delivery_details.size(); i++) {
                                            mTrack.setVisibility(View.GONE);

                                            order_status_id = delivery_details.get(i).getOrder_status_id();

                                            mOrderId = delivery_details.get(i).getOrder_id();
                                            order_id.setText(getString(R.string.txt_order_id) + delivery_details.get(i).getOrder_id());
                                            delivery_date.setText(delivery_details.get(i).getDelivery_date());

                                            if (delivery_details.get(i).getDelivery_type().equals("1")) {
                                                contactless_delivery.setVisibility(View.VISIBLE);
                                            } else {
                                                contactless_delivery.setVisibility(View.GONE);
                                            }

                                            String preparingTime = delivery_details.get(i).getPreparing_time() + " " + getResources().getString(R.string.mins);
                                            mPreparingTime.setText(preparingTime);

                                            restaurant_mobile.setText(delivery_details.get(i).getRestaurant_mobile());
                                            customer_mobile.setText(delivery_details.get(i).getCustomer_mobile());
                                            if (delivery_details.get(i).getCustomer_comment() != null && !delivery_details.get(i).getCustomer_comment().isEmpty()) {
                                                customer_comment.setVisibility(View.VISIBLE);
                                                customer_comment.setText(delivery_details.get(i).getCustomer_comment());
                                            } else {
                                                customer_comment.setVisibility(View.GONE);
                                            }
                                            restaurant_name.setText(delivery_details.get(i).getRestaurant_name());
                                            customer_name.setText(delivery_details.get(i).getCustomer_name());
                                            staus.setText(delivery_details.get(i).getStatus());
                                            pickup_address.setText(delivery_details.get(i).getPickup_address());
                                            delivery_address.setText(delivery_details.get(i).getDelivery_address());
                                            payment.setText(delivery_details.get(i).getOrder_total());
                                            tv_payment_method.setText(delivery_details.get(i).getPayment_method());
                                            Customer_Mobile_number = delivery_details.get(i).getCustomer_mobile();
                                            Restaurant_Mobile_number = delivery_details.get(i).getRestaurant_mobile();

                                            delivery_latitude = delivery_details.get(i).getDelivery_latitude();
                                            delivery_longitude = delivery_details.get(i).getDelivery_longitude();
                                            pickup_latitude = delivery_details.get(i).getPickup_latitude();
                                            pickup_longitude = delivery_details.get(i).getPickup_longitude();

                                            switch (delivery_details.get(i).getOrder_status_id()) {
                                                case "6":
                                                case "3":
                                                case "5":
                                                    mBtn_delivered.setVisibility(View.GONE);
                                                    btn_switch.setVisibility(View.VISIBLE);
                                                    mTrack.setVisibility(View.VISIBLE);
                                                    btn_out_of_delivery.setVisibility(View.GONE);
                                                    break;
                                                case "8":
                                                    mBtn_delivered.setVisibility(View.VISIBLE);
                                                    btn_switch.setVisibility(View.GONE);
                                                    mTrack.setVisibility(View.VISIBLE);
                                                    btn_out_of_delivery.setVisibility(View.GONE);
                                                    break;
                                                case "9":
                                                    btn_switch.setVisibility(View.GONE);
                                                    mBtn_delivered.setVisibility(View.VISIBLE);
                                                    Intent intent = new Intent(GoogleMapsActivity.this, NavigationActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    break;
                                                default:
                                                    mBtn_delivered.setVisibility(View.GONE);
                                                    btn_switch.setVisibility(View.GONE);
                                                    mTrack.setVisibility(View.GONE);
                                                    btn_out_of_delivery.setVisibility(View.GONE);
                                                    break;
                                            }

                                            if (!delivery_details.get(i).getPickup_latitude().isEmpty() && !delivery_details.get(i).getPickup_longitude().isEmpty() &&
                                                    !delivery_details.get(i).getDelivery_latitude().isEmpty() && !delivery_details.get(i).getDelivery_longitude().isEmpty()) {

                                                LatLng pickup = new LatLng(Double.parseDouble(delivery_details.get(i).getPickup_latitude()),
                                                        Double.parseDouble(delivery_details.get(i).getPickup_longitude()));
                                                LatLng delivery = new LatLng(Double.parseDouble(delivery_details.get(i).getDelivery_latitude()),
                                                        Double.parseDouble(delivery_details.get(i).getDelivery_longitude()));

                                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(pickup));
                                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(delivery));

                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(pickup));
                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(delivery));

                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                builder.include(pickup);
                                                builder.include(delivery);
                                                bounds = builder.build();
                                                int padding = 50; // offset from edges of the map in pixels
                                                cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(pickup));
//                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(delivery));
//                                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                                                mMap.moveCamera(cu);
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                                                DownloadTask downloadTask = new DownloadTask();
                                                // Getting URL to the Google Directions API
                                                String Direction_url = getDirectionsUrl(pickup, delivery);
                                                // Start downloading json data from Google Directions API
                                                downloadTask.execute(Direction_url);
                                            }
                                        }
                                    }
                                }
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
            Constant.loadInternetAlert(getSupportFragmentManager());
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + Constant.DirectionsApiKey; // Api Key

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection = null;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("downloading url", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParserTask parserTask = new ParserTask();

            parserTask.execute(s);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.RED);
            }

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            }
        }
    }

    class RecyclerViewDeliveryProduct extends RecyclerView.Adapter<RecyclerViewDeliveryProduct.ViewHolder> {
        private final ArrayList<Delivery_product_details> Product_list;

        RecyclerViewDeliveryProduct(ArrayList<Delivery_product_details> deliveryProductDetails) {
            this.Product_list = deliveryProductDetails;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GoogleMapsActivity.this).inflate(R.layout.delivery_product_list_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (Product_list.get(position).getImage() != null) {
                if (Product_list.get(position).getImage().length() > 0) {
                    Picasso.with(GoogleMapsActivity.this).load(Product_list.get(position).getImage()).into(holder.imageView);
                } else {
                    Picasso.with(GoogleMapsActivity.this).load(R.drawable.no_image).into(holder.imageView);
                }
            } else {
                Picasso.with(GoogleMapsActivity.this).load(R.drawable.no_image).into(holder.imageView);
            }
            holder.product.setText(Product_list.get(position).getName());
            holder.quantity.setText(Product_list.get(position).getQuantity());
            holder.total.setText(Product_list.get(position).getTotal());
        }

        @Override
        public int getItemCount() {
            return Product_list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView imageView;
            TextView product, quantity, total;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_details);
                product = itemView.findViewById(R.id.tv_product);
                quantity = itemView.findViewById(R.id.tv_quantity);
                total = itemView.findViewById(R.id.tv_total);
            }
        }
    }

    public void UpdateStatus(String status_id) {
        progress_bar_loader.setVisibility(View.VISIBLE);
        if (delivery_details != null) {
            if (delivery_details.size() > 0) {
                for (int i = 0; i < delivery_details.size(); i++) {
                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    //  Log.e("UpdateStatus: ", delivery_details.get(i).getDelivery_id() + " : " + delivery_id);
                    if (delivery_details.get(i).getOrder_id().equals(mOrderId)) {
                        mOrderId = delivery_details.get(i).getOrder_id();
                        break;
                    }
                }

                JSONObject object = new JSONObject();
                try {
                    object.put("status", status_id);
                    object.put("order_id", mOrderId);
                    object.put("language_id", String.valueOf(Constant.current_language_id()));

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                    Call<String> status = apiInterface.order_status_update(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                    status.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful()) {

                                Constant.DataStoreValue(GoogleMapsActivity.this, Constant.Order_id, mOrderId);

                                if (status_id.equals("9")) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("tasks").child(mOrderId);
                                    mDatabase.child("status").setValue("0");

                                    Constant.DataStoreValue(GoogleMapsActivity.this, Constant.Order_status_id, "1");
                                    Constant.tracking = false;

                                    DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference("task_status").child(mOrderId);
                                    mDatabase1.child("task_status_id").setValue("9");

                                    AppContext.order_id = "";
                                }

                                if (status_id.equals("8")) {
                                    //  "name": "Intransit"
                                    Constant.tracking = true;
                                    AppContext.startupdate(mOrderId);
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("task_status").child(mOrderId);
                                    mDatabase.child("task_status_id").setValue("8");
                                }

                                // Log.e( "onResponse: ",jsonObject.toString()+"" );
                                Constant.loadToastMessage(GoogleMapsActivity.this, getResources().getString(R.string.order_status_update_successfully));
                                LoadDeliveryMapDetails();
                            }
                            progress_bar_loader.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            progress_bar_loader.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("task_status");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getKey().equals(mOrderId)) {
                    mDatabase.child(mOrderId).child("task_status_id").setValue(status_id);
                    //  LoadDeliveryMapDetails();
                }
//                else {
//                    mDatabase.child(mOrderId).child("task_status_id").setValue(status_id);
//                    LoadDeliveryMapDetails();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(GoogleMapsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Constant.loadToastMessage(GoogleMapsActivity.this, getResources().getString(R.string.permission_accepted));

                } else {

                    Constant.loadToastMessage(GoogleMapsActivity.this, getResources().getString(R.string.permission_denied));

                }
                break;

            case 41:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    statusCheck();

                } else {
                    askPermission();
                }
                break;


        }
    }

    public void statusCheck() {

        GpsLocationTracker locationTrack = new GpsLocationTracker(GoogleMapsActivity.this);

        if (locationTrack.canGetLocation()) {
            if (ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                askPermission();
            } else {
                if (dialogInterface != null)
                    dialogInterface.cancel();
            }
        } else {
            if (!isActive) {
                showSettingsAlert();
            }
        }
    }

    public void showSettingsAlert() {

        isActive = true;
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(GoogleMapsActivity.this, R.style.AppTheme));

        mAlertDialog.setTitle(getResources().getString(R.string.gps_disabled));

        mAlertDialog.setMessage(getResources().getString(R.string.gps_is_not_enabled));

        mAlertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialogInterface = dialog;
                isActive = false;
                Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(mIntent);
                dialog.cancel();
            }
        });

        mAlertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialogInterface = dialog;
                isActive = false;
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        mcreateDialog = mAlertDialog.create();
        mcreateDialog.show();
    }

    private void askPermission() {

        if (ContextCompat.checkSelfPermission(GoogleMapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(GoogleMapsActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.please_allow_the_device_location_access)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    USER_LOCATION_PERMISSION_CODE);

                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        USER_LOCATION_PERMISSION_CODE);
            }
        }
    }

    private final int SECONDS = 3000;
    Handler handler = new Handler();

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                FetchCurrentLocation();
                handler.postDelayed(this, SECONDS);
            }
        }, SECONDS);
    }

    public void RunTimeStatusCheck() {
        handler.postDelayed(new Runnable() {
            public void run() {
                statusCheck();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    public void FetchCurrentLocation() {

        GpsLocationTracker locationTrack = new GpsLocationTracker(GoogleMapsActivity.this);

        if (locationTrack.canGetLocation()) {
            if (ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                askPermission();
            } else {
                double latitude = locationTrack.getLatitude();
                double longitude = locationTrack.getLongitude();
                // Log.e("latitude: %s", latitude + " ");
                //  Log.e("longitude: %s", longitude + " ");
                String currentLatiandLong = latitude + "," + longitude;
                if (currentLatiandLong != null
                    /*&& Delivery_geo != null && Pickup_geo != null*/) {
                    if (currentLatiandLong.length() > 0 /*&& Delivery_geo.length() > 0*/) {
//                        String[] Delivery_To = Pickup_geo.split(",");
//                        String[] Delivery_From = Delivery_geo.split(",");
                        String[] Current_Loc = currentLatiandLong.split(",");

                        LatLng pickup = new LatLng(Double.parseDouble(pickup_latitude), Double.parseDouble(pickup_longitude));
                        LatLng Deliver = new LatLng(Double.parseDouble(delivery_latitude), Double.parseDouble(delivery_longitude));
                        LatLng Current = new LatLng(Double.parseDouble(Current_Loc[0]), Double.parseDouble(Current_Loc[1]));

                        LatLngBounds bounds = new LatLngBounds.Builder()

                                .include(pickup)
                                .include(Current)
                                .include(Deliver).build();
                        Point displaySize = new Point();
                        LatLng center = bounds.getCenter();
                        getWindowManager().getDefaultDisplay().getSize(displaySize);
                        //   mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x,256,30));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                        mDriverLocation = new Location("");

                        mDriverLocation.setLatitude(Double.parseDouble(currentLatiandLong.split(",")[0]));
                        mDriverLocation.setLongitude(Double.parseDouble(currentLatiandLong.split(",")[1]));

                        if (mDriverLocation != null) {

                            mMovementLocationList = new ArrayList<>();
                            if (mDriverLocation != null) {

                                if (mMovementLocationList.size() == 0) {
                                    mMovementLocationList.add(mDriverLocation);

                                    // DecimalFormat mDecimalFormat = new DecimalFormat("#.#######");
                                    //  mMapCenter = new LatLng(Double.valueOf(mDecimalFormat.format(mDriverLatitude)),Double.valueOf(mDecimalFormat.format(mDriverLongitude)));
                                    mMapCenter = new LatLng(mDriverLocation.getLatitude(), mDriverLocation.getLongitude());

                                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMapCenter, 13));      // For Check 22-11-2016 PM
                                    if (mMarker != null)
                                        mMarker.remove();
                                    //Taxi1(Current) Location :-
                                    mMarker = mMap.addMarker(new MarkerOptions()
                                            .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))
                                            .position(mMapCenter)
                                            .flat(true)
                                            .rotation(0));
                                }
                                if (mMovementLocationList.size() > 0) {
                                    mMapCenter = new LatLng(mDriverLocation.getLatitude(), mDriverLocation.getLongitude());

                                    if (mMovementLocationList.size() == 1) {
                                        if (mDriverLocation != null) {
                                            Location mLocationLast = mMovementLocationList.get(mMovementLocationList.size() - 1);
                                            Location mLocationNow = mDriverLocation;
                                            double mLastLat = mLocationLast.getLatitude();
                                            double mLastLong = mLocationLast.getLongitude();
                                            double mNowLat = mLocationNow.getLatitude();
                                            double mNowLong = mLocationNow.getLongitude();

                                            // CASE I :-
                                            if (mLastLat != mNowLat && mLastLong != mNowLong) {

                                                Location mTLocation = new Location("");
                                                mTLocation.setLatitude(mNowLat);
                                                mTLocation.setLongitude(mNowLong);
                                                mMovementLocationList.add(mTLocation);
                                            }
                                        }
                                    } else if (mMovementLocationList.size() >= 2) {

                                        if (mDriverLocation != null) {
                                            Location mLocationLast = mMovementLocationList.get(mMovementLocationList.size() - 1);
                                            Location mLocationNow = mDriverLocation;
                                            // DecimalFormat mLocationDecimalFormat = new DecimalFormat("#.#######");
                                            double mLastLat = mLocationLast.getLatitude();
                                            double mLastLong = mLocationLast.getLongitude();
                                            //double mNowLat = Double.valueOf(mLocationDecimalFormat.format(mLocationNow.getLatitude()));
                                            // double mNowLong = Double.valueOf(mLocationDecimalFormat.format(mLocationNow.getLongitude()));
                                            double mNowLat = mLocationNow.getLatitude();
                                            double mNowLong = mLocationNow.getLongitude();

                                            // CASE I :-
                                            if (mLastLat != mNowLat && mLastLong != mNowLong) {

                                                Location mTLocation = new Location("");
                                                mTLocation.setLatitude(mNowLat);
                                                mTLocation.setLongitude(mNowLong);
                                                mMovementLocationList.add(mTLocation);

                                                Location mLocation1 = mMovementLocationList.get(mMovementLocationList.size() - 2); // start latlong
                                                Location mLocation2 = mMovementLocationList.get(mMovementLocationList.size() - 1); // end latlong

                                                double mNewPreviousLat1 = mLocation1.getLatitude();
                                                double mNewPreviousLong1 = mLocation1.getLongitude();
                                                double mNewNextLat2 = mLocation2.getLatitude();
                                                double mNewNextLong2 = mLocation2.getLongitude();

                                                double PI = 3.14159;
                                                double mPreviousLatitude = mNewPreviousLat1 * PI / 180;
                                                double mPreviousLongitude = mNewPreviousLong1 * PI / 180;
                                                double mNextLatitude = mNewNextLat2 * PI / 180;
                                                double mNextLongitude = mNewNextLong2 * PI / 180;

                                                double mDirectionLongitude = (mNextLongitude - mPreviousLongitude);

                                                double mY = Math.sin(mDirectionLongitude) * Math.cos(mNextLatitude);
                                                double mX = Math.cos(mPreviousLatitude) * Math.sin(mNextLatitude) - Math.sin(mPreviousLatitude)
                                                        * Math.cos(mNextLatitude) * Math.cos(mDirectionLongitude);

                                                double mTempBearing = Math.atan2(mY, mX);

                                                mTempBearing = Math.toDegrees(mTempBearing);
                                                mBearing = (mTempBearing + 360) % 360;

                                                double R = 6371000; // m

                                                double dLat = Math.toRadians(mLocation2.getLatitude() - mLocation1.getLatitude());
                                                double dLon = Math.toRadians(mLocation2.getLongitude() - mLocation1.getLongitude());
                                                double lat1 = Math.toRadians(mLocation1.getLatitude());
                                                double lat2 = Math.toRadians(mLocation2.getLatitude());

                                                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                                        + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1)
                                                        * Math.cos(lat2);
                                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                                double d = R * c;

                                                double result = d / 1000;
                                                double tempD = mDistanceTraveled;
                                                mDistanceTraveled = result;
                                                mDistanceTraveled = tempD + mDistanceTraveled;
                                            }
                                        }
                                    }
                                    if (mMarker != null) {
                                        mMarker.setPosition(mMapCenter);
                                        mMarker.setRotation((float) mBearing);
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                    "sv".equals(AppLanguageSupport.getLanguage(GoogleMapsActivity.this)) ?
                            View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_LTR);
        }
    }
}
