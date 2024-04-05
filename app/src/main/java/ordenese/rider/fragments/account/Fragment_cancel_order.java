package ordenese.rider.fragments.account;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.AppContext;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.activity.NavigationActivity;
import ordenese.rider.model.Cancel_order_status;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_cancel_order extends DialogFragment {

    RecyclerView recyclerView;
    ApiInterface apiInterface;
    Activity activity;
    private String delivery_id, order_id;
    Button btn_cancel;
    CancelStatusAdapter cancelStatusAdapter;
    private boolean network;
    private ProgressBar progressBar;


    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("delivery_id") != null) {
            delivery_id = getArguments().getString("delivery_id");
            order_id = getArguments().getString("order_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel_order, container, false);
        recyclerView = view.findViewById(R.id.rc_recyclerView_cancel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.mprogressbar);
        network = Constant.isNetworkOnline(activity);
        loadCancelStatus();


        btn_cancel.setOnClickListener(v -> {
            // Log.e("onCreateView: ", cancelStatusAdapter.getSelectedItem() + "");
            loadCancelOrder(cancelStatusAdapter.getSelectedItem(), delivery_id);
        });
        return view;
    }

    private void loadCancelStatus() {
        if (network) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<String> call = apiInterface.cancel_reason(Constant.DataGetValue(activity, Constant.Driver_Token));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONArray jsonArray = jsonObject.getJSONArray("status");
                            ArrayList<Cancel_order_status> cancelOrderStatuses = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Cancel_order_status cancel_order_status = new Cancel_order_status();
                                cancel_order_status.setName(jsonObject1.getString("name"));
                                cancel_order_status.setStatus_id(jsonObject1.getString("status_id"));
                                cancelOrderStatuses.add(cancel_order_status);
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            cancelStatusAdapter = new CancelStatusAdapter(cancelOrderStatuses);
                            recyclerView.setAdapter(cancelStatusAdapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Constant.loadInternetAlert(getFragmentManager());
        }

    }

    private void loadCancelOrder(String status_id, String delivery_id) {
        if (network) {
            progressBar.setVisibility(View.VISIBLE);

            JSONObject object = new JSONObject();
            try {
                object.put("order_id", delivery_id);
                object.put("reason", status_id);
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<String> call = apiInterface.cancel_order(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                Constant.loadToastMessage(activity, jsonObject1.getString("message"));
                                dismiss();

                                DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference("task_status").child(order_id);
                                mDatabase1.child("task_status_id").setValue("10");

                                Intent intent = new Intent(activity, NavigationActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
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
        } else {
            Constant.loadInternetAlert(getFragmentManager());
        }

    }


    class CancelStatusAdapter extends RecyclerView.Adapter<CancelStatusAdapter.ViewHolder> {
        private ArrayList<Cancel_order_status> Cancel_Status_list;
        int SelectedPosition;

        CancelStatusAdapter(ArrayList<Cancel_order_status> cancelOrderStatuses) {
            this.Cancel_Status_list = cancelOrderStatuses;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_cancel_order_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.ch_order_status.setText(Cancel_Status_list.get(position).getName().replace("\n", ""));
            holder.ch_order_status.setTag(position);
            holder.ch_order_status.setChecked(position == SelectedPosition);
            holder.ch_order_status.setOnClickListener(v -> {
                itemChecked(v);
                getSelectedItem();
            });

        }

        @Override
        public int getItemCount() {
            return Cancel_Status_list.size();
        }

        private void itemChecked(View v) {
            SelectedPosition = (int) v.getTag();
            notifyDataSetChanged();
        }

        String getSelectedItem() {
            if (SelectedPosition != -1) {
                return String.valueOf(Cancel_Status_list.get(SelectedPosition).getStatus_id());

            }
            return "";
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox ch_order_status;

            ViewHolder(View itemView) {
                super(itemView);
                ch_order_status = itemView.findViewById(R.id.ch_order_status);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }
}
