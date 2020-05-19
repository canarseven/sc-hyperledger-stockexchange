/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class Security {

    @Property()
    private String symbol;
    private String name;
    private String quantity;

    public Security(String symbol, String name, String quantity){
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

    public void modifyQuantity(String amount){
        int newQuantity = Integer.parseInt(quantity) + Integer.parseInt(amount);
        quantity = Integer.toString(newQuantity);
    }

    public String toJSONString() {
        return new JSONObject(this).toString();
    }

    public static Security fromJSONString(String json, String symbol) {
        String name = new JSONObject(json).getString("name");
        String quantity = new JSONObject(json).getString("quantity");

        Security security = new Security(symbol, name, quantity);
        return security;
    }
}
