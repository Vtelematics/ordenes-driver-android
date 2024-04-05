package ordenese.rider.model;

public class CommissionsItem{
	private String driverName;
	private String name;
	private String mobile;
	private String commission;
	private String orderId;
	private String deliveredDate;
	private String total_items;

	public void setDriverName(String driverName){
		this.driverName = driverName;
	}

	public String getDriverName(){
		return driverName;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setCommission(String commission){
		this.commission = commission;
	}

	public String getCommission(){
		return commission;
	}

	public void setOrderId(String orderId){
		this.orderId = orderId;
	}

	public String getOrderId(){
		return orderId;
	}

	public void setDeliveredDate(String deliveredDate){
		this.deliveredDate = deliveredDate;
	}

	public String getDeliveredDate(){
		return deliveredDate;
	}

	@Override
 	public String toString(){
		return 
			"CommissionsItem{" + 
			"driver_name = '" + driverName + '\'' + 
			",name = '" + name + '\'' + 
			",mobile = '" + mobile + '\'' + 
			",commission = '" + commission + '\'' + 
			",order_id = '" + orderId + '\'' + 
			",delivered_date = '" + deliveredDate + '\'' + 
			"}";
		}

	public String getTotal_items() {
		return total_items;
	}

	public void setTotal_items(String total_items) {
		this.total_items = total_items;
	}
}
