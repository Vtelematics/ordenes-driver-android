package ordenese.rider.fragments.account;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DataParser;
import ordenese.rider.R;
import ordenese.rider.Transfer;
import ordenese.rider.activity.Activity_delivery_history_detail;
import ordenese.rider.model.Delivery_Detail;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_delivery_history extends Fragment {

    ApiInterface apiInterface;
    RecyclerView recyclerView;
    Delivery_Detail_Adapter delivery_detail_adapter;
    ArrayList<Delivery_Detail> delivery_details = new ArrayList<>();
    private int page = 1, total = 0;
    private LinearLayout progressBar;
    Activity activity;
    Transfer transfer;
    private String filter_id;
    private boolean network;
    TextView tv_empty;


    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.transfer = (Transfer) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter_id = getArguments().getString("filter_id");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_history, container, false);
        recyclerView = view.findViewById(R.id.rc_recyclerView);
        progressBar = view.findViewById(R.id.progress_bar_loader);
        tv_empty = view.findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        LoadDeliveryHistory();
        return view;
    }

    public void LoadDeliveryHistory() {
        if (delivery_details != null) {
            if (delivery_details.size() > 0) {
                page = 1;

                for (int i = 0; i < delivery_details.size(); i++) {
                    delivery_details.remove(i);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        network = Constant.isNetworkOnline(activity);
        if (network) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);

            JSONObject object = new JSONObject();
            try {
                object.put("page", page);
                object.put("page_per_unit", 5);
                object.put("language_id", String.valueOf(Constant.current_language_id()));
                object.put("language_code", String.valueOf(Constant.current_language_code()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<String> call = apiInterface.my_delivery(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONObject obj = new JSONObject(response.body());
                                if (!obj.isNull("error")) {
                                    delivery_detail_adapter = new Delivery_Detail_Adapter();
                                    recyclerView.setAdapter(delivery_detail_adapter);
                                } else {

                                    total = DataParser.getOrderTotalCount(response.body());
                                    delivery_details = DataParser.getDeliveryDetails(response.body());
                                    delivery_detail_adapter = new Delivery_Detail_Adapter();
                                    recyclerView.setAdapter(delivery_detail_adapter);
                                    delivery_detail_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                        @Override
                                        public void onLoadMore() {
                                            if (page <= ((total / 10) + 1)) {
                                                if (network) {
                                                    page++;
                                                    progressBar.setVisibility(View.VISIBLE);

                                                    JSONObject object = new JSONObject();
                                                    try {
                                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                                        object.put("page", page);
                                                        object.put("page_per_unit", 5);
                                                        object.put("language_id", String.valueOf(Constant.current_language_id()));
                                                        object.put("language_code", String.valueOf(Constant.current_language_code()));

                                                        Call<String> call = apiInterface.my_delivery(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                                                        call.enqueue(new Callback<String>() {
                                                            @Override
                                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                                if (response.isSuccessful()) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    if (response.body() != null) {
                                                                        ArrayList<Delivery_Detail> temp_delivery_list = DataParser.getDeliveryDetails(response.body());

                                                                        if (temp_delivery_list != null) {
                                                                            if (temp_delivery_list.size() > 0) {
                                                                                delivery_details.addAll(temp_delivery_list);
                                                                            }

                                                                        }
                                                                        delivery_detail_adapter.notifyDataSetChanged();
                                                                        delivery_detail_adapter.setLoaded();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(activity);
                            alertDialogBuilder2.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialogBuilder2.setCancelable(false);
                            alertDialogBuilder2.setMessage(response.message());
                            alertDialogBuilder2.create();
                            alertDialogBuilder2.show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.loadInternetAlert(getChildFragmentManager());
        }

    }

    class Delivery_Detail_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Delivery_Detail> Delivery_detail;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;

        Delivery_Detail_Adapter() {
            this.Delivery_detail = delivery_details;
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

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 2) {
                view = LayoutInflater.from(activity).inflate(R.layout.fragment_delivery_history_adapter, parent, false);
                return new ViewHolderHome(view);
            } else {
                view = LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false);
                return new ViewHolderEmpty(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                ViewHolderHome holder1 = (ViewHolderHome) holder;
                holder1.order_id.setText(getResources().getString(R.string.txt_order_id) + Delivery_detail.get(position).getOrder_id());
                holder1.order_total.setText(getResources().getString(R.string.txt_order_total) + Delivery_detail.get(position).getOrder_total());
                holder1.delivery_date.setText(Delivery_detail.get(position).getDelivery_date());

                if (Delivery_detail.get(position).getDelivery_type() != null) {
                    if (Delivery_detail.get(position).getDelivery_type().equals("1")) {
                        holder1.delivery_type_linear.setVisibility(View.VISIBLE);
                        holder1.delivery_type.setText(getString(R.string.contactless_delivery));
                    } else {
                        holder1.delivery_type_linear.setVisibility(View.GONE);
                    }
                } else {
                    holder1.delivery_type_linear.setVisibility(View.GONE);
                }
                holder1.staus.setText(Delivery_detail.get(position).getStatus());
                holder1.pickup_address.setText(Delivery_detail.get(position).getPickup_address());
                holder1.delivery_address.setText(Delivery_detail.get(position).getDelivery_address());

                holder1.flatNo.setVisibility(View.GONE);
//                holder1.flatNo.setText(Delivery_detail.get(position).getFlatNo());

//                holder1.mlinearLayout.setOnClickListener(v -> {
//                    String Delivery_id = Delivery_detail.get(position).getOrder_id();
//                    Intent ii = new Intent(activity, Activity_delivery_history_detail.class);
//                    ii.putExtra("Delivery_id", Delivery_id);
//                    startActivity(ii);
//                });
                holder1.view_details.setOnClickListener(v -> {
                    String Delivery_id = Delivery_detail.get(position).getOrder_id();
                    Intent ii = new Intent(activity, Activity_delivery_history_detail.class);
                    ii.putExtra("Delivery_id", Delivery_id);
                    startActivity(ii);
                });

            } else {
                ViewHolderEmpty viewHolderEmpty = (ViewHolderEmpty) holder;
                viewHolderEmpty.tv_MenuListEmpty.setText(getString(R.string.empty));
            }
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

            TextView order_id, order_total, delivery_date, restaurant_mobile, customer_mobile, speed, staus,
                    delivery_type, pickup_address, delivery_address, flatNo, view_details;

            LinearLayout mlinearLayout, delivery_type_linear;

            ViewHolderHome(View itemView) {
                super(itemView);
                order_id = itemView.findViewById(R.id.m_order_id);
                order_total = itemView.findViewById(R.id.m_order_total);
                delivery_date = itemView.findViewById(R.id.m_delivery_date);
                delivery_type = itemView.findViewById(R.id.delivery_type);
                delivery_type_linear = itemView.findViewById(R.id.delivery_type_linear);
//                restaurant_mobile = itemView.findViewById(R.id.m_restaurant_mobile);
//                customer_mobile = itemView.findViewById(R.id.m_customer_mobile);
//                speed = itemView.findViewById(R.id.m_speed);
                staus = itemView.findViewById(R.id.m_status);
                pickup_address = itemView.findViewById(R.id.m_pickup_address);
                delivery_address = itemView.findViewById(R.id.m_delivery_address);
                mlinearLayout = itemView.findViewById(R.id.linearLayout);
                view_details = itemView.findViewById(R.id.view_details);

                flatNo = itemView.findViewById(R.id.flat_no);

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

    interface OnLoadMoreListener {
        void onLoadMore();
    }


}
