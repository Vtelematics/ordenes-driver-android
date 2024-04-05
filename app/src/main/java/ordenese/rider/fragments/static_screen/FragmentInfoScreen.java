package ordenese.rider.fragments.static_screen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.R;
import ordenese.rider.additional.Info;

public class FragmentInfoScreen extends Fragment {

    private View v_InfoScreen;
    private Activity activity;
    private InfoScreenAdapter infoScreenAdapter;
    private int page = 0;
    private ViewPager vp_BannerLoader;
    private LinearLayout ll_btn_container;
    private Handler handler;
    private int delay = 4000;
    private String list[];
    Runnable runnable = new Runnable() {
        public void run() {
            if (infoScreenAdapter != null) {
                if (infoScreenAdapter.getCount() == page) {
                    page = 0;
                } else {
                    page++;
                }
            } else {
                page = 0;
            }
            if (vp_BannerLoader != null)
                vp_BannerLoader.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        list = new String[]{/*"https://images.wallpaperscraft.com/image/food_autumn_harvest_composition_77732_720x1280.jpg",
                "https://images.wallpaperscraft.com/image/food_pancakes_crepes_honey_plate_berries_blueberries_raspberries_currants_74225_720x1280.jpg",
                "http://virginia.bghcdn.ogqcorp.com/images/cdcc/image/Nonex1280/773340.jpg",
                "https://i.pinimg.com/564x/c2/e5/ae/c2e5ae4a8e5dc5bdcd5eba1260dd28d9.jpg"*/};
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_InfoScreen = inflater.inflate(R.layout.fragment_info_screen, container, false);
        setting();
        return v_InfoScreen;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (list != null) {
            if (list.length > 0) {
                handler.postDelayed(runnable, delay);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (list != null) {
            if (list.length > 0) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    private void setting() {
        vp_BannerLoader = v_InfoScreen.findViewById(R.id.vp_info_loader);
        ll_btn_container = v_InfoScreen.findViewById(R.id.ll_btn_container);
        AppCompatTextView atv_SkipInfo=v_InfoScreen.findViewById(R.id.atv_skip_info_screen);

        infoScreenAdapter = new InfoScreenAdapter(activity, list);
        vp_BannerLoader.setAdapter(infoScreenAdapter);
        setBannerBtn(1);

        vp_BannerLoader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setBannerBtn(2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        atv_SkipInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void setBannerBtn(int type) {
        if (type == 1) {
            int pos = vp_BannerLoader.getCurrentItem();
            for (int i = 0; i < list.length; i++) {
                View view = LayoutInflater.from(activity).inflate(R.layout.layout_info_btn_holder, null, false);
                final Button button = view.findViewById(R.id.btn_info);
                button.setPadding(100, 100, 100, 100);
                int size = (int) getResources().getDisplayMetrics().density;
                if (size == 3) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                } else if (size == 2) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(24, 24);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                }
                if (pos == i) {
                    button.setBackgroundResource(R.drawable.draw_selected_bg);
                } else {
                    button.setBackgroundResource(R.drawable.draw_unselected_bg);
                }
                button.setId(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vp_BannerLoader.setCurrentItem(button.getId());
                    }
                });

                FrameLayout.LayoutParams layoutParamsMargin = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsMargin.setMargins(16, 0, 16, 10);
                layoutParamsMargin.gravity = Gravity.BOTTOM;
                ll_btn_container.setLayoutParams(layoutParamsMargin);
                ll_btn_container.setGravity(Gravity.CENTER);
                ll_btn_container.addView(button);
            }
        } else {
            int pos = vp_BannerLoader.getCurrentItem();
            ll_btn_container.removeAllViews();
            for (int i = 0; i < list.length; i++) {
                View view = LayoutInflater.from(activity).inflate(R.layout.layout_info_btn_holder, null, false);
                final Button button = view.findViewById(R.id.btn_info);
                button.setPadding(100, 100, 100, 100);
                int size = (int) getResources().getDisplayMetrics().density;
                if (size == 3) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                } else if (size == 2) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(24, 24);
                    layoutParams.setMargins(10, 0, 10, 0);
                    button.setLayoutParams(layoutParams);
                }
                if (pos == i) {
                    button.setBackgroundResource(R.drawable.draw_selected_bg);
                } else {
                    button.setBackgroundResource(R.drawable.draw_unselected_bg);
                }
                button.setId(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vp_BannerLoader.setCurrentItem(button.getId());
                    }
                });

                FrameLayout.LayoutParams layoutParamsMargin = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsMargin.setMargins(16, 0, 16, 10);
                layoutParamsMargin.gravity = Gravity.BOTTOM;
                ll_btn_container.setLayoutParams(layoutParamsMargin);
                ll_btn_container.setGravity(Gravity.CENTER);
                ll_btn_container.addView(button);
            }
        }

    }



    class InfoScreenAdapter extends PagerAdapter {

        private Activity mContext;
        private String value[];


        InfoScreenAdapter(Activity context, String sources[]) {
            this.mContext = context;
            this.value = sources;
        }

        @Override
        public int getCount() {
            return value.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.vp_row_image, container, false);
            ImageView iv_info_banner = view.findViewById(R.id.iv_info);
            Info.glide_image_loader_banner(value[position], iv_info_banner);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
