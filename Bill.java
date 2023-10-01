package restaurantManagementSystem;

import java.util.ArrayList;

public class Bill extends Order {

    public Bill()
    {
        orderedProducts = new ArrayList<>();
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
}
