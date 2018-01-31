public class Register {
    //Make sale
    private Sale sale;
    private ProductCatalog catalog;

    public Register(ProductCatalog catalog) {
        this.catalog=catalog;
    }

    public void makeNewSale() {
        sale = new Sale();
    }

    public void enterItem(int id, int quantity) {
        if (sale==null)
            makeNewSale();
        ProductSpecification spec = catalog.getSpecification(id);
        sale.makeLineItem(spec,quantity);
    }

    public void removeItem(int id,int quantity) {
        ProductSpecification spec = catalog.getSpecification(id);
        sale.removeLineItem(spec,quantity);
    }

    public void endSale() {
        sale.becomeComplete();
    }

    public void makePayment(Money amount) {
        sale.makePayment(amount);
    }

    public String getReciept() {
        String reciept = "Date: "+sale.getDate().toString()+"\n\n";
        reciept+=sale.toString();
        return reciept;
    }

    public Money getBalance() {
        return sale.getBalance();
    }

    public Sale getSale() {
        return sale;
    }
}
