package ordenese.rider.fragments.account;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.Transfer;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentForgetPassword extends Fragment {

    View v_ForgetPassword;
    ApiInterface apiInterface;
    EditText et_user_mail;
    Button btn_send;
    ImageButton btn_cancel;
    Activity activity;
    Transfer transfer;
    private ProgressBar pb_ForgetPassword;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.transfer = (Transfer) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_ForgetPassword = inflater.inflate(R.layout.fragment_forget_password, container, false);
        et_user_mail = v_ForgetPassword.findViewById(R.id.et_user_mail);
        btn_send = v_ForgetPassword.findViewById(R.id.btn_send);
        btn_cancel = v_ForgetPassword.findViewById(R.id.fg_btn_cancel);
        pb_ForgetPassword = v_ForgetPassword.findViewById(R.id.pb_forget_password);
        btn_send.setOnClickListener(v -> {
            load();
        });
        btn_cancel.setOnClickListener(v -> {
            transfer.close();
        });

        return v_ForgetPassword;
    }

    private void load() {
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (et_user_mail.getText().length() == 0) {
            Constant.loadToastMessage(activity, getResources().getString(R.string.please_enter_your_email));
        } else {
            if (!et_user_mail.getText().toString().matches(emailPattern)) {
                Constant.loadToastMessage(activity, getResources().getString(R.string.invalid_email));
            }
        }
        if (et_user_mail.getText().length() > 0) {
            pb_ForgetPassword.setVisibility(View.VISIBLE);
            String email = et_user_mail.getText().toString();

            JSONObject object = new JSONObject();
            try {
                object.put("email", email);
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<String> call = apiInterface.forget_password(Constant.DataGetValue(activity, Constant.Driver_Token), body);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        pb_ForgetPassword.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    JSONObject jsonObject2 = jsonObject.getJSONObject("success");
                                    //  Log.e( "onResponse: ",jsonObject2.getString("message") );
                                    Constant.loadToastMessage(activity, jsonObject2.getString("message"));

                                } catch (JSONException e) {
                                    // e.printStackTrace();
                                    // Log.e("Login if ",e.toString());
                                }
                            }
                        } else {
                            Constant.loadToastMessage(activity, getResources().getString(R.string.email_not_found));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        pb_ForgetPassword.setVisibility(View.GONE);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
