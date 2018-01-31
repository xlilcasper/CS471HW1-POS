import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by Brian on 1/18/2018.
 */
public class Money {
    BigDecimal amount=new BigDecimal(0).setScale(2,BigDecimal.ROUND_UP);

    public Money() {}

    public Money(BigDecimal amount,int quantity) {
        setAmount(amount.multiply(new BigDecimal(quantity)));
    }

    public Money(Money amount, int quantity) {
        setAmount(amount.getAmount().multiply(new BigDecimal(quantity)));
    }

    public Money(BigDecimal amount) {
        setAmount(amount);
    }

    public Money(double amount,int quantity) { this(new BigDecimal(amount),quantity); }

    public Money(double amount) { this(amount,1); }

    public Money plus(Money amount) {
        return new Money(this.amount.add(amount.getAmount()));
    }

    public void add(Money amount) {this.amount.add(amount.getAmount()); }

    public Money minus(Money amount) {
        return new Money(this.amount.subtract(amount.getAmount()));
    }

    public void subtract(Money amount) {this.amount.subtract(amount.getAmount());}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2,BigDecimal.ROUND_UP);
    }

    public String stringAbs() {
        return NumberFormat.getCurrencyInstance().format(this.amount.abs());
    }

    public Money negate() {
        return new Money(this.amount.negate());
    }

    public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
        int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
        return d.setScale(scale, mode);
    }

    @Override
    public String toString() {
        return NumberFormat.getCurrencyInstance().format(this.amount);
    }
}
