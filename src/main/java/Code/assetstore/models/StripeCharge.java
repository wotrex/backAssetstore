package Code.assetstore.models;

import java.util.HashMap;
import java.util.Map;

public class StripeCharge {
    private final long amount;
    private final String receiptEmail;
    private final String source;
    private final String currency;

    public StripeCharge(long amount, String receiptEmail) {
        this.amount = amount;
        this.source = "tok_visa";
        this.currency = "UAH";
        this.receiptEmail = receiptEmail;
    }

    public Map<String, Object> getCharge() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", this.amount);
        params.put("currency", this.currency);
        // source should obtained with Stripe.js
        params.put("source", this.source);
        params.put(
                "description",
                "My First Test Charge (created for API docs)"
        );
        params.put("receipt_email",this.receiptEmail);
        return params;
    }
    public long getAmount() {
        return amount;
    }
    public String getReceiptEmail() {
        return receiptEmail;
    }
    public String getSource() {
        return source;
    }
    public String getCurrency() {
        return currency;
    }
}
