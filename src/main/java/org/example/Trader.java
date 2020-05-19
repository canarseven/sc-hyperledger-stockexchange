/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;
import org.example.Security;

@DataType()
public class Trader {

    @Property()
    private String hin;
    private String balance;
    private List<Security> securities;

    public Trader(String hin, String balance){
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

    public void modBalance(int amount) {
        this.balance += amount;
    }

    public boolean addSecurity(Security newSecurity) {
        for(Security security : securities) {
            if(security.getSymbol().equals(newSecurity.getSymbol())) {
                security.modifyQuantity(newSecurity.getQuantity());
                return true;
            }
        }
        securities.add(newSecurity);
        return true;
    }

    public boolean removeSecurity(String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                securities.remove(security);
                return true;
            }
        }
        return false;
    }

    public boolean isMySecurity(String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public Security getMySecurity(String symbol) {
        for(Security security : securities) {
            if(security.getSymbol().equals(symbol)) {
                return security;
            }
        }
        return null;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public String toJSONString() {
        return new JSONObject(this).toString();
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
            trader.addSecurity(new Security(security.getString("symbol"), security.getString("name"), security.getString("quantity")));
        }
        return trader;
    }
}
