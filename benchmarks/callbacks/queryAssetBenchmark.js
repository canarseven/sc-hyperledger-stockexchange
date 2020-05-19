'use strict';

module.exports.info  = 'Template callback';

const contractID = 'hypexchange';
const version = '1';

let bc, ctx, clientArgs, clientIdx;

module.exports.init = async function(blockchain, context, args) {
    bc = blockchain;
    ctx = context;
    clientArgs = args;
    clientIdx = context.clientIdx.toString();

    // -------------- initOrderId() --------------
    myArgs = { 
        chaincodeFunction: 'initOrderId',
        invokerIdentity: 'admin',
        chaincodeArguments: []
    };
    try {
        await bc.bcObj.invokeSmartContract(ctx, contractID, version, myArgs);
    } catch (error) {
        console.log(error);
    }

    // -------------- createTrader() --------------
    myArgs = { 
        chaincodeFunction: 'createTrader',
        invokerIdentity: 'admin',
        chaincodeArguments: []
    };
    try {
        await bc.bcObj.invokeSmartContract(ctx, contractID, version, myArgs);
    } catch (error) {
        console.log(error);
    }

    // -------------- createSecurities() try with 10 first and then 2188 from asx --------------



    for (let i=0; i<clientArgs.assets; i++) {
        try {
            const symbol = 'AAPL';
            const name = 'Apple Inc.';
            const quantity = '1000';
            console.log(`Client ${clientIdx}: Creating asset ${symbol}`);
            const myArgs = {
                chaincodeFunction: 'createSecurity',
                invokerIdentity: 'admin',
                chaincodeArguments: [symbol, name, quantity]
            };
            await bc.bcObj.invokeSmartContract(ctx, contractID, version, myArgs);
        } catch (error) {
            console.log(`Client ${clientIdx}: Smart Contract threw with error: ${error}` );
        }
    }
};

module.exports.run = function() {
    const symbol = 'AAPL';
    const myArgs = {
        chaincodeFunction: 'getSecurity',
        invokerIdentity: 'admin',
        chaincodeArguments: [`${symbol}`]
    };
    return bc.bcObj.querySmartContract(ctx, contractID, version, myArgs);
};

module.exports.end = async function() {
    const symbol = 'AAPL';
    const secArgs = {
        chaincodeFunction: 'removeSecurity',
        invokerIdentity: 'admin',
        chaincodeArguments: [symbol]
    };
    await bc.bcObj.querySmartContract(ctx, contractID, version, secArgs);

    const traderArgs = {
        chaincodeFunction: 'removeTrader',
        invokerIdentity: 'admin',
        chaincodeArguments: []
    };
    return bc.bcObj.querySmartContract(ctx, contractID, version, traderArgs);
};