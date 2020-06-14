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
    @Transaction()
    public void init(final Context ctx) {
        initOrderId(ctx);
    }

    @Transaction()
    public boolean initOrderId(final Context ctx) {
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
    public String getOrderId(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String jsonString = new String(stub.getState("ORDERID"));
        return jsonString;
    }

    @Transaction()
    public String getAllOrders(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        int max = Integer.parseInt(getOrderId(ctx));
        List<String> allOrders = new ArrayList<String>();
        for (int i = 0; i < max; i++) {
            String orderId = Integer.toString(i);
            allOrders.add(new String(stub.getState(orderId)));
        }
        //logger.info(allOrders);
        return allOrders.toString();
    }

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
    public List<Security> getMySecurities(final Context ctx) {
        return getMyAccount(ctx).getSecurities();
    }

    @Transaction()
    public boolean orderIdExists(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState("ORDERID");
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public boolean securityExists(final Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();
        byte[] buffer = stub.getState(symbol);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public boolean orderExists(final Context ctx, String orderHash) {
        return (getAllOrders(ctx).contains(orderHash));
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

    @Transaction()
    public boolean removeSecurity(final Context ctx, String symbol) {
        ChaincodeStub stub = ctx.getStub();
        stub.delState(symbol);
        return true;
    }

    @Transaction()
    public void createSecurity(final Context ctx, String symbol, String name, String quantity) {
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
        trader.modifySecurityQuantity(security);

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
    public String createOrder(final Context ctx, String orderId, String method, String symbol, String quantity, String price, String timestamp) {
        ChaincodeStub stub = ctx.getStub();
        Order order = new Order(orderId, symbol, quantity, price, method, timestamp, "true", "false", getMyHin(ctx));
        Trader trader = getMyAccount(ctx);

        // --------- Save order ----------
        String orderState = genson.serialize(order);
        stub.putStringState(getOrderId(ctx), orderState);

        // --------- Emit Event ----------
        JSONObject obj = new JSONObject();
        obj.put("symbol", symbol);
        obj.put("method", method);
        obj.put("quantity", quantity);
        obj.put("price", price);
        obj.put("timestamp", timestamp);
        stub.setEvent("CreatedOrder", obj.toString().getBytes(UTF_8));
    }

    @Transaction()
    public boolean settleOrder(final Context ctx, String buyId, String sellId, String symbol, String price, String quantity, String buyTimestamp, String sellTimestamp, String sellHin) {
        ChaincodeStub stub = ctx.getStub();
        
        Order buyOrder = new Order(buyId, symbol, quantity, price, "0", buyTimestamp, "true", "false", getMyHin(ctx));
        Order sellOrder = new Order(sellId, symbol, quantity, price, "1", sellTimestamp, "true", "false", sellHin);

        // ------- Check if orders exist ------
        if (!orderExists(ctx, buyOrder.getHash()) || !orderExists(ctx, sellOrder.getHash())){
            throw new RuntimeException("One of the two orders you provided do not exist. BuyOrder: " + buyOrder.getHash() + ", SellOrder: " + sellOrder.getHash());
        }

        Trader buyer = getMyAccount(ctx);
        Trader seller = Trader.fromJSONString(stub.getStringState(sellHin));

        // -------- Transfer the stock from seller to buyer ----------
        int quant = Integer.parseInt(quantity);
        Security tradedSecurity = genson.deserialize(stub.getStringState(symbol), Security.class);
        buyer.modifySecurityQuantity(new Security(symbol, tradedSecurity.getName(), quantity));

        quant = Integer.parseInt(quantity);
        quant *= -1;
        quantity = Integer.toString(quant);
        seller.modifySecurityQuantity(new Security(symbol, tradedSecurity.getName(), quantity));

        if(seller.getMySecurity(symbol).getQuantity().equals("0")){
            seller.removeSecurity(symbol);
        }

        // -------- Transfer the funds from buyer to seller ----------
        int total = quant * Integer.parseInt(price);
        buyer.modBalance(total);

        total *= -1;
        seller.modBalance(total);

        // -------- Modify the orders ------------

        Order modBuyOrder = new Order(buyId, symbol, quantity, price, "0", buyTimestamp, "true", "true", getMyHin(ctx));
        Order modSellOrder = new Order(sellId, symbol, quantity, price, "1", sellTimestamp, "true", "true", sellHin);

        // ------- Replace original orders with modified orders ---------
        String modBuyOrderState = genson.serialize(modBuyOrder);
        String modSellOrderState = genson.serialize(modSellOrder);
        stub.putStringState(buyId, modBuyOrderState);
        stub.putStringState(sellId, modSellOrderState);

        // --------- Emit Event ----------
        JSONObject obj = new JSONObject();
        obj.put("buyId", buyId);
        obj.put("sellId", sellId);
        stub.setEvent("SettledOrder", obj.toString().getBytes(UTF_8));
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

}
