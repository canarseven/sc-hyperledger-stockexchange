'use strict';

module.exports.info  = 'Creating Sell Orders';

const fs = require('fs');
var encode = require( 'hashcode' ).hashCode;

const contractID = 'hypexchange';
const version = 'v1';

let bc, ctx, clientArgs, clientIdx;

const symbols = ["AAPL", "SNAP", "FB", "TMUS", "GOOA", "NVDA", "PNW", "SPLK", "MLK", "VLK"]
const names = ["Apple Inc.", "Snap Inc.", "Facebook", "TMobile US", "Alphabet Inc.", "NVIDIA", "Palo Alto Networks", "Splunk Inc.", "Mlunk Inc.", "Vlunk Inc."]

var sellTimestamps = [];

var sellOrders = [];

module.exports.init = async function(blockchain, context, args) {
    bc = blockchain;
    ctx = context;
    clientArgs = args;
    clientIdx = context.clientIdx.toString();

    sleep(5000);

    return Promise.resolve();
};

function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
}

module.exports.run = function() {
    const symbol = clientArgs.symbol;

    const quantity = clientArgs.quantity;
    const price = clientArgs.price;
    
    // ----- Create Sell Order -----
    var sellTimestamp = Date.now().toString();
    sellTimestamps.push(sellTimestamp);

    var myArgs = {
        chaincodeFunction: 'createOrder',
        chaincodeArguments: ['1', symbol, quantity, price, sellTimestamp]
    };
    try {
        //console.log(`${clientIdx} SELLING ${quantity} ${symbol}@${price}€`);
        return bc.invokeSmartContract(ctx, contractID, version, myArgs);
        /*console.log("Sleeeping");
        sleep(3000);
        console.log("Woke up!");*/
    } catch (error) {
        return console.log(`SELLORDER ${error}`);
    }
};

async function asyncForEach(array, callback) {
    for (let index = 0; index < array.length; index++) {
      await callback(array[index], index, array);
    }
}

module.exports.end = async function() {
    const symbol = clientArgs.symbol;

    const quantity = clientArgs.quantity;
    const price = clientArgs.price;

    //get traderHin
    var myArgs = {
        chaincodeFunction: 'getMyHin',
        chaincodeArguments: []
    };
    var myHinTx = await bc.bcObj.querySmartContract(ctx, contractID, version, myArgs);
    var myHin = myHinTx[0].status.result.toString('utf8')
    console.log(myHin);

    var myArgs = {
        chaincodeFunction: 'getAllOrders',
        chaincodeArguments: []
    };
    var ordersTx = await bc.bcObj.querySmartContract(ctx, contractID, version, myArgs);

    //prints all orders as json
    var ordersString = ordersTx[0].status.result.toString('utf8')
    var ordersJSON = JSON.parse(ordersString);

    console.log(ordersJSON);

    // Replicate sell orders that have been stored on blockchain
    await asyncForEach(sellTimestamps, async (element) => {
        let hashMe = symbol + quantity +  price +  "1" +  element + "true" + "false" + myHin.toString();
        //console.log(hashMe);
        let myArgs = {
            chaincodeFunction: 'getHashCode',
            chaincodeArguments: [hashMe]
        };
        let hashCodeTx = await bc.bcObj.querySmartContract(ctx, contractID, version, myArgs);
        let myHash = hashCodeTx[0].status.result.toString('utf8')

        for (let i = 0; i < ordersJSON.length; i++){
            if (ordersJSON[i].hash == myHash) {
                let orderObject = {
                    "orderId": i,
                    "hash": myHash,
                    "symbol": symbol,
                    "quantity": quantity,
                    "price": price,
                    "method": "1",
                    "timestamp": element,
                    "valid": "true",
                    "processed": "false",
                    "traderHin": myHin.toString()
                }
                sellOrders.push(orderObject);
                //console.log(`${myHash}: ${hashMe}`);
            }
        }
    });

    const sellStream = fs.createWriteStream('selltimestamps.txt');
    const sellPath = sellStream.path;

    sellOrders.forEach(value => sellStream.write(`${JSON.stringify(value)}\n`));
    sellStream.on('finish', () => {
        console.log(`wrote all the array data to file ${sellPath}`);
    });
    sellStream.on('error', (err) => {
        console.error(`There is an error writing the file ${sellPath} => ${err}`)
    });
    sellStream.end();

    return Promise.resolve();
};