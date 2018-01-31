public class Store {
    private Address address=new Address();
    private String name="";
    private ProductCatalog catalog = ProductCatalog.getInstance();
    private Register register = new Register(catalog);
    private Ledger ledger = new Ledger();

    public Register getRegister() {
        return register;
    }

    public boolean itemExists(int itemId) {
        return catalog.getSpecification(itemId)!=null;
    }

    public void addSale(Sale sale) {
        ledger.addSale(sale);
    }

    public Ledger getLedger() {
        return ledger;
    }

    public ProductSpecification getProduct(int id) {
        return catalog.getSpecification(id);
    }

    public Sale getSale(long saleId) {
        return ledger.getSale(saleId);
    }

    public Money getTotalSales() {
        return ledger.getTotalSales();
    }

}
