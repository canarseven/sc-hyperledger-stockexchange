'use strict';

module.exports.info  = 'Template callback';

const contractID = 'hypexchange';
const version = 'v1';

let bc, ctx, clientArgs, clientIdx;

const symbols = ["AAPL", "SNAP", "FB", "TMUS", "GOOA", "NVDA", "PNW", "SPLK", "MLK", "VLK"]
const names = ["Apple Inc.", "Snap Inc.", "Facebook", "TMobile US", "Alphabet Inc.", "NVIDIA", "Palo Alto Networks", "Splunk Inc.", "Mlunk Inc.", "Vlunk Inc."]

module.exports.init = async function(blockchain, context, args) {
    bc = blockchain;
    ctx = context;
    clientArgs = args;
    clientIdx = context.clientIdx.toString();

    // -------------- createTrader() --------------
    var myArgs = { 
        chaincodeFunction: 'createTrader',
        chaincodeArguments: []
    };

    try {
        console.log(`${clientIdx}: Creating Trader`);
        await bc.invokeSmartContract(ctx, contractID, version, myArgs);
    } catch (error) {
        console.log(error);
    }

    if (clientIdx == '0') {
        for (let i=0; i<clientArgs.securities; i++) {
            try {
                const symbol = symbols[i];
                const name = names[i];
                const quantity = '1000';
                console.log(`Client ${clientIdx}: Creating asset ${symbol}`);
                const myArgs = {
                    chaincodeFunction: 'createSecurity',
                    chaincodeArguments: [symbol, name, quantity]
                };
                await bc.invokeSmartContract(ctx, contractID, version, myArgs);
            } catch (error) {
                console.log(`Client ${clientIdx}: Smart Contract threw with error: ${error}` );
            }
        }
    }

    return Promise.resolve();
};

module.exports.run = function() {
    var randomIndex = Math.floor(Math.random() * (clientArgs.securities-1))
    const symbol = symbols[randomIndex];
    const myArgs = {
        chaincodeFunction: 'getSecurity',
        chaincodeArguments: [`${symbol}`]
    };
    return bc.bcObj.querySmartContract(ctx, contractID, version, myArgs);
};

module.exports.end = async function() {
    return Promise.resolve();
};