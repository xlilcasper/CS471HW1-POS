import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

//Should get total
public class Sale {
    private List<SalesLineItem> salesLineItems = new ArrayList();
    private Date date = new Date();
    private boolean isComplete=false;
    private Payment payment = new Payment(new Money(0.00));
    private static AtomicLong idCounter = new AtomicLong();
    long id;

    public Sale() {
        id = idCounter.getAndIncrement();
    }

    public void becomeComplete() {
        isComplete=true;
    }

    public void makeLineItem(ProductSpecification id) {
        makeLineItem(id,1);
    }

    public void makeLineItem(ProductSpecification id, int quantity) {
        salesLineItems.add(new SalesLineItem(id,quantity));

    }

    public void removeLineItem(ProductSpecification id,int quantity) {
        Iterator<SalesLineItem> i = salesLineItems.iterator();
        while (i.hasNext()) {
            SalesLineItem salesLineItem = i.next();
            if (salesLineItem.isSameItem(id)) {
                //we found a match. if quantity==amount we are done
                if (salesLineItem.getQuantity()==quantity) {
                    i.remove();
                    return;
                } else if (salesLineItem.getQuantity()>quantity) {
                    salesLineItem.setQuantity(salesLineItem.getQuantity()-quantity);
                    return;
                } else {
                    quantity-=salesLineItem.getQuantity();
                    i.remove();
                }
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public void makePayment(Money amount) {
        payment = new Payment(amount);
    }

    public Money getBalance() {
        return payment.getAmount().minus(getTotal());
    }

    public Money getSubTotal() {
        return salesLineItems.stream().map(salesLineItem -> salesLineItem.getSubtotal()).reduce(new Money(), Money::plus);
    }

    public Money getTax() {
        return new Money(getSubTotal().getAmount().multiply(new BigDecimal(0.06)));
    }

    public Money getTotal() {
        return getSubTotal().plus(getTax());
    }

    @Override
    public String toString() {
        String out="";
        for (SalesLineItem item : salesLineItems) {
            out+=item.toString()+"\n";
        }

        out+="\nSub-Total:\t\t"+getSubTotal();
        if (isComplete) {
            out += "\nTax:\t\t\t\t" + getTax();
            out += "\nTotal:\t\t\t" + getTotal();
            out += "\nPayment:\t\t\t" + payment.getAmount();
            if (getBalance().getAmount().compareTo(BigDecimal.ZERO)>=0)
                out+="\nChange:\t\t\t"+getBalance();
            //out += "\nBalance:\t\t\t" + getBalance();
        }
        return out;
    }

    public long getSaleId() {
        return id;
    }
}
