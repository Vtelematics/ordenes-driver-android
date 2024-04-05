package ordenese.rider.fragments.static_screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.activity.ActivitySplashScreen;


public class No_internet_connection extends DialogFragment {
    Activity activity;
    Button btn_try;
    private boolean network;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity= (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internet_connection,container,false);
        btn_try = view.findViewById(R.id.btn_try);
        btn_try.setOnClickListener(v -> {
            network = Constant.isNetworkOnline(activity);
            if (network){
                startActivity(new Intent(activity, ActivitySplashScreen.class));
            }else {
                Constant.loadToastMessage(activity, getResources().getString(R.string.no_internet_connection));
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;

        int dialogWidth = 700;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().setCanceledOnTouchOutside(false);

    }


}
