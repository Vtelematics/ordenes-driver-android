package ordenese.rider.model;

import java.util.ArrayList;

/**
 * Created by user on 8/23/2018.
 */

public class Customer_Detail {
    private String first_name;
    private String last_name;
    private String email;
    private String mobile;
    private String address;
    private String licenseNo;

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getDriver_pic() {
        return driver_pic;
    }

    public void setDriver_pic(String driver_pic) {
        this.driver_pic = driver_pic;
    }

    private String driver_pic;
    private ArrayList<Customer_bank_detail> customer_bank_details;

    public ArrayList<Customer_bank_detail> getCustomer_bank_details() {
        return customer_bank_details;
    }

    public void setCustomer_bank_details(ArrayList<Customer_bank_detail> customer_bank_details) {
        this.customer_bank_details = customer_bank_details;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
