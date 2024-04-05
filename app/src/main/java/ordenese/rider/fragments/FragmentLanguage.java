package ordenese.rider.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.activity.NavigationActivity;
import ordenese.rider.model.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentLanguage extends Fragment {
    RecyclerView mRecyclerView;
    ApiInterface apiInterface;
    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<String> call = apiInterface.getLanguage(String.valueOf(Constant.current_language_id()));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject1.getJSONArray("languages");
                        ArrayList<Language> languages = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Language language = new Language();
                            language.setName(jsonObject.getString("name"));
                            language.setCode(jsonObject.getString("code"));
                            languages.add(language);
                        }
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(languages);
                        mRecyclerView.setAdapter(recyclerViewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
        return view;
    }



    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private final ArrayList<Language> LanguageList;

        RecyclerViewAdapter(ArrayList<Language> languages) {
            this.LanguageList = languages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_language_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           holder.language.setText(LanguageList.get(position).getName());
           holder.language.setOnClickListener(v -> startActivity(new Intent(activity,NavigationActivity.class)));
        }

        @Override
        public int getItemCount() {
            return LanguageList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView language;
            ViewHolder(View itemView) {
                super(itemView);
                language = itemView.findViewById(R.id.language);
            }
        }
    }
}
