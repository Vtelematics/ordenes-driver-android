package ordenese.rider.Api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by user on 8/16/2018.
 **/

public interface ApiInterface {

    @POST("api/driver/login")
    Call<String> user_login(@Body RequestBody body);

    @POST("api/driver/change-password")
    Call<String> change_password(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/forget-password")
    Call<String> forget_password(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/profile-info")
    Call<String> profile_info(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/profile-edit")
    Call<String> profile_edit(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/profile-picture")
    Call<String> profile_picture(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/shift-change")
    Call<String> shift_change(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/order-accept")
    Call<String> order_accept(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/order-info")
    Call<String> order_info(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/order-unassigned")
    Call<String> order_unassigned(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/order-delivered")
    Call<String> my_delivery(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/order-status/update")
    Call<String> order_status_update(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/cancel-reason")
    Call<String> cancel_reason(@Header("Driver-Authorization") String Token);

    @POST("api/driver/order-cancel")
    Call<String> cancel_order(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/delivery-commission")
    Call<String> delivery_commission(@Header("Driver-Authorization") String Token, @Body RequestBody body);

    @POST("api/driver/logout")
    Call<String> log_out (@Header("Driver-Authorization") String Token, @Body RequestBody body);



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*  get Language */
    @GET("index.php?route=delivery/local/language")
    Call<String> getLanguage(@Query("language_id") String language_id);

    /*  Get language */
    @GET("index.php?route=delivery/local/language")
    Call<String> getLanguages();

    /* get signature*/
    @FormUrlEncoded
    @POST("index.php?route=delivery/order/delivery_signature")
    Call<String> createSignature(@Field("delivery_id") String delivery_id, @Field("signature") String signature, @Field("language_id") String language_id);

}
