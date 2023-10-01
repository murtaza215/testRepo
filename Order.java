package restaurantManagementSystem;

import java.util.ArrayList;

public class Order 
{
	public ArrayList<Product> orderedProducts;
	
	public Order() 
	{
		orderedProducts = new ArrayList<>();
	}
	public void addProduct(Product product) {
		orderedProducts.add(product);
	}
	public void listOrder() 
	{
		if(orderedProducts.size()==0)
		{
			System.out.println("You have not ordered anything yet");
		}
		else 
		{
			for (Product orderedProduct: orderedProducts)
			{
				System.out.println(orderedProduct);
			}
		}
	}
	public ArrayList<Product> getOrderedProducts()
	{
		return orderedProducts;
	}
	public double calculateTotalPrice()
	{
		double totalPrice = 0.0;
		for(int i = 0 ; i< orderedProducts.size() ; i++) 
		{
			totalPrice += orderedProducts.get(i).getSellingPrice();
		}
		return totalPrice;
	}
	public double calculateOrderExpenses() {
		double orderExpense = 0.0;
		for(Product orderedProduct: orderedProducts) {
			orderExpense += orderedProduct.calculateExpense();
		}
		return orderExpense;
	}

}
