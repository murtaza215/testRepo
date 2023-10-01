package restaurantManagementSystem;

public class Cook extends Employee {
	private double salary;
	private double taxRate;
	public Cook(int id, String name, double salary)
	{
		super(id,name);
		this.salary = salary;
		this.taxRate = 0.18;
	}
	public Cook()
	{
	  super();
	  salary = 0.0;
	  taxRate = 0.18;
	}
	//setter function for salary
	public void setSalary(double salary) 
	{
		this.salary = salary;
	}
	//Getter functions
	public double getSalary()
	{
		return salary;
	}
	public double getTaxRate()
	{
		return taxRate;
	}
	public double calculateExpense() 
	{
		//System.out.println("salary: " + this.getSalary()+"\t taxRate: "+(this.getSalary() * this.getTaxRate()));
		return this.getSalary() + (this.getSalary() * this.getTaxRate());
	
	}

}
