'use strict';

module.exports.info  = 'Settling Orders';

const fs = require('fs');

const contractID = 'hypexchange';
const version = 'v1';

let bc, ctx, clientArgs, clientIdx;

var sellOrders = [];
var buyOrders = [];

module.exports.init = async function(blockchain, context, args) {
    bc = blockchain;
    ctx = context;
    clientArgs = args;
    clientIdx = context.clientIdx.toString();


    var data = fs.readFileSync('selltimestamps.txt', 'utf8');
    var splitted = data.toString().split("\n");
    console.log(splitted);
    for (let i = 0; i<(splitted.length - 1); i++) {
        sellOrders.push(JSON.parse(splitted[i]));
    }

    data = fs.readFileSync('buytimestamps.txt', 'utf8');
    splitted = data.toString().split("\n");
    for (let i = 0; i<(splitted.length - 1); i++) {
        buyOrders.push(JSON.parse(splitted[i]));
    }

    //get the length of each timestamp arrays
    var sells = sellOrders.length;
    var buys = buyOrders.length;

    // get the number of least elements
    var min = Math.min(sells, buys);

    // set both to the same length, to avoid error during run()
    sellOrders.length = min;
    buyOrders.length = min;

    console.log(sellOrders);
    console.log(buyOrders);

    return Promise.resolve();
};

module.exports.run = function() {
    const symbol = clientArgs.symbol;
    const quantity = clientArgs.quantity;
    const price = clientArgs.price;
    // ----- Settle Order -----
    //settleOrder(final Context ctx, String buyId, String sellId, String symbol, String price, String quantity, String buyTimestamp, String sellTimestamp, String sellHin)
    try {
        var buyOrder = buyOrders[0];
        buyOrders.shift();
        var sellOrder = sellOrders[0];
        sellOrders.shift();
    } catch (error) {
        console.log(error);
        return 0;
    }

    if (buyOrder) {
        var myArgs = {
            chaincodeFunction: 'settleOrder',
            chaincodeArguments: [buyOrder.orderId.toString(), sellOrder.orderId.toString(), symbol, price, quantity, buyOrder.timestamp, sellOrder.timestamp, sellOrder.traderHin]
        };
        try {
            console.log(`${buyOrder.orderId} SETTLING WITH ${sellOrder.orderId}...`);
            return bc.invokeSmartContract(ctx, contractID, version, myArgs);
        } catch (error) {
            return console.log(`SETTLEMENT ${error}`);
        }
    } else {
        return 0;
    }
};

module.exports.end = async function() {

    return Promise.resolve();
};