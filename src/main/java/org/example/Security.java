/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Security {

    @Property()
    private final String symbol;
    private final String name;
    private String quantity;

    public Security(@JsonProperty("symbol") final String symbol, @JsonProperty("name") final String name, 
            @JsonProperty("quantity") String quantity){
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void modifyQuantity(@JsonProperty("amount") String amount){
        int newQuantity = Integer.parseInt(quantity) + Integer.parseInt(amount);
        quantity = Integer.toString(newQuantity);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [symbol=" + symbol 
            + ", name=" + name + ", quantity=" + quantity + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Security other = (Security) obj;

        return Objects.deepEquals(new String[] {getSymbol(), getName(), getQuantity()},
                new String[] {other.getSymbol(), other.getName(), other.getQuantity()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol(), getName(), getQuantity());
    }

    /*public static Security fromJSONString(String json, String symbol) {
        String name = new JSONObject(json).getString("name");
        String quantity = new JSONObject(json).getString("quantity");

        Security security = new Security(symbol, name, quantity);
        return security;
    }*/
}
