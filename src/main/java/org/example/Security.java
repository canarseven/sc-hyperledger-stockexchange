/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Security {

    @Property()
    private final String owner;
    @Property()
    private final String symbol;
    @Property()
    private final String name;
    @Property()
    private final String totalSupply;
    @Property()
    private Map<String, Integer> balances;

    public Security(@JsonProperty("owner") final String owner, @JsonProperty("symbol") final String symbol, @JsonProperty("name") final String name, 
            @JsonProperty("totalSupply") String totalSupply){
        this.owner = owner;
        this.symbol = symbol;
        this.name = name;
        this.totalSupply = totalSupply;
        this.balances = new HashMap<String, Integer>();
        this.balances.put(owner, Integer.parseInt(totalSupply));
    }

    public String getOwner() {
        return owner;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public Map<String, Integer> getBalances() {
        return balances;
    }

    public int getBalanceOf(@JsonProperty("hin") String hin){
        return balances.get(hin);
    }

    public void transfer(@JsonProperty("from") String fromHin, @JsonProperty("to") String toHin, @JsonProperty("amount") String amount){
        int intAmount = Integer.parseInt(amount);
        if (balances.get(fromHin) >= intAmount) {
            balances.put(fromHin, balances.get(fromHin) - intAmount);
            balances.put(toHin, balances.get(toHin) + intAmount);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [owner=" + owner + ", symbol=" + symbol 
            + ", name=" + name + ", totalSupply=" + totalSupply + ", balances=" + balances + "]";
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

        return Objects.deepEquals(new String[] {getOwner(), getSymbol(), getName(), getTotalSupply()},
                new String[] {other.getOwner(), other.getSymbol(), other.getName(), other.getTotalSupply()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getSymbol(), getName(), getTotalSupply());
    }
}
