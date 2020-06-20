/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Trader {

    @Property()
    private final String hin;

    @Property()
    private String balance;

    @Property()
    private String isMember;

    public Trader(@JsonProperty("hin") final String hin, @JsonProperty("balance") String balance){
        this.hin = hin;
        this.balance = balance;
        this.isMember = "true";
    }

    public String getHin() {
        return hin;
    }

    public String getBalance() {
        return balance;
    }

    public String getStatus() {
        return isMember;
    }

    public void modBalance(@JsonProperty("amount") int amount) {
        this.balance += amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHin(), getBalance(), getStatus());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [hin=" + hin + ", balance="
                + balance + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Trader other = (Trader) obj;

        return Objects.deepEquals(new String[] {getHin()}, new String[] {other.getHin()});
    }

    public static Trader fromJSONString(String json) {
        JSONObject jobj = new JSONObject(json);
        String hin = jobj.getString("hin");
        String balance = jobj.getString("balance");
        Trader trader = new Trader(hin, balance);
        return trader;
    }
}
