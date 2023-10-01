package restaurantManagementSystem;

public class Beverage extends Product{
	public Beverage(String name, double sellingPrice, double purchasePrice) {
		super(name);
		super.setSellingPrice(sellingPrice);
	    super.setPurchasePrice(purchasePrice);
	}
	public double calculateExpense() {
		return super.getPurchasePrice();
	}
	

}
