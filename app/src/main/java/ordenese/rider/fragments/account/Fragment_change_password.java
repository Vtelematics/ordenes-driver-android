package ordenese.rider.fragments.account;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Header;


public class Fragment_change_password extends DialogFragment {
    ApiInterface apiInterface;
    TextView txt_password, txt_conform, txt_old, error_password, error_conform_password, error_old_password;
    Button btn_save;
    Activity activity;
    ImageView btn_cancel;
    LinearLayout progressBar;
    private boolean network;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        txt_old = view.findViewById(R.id.ed_old_password);
        txt_password = view.findViewById(R.id.ed_Change_password);
        txt_conform = view.findViewById(R.id.ed_confirm);
        error_old_password = view.findViewById(R.id.error_old_password);
        error_password = view.findViewById(R.id.error_password);
        error_conform_password = view.findViewById(R.id.error_confirm_password);
        btn_save = view.findViewById(R.id.button_save);
        btn_cancel = view.findViewById(R.id.close_button);
        progressBar = view.findViewById(R.id.pb_progress_bar);
        btn_cancel.setOnClickListener(v -> dismiss());
        btn_save.setOnClickListener(v -> {
            if (txt_old.getText().toString().length() == 0) {
                error_old_password.setVisibility(View.VISIBLE);
                error_old_password.setText(getString(R.string.error_old_password));
            } else {
                error_old_password.setVisibility(View.GONE);
            }
            if (txt_password.getText().toString().length() == 0) {
                error_password.setVisibility(View.VISIBLE);
                error_password.setText(getString(R.string.error_password));
            } else {
                error_password.setVisibility(View.GONE);
            }
            if (txt_conform.getText().toString().length() == 0) {
                error_conform_password.setVisibility(View.VISIBLE);
                error_conform_password.setText(getString(R.string.error_confirm_password));
            } else {
                if (txt_password.getText().toString().equals(txt_conform.getText().toString())) {
                    error_password.setVisibility(View.GONE);
                    error_conform_password.setVisibility(View.GONE);
                } else {
                    error_conform_password.setVisibility(View.VISIBLE);
                    error_conform_password.setText(getString(R.string.error_confirm));
                }
            }
            if (txt_old.getText().toString().length() > 0
                    && txt_password.getText().toString().length() > 0
                    && txt_conform.getText().toString().length() > 0
                    && txt_password.getText().toString().equals(txt_conform.getText().toString())) {
                String Token = Constant.DataGetValue(activity, Constant.Driver_Token);
                String password = txt_password.getText().toString();
                String confirm = txt_conform.getText().toString();

                JSONObject object = new JSONObject();
                try {
                    object.put("password", password);
                    object.put("confirm_password", confirm);
                    object.put("language_id", String.valueOf(Constant.current_language_id()));

                    network = Constant.isNetworkOnline(activity);
                    if (network) {
                        progressBar.setVisibility(View.VISIBLE);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                        apiInterface = ApiClient.getClient().create(ApiInterface.class);

                        Call<String> call = apiInterface.change_password(Token, body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                        Constant.loadToastMessage(activity, jsonObject1.getString("message"));
                                        Fragment_change_password.this.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Constant.loadToastMessage(activity, getString(R.string.error_old_password));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        getDialog().setTitle(getResources().getString(R.string.change_password));
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // int dialogWidth = 700;
        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }
}
