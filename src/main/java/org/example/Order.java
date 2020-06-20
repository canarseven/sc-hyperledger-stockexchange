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
    private final String orderId;

    @Property()
    private String orderHash;

    public Order(@JsonProperty("orderId") final String orderId, @JsonProperty("symbol") final String symbol,
            @JsonProperty("quantity") final String quantity, @JsonProperty("price") final String price,
            @JsonProperty("method") final String method, @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("valid") final String valid, @JsonProperty("processed") final String processed,
            @JsonProperty("traderHin") final String traderHin){
        final String orderString = orderId + symbol + quantity +  price +  method +  timestamp + valid + processed + traderHin;
        this.orderHash = Integer.toString(orderString.hashCode());
        this.orderId = orderId;
    }

    public String getHash() {
        return orderHash;
    }

    public String getOrderId() {
        return orderId;
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

        return Objects.deepEquals(new String[] {getOrderId(), getHash()},
                new String[] {other.getOrderId(), other.getHash()});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [orderId =" + orderId + ", orderHash=" + orderHash + "]";
    }
}
