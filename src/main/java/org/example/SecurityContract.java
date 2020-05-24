/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.QueryResultsIteratorWithMetadata;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.owlike.genson.Genson;


@Contract(name = "SecurityContract",
    info = @Info(title = "Security contract",
                description = "My Smart Contract",
                version = "0.0.1",
                license =
                        @License(name = "Apache-2.0",
                                url = ""),
                                contact =  @Contact(email = "can@arseven.at",
                                                name = "hypexchange",
                                                url = "http://arseven.at")))
@Default
public class SecurityContract implements ContractInterface {

    @Transaction()
    public boolean initOrderId(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        if (!orderIdExists(ctx)) {
            String orderId = "0";
            stub.putState("ORDERID", orderId.getBytes(UTF_8));
            return true;
        } else {
            return false;
        }
    }

    @Transaction()
    public String getOrderId(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String jsonString = new String(stub.getState("ORDERID"));
        return jsonString;
    }

    @Transaction()
    public String getAllOrders(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        int max = Integer.parseInt(getOrderId(ctx));
        String allOrders = "";
        for (int i = 0; i < max; i++) {
            String orderId = Integer.toString(i);
            allOrders += orderId + ": " + new String(stub.getState(orderId)) + "; ";
        }
        return allOrders;
    }

    @Transaction()
    public String getMyHin(Context ctx) {
        return Integer.toString(ctx.getClientIdentity());
    }

    @Transaction()
    public Trader getMyAccount(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String traderState = stub.getStringState(getMyHin(ctx));
        Trader myTrader = genson.deserialize(traderState, Trader.class);
        return myTrader;
    }

    @Transaction()
    public String getMyAccString(Context ctx) {
        return getMyAccount(ctx).toString();
    }

