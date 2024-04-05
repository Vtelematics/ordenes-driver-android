package ordenese.rider.fragments.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.Transfer;
import ordenese.rider.activity.NavigationActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;


public class FragmentLogin extends Fragment {

    private View v_LoginContainer;
    private Activity activity;
    Transfer transfer;
    ApiInterface apiInterface;
    LinearLayout progress_bar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        activity = (Activity) context;
        transfer = (Transfer) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_LoginContainer = inflater.inflate(R.layout.fragment_login, container, false);
        load();
        return v_LoginContainer;
    }

    private void load() {
        Button btn_Login = v_LoginContainer.findViewById(R.id.btn_login);
        final AppCompatTextView atv_ForgetPassword = v_LoginContainer.findViewById(R.id.atv_forget_password);
        final TextInputLayout til_UserNameHolder, til_PasswordHolder;
        final EditText et_UserName, et_Password;

        til_UserNameHolder = v_LoginContainer.findViewById(R.id.til_user_name_holder);
        til_PasswordHolder = v_LoginContainer.findViewById(R.id.til_password_holder);
        et_UserName = v_LoginContainer.findViewById(R.id.et_user_name);
        et_Password = v_LoginContainer.findViewById(R.id.et_password);
        progress_bar = v_LoginContainer.findViewById(R.id.pb_progress_bar);

        atv_ForgetPassword.setOnClickListener(v -> {
            transfer.loadForgetPassword();
        });

        btn_Login.setOnClickListener(v -> {


            if (et_UserName.getText().toString().length() == 0) {
                Constant.loadToastMessage(activity, getResources().getString(R.string.please_enter_your_user_name));
            } else {
                til_UserNameHolder.setErrorEnabled(false);
            }

            if (et_Password.getText().toString().length() == 0) {
                Constant.loadToastMessage(activity, getResources().getString(R.string.please_enter_your_password));
            } else {
                til_PasswordHolder.setErrorEnabled(false);
            }

            if (et_UserName.getText().toString().length() > 0
                    && et_Password.getText().toString().length() > 0) {
                String username = et_UserName.getText().toString();
                String password = et_Password.getText().toString();

                String user_id = OneSignal.getDeviceState().getUserId();

                if (user_id != null && !user_id.equals("")) {

                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    progress_bar.setVisibility(View.VISIBLE);

                    JSONObject object = new JSONObject();
                    try {
                        object.put("telephone", username);
                        object.put("password", password);
                        object.put("device_type", "1");
                        object.put("push_id", user_id);
                        object.put("language_id", String.valueOf(Constant.current_language_id()));

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                        Call<String> call = apiInterface.user_login(body);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progress_bar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {

                                    OneSignal.provideUserConsent(true);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        if (!jsonObject.isNull("success")) {
                                            JSONObject jsonObject1 = jsonObject.getJSONObject("driver_info");

                                            Constant.login = true;

                                            String mDriverName = jsonObject1.getString("name");
                                            Constant.DataStoreValue(activity, Constant.DriverName, mDriverName);
                                            Constant.DataStoreValue(activity, Constant.DriverMobile, jsonObject1.getString("telephone"));
                                            Constant.DataStoreValue(activity, Constant.DriverImgUrl, jsonObject1.getString("driver_pic"));
                                            Constant.DataStoreValue(activity, Constant.DriverDataUpdated, "Updated");

                                            if (!jsonObject1.isNull("uid")) {
                                                Constant.DataStoreValue(activity, "rider_uid", jsonObject1.getString("uid"));
                                            }
                                            if (!jsonObject1.isNull("admin_uid")) {
                                                Constant.DataStoreValue(activity, "admin_uid", jsonObject1.getString("admin_uid"));
                                            }

//                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                                    HashMap<String, Object> hashMap1 = new HashMap<>();
//                                    hashMap1.put("token", Constant.DataGetValue(activity, "token_notify"));
//                                    reference.child("users_list").child(jsonObject1.getString("uid")).updateChildren(hashMap1);

                                            JSONObject jsonObject2 = jsonObject.getJSONObject("success");
                                            Constant.loadToastMessage(activity, jsonObject2.getString("message"));
                                            Constant.DataStoreValue(activity, Constant.Driver_Token, jsonObject1.getString("secret_key"));
                                            Constant.DataStoreValue(activity, Constant.Driver_Id, jsonObject1.getString("driver_id"));
                                            Constant.DataStoreValue(activity, Constant.CustomerDetails, jsonObject1.toString());

                                            startActivity(new Intent(activity, NavigationActivity.class));
                                            activity.finish();
                                        } else if (!jsonObject.isNull("error")) {
                                            JSONObject jsonObject2 = jsonObject.getJSONObject("error");
                                            Constant.loadToastMessage(activity, jsonObject2.getString("message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Constant.loadToastMessage(activity, getResources().getString(R.string.invalid_user_name_and_or_password));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                progress_bar.setVisibility(View.GONE);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
