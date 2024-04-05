package ordenese.rider.additional;

import android.widget.ImageView;
import android.widget.Toast;

import ordenese.rider.AppContext;
import ordenese.rider.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class Info {
    
    public static void toast(String message) {
        Toast.makeText(AppContext.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void glide_image_loader(String url, final ImageView imageView) {
        Glide.with(AppContext.getAppContext()).load(url).apply(getOption("Default")).into(imageView);
    }

    public static void glide_image_loader_fixed_size(String url, final ImageView imageView) {
        Glide.with(AppContext.getAppContext()).load(url).apply(getOption("fixed")).into(imageView);
    }

    public static void glide_image_loader_banner(String url, final ImageView imageView) {
        Glide.with(AppContext.getAppContext()).load(url).apply(getOption("Default")).into(imageView);
    }

    public static void glide_image_loader_banner(int drawable, final ImageView imageView) {
        Glide.with(AppContext.getAppContext()).load(drawable).apply(getOption("Default")).into(imageView);
    }

    private static RequestOptions getOption(String which) {

        RequestOptions options;
        switch (which) {
            case "fixed":
                options = new RequestOptions()
                        .error(R.drawable.app_logo_2)
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            case "Category":
                options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            default:
                options = new RequestOptions()
                        .error(R.drawable.app_logo_2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
        }
        return options;

    }

}
