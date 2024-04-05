package ordenese.rider;

/**
 * Created by user on 8/17/2018.
 */

public interface Transfer {
    void loadInfoFragment();
    void loadLanguageFragment();
    void loadCustomerInfoFragment();
    void loadForgetPassword();
    void loadCustomerLoginFragment();
    void loadDeliveryHistory(String filter);
    void loadDeliveryHistoryDetail(String delivery_id);
    void loadCustomerSignature(String delivery_id, String customer_name);
    void close();
    void loadLocation();
    void earningHistory();

}
