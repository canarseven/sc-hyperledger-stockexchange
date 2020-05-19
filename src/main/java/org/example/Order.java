/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class Order {

    @Property()
    private String orderHash;

    public Order(String symbol, String quantity, String price, String method, String timestamp, String valid, String processed, String traderHin){
        String preString = symbol + quantity +  price +  method +  timestamp + valid + processed + traderHin;
        this.orderHash = Integer.toString(preString.hashCode());
    }

    public String getHash() {
        return orderHash;
    }

    public String toJSONString() {
        return new JSONObject(this).toString();
    }

    /*public static Order fromJSONString(String json) {
        JSONObject jobject = new JSONObject(json);
        String symbol = jobject.getString("symbol");
        String quantity = jobject.getString("quantity");
        String price = jobject.getString("price");
        String method = jobject.getString("method");
        String timestamp = jobject.getString("timestamp");
        String valid = jobject.getString("valid");
        String processed = jobject.getString("processed");
        String trader = jobject.getString("hin");
        Order order = new Order(symbol, quantity, price, method, timestamp, valid, processed, trader);
        return order;
    }*/
}
