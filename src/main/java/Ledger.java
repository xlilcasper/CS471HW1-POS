import java.math.BigDecimal;
import java.util.HashMap;
import java.util.stream.Collectors;

//Singleton
public class Ledger {
    HashMap<Long,Sale> sales = new HashMap<>();

    public void addSale(Sale sale) {
        sales.put(sale.getSaleId(),sale);
    }

    public Sale getSale(long SaleId) {
        return sales.get(SaleId);
    }

    public Money getTotalSales() {
        BigDecimal total = sales.entrySet().stream().map(entry -> entry.getValue().getTotal().getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Money(total);
    }

    @Override
    public String toString() {
        return sales.entrySet().stream().map(entry -> entry.getValue().getSaleId()+"\t"+entry.getValue().getTotal()).collect(Collectors.joining("\n"));
    }
}
