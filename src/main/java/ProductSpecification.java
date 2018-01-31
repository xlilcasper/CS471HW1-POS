public class ProductSpecification {
    String description="";
    Money price=new Money();
    int itemID=0;

    public ProductSpecification(int itemID, Money price, String description) {
        this.description = description;
        this.price = price;
        this.itemID = itemID;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public int getItemID() {
        return itemID;
    }

    @Override
    public String toString() {
        return this.itemID +"\t"+this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSpecification)) return false;

        ProductSpecification that = (ProductSpecification) o;

        return getItemID() == that.getItemID();

    }
}
