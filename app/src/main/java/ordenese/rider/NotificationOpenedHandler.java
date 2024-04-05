
package ordenese.rider;

import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import ordenese.rider.Common.Constant;
import ordenese.rider.activity.ActivityContainerWithOutTB;
import ordenese.rider.activity.ActivityContainerWithTB;
import ordenese.rider.activity.ActivitySplashScreen;
import ordenese.rider.activity.NavigationActivity;

public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

    AppContext mInstance;

    public NotificationOpenedHandler(AppContext mInstance) {
        this.mInstance = mInstance;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {

        try {
            JSONObject object = osNotificationOpenedResult.toJSONObject();
            if (!object.isNull("notification")) {

                JSONObject jsonObject = object.getJSONObject("notification");
                if (!jsonObject.isNull("additionalData")) {
                    JSONObject ob = jsonObject.getJSONObject("additionalData");
                    if (!ob.isNull("order_status_id")) {
                        start_Activity(ob.getString("order_status_id"));
                    }
                }
            }
        } catch (JSONException e) {
            start_Activity("");
            e.printStackTrace();
        }
    }

    private void start_Activity(String order_status_id) {
        try {
            if (!Constant.DataGetValue(mInstance, Constant.Driver_Token).equals("empty")) {
                Intent intent;
                if (order_status_id.equals("3")) {
                    intent = new Intent(mInstance, ActivityContainerWithTB.class);
                    intent.putExtra("type", "UnAssignOrderList");
                } else {
                    intent = new Intent(mInstance, NavigationActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                mInstance.startActivity(intent);
            } else {
                Intent intent = new Intent(mInstance, ActivitySplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                mInstance.startActivity(intent);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}

