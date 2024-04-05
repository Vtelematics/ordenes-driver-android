package ordenese.rider.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.CalendarDate;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DateCalendar extends DialogFragment implements View.OnClickListener{

    private View mDateCalendarView;
    private CalendarView mCalendarDateView;
    private CalendarDate mCalendarDate;
    private TextView mCancel,mSubmit;
    String mDay="",mMonth="",mYear="";
    private String date;


    public DateCalendar() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
             date =    getArguments().getString("date");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);



        mDateCalendarView = inflater.inflate(R.layout.date_calendar, container, false);

        mCalendarDate = (CalendarDate)getTargetFragment();
        mCalendarDateView = (CalendarView) mDateCalendarView.findViewById(R.id.calendar_View_restaurant_dob);

        mCancel = (TextView) mDateCalendarView.findViewById(R.id.tv_View_restaurant_dob_cancel);
        mCancel.setOnClickListener(this);
        mSubmit = (TextView) mDateCalendarView.findViewById(R.id.tv_View_restaurant_dob_submit);
        mSubmit.setOnClickListener(this);

        String selectedDate = date;
        try {
            mCalendarDateView.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate).getTime(), true, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mCalendarDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
              /*  dateDisplay.setText("Date: " + i2 + " / " + i1 + " / " + i);*/
                /// Toast.makeText(getActivity(), "Selected Date:\n" + "Day = " + day + "\n" + "Month = " + month + "\n" + "Year = " + year, Toast.LENGTH_LONG).show();

                mDay = String.valueOf(day);
                mMonth = String.valueOf(month+1);
                mYear = String.valueOf(year);

            }
        });

        return mDateCalendarView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {

                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ae".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        int mDobViewId = v.getId();
        if(mDobViewId == R.id.tv_View_restaurant_dob_cancel){
            dismiss();
        }else if(mDobViewId == R.id.tv_View_restaurant_dob_submit){
            if(!mDay.isEmpty() && !mMonth.isEmpty() && !mYear.isEmpty()){

                mCalendarDate.selectedDate(mDay,mMonth,mYear);
                dismiss();

            }else {
                Constant.loadToastMessage(getActivity(),getActivity().getResources().getString(R.string.please_select_the_date));
            }

        }

    }
}
