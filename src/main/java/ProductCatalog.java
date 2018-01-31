import java.util.HashMap;
import java.util.Map;

//Singleton
public class ProductCatalog {
    private static ProductCatalog instance = null;
    private Map<Integer, ProductSpecification> productSpect = new HashMap();
    protected ProductCatalog() {
        //Fill our items list.
        int itemId=1;
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(0.50),"Apple"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(0.50),"Orange"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(.75),"Banana"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(3.50),"Salad"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(0.39),"Cookie"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(1.49),"Hamburger"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(1.00),"French Fries"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(0.99),"Potato Chips"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(1.99),"Ice cream"));
        productSpect.put(itemId,new ProductSpecification(itemId++,new Money(1.99),"Soda"));
    }

    public static ProductCatalog getInstance() {
        if (instance==null) {
            instance=new ProductCatalog();
        }
        return instance;
    }
    public ProductSpecification getSpecification(int itemId) {
        return productSpect.get(itemId);
    }
}

