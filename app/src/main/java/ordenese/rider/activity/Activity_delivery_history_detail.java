package ordenese.rider.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DirectionsJSONParser;
import ordenese.rider.R;
import ordenese.rider.fragments.account.ScrollMaps;
import ordenese.rider.model.Delivery_Detail;
import ordenese.rider.model.Delivery_product_details;
import ordenese.rider.model.Delivery_product_total;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_delivery_history_detail extends AppCompatActivity implements OnMapReadyCallback {

    ApiInterface apiInterface;
    ImageView restaurant_contact, customer_contact, restaurant_whatsapp, customer_whatsapp;
    TextView order_id, delivery_date, restaurant_mobile, customer_mobile, staus,
            pickup_address, delivery_address, restaurant_name, customer_name,
            payment, tv_payment_method, mTrack, contactless_delivery, flatNo;
    RecyclerView recyclerViewProduct, recyclerViewTotal;
    ArrayList<Delivery_Detail> delivery_details = new ArrayList<>();
    LinearLayout progress_bar_loader;
    private GoogleMap mMap;
    private String delivery_id;
    ScrollView mscrollView;
    Button btn_change_view_sv, btn_change_view_nv;
    private String Customer_Mobile_number, Restaurant_Mobile_number;
    private static final int PERMISSION_REQUEST_CODE = 1;

    boolean network;

    LatLngBounds bounds;
    CameraUpdate cu;

    TextView mPreparingTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_delivery_history_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPreparingTime = findViewById(R.id.preparing_time);

        //   image_customer = findViewById(R.id.dv_image_customer);
        //   image_restaurant = findViewById(R.id.dv_image_restaurant);
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
        payment = findViewById(R.id.dv_payment);
        contactless_delivery = findViewById(R.id.contactless_delivery);
        contactless_delivery.setVisibility(View.GONE);

        flatNo = findViewById(R.id.customer_flat_no);

        recyclerViewProduct = findViewById(R.id.dv_recyclerViewProduct);
        recyclerViewTotal = findViewById(R.id.dv_recyclerViewTotal);
        progress_bar_loader = findViewById(R.id.progress_bar_loader);
        btn_change_view_sv = findViewById(R.id.btn_change_view_sv);
        btn_change_view_nv = findViewById(R.id.btn_change_view_nv);
        restaurant_contact = findViewById(R.id.contact_restaurant);
        customer_contact = findViewById(R.id.contact_customer);
        restaurant_whatsapp = findViewById(R.id.whatsapp_restaurant);
        customer_whatsapp = findViewById(R.id.whatsapp_customer);
        tv_payment_method = findViewById(R.id.tv_payment_method);
        mTrack = findViewById(R.id.track_location);

        ScrollMaps mapFragment = (ScrollMaps) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mscrollView = findViewById(R.id.scrollView);
            ((ScrollMaps) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(() -> mscrollView.requestDisallowInterceptTouchEvent(true));


        });

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if (b != null) {
            delivery_id = (String) b.get("Delivery_id");
        }

        LoadDeliveryHistory();

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

        restaurant_contact.setOnClickListener(v -> {
            if (checkPermission()) {
                loadCallIntent(Restaurant_Mobile_number);
            } else {
                requestPermission();
            }

        });

        customer_contact.setOnClickListener(v -> {
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

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + delivery_address.getText().toString()));
                    startActivity(intent);

                } catch (ActivityNotFoundException ane) {
                    Toast.makeText(Activity_delivery_history_detail.this, "Please Install Google Maps ", Toast.LENGTH_LONG).show();

                }
            }
        });

        //autoAssignApiCall();
        //  runThread();

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
        if (Mobile_number != null) {
            String phone = Mobile_number;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void LoadDeliveryHistory() {
        progress_bar_loader.setVisibility(View.GONE);
        network = Constant.isNetworkOnline(Activity_delivery_history_detail.this);
        if (network) {
            progress_bar_loader.setVisibility(View.VISIBLE);
            try {
                JSONObject object = new JSONObject();
                object.put("order_id", delivery_id);
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<String> call = apiInterface.order_info(Constant.DataGetValue(Activity_delivery_history_detail.this, Constant.Driver_Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progress_bar_loader.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
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
                                recyclerViewProduct.setLayoutManager(new LinearLayoutManager(Activity_delivery_history_detail.this));
                                RecyclerViewDeliveryProduct recyclerViewDeliveryProduct = new RecyclerViewDeliveryProduct(deliveryProductDetails);
                                recyclerViewProduct.setAdapter(recyclerViewDeliveryProduct);

                                Delivery_Detail delivery_detail = new Delivery_Detail();
                                delivery_detail.setOrder_id(jsonObject1.getString("order_id"));
                                delivery_detail.setOrder_total(jsonObject1.getString("total"));
                                delivery_detail.setCustomer_mobile(jsonObject1.getString("customer_mobile"));
                                delivery_detail.setDelivery_address(jsonObject1.getString("delivery_address"));
                                delivery_detail.setPreparing_time(jsonObject1.getString("preparing_time"));

                                delivery_detail.setDelivery_date(jsonObject1.getString("date_delivery"));
                                delivery_detail.setDelivery_type(jsonObject1.getString("contactless_delivery"));
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
                                if (!jsonObject1.isNull("pickup_latitude")) {
                                    delivery_detail.setPickup_longitude(jsonObject1.getString("pickup_latitude"));
                                } else {
                                    delivery_detail.setPickup_longitude("");
                                }

//                                    delivery_detail.setCustomer_image(jsonObject1.getString("customer_image"));
                                delivery_detail.setRestaurant_image(jsonObject1.getString("logo"));
                                delivery_detail.setRestaurant_name(jsonObject1.getString("vendor"));
                                delivery_detail.setCustomer_name(jsonObject1.getString("customer_first_name"));
                                delivery_details.add(delivery_detail);


                                if (delivery_details != null) {
                                    if (delivery_details.size() > 0) {
                                        for (int i = 0; i < delivery_details.size(); i++) {
                                            order_id.setText(getResources().getString(R.string.order_id) + " : " + delivery_details.get(i).getOrder_id());
                                            delivery_date.setText(delivery_details.get(i).getDelivery_date());
                                            restaurant_mobile.setText(delivery_details.get(i).getRestaurant_mobile());
                                            customer_mobile.setText(delivery_details.get(i).getCustomer_mobile());
                                            restaurant_name.setText(delivery_details.get(i).getRestaurant_name());
                                            customer_name.setText(delivery_details.get(i).getCustomer_name());
                                            //  speed.setText(delivery_details.get(i).getSpeed());
                                            staus.setText(delivery_details.get(i).getStatus());
                                            pickup_address.setText(delivery_details.get(i).getPickup_address());
                                            String preparingTime = delivery_details.get(i).getPreparing_time() + " " + getResources().getString(R.string.mins);
                                            mPreparingTime.setText(preparingTime);

                                            if (delivery_details.get(i).getDelivery_type().equals("1")) {
                                                contactless_delivery.setVisibility(View.VISIBLE);
                                            } else {
                                                contactless_delivery.setVisibility(View.GONE);
                                            }

//                                            String mFlatNo = getResources().getString(R.string.auto_assign_flat) + " " + delivery_details.get(i).getFlatNo();
//                                            flatNo.setText(mFlatNo);

                                            delivery_address.setText(delivery_details.get(i).getDelivery_address());
                                            payment.setText(delivery_details.get(i).getOrder_total());
                                            tv_payment_method.setText(delivery_details.get(i).getPayment_method());
                                            Customer_Mobile_number = delivery_details.get(i).getCustomer_mobile();
                                            Restaurant_Mobile_number = delivery_details.get(i).getRestaurant_mobile();

                                            if (!delivery_details.get(i).getPickup_latitude().isEmpty() && !delivery_details.get(i).getPickup_longitude().isEmpty() &&
                                                    !delivery_details.get(i).getDelivery_latitude().isEmpty() && !delivery_details.get(i).getDelivery_longitude().isEmpty()) {

                                                LatLng pickup = new LatLng(Double.parseDouble(delivery_details.get(i).getPickup_latitude()),
                                                        Double.parseDouble(delivery_details.get(i).getPickup_longitude()));
                                                LatLng delivery = new LatLng(Double.parseDouble(delivery_details.get(i).getDelivery_latitude()),
                                                        Double.parseDouble(delivery_details.get(i).getDelivery_longitude()));

                                                MarkerOptions markerPickup = new MarkerOptions();
                                                markerPickup.position(pickup);
                                                markerPickup.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                mMap.addMarker(markerPickup);
                                                mMap.addMarker(new MarkerOptions().position(delivery));

                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                builder.include(pickup);
                                                builder.include(delivery);
                                                bounds = builder.build();
                                                int padding = 50; // offset from edges of the map in pixels
                                                cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

//                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(pickup));
//                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(delivery));
//                                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                                                //mMap.moveCamera(cu);
                                                // mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

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
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.loadInternetAlert(getSupportFragmentManager());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //  Log.d("downloading url", e.toString());
        } finally {
            iStream.close();
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
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList<LatLng>();
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
            View view = LayoutInflater.from(Activity_delivery_history_detail.this).inflate(R.layout.delivery_product_list_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (Product_list.get(position).getImage() != null) {
                if (Product_list.get(position).getImage().length() > 0) {
                    Picasso.with(Activity_delivery_history_detail.this).load(Product_list.get(position).getImage()).into(holder.imageView);
                } else {
                    Picasso.with(Activity_delivery_history_detail.this).load(R.drawable.no_image).into(holder.imageView);
                }
            } else {
                Picasso.with(Activity_delivery_history_detail.this).load(R.drawable.no_image).into(holder.imageView);
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

    class RecyclerViewDeliveryTotal extends RecyclerView.Adapter<RecyclerViewDeliveryTotal.ViewHolder> {

        private final ArrayList<Delivery_product_total> Total_list;

        RecyclerViewDeliveryTotal(ArrayList<Delivery_product_total> deliveryProductTotals) {
            this.Total_list = deliveryProductTotals;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Activity_delivery_history_detail.this).inflate(R.layout.delivery_total_list_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(Total_list.get(position).getTitle());
            holder.value.setText(Total_list.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return Total_list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, value;

            ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tv_title);
                value = itemView.findViewById(R.id.tv_value);
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Activity_delivery_history_detail.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Activity_delivery_history_detail.this, Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
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
                    "ar".equals(AppLanguageSupport.getLanguage(Activity_delivery_history_detail.this)) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

}
