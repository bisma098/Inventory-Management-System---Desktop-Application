package ims.model;
import java.util.*;

public class ProductCatalogue {
    private List<Product> catalogue;

    public ProductCatalogue()
    {
        catalogue= new ArrayList<Product>(); 
    
    }

    public void addProduct(Product prod)
    {
        catalogue.add(prod);
    }
}
