package com.promeets.android.object;

/**
 * Created by sosasang on 1/11/17.
 */

public class CreditCard {
    public String brand;
    public String last4;
    public String expYear;
    public String expMonth;
    public boolean isValidated;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String cardId;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public void setValidated(boolean validateStatus) {
        isValidated = validateStatus;
    }


}
