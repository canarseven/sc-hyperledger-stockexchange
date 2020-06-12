/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Order {

    @Property()
    private final String orderHash;

    public Order(@JsonProperty("orderId") final String orderId, @JsonProperty("symbol") final String symbol,
            @JsonProperty("quantity") final String quantity, @JsonProperty("price") final String price,
            @JsonProperty("method") final String method, @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("valid") final String valid, @JsonProperty("processed") final String processed,
            @JsonProperty("traderHin") final String traderHin){
        final String orderString = orderId + symbol + quantity +  price +  method +  timestamp + valid + processed + traderHin;
        this.orderHash = Integer.toString(orderString.hashCode());
    }

    public String getHash() {
        return orderHash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Order other = (Order) obj;

        return Objects.deepEquals(new String[] {getHash()},
                new String[] {other.getHash()});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [orderHash=" + orderHash + "]";
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
