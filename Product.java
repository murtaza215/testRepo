package restaurantManagementSystem;

public class Product implements Expense
{
	private String name;
	private double purchasePrice;
	private double sellingPrice;
	private double utilityCost;
	public Product(String n , double s , double p, double u)
	{
		name = n;
		sellingPrice = s;
		purchasePrice = p;
		utilityCost = u;
	}
	public Product(String n) {
		name = n;
	}
	//Getter Functions
	public String getName()
	{
		return name;
	}
	public double getPurchasePrice()
	{
		return purchasePrice;
	}
	public double getSellingPrice()
	{
		return sellingPrice;
	}
	public double getUtilityCost()
	{
		return utilityCost;
	}
	//Setter Functions
	public void setName(String name)
	{
		this.name = name;
	}
	public void setSellingPrice(double sellingPrice)
	{
		this.sellingPrice = sellingPrice ;
	}
	public void setPurchasePrice(double purchasePrice) 
	{
		this.purchasePrice = purchasePrice;
	}
	public void setUtilityCost(double utilityCost) {
		this.utilityCost = utilityCost;
	}
	public String toString()
	{
		return this.name + ": " + this.sellingPrice;
	}
	@Override
	public double calculateExpense() {
		// TODO Auto-generated method stub
		return 0;
	}
	


}
