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
    private List<Security> securities;

    public Trader(@JsonProperty("hin") final String hin, @JsonProperty("balance") String balance){
        this.hin = hin;
        this.balance = balance;
        this.securities = new ArrayList<Security>();
    }

    public String getHin() {
        return hin;
    }

    public String getBalance() {
        return balance;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public void modBalance(@JsonProperty("amount") int amount) {
        this.balance += amount;
    }

    public boolean modifySecurityQuantity(Security newSecurity) {
        for(Security security : securities) {
            if(security.getSymbol().equals(newSecurity.getSymbol())) {
                security.modifyQuantity(newSecurity.getQuantity());
                return true;
            }
        }
        securities.add(newSecurity);
        return true;
    }

    public boolean removeSecurity(@JsonProperty("symbol") String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                securities.remove(security);
                return true;
            }
        }
        return false;
    }

    public boolean isMySecurity(@JsonProperty("symbol") String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public Security getMySecurity(@JsonProperty("symbol") String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                return security;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHin(), getBalance(), getSecurities());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [hin=" + hin + ", balance="
                + balance + ", securities=" + securities + "]";
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
        JSONArray securities = jobj.getJSONArray("securities");

        Trader trader = new Trader(hin, balance);
        int n = securities.length();
        for (int i = 0; i < n; ++i) {
            JSONObject security = securities.getJSONObject(i);
            trader.modifySecurityQuantity(new Security(security.getString("symbol"), security.getString("name"), security.getString("quantity")));
        }
        return trader;
    }
}
