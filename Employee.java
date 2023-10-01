package restaurantManagementSystem;

import java.util.concurrent.atomic.AtomicInteger;

public class Employee implements Expense {
	//Class Attributes
    private static final AtomicInteger count = new AtomicInteger(0); 
	private int id;
	private String name;
	//Parameterized Constructor
	public Employee(int id, String name)
	{
		this.id = count.incrementAndGet();
		this.name = name;
	}
	public Employee()
	{
		id = count.incrementAndGet();
		name = "";
	}
	//Setter function for name
	public void setName(String name) 
	{
		this.name = name;
	}
	//Getter Functions for attributes
	public int getID() 
	{
	  return id;
	}
	
	public String getName() 
	{
		return name;
	}
	//To String method to display contents of class
	public String toString() {
		return "Employee " + this.getID() + ":" + " " + this.getName();
	}
	@Override
	public double calculateExpense() {
		return 0.0;
	}


}
