/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;


import com.owlike.genson.Genson;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.QueryResultsIteratorWithMetadata;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;


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

    private final Genson genson = new Genson();
    private static final Logger logger = Logger.getLogger(SecurityContract.class.getName());

    /**
     * Placeholder for init function
     *
     * @param ctx   the transaction context
     */
    /*@Transaction()
    public void init(final Context ctx) {
    }*/

    @Transaction()
    public String getHashCode(final Context ctx, String hashMe) {
        return Integer.toString(hashMe.hashCode());
    }

    @Transaction()
    public String getMyId(final Context ctx) {
        return ctx.getClientIdentity().getId();
    }

    @Transaction()
    public String getMyHin(final Context ctx) {
        return Integer.toString(ctx.getClientIdentity().getId().hashCode());
    }

    @Transaction()
    public Trader getMyAccount(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String traderState = stub.getStringState(getMyHin(ctx));
        Trader myTrader = Trader.fromJSONString(traderState);
        return myTrader;
    }

    @Transaction()
    public String getMyAccString(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        return stub.getStringState(getMyHin(ctx));
        //return getMyAccount(ctx).toString();
    }

    @Transaction()
    public String getMyBalance(final Context ctx) {
        return getMyAccount(ctx).getBalance();
    }

    @Transaction()
    public boolean traderExists(final Context ctx, String hin) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(hin);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public Trader createTrader(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String hin = getMyHin(ctx);
        boolean exists = traderExists(ctx,hin);
        if (exists) {
            throw new RuntimeException("The trader "+hin+" already exists");
        }
        Trader trader = new Trader(hin, "10000");
        String traderState = genson.serialize(trader);
        stub.putStringState(hin, traderState);
        logger.info("CREATED TRADER: "+traderState);
        return trader;
    }

    @Transaction()
    public boolean removeMyTrader(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        stub.delState(getMyHin(ctx));
        return true;
    }

    // ----------------- Security (Stocks) related functions -------------------

    @Transaction()
    public boolean securityExists(final Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(symbol);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public Security getSecurity(final Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();

        boolean exists = securityExists(ctx,symbol);
        if (!exists) {
            throw new RuntimeException("The security " + symbol + " does not exist");
        }

        Security security = genson.deserialize(stub.getStringState(symbol), Security.class);
        return security;
    }

    @Transaction()
    private void deleteSecurity(final Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();

        boolean exists = securityExists(ctx,symbol);
        if (!exists) {
            throw new RuntimeException("The asset "+symbol+" does not exist");
        }
        stub.delState(symbol);
    }

    @Transaction()
    public void createSecurity(final Context ctx, String symbol, String name, String quantity) {
        ChaincodeStub stub = ctx.getStub();
        Trader trader = getMyAccount(ctx);
        boolean exists = securityExists(ctx,symbol);
        if (exists) {
            throw new RuntimeException("The security " + symbol + " already exists");
        }
        Security security = new Security(trader.getHin(), symbol, name, quantity);
        String securityState = genson.serialize(security);
        stub.putStringState(symbol, securityState);

        JSONObject obj = new JSONObject();
        obj.put("symbol", symbol);
        obj.put("name", name);
        obj.put("quantity", quantity);
        stub.setEvent("CreatedSecurity", obj.toString().getBytes(UTF_8));
    }

    // ----------------- Order related functions -------------------

    @Transaction()
    public boolean orderExists(final Context ctx, String orderId) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(orderId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public Order getOrder(final Context ctx, String orderId) {
        ChaincodeStub stub = ctx.getStub();

        boolean exists = orderExists(ctx, orderId);
        if (!exists) {
            throw new RuntimeException("The order " + orderId + " does not exist");
        }

        Order order = genson.deserialize(stub.getStringState(orderId), Order.class);
        return order;
    }

    @Transaction()
    public void createOrder(final Context ctx, String orderId, String method, String symbol, String quantity, String price, String timestamp) {
        ChaincodeStub stub = ctx.getStub();
        Trader trader = getMyAccount(ctx);
        if (trader == null) {
            throw new RuntimeException("The Trader is not registered with this exchange.");
        }
        Order newOrder = new Order(orderId, symbol, quantity, price, method, timestamp, "true", "false", trader.getHin());

        // --------- Save order ----------
        String orderString = genson.serialize(newOrder);
        stub.putStringState(orderId, orderString);

        // --------- Emit Event ----------
        JSONObject obj = new JSONObject();
        obj.put("symbol", symbol);
        obj.put("method", method);
        obj.put("quantity", quantity);
        obj.put("price", price);
        obj.put("timestamp", timestamp);
        obj.put("owner", trader.getHin());
        stub.setEvent("CreatedOrder", obj.toString().getBytes(UTF_8));
    }

    @Transaction()
    public void settleOrder(final Context ctx, String buyId, String sellId, String symbol, String price, String quantity, String buyTimestamp, String sellTimestamp, String sellHin) {
        ChaincodeStub stub = ctx.getStub();
        Trader buyer = getMyAccount(ctx);
        Trader seller = Trader.fromJSONString(stub.getStringState(sellHin));
        Order buyOrder = new Order(buyId, symbol, quantity, price, "0", buyTimestamp, "true", "false", buyer.getHin());
        Order sellOrder = new Order(sellId, symbol, quantity, price, "1", sellTimestamp, "true", "false", sellHin);

        // ------- Check if orders exist ------
        if (orderExists(ctx, buyId) == false || orderExists(ctx, sellId) == false){
            throw new RuntimeException("One of the two orders you provided do not exist. BuyOrder: " + buyOrder.getHash() + ", SellOrder: " + sellOrder.getHash());
        }

        // -------- Transfer the stock from seller to buyer ----------
        Security tradedSecurity = genson.deserialize(stub.getStringState(symbol), Security.class);

        int quant = Integer.parseInt(quantity);
        quant *= -1;
        quantity = Integer.toString(quant);

        // -------- Transfer the funds from buyer to seller ----------
        int total = quant * Integer.parseInt(price);
        buyer.modBalance(total);
        total *= -1;                //multiply by -1 due to the (-) quant
        seller.modBalance(total);

        // -------- Modify the orders -----------
        buyOrder = new Order(buyId, symbol, quantity, price, "0", buyTimestamp, "true", "true", buyer.getHin());
        sellOrder = new Order(sellId, symbol, quantity, price, "1", sellTimestamp, "true", "true", sellHin);

        // ------- Replace original orders with modified orders ---------
        String orderState = genson.serialize(buyOrder);
        stub.putStringState(buyId, orderState);
        orderState = genson.serialize(sellOrder);
        stub.putStringState(sellId, orderState);

        // --------- Emit Event ----------
        JSONObject obj = new JSONObject();
        obj.put("buyId", buyId);
        obj.put("sellId", sellId);
        stub.setEvent("SettledOrder", obj.toString().getBytes(UTF_8));
    }

}
