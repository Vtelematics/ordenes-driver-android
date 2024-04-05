package ordenese.rider.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DataParser;
import ordenese.rider.R;
import ordenese.rider.activity.ActivityContainerWithTB;
import ordenese.rider.activity.GoogleMapsActivity;
import ordenese.rider.activity.NavigationActivity;
import ordenese.rider.model.AutoAssignDataSet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnAssignOrderList extends Fragment {

    View mView;
    RecyclerView recyclerView;
    Delivery_Detail_Adapter adapter;
    NestedScrollView nestedScrollView;
    Activity activity;
    TextView tv_empty;
    boolean network;
    ApiInterface apiInterface;
    ArrayList<AutoAssignDataSet> autoAssignDataSets = new ArrayList<>();
    ProgressDialog progress_bar;

    boolean isLoading;
    int visibleThreshold = 5, page = 1, total = 0;
    int lastVisibleItem, totalItemCount;
    OnLoadMoreListener onLoadMoreListener;

    public UnAssignOrderList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_un_assign_order_list, container, false);

        setHasOptionsMenu(true);

        progress_bar = new ProgressDialog(activity);
        progress_bar.setMessage(getResources().getString(R.string.loading_please_wait));
        progress_bar.setCanceledOnTouchOutside(false);
        progress_bar.setCancelable(false);
        progress_bar.show();

        recyclerView = mView.findViewById(R.id.un_assign_order_rec_view);
        tv_empty = mView.findViewById(R.id.tv_empty);

