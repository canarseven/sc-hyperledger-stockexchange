'use strict';

module.exports.info  = 'Creating Buy Orders';

const fs = require('fs');
var encode = require( 'hashcode' ).hashCode;

const contractID = 'hypexchange';
const version = 'v1';

let bc, ctx, clientArgs, clientIdx;

const symbols = ["AAPL", "SNAP", "FB", "TMUS", "GOOA", "NVDA", "PNW", "SPLK", "MLK", "VLK"]
const names = ["Apple Inc.", "Snap Inc.", "Facebook", "TMobile US", "Alphabet Inc.", "NVIDIA", "Palo Alto Networks", "Splunk Inc.", "Mlunk Inc.", "Vlunk Inc."]

var buyTimestamps = [];

var buyOrders = [];

module.exports.init = async function(blockchain, context, args) {
    bc = blockchain;
    ctx = context;
    clientArgs = args;
    clientIdx = context.clientIdx.toString();

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
    
    // ----- Create Buy Order -----
    //createOrder(final Context ctx, String method, String symbol, String quantity, String price, String timestamp)
    var buyTimestamp = Date.now().toString();
    buyTimestamps.push(buyTimestamp);

    var myArgs = {
        chaincodeFunction: 'createOrder',
        chaincodeArguments: ['0', symbol, quantity, price, buyTimestamp]
    };
    try {
        //console.log(`${clientIdx} BUYING ${quantity} ${symbol}@${price}€`);
        return bc.invokeSmartContract(ctx, contractID, version, myArgs);
    } catch (error) {
        return console.log(`BUYORDER ${error}`);
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

    // Replicate buy orders that have been stored on blockchain
    await asyncForEach(buyTimestamps, async (element) => {
        let hashMe = symbol + quantity +  price +  "0" +  element + "true" + "false" + myHin.toString();
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
                    "method": "0",
                    "timestamp": element,
                    "valid": "true",
                    "processed": "false",
                    "traderHin": myHin.toString()
                }
                buyOrders.push(orderObject);
                //console.log(`${myHash}: ${hashMe}`);
            }
        }
    });
    console.log(buyOrders);
    
    const buyStream = fs.createWriteStream('buytimestamps.txt');
    const buyPath = buyStream.path;

    // write each value of the array on the file breaking line
    buyOrders.forEach(value => buyStream.write(`${JSON.stringify(value)}\n`));
    // the finish event is emitted when all data has been flushed from the stream
    buyStream.on('finish', () => {
        console.log(`wrote all the array data to file ${buyPath}`);
    });
    // handle the errors on the write process
    buyStream.on('error', (err) => {
        console.error(`There is an error writing the file ${buyPath} => ${err}`)
    });
    // close the stream
    buyStream.end();

    return Promise.resolve();
};