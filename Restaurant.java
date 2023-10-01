package restaurantManagementSystem;
import java.util.ArrayList;
import java.util.Random;

public class Restaurant 
{
	private ArrayList<Employee> employees = new ArrayList<>();
	private ArrayList<Product> products = new ArrayList<>();
	
	public Restaurant() 
	{
		initEmployees();
		initProducts();
	}
	
	private void initEmployees() 
	{
		addCook("Monica",100);
		addWaiter("Ross");
		addWaiter("Phobe");
		addWaiter("Rachel");
	}
	public void listEmployees() 
	{
		for(Employee employee : employees) 
		{
			System.out.println(employee);
		}
	}

	private void initProducts() 
	{
	
		// Parameters for Product Constructor:
		// Product Name, Selling Price, Purchase Price and Utility Cost
		
		products.add(new MainDish("Pizza", 6, 2, 2));
		products.add(new MainDish("Burger", 5, 1.5, 2));
		
		products.add(new Beverage("Coke", 2, 0.5));
		products.add(new Beverage("Lemonade", 2, 0.3));
		
		products.add(new Dessert("Tiramusu", 4, 1, 1));
		products.add(new Dessert("Cake", 3, 0.5, 1));
		products.add(new Dessert("Ice Cream", 3, 0.5, 0.5));
		
		ArrayList<Product> HGproducts = new ArrayList<>();
		HGproducts.add(new MainDish("Pizza", 6, 2, 2));
		HGproducts.add(new Beverage("Coke", 2, 0.5));
		HGproducts.add(new Dessert("Tiramusu", 4, 1, 1));
		products.add(new MenuProduct("Hunger Games Menu", HGproducts));
		
		ArrayList<Product> Kidsproducts = new ArrayList<>();
		Kidsproducts.add(new MainDish("Burger", 5, 1.5, 2));
		Kidsproducts.add(new Beverage("Lemonade", 2, 0.3));
		Kidsproducts.add(new Dessert("Ice Cream", 3, 0.5, 0.5));
		products.add(new MenuProduct("Kids Menu", Kidsproducts));
	}
	
	public void addCook(String name, double salary) 
	{
		Cook cook = new Cook();
		cook.setName(name);
		cook.setSalary(salary);
		employees.add(cook);
	}
	public void addWaiter(String name)
	{
		Waiter waiter = new Waiter();
		waiter.setName(name);
		employees.add(waiter);
	}
	public Waiter assignWaiter()
	{
		ArrayList<Waiter> waiters = new ArrayList<>();
		for(Employee employee : employees)
		{
			if(employee instanceof Waiter)
			{
				waiters.add((Waiter)employee);
			}
		}
		Random rand = new Random();
		int randomIndex = rand.nextInt(waiters.size());
		return waiters.get(randomIndex);
	}
	public double calculateEmployeeExpenses()
	{
		double employeesExpense = 0.0;
		for(Employee employee : employees) 
		{
			employeesExpense += employee.calculateExpense();
		}
		return employeesExpense;
	}
	public double calculateOrderExpenses()
	{
		ArrayList<Order> orderReceivedList = new ArrayList<>();
		double ordersExpense = 0.0;
		for(Employee employee : employees)
		{
			if(employee instanceof Waiter)
	    	{
	    		orderReceivedList = ((Waiter) employee).getOrdersReceived();
	    		for(Order order : orderReceivedList) 
	    		{
	    			ordersExpense += order.calculateOrderExpenses();
	    		}
	    	}
		}
		return ordersExpense;
	}
	public double calculateExpenses()
	{
		return this.calculateEmployeeExpenses() + this.calculateOrderExpenses();
	}
	public double calculateRevenue()
	{
		ArrayList<Order> orderReceivedList = new ArrayList<>();
		double totalRevenue = 0.0;
	    for(Employee employee : employees)
	    {
	    	if(employee instanceof Waiter)
	    	{
	    		orderReceivedList = ((Waiter) employee).getOrdersReceived();
	    		for(Order order : orderReceivedList) 
	    		{
	    			totalRevenue += order.calculateTotalPrice();
	    		}
	    	}
	    }
		return totalRevenue;
	}
	public ArrayList<Product> getProducts()
	{
		return products;
	}
	
}



// Implement the rest of the class




