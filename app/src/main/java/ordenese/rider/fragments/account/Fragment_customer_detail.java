package ordenese.rider.fragments.account;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DataParser;
import ordenese.rider.R;
import ordenese.rider.activity.NavigationActivity;
import ordenese.rider.model.Customer_Detail;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class Fragment_customer_detail extends Fragment {

    LinearLayout tv_linear_layout, progressbar;
    EditText first_name, last_name, email, license_no, name, account_no, bank, ifsc_code;
    Button btn_save, btn_change_password;
    CircleImageView image_category;
    ImageView bg_image;
    TextView customer_name, phone;
    ApiInterface apiInterface;
    Activity activity;
    ArrayList<Customer_Detail> customer_details = new ArrayList<>();
    private boolean network;

    private static int PICK_IMAGE_CAMERA = 0, PICK_IMAGE_GALLERY = 0;
    private final static int IMAGE = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    public static ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_details, container, false);

        tv_linear_layout = view.findViewById(R.id.tv_linear_layout);
        first_name = view.findViewById(R.id.edit_first_name);
        last_name = view.findViewById(R.id.edit_last_name);
        email = view.findViewById(R.id.edit_email);
        phone = view.findViewById(R.id.edit_phone);
        license_no = view.findViewById(R.id.edit_license_no);
        name = view.findViewById(R.id.edit_name);
        account_no = view.findViewById(R.id.edit_account_number);
        bank = view.findViewById(R.id.edit_bank);
        ifsc_code = view.findViewById(R.id.edit_ifsc_code);
        btn_save = view.findViewById(R.id.btn_save);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        image_category = view.findViewById(R.id.m_image_category);
        progressbar = view.findViewById(R.id.progress_bar);
        customer_name = view.findViewById(R.id.customer_name);
        bg_image = view.findViewById(R.id.bg_image);
        loadCustomer(false, null, null, null);

        btn_save.setOnClickListener(v -> {
            editCustomer();
        });
        btn_change_password.setOnClickListener(v -> {
            Fragment_change_password fragment = new Fragment_change_password();
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            fragment.show(getChildFragmentManager(), "change_password");
        });
        image_category.setOnClickListener(v -> {
            if (hasPermissions(activity, PERMISSIONS)) {
                selectImage();
            } else {
                requestPermission(IMAGE);
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String encodedString;
                            File file;

                            if (PICK_IMAGE_CAMERA == 1) {

                                PICK_IMAGE_CAMERA = 0;

                                Bundle extras = data.getExtras();
                                Bitmap photo = (Bitmap) extras.get("data");

//                                image_tag.setImageBitmap(photo);

                                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                                Uri tempUri = getImageUri(activity, photo);
                                // CALL THIS METHOD TO GET THE ACTUAL PATH
                                File finalFile = new File(getRealPathFromURI(tempUri));
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byte_arr = stream.toByteArray();

                                encodedString = Base64.encodeToString(byte_arr, 0);
                                loadSetCustomerImage(encodedString, photo);

                            } else if (PICK_IMAGE_GALLERY == 1) {

                                PICK_IMAGE_GALLERY = 0;

                                Uri filePath = data.getData();
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);
                                    file = new File("" + filePath);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byte_arr = stream.toByteArray();
                                    encodedString = Base64.encodeToString(byte_arr, 0);

                                    loadSetCustomerImage(encodedString, bitmap);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

        return view;
    }

    private void selectImage() {
        try {
            PackageManager pm = activity.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "hre", Toast.LENGTH_SHORT).show();
                ImagePicker addPhotoBottomDialogFragment =
                        new ImagePicker(activity);
                addPhotoBottomDialogFragment.show(getChildFragmentManager(),
                        addPhotoBottomDialogFragment.getTag());
            } else {
                showAlert();
            }
        } catch (Exception e) {
        Toast.makeText(activity, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(getResources().getString(R.string.app_needs_to_access_the_Camera));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.deny),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.allow),
                (dialog, which) -> {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            PICK_IMAGE_CAMERA);


                });
        alertDialog.show();
    }

    private void requestPermission(int Type) {

        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, Type);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void editCustomer() {
        progressbar.setVisibility(View.GONE);
        if (network) {
            String F_name = first_name.getText().toString();
            String L_name = last_name.getText().toString();
            String Email = email.getText().toString();
            String Phone = phone.getText().toString();

            progressbar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);

            JSONObject object = new JSONObject();
            try {
                object.put("name", F_name);
                object.put("sur_name", L_name);
                object.put("email", Email);
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<String> edit = apiInterface.profile_edit(Constant.DataGetValue(activity, Constant.Driver_Token), body);

                edit.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progressbar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                Constant.loadToastMessage(activity, jsonObject1.getString("message"));

                                String mDriverName = F_name;
                                Constant.DataStoreValue(activity, Constant.DriverName, mDriverName);
                                Constant.DataStoreValue(activity, Constant.DriverMobile, Phone);
                                Constant.DataStoreValue(activity, Constant.DriverDataUpdated, "Updated");

                                Intent intent = new Intent(activity, NavigationActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressbar.setVisibility(View.GONE);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.loadInternetAlert(getFragmentManager());
        }

    }

    private void loadCustomer(Boolean mIsImageUpload, CircleImageView imageCategory, ImageView bgImage, Bitmap selected__Image) {
        progressbar.setVisibility(View.GONE);
        network = Constant.isNetworkOnline(activity);
        if (network) {
            if (!mIsImageUpload) {
                progressbar.setVisibility(View.VISIBLE);
            }
            JSONObject object = new JSONObject();
            try {
                object.put("language_id", String.valueOf(Constant.current_language_id()));

                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<String> call = apiInterface.profile_info(Constant.DataGetValue(activity, Constant.Driver_Token), body);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progressbar.setVisibility(View.GONE);

                        if (mIsImageUpload) {
                            //This call for uploaded image url path get process through profile api call.
                            imageCategory.setImageBitmap(selected__Image);
                            bgImage.setImageBitmap(selected__Image);
                        }

                        if (response.isSuccessful()) {
                            customer_details = DataParser.getCustomerDetails(response.body());
                            if (customer_details != null) {
                                if (customer_details.size() > 0) {
                                    for (int i = 0; i < customer_details.size(); i++) {
                                        first_name.setText(customer_details.get(i).getFirst_name());
                                        last_name.setText(customer_details.get(i).getLast_name());
                                        email.setText(customer_details.get(i).getEmail());
                                        phone.setText(customer_details.get(i).getMobile());

                                        String mDriverName = customer_details.get(i).getFirst_name();
                                        Constant.DataStoreValue(activity, Constant.DriverName, mDriverName);
                                        Constant.DataStoreValue(activity, Constant.DriverMobile, customer_details.get(i).getMobile());
                                        Constant.DataStoreValue(activity, Constant.DriverImgUrl, customer_details.get(i).getDriver_pic());
                                        Constant.DataStoreValue(activity, Constant.DriverDataUpdated, "Updated");

                                        license_no.setText(customer_details.get(i).getLicenseNo());
                                        customer_name.setText(customer_details.get(i).getFirst_name());

                                        if (customer_details.get(i).getDriver_pic() != null) {
                                            if (customer_details.get(i).getDriver_pic().length() > 0) {
                                                Picasso.with(activity).load(customer_details.get(i).getDriver_pic()).into(image_category);
                                                // Log.e("onResponse1: ", "image");
                                            } else {
                                                Picasso.with(activity).load(R.drawable.no_image).into(image_category);
                                                //   Log.e("onResponse2: ", "no image length");
                                            }
                                        } else {
                                            Picasso.with(activity).load(R.drawable.no_image).into(image_category);
                                            //  Log.e("onResponse3: ", "no image null");
                                        }

                                        if (customer_details.get(i).getDriver_pic() != null) {
                                            if (customer_details.get(i).getDriver_pic().length() > 0) {
                                                Picasso.with(activity).load(customer_details.get(i).getDriver_pic()).into(bg_image);
                                            } else {
                                                Picasso.with(activity).load(R.drawable.no_image).into(bg_image);
                                            }
                                        } else {
                                            Picasso.with(activity).load(R.drawable.no_image).into(bg_image);
                                        }

                                        if (customer_details.get(i).getCustomer_bank_details() != null) {
                                            if (customer_details.get(i).getCustomer_bank_details().size() > 0) {
                                                for (int j = 0; j < customer_details.get(i).getCustomer_bank_details().size(); j++) {
                                                    name.setText(customer_details.get(i).getCustomer_bank_details().get(j).getAccount_name());
                                                    account_no.setText(customer_details.get(i).getCustomer_bank_details().get(j).getAccount_no());
                                                    bank.setText(customer_details.get(i).getCustomer_bank_details().get(j).getBank_name());
                                                    ifsc_code.setText(customer_details.get(i).getCustomer_bank_details().get(j).getBank_code());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressbar.setVisibility(View.GONE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.loadInternetAlert(getFragmentManager());
        }

    }

    private void loadSetCustomerImage(String image, Bitmap selectedImage) {

//        String mImage = "data:image/jpeg;base64," + image;

        progressbar.setVisibility(View.VISIBLE);

        JSONObject object = new JSONObject();
        try {
            object.put("image", image);

            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            Call<String> call = apiInterface.profile_picture(Constant.DataGetValue(activity, Constant.Driver_Token), body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                            // Constant.loadToastMessage(activity, jsonObject1.getString("message"));
                            loadCustomer(true, image_category, bg_image, selectedImage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressbar.setVisibility(View.GONE);
                        }
                    } else {
                        progressbar.setVisibility(View.GONE);
                        Constant.loadToastMessage(activity, getResources().getString(R.string.image_upload_failed));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progressbar.setVisibility(View.GONE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class ImagePicker extends BottomSheetDialogFragment {

        LinearLayout take_camera, take_gallery;
        Activity activity;

        public ImagePicker(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {

            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.image_upload, container, false);

            take_camera = view.findViewById(R.id.take_camera);
            take_gallery = view.findViewById(R.id.take_gallery);

            take_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    someActivityResultLauncher.launch(intent);
                    PICK_IMAGE_CAMERA = 1;
                    PICK_IMAGE_GALLERY = 0;
                }
            });
            take_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    someActivityResultLauncher.launch(pickPhoto);
                    PICK_IMAGE_CAMERA = 0;
                    PICK_IMAGE_GALLERY = 1;
                }
            });

            return view;
        }
    }

}
