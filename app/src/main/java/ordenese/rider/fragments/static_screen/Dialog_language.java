package ordenese.rider.fragments.static_screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.activity.ActivitySplashScreen;
import ordenese.rider.model.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by happy on 12/21/2018.
 */

public class Dialog_language extends DialogFragment {

    private ArrayList<Language> Language_list;
    private RecyclerView rc_LanguageList;
    private Activity activity;
    private TextView btn_cancel, btn_save;
    LanguageAdapter languageAdapter;
    private boolean network;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_language_list,container,false);
        rc_LanguageList = view.findViewById(R.id.rc_language_list);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);

         // Constant.loadToastMessage(getActivity(),"68 called");

        LoadLanguageNew();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String mCurrentLanguage_Code =  languageAdapter.getSelectedItem();
               String Language_id = languageAdapter.getSelectLanguageId();

               if (mCurrentLanguage_Code != null){

                   String mExistingLangCode = Constant.DataGetValue(activity,Constant.CurrentLanguageCode);
                   if (!mExistingLangCode.equals(mCurrentLanguage_Code)) {
                       AppLanguageSupport.setLocale(getActivity(), mCurrentLanguage_Code);
                       Constant.DataStoreValue(activity, Constant.CurrentLanguageCode, mCurrentLanguage_Code);
                       Constant.DataStoreValue(activity, Constant.CurrentLanguageId, Language_id);
                       hide();
                       reload();
                       dismiss();

                       //Test
                       Log.e("Test","mCurrentLanguage_Code"+mCurrentLanguage_Code);
                       Log.e("Test","CurrentLanguageId"+Language_id);

                   } else {
                       dismiss();
                   }


                  /* if (Language_Code.equals("en")) {
                       if (!Constant.current_language_checkout().equals("en")) {
                           AppLanguageSupport.setLocale(getActivity(), "en");
                           Constant.DataStoreValue(activity, Constant.CurrentLanguage, "true");
                           Constant.DataStoreValue(activity, Constant.CurrentLanguageCode, Language_Code);
                           Constant.DataStoreValue(activity, Constant.CurrentLanguageId, Language_id);
                           if (Constant.DataGetValue(activity, Constant.LanguageSelection).equals("empty")) {
                               Constant.DataStoreValue(activity, Constant.LanguageSelection, "true");
                           }
                           hide();
                           reload();
                       } else {
                           dismiss();
                       }
                   } else {

                       if (!Constant.current_language_checkout().equals("sv")) {

                           AppLanguageSupport.setLocale(getActivity(), "sv");
                           Constant.DataStoreValue(activity, Constant.CurrentLanguage, "false");
                           Constant.DataStoreValue(activity, Constant.CurrentLanguageCode, Language_Code);
                           Constant.DataStoreValue(activity, Constant.CurrentLanguageId, Language_id);
                           hide();
                           if (Constant.DataGetValue(activity, Constant.LanguageSelection).equals("empty")) {
                               Constant.DataStoreValue(activity, Constant.LanguageSelection, "true");
                           }
                           reload();
                       } else {
                           dismiss();
                       }
                   }
                   if (Constant.DataGetValue(activity, Constant.LanguageSelection).equals("empty")) {
                       Constant.DataStoreValue(activity, Constant.LanguageSelection, "true");
                   }
                   dismiss();*/



               } else {
                   Toast.makeText(activity, getResources().getString(R.string.please_select_language), Toast.LENGTH_SHORT).show();
               }




            }
        });
        return view;
    }

    private void LoadLanguageNew() {
        network = Constant.isNetworkOnline(activity);
        if (network) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<String> Call = apiInterface.getLanguages();
            Call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                  if (response.isSuccessful()){
                      try {

                          JSONObject jsonObject = new JSONObject(response.body());
                          JSONArray jsonArray = jsonObject.getJSONArray("languages");
                          Language_list = new ArrayList<>();
                          for (int i = 0; i < jsonArray.length(); i++) {
                              JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                              Language language = new Language();
                              if (!jsonObject1.isNull("name")) {
                                  language.setName(jsonObject1.getString("name"));
                              } else {
                                  language.setName(getString(R.string.english));
                              }
                              if (!jsonObject1.isNull("language_id")) {
                                  language.setLanguage_id(jsonObject1.getInt("language_id"));
                              } else {
                                  language.setLanguage_id(1);
                              }
                              if (!jsonObject1.isNull("code")) {
                                  language.setCode(jsonObject1.getString("code"));
                              } else {
                                  language.setCode("en");
                              }

                              if (i == 0) {
                                  language.setSelect(true);
                              } else {
                                  language.setSelect(false);
                              }

                              Language_list.add(language);
                          }


                          if (Language_list != null) {
                              if (Language_list.size() > 0) {
                                  for (int i = 0; i < Language_list.size(); i++) {
                                      if (Constant.current_language_id() == Language_list.get(i).getLanguage_id()) {
                                          Language_list.get(i).setSelect(true);
                                      } else {
                                          Language_list.get(i).setSelect(false);
                                      }
                                  }
                              }
                          }

                          rc_LanguageList.setLayoutManager(new LinearLayoutManager(activity));
                          languageAdapter = new LanguageAdapter(Language_list);
                          rc_LanguageList.setAdapter(languageAdapter);

                      } catch (JSONException e) {
                          e.printStackTrace();
                          dismiss();
                      }
                  } else {
                      try {
                          ResponseBody requestBody = response.errorBody();
                          BufferedReader r = new BufferedReader(new InputStreamReader(requestBody.byteStream()));
                          StringBuilder total = new StringBuilder();
                          String line;
                          while ((line = r.readLine()) != null) {
                              total.append(line).append('\n');
                          }

                          JSONObject jObjError = new JSONObject(total.toString());
                          if (!jObjError.isNull("error")) {
                              JSONObject jsonErrorObject = jObjError.getJSONObject("error");
                              if (!jsonErrorObject.isNull("message")) {
                                  Constant.loadToastMessage(activity,jsonErrorObject.getString("message"));
                                  //  loadProgress.setVisibility(View.GONE);

                              } else {
                                  Constant.loadToastMessage(activity,getString(R.string.error));
                              }
                          } else {
                              Constant.loadToastMessage(activity,getString(R.string.error));
                          }

                      } catch (Exception e) {
                          e.printStackTrace();// Log.e("register fail excep", e.toString());
                          Constant.loadToastMessage(activity,getString(R.string.error));
                      }
                  }

                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                }
            });
        } else {
            Constant.loadInternetAlert(getFragmentManager());
        }
    }

    private class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolderLanguage> {


        private final ArrayList<Language> Language_List;
        private int selectedPosition = -1;

        LanguageAdapter(ArrayList<Language> language_list) {
            this.Language_List = language_list;
        }

        @NonNull
        @Override
        public ViewHolderLanguage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.language_list_adapter,parent,false);
            return new ViewHolderLanguage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderLanguage holder, int position) {
            holder.language.setText(Language_List.get(position).getName());
            holder.language.setChecked(position == selectedPosition);
            holder.language.setTag(position);
            holder.language.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }
            });

        }

        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            notifyDataSetChanged();
        }

         String getSelectedItem() {
            if (selectedPosition != -1) {
              //  Toast.makeText(activity, "Selected Language : " + Language_List.get(selectedPosition).getCode(), Toast.LENGTH_SHORT).show();
                return Language_List.get(selectedPosition).getCode();
            }
            return null;
        }

        String getSelectLanguageId(){
            if (selectedPosition != -1) {
                return String.valueOf(Language_List.get(selectedPosition).getLanguage_id());
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return Language_List.size();
        }

        class ViewHolderLanguage extends RecyclerView.ViewHolder {
            RadioButton language;
             ViewHolderLanguage(View itemView) {
                super(itemView);
                 language = itemView.findViewById(R.id.radioButton);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void reload() {
        Intent intent = new Intent(activity, ActivitySplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        activity.finish();
    }

    private void hide() {
        rc_LanguageList.setVisibility(View.GONE);
    }

}