    @Transaction()
    public boolean orderIdExists(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState("ORDERID");
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public boolean securityExists(Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(symbol);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public boolean orderExists(Context ctx, String orderHash) {
        return (getAllOrders(ctx).contains(orderHash));
    }

    @Transaction()
    public boolean traderExists(Context ctx, String hin) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(hin);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public String getMyId(Context ctx) {
        return ctx.getClientIdentity().getId();
    }

    @Transaction()
    public String getMyBalance(Context ctx) {
        return getMyAccount(ctx).getBalance();
    }

    @Transaction()
    public void createTrader(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String hin = getMyHin(ctx);
        boolean exists = traderExists(ctx,hin);
        if (exists) {
            throw new RuntimeException("The trader "+hin+" already exists");
        }
        Trader trader = new Trader(hin, "10000");
        String traderState = genson.serialize(hin, traderState);
        stub.putStringState(hin, traderState);
    }

    @Transaction()
    public boolean removeMyTrader(Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        stub.delState(getMyHin(ctx));
        return true;
    }

    @Transaction()
    public boolean removeSecurity(Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();
        stub.delState(symbol);
        return true;
    }


    //@Transaction()
    //public String getMyAccount(Context ctx) {
    //    return new String(ctx.getStub().getState(Integer.toString(ctx.getClientIdentity().getId().hashCode())));
    //}

    @Transaction()
    public void createSecurity(Context ctx, String symbol, String name, String quantity) {
        ChaincodeStub stub = ctx.getStub();
        boolean exists = securityExists(ctx,symbol);
        if (exists) {
            throw new RuntimeException("The security " + symbol + " already exists");
        }
        Security security = new Security(symbol, name, quantity);
        String securityState = genson.serialize(security);
        stub.putStringState(symbol, securityState);

        // ------- Add this newly created Stock to the admin account --------
        Trader trader = getMyAccount(ctx);
        trader.modifySecurityQu(security);

        // -------- Save changes to trader account ---------
        String traderState = genson.serialize(trader);
        stub.putStringState(trader.getHin(), traderState);

        JSONObject obj = new JSONObject();
        obj.put("symbol", symbol);
        obj.put("name", name);
        obj.put("quantity", quantity);
        stub.setEvent("CreatedSecurity", obj.toString().getBytes(UTF_8));
    }
    
    @Transaction()
    public String createOrder(Context ctx, String method, String symbol, String quantity, String price, String timestamp) {
        ChaincodeStub stub = ctx.getStub();
        Order order = new Order(symbol, quantity, price, method, timestamp, "true", "false", getMyHin(ctx));
        Trader trader = getMyAccount(ctx);

        // --------- Check if seller even has the stock -----------
        if (method.equals("1") && !trader.isMySecurity(symbol)) {
            return "The trader does not own this security to sell it.";
        } else {
            // --------- Check if seller has enough amount of stocks -----------
            if (method.equals("1") && Integer.parseInt(trader.getMySecurity(symbol).getQuantity()) < Integer.parseInt(quantity)) {
                return "The trader has not enough funds to sell so many securities.";
            } else if (method.equals("0") && Integer.parseInt(price) * Integer.parseInt(quantity) > Integer.parseInt(trader.getBalance())) {
                return "The trader has not enough funds to buy so many securities.";
            }

            // --------- Check if order already exists -----------
            boolean exists = orderExists(ctx, order.getHash());
            if (exists) {
                return "The order " + order.getHash() + " already exists";
            }

            // --------- Save order ----------
            String orderState = genson.serialize(order);
            stub.putStringState(getOrderId(ctx), order);

            // --------- Increment order id ---------
            int orderId = Integer.parseInt(getOrderId(ctx));
            orderId += 1;
            stub.putState("ORDERID", Integer.toString(orderId).getBytes(UTF_8));

            // --------- create Events instead of return statement ----------
            String output = order.getHash() + ";" + Integer.toString((Integer.parseInt(getOrderId(ctx)) - 1));
            return output;
        }
    }

    @Transaction()
    public boolean settleOrder(Context ctx, String buyId, String sellId, String symbol, String price, String quantity, String buyTimestamp, String sellTimestamp, String sellHin) {
        ChaincodeStub stub = ctx.getStub();
        
        Order buyOrder = new Order(symbol, quantity, price, "0", buyTimestamp, "true", "false", getMyHin(ctx));
        Order sellOrder = new Order(symbol, quantity, price, "1", sellTimestamp, "true", "false", sellHin);

        System.out.println(buyOrder.getHash());
        System.out.println(sellOrder.getHash());

        if (!orderExists(ctx, buyOrder.getHash()) || !orderExists(ctx, sellOrder.getHash())){
            throw new RuntimeException("One of the two orders you provided do not exist. BuyOrder: " + buyOrder.getHash() + ", SellOrder: " + sellOrder.getHash());
        }

        Trader buyer = getMyAccount(ctx);
        Trader seller = genson.deserialize(stub.getStringState(sellHin), Trader.class);

        // -------- Transfer the stock from seller to buyer ----------
        int quant = Integer.parseInt(quantity);
        Security tradedSecurity = genson.deserialize(stub.getStringState(symbol), Security.class);
        buyer.addSecurity(new Security(symbol, tradedSecurity.getName(), quantity));

        quant = Integer.parseInt(quantity);
        quant *= -1;
        quantity = Integer.toString(quant);
        seller.addSecurity(new Security(symbol, tradedSecurity.getName(), quantity));

        if(seller.getMySecurity(symbol).getQuantity().equals("0")){
            seller.removeSecurity(symbol);
        }

        // -------- Transfer the funds from buyer to seller ----------
        int total = quant * Integer.parseInt(price);
        buyer.modBalance(total);

        total *= -1;
        seller.modBalance(total);

        // -------- Modify the orders ------------

        Order modBuyOrder = new Order(symbol, quantity, price, "0", buyTimestamp, "true", "true", getMyHin(ctx));
        Order modSellOrder = new Order(symbol, quantity, price, "1", sellTimestamp, "true", "true", sellHin);

        // ------- Replace original orders with modified orders ---------
        String modBuyOrderState = genson.serialize(modBuyOrder);
        String modSellOrderState = genson.serialize(modSellOrder);
        stub.putStringState(buyId, modBuyOrderState);
        stub.putStringState(sellId, modSellOrderState);

        // ------- Emit event instead of return statement ---------
        return true;
    }

    @Transaction()
    public Security getSecurity(Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();

        boolean exists = securityExists(ctx,symbol);
        if (!exists) {
            throw new RuntimeException("The security " + symbol + " does not exist");
        }

        Security security = genson.deserialize(stub.getStringState(symbol), Security.class);
        return security;
    }

    /*@Transaction()
    private void updateSecurity(Context ctx, String symbol, String name) {
        boolean exists = securityExists(ctx,symbol);
        if (!exists) {
            throw new RuntimeException("The security "+symbol+" does not exist");
        }
        Security security = new Security(symbol, name, );

        ctx.getStub().putState(symbol, asset.toJSONString().getBytes(UTF_8));
    }*/

    @Transaction()
    private void deleteSecurity(Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();

        boolean exists = securityExists(ctx,symbol);
        if (!exists) {
            throw new RuntimeException("The asset "+symbol+" does not exist");
        }
        stub.delState(symbol);
    }

}