//        progress_bar = mView.findViewById(R.id.progress_bar);
        nestedScrollView = mView.findViewById(R.id.nested_view);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        if (scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) {
                            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            assert linearLayoutManager != null;
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                isLoading = true;
                            }
                        }
                    }
                }
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        page = 1;
        autoAssignApiCall();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ae".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);

        menu.findItem(R.id.refresh).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            progress_bar.show();
            page = 1;
            autoAssignApiCall();
        }
        return super.onOptionsItemSelected(item);
    }

    private void autoAssignApiCall() {
        progress_bar.show();
        autoAssignDataSets = new ArrayList<>();

        network = Constant.isNetworkOnline(activity);
        if (!Constant.DataGetValue(activity, Constant.DriverStatus).equals("empty") &&
                !Constant.DataGetValue(activity, Constant.DriverStatus).equals("0")) {
            if (network) {
                JSONObject object = new JSONObject();
                try {
                    object.put("page", page);
                    object.put("page_per_unit", "15");

                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                    Call<String> call = apiInterface.order_unassigned(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject object1 = new JSONObject(response.body());
                                    if (!object1.isNull("total")) {
                                        total = Integer.parseInt(object1.getString("total"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                autoAssignDataSets = DataParser.autoAssignDataSets(response.body());
                                if (autoAssignDataSets != null && autoAssignDataSets.size() != 0) {
                                    tv_empty.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                    adapter = new Delivery_Detail_Adapter(autoAssignDataSets);
                                    recyclerView.setAdapter(adapter);

                                    setOnLoadMoreListener(() -> {
                                        page++;
                                        if (page <= ((total / 15) + 1)) {

                                            progress_bar.show();

                                            JSONObject object = new JSONObject();
                                            try {
                                                object.put("page", page);
                                                object.put("page_per_unit", "15");

                                                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                                                Call<String> call1 = apiInterface.order_unassigned(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                                                call1.enqueue(new Callback<String>() {
                                                    @SuppressLint("NotifyDataSetChanged")
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        if (response.isSuccessful()) {
                                                            ArrayList<AutoAssignDataSet> autoAssignDataSetsTemp;
                                                            autoAssignDataSetsTemp = DataParser.autoAssignDataSets(response.body());
                                                            if (autoAssignDataSetsTemp.size() != 0) {
                                                                autoAssignDataSets.addAll(autoAssignDataSetsTemp);
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            setLoaded();
                                                        }
                                                        progress_bar.cancel();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<String> call, Throwable t) {
                                                        progress_bar.cancel();
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                progress_bar.cancel();
                                            }
                                        }
                                    });

                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    tv_empty.setVisibility(View.VISIBLE);
                                }
                            }
                            progress_bar.cancel();
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            progress_bar.cancel();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress_bar.cancel();
                }
            } else {
                Constant.loadInternetAlert(getParentFragmentManager());
            }
        }

        progress_bar.cancel();

    }

    void setLoaded() {
        isLoading = false;
    }

    void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();

    }

    class Delivery_Detail_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<AutoAssignDataSet> Delivery_detail;

        Delivery_Detail_Adapter(ArrayList<AutoAssignDataSet> Delivery_detail) {
            this.Delivery_detail = Delivery_detail;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.unassigned_order_rec_view, parent, false);
            return new Delivery_Detail_Adapter.ViewHolderHome(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            Delivery_Detail_Adapter.ViewHolderHome holder = (Delivery_Detail_Adapter.ViewHolderHome) viewHolder;

            holder.order_id.setText(getString(R.string.txt_order_id) + Delivery_detail.get(position).getmOrderId());
            holder.order_total.setText(getString(R.string.txt_order_total) + Delivery_detail.get(position).getmOrderId());

            holder.delivery_date.setVisibility(View.GONE);

            if (Delivery_detail.get(position).getContactless_delivery().equals("1")) {
                holder.delivery_type_linear.setVisibility(View.VISIBLE);
                holder.delivery_type.setText(getString(R.string.contactless_delivery));
            } else {
                holder.delivery_type_linear.setVisibility(View.GONE);
            }
            holder.staus.setVisibility(View.GONE);

//            if (Delivery_detail.get(position).getmStatus().isEmpty() || Delivery_detail.get(position).getmStatus().equals("1") ||
//                    Delivery_detail.get(position).getmStatus().equals("0")) {
//                holder.staus.setVisibility(View.GONE);
//            } else {
//                holder.staus.setVisibility(View.VISIBLE);
//                holder.staus.setText(getString(R.string.text_order_status) + Delivery_detail.get(position).getmStatus());
//            }

            holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept(Delivery_detail.get(position).getmOrderId());
                }
            });

            holder.pickup_address.setText(Delivery_detail.get(position).getmPickupAddress());
            holder.delivery_address.setText(Delivery_detail.get(position).getmDeliveryAddress());
            holder.restaurant_name.setText(Delivery_detail.get(position).getmRestaurantName());
        }

        @Override
        public int getItemCount() {
            return Delivery_detail.size();
        }

        private class ViewHolderHome extends RecyclerView.ViewHolder {
            TextView order_id, order_total, delivery_date, staus,
                    pickup_address, delivery_address, delivery_type, restaurant_name, accept_btn;
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
                restaurant_name = itemView.findViewById(R.id.restaurant_name);
                accept_btn = itemView.findViewById(R.id.accept);

                mlinearLayout = itemView.findViewById(R.id.linearLayout);
            }
        }

    }

    private void accept(String order_id) {

        progress_bar.show();
        JSONObject object = new JSONObject();
        try {
            object.put("order_id", order_id);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            Call<String> On = apiInterface.order_accept(Constant.DataGetValue(activity, Constant.Driver_Token), body);
            On.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    try {
                        JSONObject object1 = new JSONObject(response.body());
                        if (!object1.isNull("success")) {

                            Intent ii = new Intent(activity, GoogleMapsActivity.class);
                            ii.putExtra("Delivery_id", order_id);
                            startActivity(ii);

                        } else if (!object1.isNull("error")) {
                            JSONObject jsonObject = object1.getJSONObject("error");
                            if (!jsonObject.isNull("message")) {
                                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(activity);
                                alertDialogBuilder2.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        page = 1;
                                        autoAssignApiCall();
                                    }
                                });
                                alertDialogBuilder2.setCancelable(false);
                                alertDialogBuilder2.setMessage(jsonObject.getString("message"));
                                alertDialogBuilder2.create();
                                alertDialogBuilder2.show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progress_bar.cancel();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progress_bar.cancel();
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(activity);
                    alertDialogBuilder2.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogBuilder2.setCancelable(false);
                    alertDialogBuilder2.setMessage(t.getMessage());
                    alertDialogBuilder2.create();
                    alertDialogBuilder2.show();
                }
            });
        } catch (JSONException e) {
            progress_bar.cancel();
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(activity);
            alertDialogBuilder2.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialogBuilder2.setCancelable(false);
            alertDialogBuilder2.setMessage(e.getMessage());
            alertDialogBuilder2.create();
            alertDialogBuilder2.show();
            e.printStackTrace();
        }

    }

}