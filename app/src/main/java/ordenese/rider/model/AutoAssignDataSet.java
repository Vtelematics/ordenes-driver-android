package ordenese.rider.model;

public class AutoAssignDataSet {

    private String mOrderId;
    private String mDeliveryAddress;
    private String mPickupAddress;
    private String mPaymentMethod;
    private String mRestaurantName;
    private String mPreparingTime;
    private String mFlatNo;
    private String mStatus;
    private String contactless_delivery;

    public String getContactless_delivery() {
        return contactless_delivery;
    }

    public void setContactless_delivery(String contactless_delivery) {
        this.contactless_delivery = contactless_delivery;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmFlatNo() {
        return mFlatNo;
    }

    public void setmFlatNo(String mFlatNo) {
        this.mFlatNo = mFlatNo;
    }

    public String getmRestaurantName() {
        return mRestaurantName;
    }

    public void setmRestaurantName(String mRestaurantName) {
        this.mRestaurantName = mRestaurantName;
    }

    public String getmPreparingTime() {
        return mPreparingTime;
    }

    public void setmPreparingTime(String mPreparingTime) {
        this.mPreparingTime = mPreparingTime;
    }

    public String getmOrderId() {
        return mOrderId;
    }

    public void setmOrderId(String mOrderId) {
        this.mOrderId = mOrderId;
    }

    public String getmDeliveryAddress() {
        return mDeliveryAddress;
    }

    public void setmDeliveryAddress(String mDeliveryAddress) {
        this.mDeliveryAddress = mDeliveryAddress;
    }

    public String getmPickupAddress() {
        return mPickupAddress;
    }

    public void setmPickupAddress(String mPickupAddress) {
        this.mPickupAddress = mPickupAddress;
    }

    public String getmPaymentMethod() {
        return mPaymentMethod;
    }

    public void setmPaymentMethod(String mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }
}
