package ordenese.rider.Common;

import ordenese.rider.model.AutoAssignDataSet;
import ordenese.rider.model.CommissionsItem;
import ordenese.rider.model.Customer_Detail;
import ordenese.rider.model.Customer_bank_detail;
import ordenese.rider.model.Delivery_Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 8/29/2018.
 */

public class DataParser {
    public static ArrayList<Delivery_Detail> getDeliveryDetails(String body) {

        ArrayList<Delivery_Detail> delivery_details = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(body);

            JSONArray jsonArray = jsonObject.getJSONArray("order");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                Delivery_Detail delivery_detail = new Delivery_Detail();
                if (!jsonObject1.isNull("order_id")) {
                    delivery_detail.setOrder_id(jsonObject1.getString("order_id"));
                } else {
                    delivery_detail.setOrder_id("");
                }
                if (!jsonObject1.isNull("order_status_id")) {
                    delivery_detail.setOrder_status_id(jsonObject1.getString("order_status_id"));
                } else {
                    delivery_detail.setOrder_status_id("");
                }
                if (!jsonObject1.isNull("total")) {
                    delivery_detail.setOrder_total(jsonObject1.getString("total"));
                } else {
                    delivery_detail.setOrder_total("");
                }
                if (!jsonObject1.isNull("customer_mobile")) {
                    delivery_detail.setCustomer_mobile(jsonObject1.getString("customer_mobile"));
                } else {
                    delivery_detail.setCustomer_mobile("");
                }
                if (!jsonObject1.isNull("comment")) {
                    delivery_detail.setCustomer_comment(jsonObject1.getString("comment"));
                } else {
                    delivery_detail.setCustomer_comment("");
                }
                if (!jsonObject1.isNull("delivery_address")) {
                    delivery_detail.setDelivery_address(jsonObject1.getString("delivery_address"));
                } else {
                    delivery_detail.setDelivery_address("");
                }
                if (!jsonObject1.isNull("date_delivery")) {
                    delivery_detail.setDelivery_date(jsonObject1.getString("date_delivery"));
                } else {
                    delivery_detail.setDelivery_date("");
                }
                if (!jsonObject1.isNull("delivery_status")) {
                    delivery_detail.setDelivery_status(jsonObject1.getString("delivery_status"));
                } else {
                    delivery_detail.setDelivery_status("");
                }
                if (!jsonObject1.isNull("pickup_address")) {
                    delivery_detail.setPickup_address(jsonObject1.getString("pickup_address"));
                } else {
                    delivery_detail.setPickup_address("");
                }
                if (!jsonObject1.isNull("delivery_house_number")) {
                    delivery_detail.setFlatNo(jsonObject1.getString("delivery_house_number"));
                } else {
                    delivery_detail.setFlatNo("");
                }
                if (!jsonObject1.isNull("vendor_mobile")) {
                    delivery_detail.setRestaurant_mobile(jsonObject1.getString("vendor_mobile"));
                } else {
                    delivery_detail.setRestaurant_mobile("");
                }
                if (!jsonObject1.isNull("delivery_status")) {
                    delivery_detail.setStatus(jsonObject1.getString("delivery_status"));
                } else {
                    delivery_detail.setStatus("");
                }
                if (!jsonObject1.isNull("logo")) {
                    delivery_detail.setRestaurant_image(jsonObject1.getString("logo"));
                } else {
                    delivery_detail.setRestaurant_image("");
                }
                if (!jsonObject1.isNull("contactless_delivery")) {
                    delivery_detail.setDelivery_type(jsonObject1.getString("contactless_delivery"));
                } else {
                    delivery_detail.setDelivery_type("");
                }
                delivery_details.add(delivery_detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return delivery_details;
    }

    public static ArrayList<Customer_Detail> getCustomerDetails(String body) {

        ArrayList<Customer_Detail> customer_details = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject driverInfo = jsonObject.getJSONObject("driver_info");
            Customer_Detail customer_detail = new Customer_Detail();

            if (!driverInfo.isNull("name")) {
                customer_detail.setFirst_name(driverInfo.getString("name"));
            } else {
                customer_detail.setFirst_name("");
            }
            if (!driverInfo.isNull("sur_name")) {
                customer_detail.setLast_name(driverInfo.getString("sur_name"));
            } else {
                customer_detail.setLast_name("");
            }
            if (!driverInfo.isNull("email")) {
                customer_detail.setEmail(driverInfo.getString("email"));
            } else {
                customer_detail.setEmail("");
            }
            if (!driverInfo.isNull("telephone")) {
                customer_detail.setMobile(driverInfo.getString("telephone"));
            } else {
                customer_detail.setMobile("");
            }

            if (!driverInfo.isNull("driver_pic")) {
                customer_detail.setDriver_pic(driverInfo.getString("driver_pic"));
            } else {
                customer_detail.setDriver_pic("");
            }

            JSONObject bank_detail = driverInfo.getJSONObject("bank_details");

            ArrayList<Customer_bank_detail> customerBankDetails = new ArrayList<>();
            Customer_bank_detail customer_bank_detail = new Customer_bank_detail();

            if (!bank_detail.isNull("account_name")) {
                customer_bank_detail.setAccount_name(bank_detail.getString("account_name"));
            } else {
                customer_bank_detail.setAccount_name("");
            }
            if (!bank_detail.isNull("account_no")) {
                customer_bank_detail.setAccount_no(bank_detail.getString("account_no"));
            } else {
                customer_bank_detail.setAccount_no("");
            }
            if (!bank_detail.isNull("bank_code")) {
                customer_bank_detail.setBank_code(bank_detail.getString("bank_code"));
            } else {
                customer_bank_detail.setBank_code("");
            }
            if (!bank_detail.isNull("bank_name")) {
                customer_bank_detail.setBank_name(bank_detail.getString("bank_name"));
            } else {
                customer_bank_detail.setBank_name("");
            }
            customerBankDetails.add(customer_bank_detail);

            customer_detail.setCustomer_bank_details(customerBankDetails);
            customer_details.add(customer_detail);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customer_details;
    }

    public static int getOrderTotalCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.isNull("total")) {
                return jsonObject.getInt("total");
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static ArrayList<CommissionsItem> getCommissionList(String body) {

        ArrayList<CommissionsItem> commissionsItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray jsonArray = jsonObject.getJSONArray("commissions");
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject CommissionItems = jsonArray.getJSONObject(i);
                        CommissionsItem commissionsItem = new CommissionsItem();
                        if (!CommissionItems.isNull("name")) {
                            commissionsItem.setName(CommissionItems.getString("name"));
                        } else {
                            commissionsItem.setName("");
                        }
                        if (!CommissionItems.isNull("order_id")) {
                            commissionsItem.setOrderId(CommissionItems.getString("order_id"));
                        } else {
                            commissionsItem.setOrderId("");
                        }
                        if (!CommissionItems.isNull("driver_name")) {
                            commissionsItem.setDriverName(CommissionItems.getString("driver_name"));
                        } else {
                            commissionsItem.setDriverName("");
                        }
                        if (!CommissionItems.isNull("mobile")) {
                            commissionsItem.setMobile(CommissionItems.getString("mobile"));
                        } else {
                            commissionsItem.setMobile("");
                        }
                        if (!CommissionItems.isNull("delivery_fee")) {
                            commissionsItem.setCommission(CommissionItems.getString("delivery_fee"));
                        } else {
                            commissionsItem.setCommission("");
                        }
                        if (!CommissionItems.isNull("delivered_date")) {
                            commissionsItem.setDeliveredDate(CommissionItems.getString("delivered_date"));
                        } else {
                            commissionsItem.setDeliveredDate("");
                        }

                        if (!CommissionItems.isNull("total_items")) {
                            commissionsItem.setTotal_items(CommissionItems.getString("total_items"));
                        } else {
                            commissionsItem.setTotal_items("");
                        }

                        commissionsItems.add(commissionsItem);
                    }
                }
            }

            return commissionsItems;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }


    public static ArrayList<AutoAssignDataSet> autoAssignDataSets(String response) {

        ArrayList<AutoAssignDataSet> autoAssignDataSets = new ArrayList<>();
        AutoAssignDataSet autoAssignDataSet;

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("order")) {

                JSONArray array = jsonObject.getJSONArray("order");

                if (array.length() != 0) {

                    for (int i = 0; i < array.length(); i++) {
                        autoAssignDataSet = new AutoAssignDataSet();
                        JSONObject object = array.getJSONObject(i);

                        if (!object.isNull("order_id")) {
                            autoAssignDataSet.setmOrderId(object.getString("order_id"));
                        } else {
                            autoAssignDataSet.setmOrderId("");
                        }

                        if (!object.isNull("pickup_address")) {
                            autoAssignDataSet.setmPickupAddress(object.getString("pickup_address"));
                        } else {
                            autoAssignDataSet.setmPickupAddress("");
                        }

                        if (!object.isNull("delivery_address")) {
                            autoAssignDataSet.setmDeliveryAddress(object.getString("delivery_address"));
                        } else {
                            autoAssignDataSet.setmDeliveryAddress("");
                        }

                        if (!object.isNull("payment_method")) {
                            autoAssignDataSet.setmPaymentMethod(object.getString("payment_method"));
                        } else {
                            autoAssignDataSet.setmPaymentMethod("");
                        }

                        if (!object.isNull("vendor")) {
                            autoAssignDataSet.setmRestaurantName(object.getString("vendor"));
                        } else {
                            autoAssignDataSet.setmRestaurantName("");
                        }

                        if (!object.isNull("preparing_time")) {
                            autoAssignDataSet.setmPreparingTime(object.getString("preparing_time"));
                        } else {
                            autoAssignDataSet.setmPreparingTime("");
                        }

//                        if (!object.isNull("order_status")) {
//                            autoAssignDataSet.setmStatus(object.getString("order_status"));
//                        } else {
//                            autoAssignDataSet.setmStatus("");
//                        }

                        if (!object.isNull("contactless_delivery")) {
                            autoAssignDataSet.setContactless_delivery(object.getString("contactless_delivery"));
                        } else {
                            autoAssignDataSet.setContactless_delivery("");
                        }

                        autoAssignDataSets.add(autoAssignDataSet);
                    }

                }
            } else {
                autoAssignDataSets.add(null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return autoAssignDataSets;
    }

}
