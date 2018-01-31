public class Payment {
    Money amount=new Money();

    public Payment(Money amount) {
        this.amount = amount;
    }

    public Money getAmount() {
        return amount;
    }
}
