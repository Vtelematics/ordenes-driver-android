package ordenese.rider.fragments.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.rider.Api.ApiClient;
import ordenese.rider.Api.ApiInterface;
import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.CalendarDate;
import ordenese.rider.Common.Constant;
import ordenese.rider.Common.DataParser;
import ordenese.rider.R;
import ordenese.rider.model.CommissionsItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningHistory extends Fragment implements View.OnClickListener, CalendarDate {

    Activity activity;
    TextView from_date_tv, to_date_tv, mTotal, mEmpty, mEarnings;
    int i = 0;
    RecyclerView order_details_list;
    ProgressBar progressBar;
    private int page = 1, total = 0;
    String key = "";
    private ArrayList<CommissionsItem> CommissionList = new ArrayList<>();
    private EarningHistoryAdapter earningHistoryAdapter;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;

    public EarningHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earning_history, container, false);

        from_date_tv = view.findViewById(R.id.from_date_tv);
        to_date_tv = view.findViewById(R.id.to_date_tv);
        order_details_list = view.findViewById(R.id.order_details_list);
        progressBar = view.findViewById(R.id.progress);
        mTotal = view.findViewById(R.id.total_orders);
        mEarnings = view.findViewById(R.id.total_earnings);
        order_details_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        mEmpty = view.findViewById(R.id.tv_empty);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -1);
        Date dates = new Date();

        Calendar call = Calendar.getInstance();
        call.add(Calendar.DATE, -6);

        String today = dateFormat.format(dates);
        String past1week = dateFormat.format(call.getTime());
        from_date_tv.setText(past1week);
        to_date_tv.setText(today);

        getAdapter(past1week, today);

        from_date_tv.setOnClickListener(v -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String Start_Date = from_date_tv.getText().toString();
            if (Start_Date.length() > 0) {
                String list[] = Start_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1;
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month, Date);
            dialog.show();
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        // from_date_tv.setText("");
                    }
                }
            });
            key = "Start_Date";
        });
        to_date_tv.setOnClickListener((View v) -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String End_Date = to_date_tv.getText().toString();
            if (End_Date.length() > 0) {
                String list[] = End_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1;
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month, Date);
            dialog.show();
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        // to_date_tv.setText("");
                    }
                }
            });
            key = "End_Date";
        });


        return view;
    }

    private void getAdapter(String from, String to) {
        if (CommissionList != null) {
            if (CommissionList.size() > 0) {
                page = 1;

                for (int i = 0; i < CommissionList.size(); i++) {
                    CommissionList.remove(i);
                }
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        try {
            JSONObject object = new JSONObject();
            object.put("page", page);
            object.put("page_per_unit", "10");
            object.put("start_date", from);
            object.put("end_date", to);
            object.put("language_id", String.valueOf(Constant.current_language_id()));

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            Call<String> call = apiInterface.delivery_commission(Constant.DataGetValue(activity, Constant.Driver_Token), body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body());
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("totals");
                            total = Integer.parseInt(jsonObject1.getString("total"));
                            String Earnings = jsonObject2.getString("total_amount");
                            mEarnings.setText(Earnings);

                            mTotal.setText(jsonObject2.getString("total"));
                            CommissionList = DataParser.getCommissionList(response.body());
                            if (CommissionList != null)
                                if (CommissionList.size() > 0) {
                                    mEmpty.setVisibility(View.GONE);
                                    earningHistoryAdapter = new EarningHistoryAdapter();
                                    order_details_list.setAdapter(earningHistoryAdapter);
                                    earningHistoryAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                        @Override
                                        public void onLoadMore() {
                                            page++;
                                            if (page <= ((total / 10) + 1)) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                try {
                                                    JSONObject object = new JSONObject();
                                                    object.put("page", page);
                                                    object.put("page_per_unit", "10");
                                                    object.put("start_date", from);
                                                    object.put("end_date", to);
                                                    object.put("language_id", String.valueOf(Constant.current_language_id()));

                                                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                                                    Call<String> call = apiInterface.delivery_commission(Constant.DataGetValue(activity, Constant.Driver_Token), body);
                                                    call.enqueue(new Callback<String>() {
                                                        @SuppressLint("NotifyDataSetChanged")
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {
                                                            if (response.isSuccessful()) {
                                                                progressBar.setVisibility(View.GONE);
                                                                if (response.body() != null) {
                                                                    ArrayList<CommissionsItem> temp_delivery_list = DataParser.getCommissionList(response.body());
                                                                    if (temp_delivery_list != null) {
                                                                        if (temp_delivery_list.size() > 0) {
                                                                            CommissionList.addAll(temp_delivery_list);
                                                                        }
                                                                    }
                                                                    earningHistoryAdapter.notifyDataSetChanged();
                                                                    earningHistoryAdapter.setLoaded();
                                                                }
                                                            }
                                                        }
                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {

                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();

                                                }
                                            }
                                        }
                                    });
                                } else {
                                    order_details_list.setAdapter(null);
                                    mEmpty.setVisibility(View.VISIBLE);
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

            progressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void selectedDate(String day, String month, String year) {
        String date = year + "-" + month + "-" + day;

    }

    private void loadDate() {

        myCalendar = Calendar.getInstance();

        date = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //  Log.e("onCreateView: ", " " + year + " - " + ((monthOfYear) + 1) + " - " + dayOfMonth);
            updateDate(year, ((monthOfYear) + 1), dayOfMonth, key);


        };

    }

    private void updateDate(int year, int monthOfYear, int dayOfMonth, String key) {
        switch (key) {
            case "Start_Date":
                from_date_tv.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                to_date_tv.setText("");
                break;
            case "End_Date":
                to_date_tv.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                getAdapter(from_date_tv.getText().toString(), to_date_tv.getText().toString());
                break;


        }

    }

//    private void showDateCalendar(String Date) {
//        DateCalendar m_dateCalendar = new DateCalendar();
//        Bundle bundle = new Bundle();
//        bundle.putString("date", Date);
//        m_dateCalendar.setArguments(bundle);
//        m_dateCalendar.setTargetFragment(this, 4573837);
//        m_dateCalendar.show(getFragmentManager(), "m_dateCalendar");
//    }


    class EarningHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final ArrayList<CommissionsItem> Commission;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;

        public EarningHistoryAdapter() {
            this.Commission = CommissionList;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) order_details_list.getLayoutManager();
            order_details_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            view = LayoutInflater.from(activity).inflate(R.layout.earning_order_list, parent, false);
            return new ViewHolderHome(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolderHome viewHolder = (ViewHolderHome) holder;

            viewHolder.order_id.setText("#" + Commission.get(position).getOrderId());
            viewHolder.order_time.setText(Commission.get(position).getDeliveredDate());
            viewHolder.ordered_item.setText(Commission.get(position).getTotal_items() + getResources().getString(R.string.items));
            //  viewHolder.status.setText(Commission.get(position).getName());
            viewHolder.order_total.setText(Commission.get(position).getCommission());
            //  viewHolder.delivery_date.setText(earningHistoryList.get(position).getOrder_date());

        }


        @Override
        public int getItemCount() {

            return Commission.size();

        }

        void setLoaded() {
            isLoading = false;
        }

        void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }


        private class ViewHolderHome extends RecyclerView.ViewHolder {
            TextView order_id, order_total, delivery_date, status, order_time, ordered_item;


            ViewHolderHome(View itemView) {
                super(itemView);
                order_id = itemView.findViewById(R.id.order_id);
                order_total = itemView.findViewById(R.id.order_amount);
                delivery_date = itemView.findViewById(R.id.order_date);
                status = itemView.findViewById(R.id.order_status);
                order_time = itemView.findViewById(R.id.order_time);
                ordered_item = itemView.findViewById(R.id.order_items);

            }
        }

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
