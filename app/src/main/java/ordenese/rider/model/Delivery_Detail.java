package ordenese.rider.model;

/**
 * Created by user on 8/18/2018.
 */

public class Delivery_Detail {


    private String order_id, delivery_id, total, task_date_added, restaurant_mobile, customer_mobile, order_status, speed,
            marker_p_address, marker_d_address, delivery_status, pickup_geo, delivery_geo, restaurant_image, customer_image,
            restaurant_name, customer_name, payment_method, order_status_id, contactless_delivery, marker_d_flat, status,customer_comment;
    private String preparing_time,pickup_latitude,pickup_longitude,delivery_latitude,delivery_longitude;

    public String getPickup_latitude() {
        return pickup_latitude;
    }

    public void setPickup_latitude(String pickup_latitude) {
        this.pickup_latitude = pickup_latitude;
    }

    public String getPickup_longitude() {
        return pickup_longitude;
    }

    public void setPickup_longitude(String pickup_longitude) {
        this.pickup_longitude = pickup_longitude;
    }

    public String getDelivery_latitude() {
        return delivery_latitude;
    }

    public void setDelivery_latitude(String delivery_latitude) {
        this.delivery_latitude = delivery_latitude;
    }

    public String getDelivery_longitude() {
        return delivery_longitude;
    }

    public void setDelivery_longitude(String delivery_longitude) {
        this.delivery_longitude = delivery_longitude;
    }

    public String getCustomer_comment() {
        return customer_comment;
    }

    public void setCustomer_comment(String customer_comment) {
        this.customer_comment = customer_comment;
    }

    public String getPreparing_time() {
        return preparing_time;
    }

    public void setPreparing_time(String preparing_time) {
        this.preparing_time = preparing_time;
    }

    public String getFlatNo() {
        return marker_d_flat;
    }

    public void setFlatNo(String marker_d_flat) {
        this.marker_d_flat = marker_d_flat;
    }

    public String getDelivery_type() {
        return contactless_delivery;
    }

    public void setDelivery_type(String contactless_delivery) {
        this.contactless_delivery = contactless_delivery;
    }

    public String getOrder_status_id() {
        return order_status_id;
    }

    public void setOrder_status_id(String order_status_id) {
        this.order_status_id = order_status_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_total() {
        return total;
    }

    public void setOrder_total(String total) {
        this.total = total;
    }

    public String getDelivery_date() {
        return task_date_added;
    }

    public void setDelivery_date(String task_date_added) {
        this.task_date_added = task_date_added;
    }

    public String getRestaurant_mobile() {
        return restaurant_mobile;
    }

    public void setRestaurant_mobile(String restaurant_mobile) {
        this.restaurant_mobile = restaurant_mobile;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public String getStatus() {
        return order_status;
    }

    public void setStatus(String order_status) {
        this.order_status = order_status;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPickup_address() {
        return marker_p_address;
    }

    public void setPickup_address(String marker_p_address) {
        this.marker_p_address = marker_p_address;
    }

    public String getDelivery_address() {
        return marker_d_address;
    }

    public void setDelivery_address(String marker_d_address) {
        this.marker_d_address = marker_d_address;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getPickup_geo() {
        return pickup_geo;
    }

    public void setPickup_geo(String pickup_geo) {
        this.pickup_geo = pickup_geo;
    }

    public String getDelivery_geo() {
        return delivery_geo;
    }

    public void setDelivery_geo(String delivery_geo) {
        this.delivery_geo = delivery_geo;
    }

    public void setRestaurant_image(String restaurant_image) {
        this.restaurant_image = restaurant_image;
    }

    public void setCustomer_image(String customer_image) {
        this.customer_image = customer_image;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTotal() {
        return total;
    }
}
