import java.util.ArrayList;

public class SalesLineItem {
    ProductSpecification itemInfo;
    int quantity=0;

    public SalesLineItem(ProductSpecification itemInfo, int quantity) {
        this.itemInfo = itemInfo;
        this.quantity = quantity;
    }

    public Money getSubtotal() {
        return new Money(itemInfo.getPrice(),quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSameItem(ProductSpecification item) {
        return item.equals(itemInfo);
    }

    @Override
    public String toString() {
        String line = itemInfo.toString();
        int tabs = 7-(line.length()/5);
        while (tabs-->0)
            line+="\t";
        if (this.quantity>1)
            line+="\n\t\t\t"+this.quantity+"\t@\t"+itemInfo.getPrice()+"\t\t";
        line+="\t"+getSubtotal();
        return line;
    }
}
