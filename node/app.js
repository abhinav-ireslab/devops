var properties = require('./properties');

var bodyParser = require('body-parser');
var express = require('express');
var app = express();
var bitcore = require('bitcore-lib');
var explorers = require('bitcore-explorers');
var Mnemonic = require('bitcore-mnemonic');
var EthereumBip44 = require('ethereum-bip44');
var ethereumjsUtil = require('ethereumjs-util')

var Address = bitcore.Address;
var PrivateKey = bitcore.PrivateKey;
const unit = bitcore.Unit;

var Web3 = require('web3');
var web3 = new Web3(new Web3.providers.HttpProvider(properties.gethUrl));

var BigNumber = require('bignumber.js');
const EthereumTx = require('ethereumjs-tx')

const insight = new explorers.Insight(properties.insightBitcoinUrl,
    properties.network);
const parseJson = require('parse-json');
app.use(bodyParser.json());

app.get('/', function(req, res) {
    res.send("CoinClaim Node Server is up and running......");
})

var server = app.listen(8081, function() {
    var host = server.address().address
    var port = server.address().port

    console.log("CoinClaim Node Server listening at http://%s:%s", host, port)
});

function getCompanyAccountsRootKey() {
    /** ************************************************************ */
    /* Generating HD Wallet using memonic data */
    /** ************************************************************ */
    var code = new Mnemonic(properties.companyAccountsMemonic);
    var hdpriv = code.toHDPrivateKey(properties.companyAccountsPassword, properties.network);
    return hdpriv;
}

function getUserAccountsRootKey() {
    /** ************************************************************ */
    /* Generating HD Wallet using memonic data */
    /** ************************************************************ */
    var code = new Mnemonic(properties.userAccountsMemonic);
    var hdpriv = code.toHDPrivateKey( properties.userAccountsPassword, properties.network);
    return hdpriv;
}

app.post('/derive_private_key', function(req, res){
    
    console.log('POST REQUEST TO DERIVE PRIVATE KEY');
    
    var body = req.body;
    var clientType = body.clientType;
    
     var hdpriv;
     if(clientType == "COMPANY"){
         hdpriv = getCompanyAccountsRootKey();
     }else if(clientType = "USER"){
         hdpriv = getUserAccountsRootKey();
     }

     var privateKey = getEthereumAddressPrivateKey(hdpriv,body.index);
     var ethereumAddress = getEthereumAddress(hdpriv, body.index);
     
     var bitcoinPrivateKey = getBitcoinPrivateKey(hdpriv, body.index);
     var bitcoinAddress = getBitcoinAddress(hdpriv, body.index);
     var result = {
             uniqueEthereumAddress: ethereumAddress,
             ethereumAddressPrivateKey: privateKey,
             uniqueBitcoinAddress: bitcoinAddress,
             bitcoinAddressPrivateKey: bitcoinPrivateKey,
             clientType: clientType,
             index: body.index
     }
     
     console.log(result);
     res.end(JSON.stringify(result));
});


app
    .post(
        '/address_gen',
        function(req, res) {
            console
                .log('POST REQUEST FOR GENERATING ETHER ADDRESS AND BITCOIN ADDRESS');
            console.log(req.body);
            res.writeHead(200, {
                'Content-Type': 'application/json'
            });
            /** ************************************************************ */
            /* Read request data */
            /** ************************************************************ */
            var body = req.body;
            var index = body.index;
            var clientType = body.clientType;

            var hdpriv;
            if(clientType == "COMPANY"){
                console.log('GENERATING ADDRESSES FOR COMPANY');
                hdpriv = getCompanyAccountsRootKey();
                
            }else if(clientType == "USER"){
                console.log('GENERATING ADDRESSES FOR USER');
                hdpriv = getUserAccountsRootKey();
            }
            
            /** ************************************************************ */
            /* BITCOIN ADDRESS */
            /** ************************************************************ */
            // var hdprivIndex = hdpriv.derive(parseInt(index), false);
            var bitcoinAddress = getBitcoinAddress(hdpriv, index);

            // hdprivIndex.privateKey.toAddress().toString();
            console.log("bitcoin address : " + bitcoinAddress);
            /** ************************************************************ */
            /* ETHEREUM ADDRESS */
            /** ************************************************************ */
            var ethereumAddress = getEthereumAddress(hdpriv, index);

            console.log("ether address : " + ethereumAddress);
            /** ************************************************************ */
            /* GENERATING RESPONSE */
            /** ************************************************************ */
            
            var result = {
                uniqueEthereumAddress: ethereumAddress,
                uniqueBitcoinAddress: bitcoinAddress,
                index: index
            }

            res.end(JSON.stringify(result));

            /** ************************************************************ */
            /* END FUNCTION */
            /** ************************************************************ */
        });

function getBitcoinAddress(hdpriv, index) {
    var hdprivIndex = hdpriv.derive(parseInt(index), false);
    var bitcoinAddress = hdprivIndex.privateKey.toAddress().toString();
    return bitcoinAddress;
}

function getBitcoinPrivateKey(hdpriv, index) {
    var hdprivIndex = hdpriv.derive(parseInt(index), false);
    var bitcoinPrivateKey = hdprivIndex.privateKey.toString();
    return bitcoinPrivateKey;
}

function getEthereumAddress(xpriv, index) {
    var wallet = new EthereumBip44(xpriv);
    var addressVar = wallet.getAddress(index);
    var publickey = ethereumjsUtil.toChecksumAddress(addressVar);
    return publickey;
}

function getEthereumAddressPrivateKey(xpriv, index) {
    var wallet = new EthereumBip44(xpriv);
    var privateKey = wallet.getPrivateKey(index);
    var publickeyHex = privateKey.toString('hex');
    return publickeyHex;
}

/** ************************************************************ */
/* CHECKING ETHEREUM BALANCE BY ADDRESS */
/** ************************************************************ */
app.post('/check_eth_balance', function(req, res) {

    var body = req.body;
    var fromAddress = body.fromAddress;
    
    console.log('POST REQUEST TO CHECK ETH BALANCE FOR ADDRESS - ' + fromAddress);
    
    web3.eth.getBalance(fromAddress, function(error, balance) {

        // console.log('ETH Balance - ' + web3.utils.fromWei(balance, 'ether'));
        console.log('ETH Balance - ' + balance);

        var result = {
            fromAddress: fromAddress,
            amount: balance
        }
        
        res.end(JSON.stringify(result));
    });
});


app.post('/check_token_balance', function(req, res){
    
    var body = req.body;
    var beneficiaryAddress = body.beneficiaryAddress;
    var tokenAddress = body.tokenAddress;
    
    console.log('POST REQUEST TO CHECK TOKEN BALANCE FOR ADDRESS - ' + beneficiaryAddress);
    console.log('Web3.sha3("balanceOf(address)") - ' + web3.utils.sha3("balanceOf(address)"));
    
    web3.eth.getBalance(beneficiaryAddress, function(error, balance) {

        console.log('ETH Balance - ' + web3.utils.fromWei(balance, 'ether'))

        var result = {
            beneficiaryAddress: beneficiaryAddress,
            balance: balance
        }
        
        res.end(JSON.stringify(result));
    });
});


/** ************************************************************ */
/* CHECKING BITCOIN BALANCE BY ADDRESS */
/** ************************************************************ */
app.post('/check_btc_balance', function(req, res) {

    var body = req.body;
    var fromAddress = body.fromAddress;
    var balance = 0;

    console.log('POST REQUEST TO CHECK BTC BALANCE FOR ADDRESS - ' + fromAddress);

    /** ************************************************************ */
    /* CHECKING BITCOIN UNSPEND TRANSACTION OBJECT */
    /** ************************************************************ */
    insight.getUnspentUtxos(fromAddress, function(error, utxos) {
        balance = unit.fromSatoshis(0).toSatoshis();

        /** ************************************************************ */
        /* CALCULATION BALANCE IF TRANSACTION IS CONFIRMED ATLEAST ONE */
        /** ************************************************************ */
        for (var i = 0; i < utxos.length; i++) {
            
            if (utxos[i]['confirmations'] == 'undefined' || utxos[i]['confirmations'] == null || utxos[i]['confirmations'] > 0) {
                balance += unit.fromSatoshis(parseInt(utxos[i]['satoshis']))
                    .toSatoshis();
            }
        }

		if(balance == 0){
			 balance = unit.fromSatoshis(parseInt(utxos[0]['satoshis'])).toSatoshis();
		}

        console.log('BTC Balance - ' + balance)
        
        var result = {
            fromAddress: fromAddress,
            amount: balance
        }
        res.end(JSON.stringify(result));
    });

});

/** ************************************************************ */
/* send ethereum transaction data */
/** ************************************************************ */

app
    .post(
        '/ether_transfer',
        function(req, res) {
            console.log('POST /');
            console.log();
            res.writeHead(200, {
                'Content-Type': 'application/json'
            });

            var body = req.body;
            var to = body.toAddress;
            /** ************************************************************ */
            /* Read request data */
            /** ************************************************************ */

            var from = body.fromAddress;
            var index = body.index;
            var value = body.amount;

            var clientType = body.clientType;
            
            console.log('Ether Transfer request FROM - ' + from +
                ' , TO - ' + to);

            /** ************************************************************ */
            /* Read transaction count and create transaction object */
            /** ************************************************************ */

            web3.eth
                .getTransactionCount(from)
                .then(
                    function(nonce) {

                        var estimate = web3.eth
                            .estimateGas({
                                from: from,
                                to: to,
                                amount: value
                            })
                            .then(
                                function(limit) {

                                    limit = limit * 1;

                                    limit = parseInt(limit);
                                    var gasprice = new BigNumber(
                                        21000000000);

                                    var balance = value -
                                        (gasprice
                                            .times(limit));
                                    var valueToHex = web3.utils
                                        .toHex(balance);
                                    var gasLimitToHex = web3.utils
                                        .toHex(limit);
                                    var gasPriceToHex = web3.utils
                                        .toHex(gasprice);

                                    var txnObj = {
                                        to: to,
                                        value: valueToHex,
                                        gasLimit: gasLimitToHex,
                                        gasPrice: gasPriceToHex,
                                        nonce: nonce
                                    };

                                    console
                                        .log("Transaction Object - " +
                                                JSON
                                                .stringify(txnObj));
                                    
                                    var xpriv;
                                    if(clientType == "COMPANY"){
                                        xpriv = getCompanyAccountsRootKey();
                                    }else if(clientType = "USER"){
                                        xpriv = getUserAccountsRootKey();
                                    }
                                    
                                    const privateKeyHex = getEthereumAddressPrivateKey(
                                        xpriv,
                                        index);
                                    const privateKeyBuffer = Buffer
                                        .from(
                                            privateKeyHex,
                                            'hex')

                                    var transactionHashString;
                                    const tx = new EthereumTx(
                                        txnObj);
                                    tx
                                        .sign(privateKeyBuffer);
                                    const serializedTx = tx
                                        .serialize();

                                    web3.eth
                                        .sendSignedTransaction(
                                            '0x' +
                                            serializedTx
                                            .toString('hex'),
                                            function(
                                                err,
                                                transactionHash) {
                                                if (!err) {
                                                    console
                                                        .log(transactionHash); // "0x7f9fade1c0d57a7af66ab4ead7c2eb7b11a91385"
                                                    transactionHashString = transactionHash;
                                                    code = 200;
                                                    description = "Success";
                                                    errorCode = null;
                                                } else {
                                                    console
                                                        .log(err);
                                                    transactionHashString = "";
                                                    code = 100,
                                                        description = "Error in sending transaction",
                                                        errorCode = 110
                                                }
                                                var result = {
                                                    fromAddress: from,
                                                    index: index,
                                                    amount: value,
                                                    transactionReciept: transactionHashString,
                                                    resultCode: code,
                                                    description: description,
                                                    errorCode: errorCode
                                                }

                                                console
                                                    .log('result - ' +
                                                        JSON
                                                        .stringify(result));

                                                res
                                                    .end(JSON
                                                        .stringify(result));
                                            });

                                });
                    });

            /** ************************************************************ */
            /* send transaction data */
            /** ************************************************************* */
        });



/** ************************************************************ */
/* send bitcoin transaction data */
/** ************************************************************ */

app
    .post(
        '/bitcoin_transfer',
        function(req, res) {

            res.writeHead(200, {
                'Content-Type': 'application/json'
            });

            var body = req.body;

            var bitcoinFromAddress = body.fromAddress;
            var bitcoinToAddress = body.toAddress;
            var index = body.index;
            var txnAmount = body.amount;

            var clientType = body.clientType;
            
            console.log('BITCOIN TRANSFER REQUEST FROM - ' +
                bitcoinFromAddress + ', TO - ' + bitcoinToAddress);

            const unit = bitcore.Unit;
            const minerFee = unit.fromMilis(0.128).toSatoshis();

            const bitcore_transaction = ""; //

            var result = {
                fromAddress: bitcoinFromAddress,
                toAddress: bitcoinToAddress,
                index: index,
                amount: txnAmount,
                transactionReciept: null,
                resultCode: null,
                description: null,
                errorCode: null
            }
            
            insight
                .getUnspentUtxos(
                    bitcoinFromAddress,
                    function(error, utxos) {

                        try {
                            var fromAddress = new Address(
                                bitcoinFromAddress);
                            var toAddress = new Address(
                                bitcoinToAddress);
                            
                            if (error) {
                                result.code = 100;
                                result.description = "Error while calling unspent transaction";
                                result.errorCode = 120;
                                res.end(JSON.stringify(result));
                                
                            }else{
                                /*
								 * if no transactions have happened, there is no
								 * balance on the address.
								 */
                                if (utxos.length == 0) {
                                    result.code = 100;
                                    result.description = "You don't have enough Satoshis to cover the miner fee.";
                                    result.errorCode = 140;
                                    res.end(JSON.stringify(result));
                                    
                                }else{
                                     // Getting balance from utxo's
                                    var readBalance = 0;
                                    for (var i = 0; i < utxos.length; i++) {
                                        if (utxos[i]['confirmations'] == 'undefined' || utxos[i]['confirmations'] == null || utxos[i]['confirmations'] > 0) {
                                            
                                            readBalance += unit.fromSatoshis(parseInt(utxos[i]['satoshis'])).toSatoshis();
                                            console.log("Account Balance - " + readBalance);
                                        }
                                    }

                                    // Balance check
                                   if (txnAmount <= 0 || readBalance <= 0) {
                                        result.code = 100;
                                        result.description = "There is not enough balance in the address.";
                                        result.errorCode = 150;
                                        res.end(JSON.stringify(result));
                                        
                                     // Check account balance & total cost
                                    }else if (readBalance < (txnAmount + minerFee)){
                                           result.code = 100;
                                           result.description = "You don't have enough Satoshi's balance to cover the miner fee.";
                                           result.errorCode = 160;
                                           res.end(JSON.stringify(result));
                                           
                                   }else{    
                                        console.log("Creating Bitcoin Transaction for Transaction Amount - (Satoshis) " + txnAmount);
                                        
                                        /** ************************************************************ */
                                        /* BITCOIN Private key */
                                        /** ************************************************************ */
                                        var xpriv;
                                        if(clientType == "COMPANY"){
                                            xpriv = getCompanyAccountsRootKey();
                                        }else if(clientType = "USER"){
                                            xpriv = getUserAccountsRootKey();
                                        }

                                        const bitcoinPrivatekey = getBitcoinPrivateKey(xpriv, index);
                                        
                                        let bitcore_transaction = new bitcore.Transaction().from(utxos).to(bitcoinToAddress,txnAmount).fee(minerFee).change(bitcoinFromAddress).sign(bitcoinPrivatekey);
                                        bitcore_transaction.serialize();

                                        console.log("BITCORE TRANSACTION - " + bitcore_transaction);
                                        
                                        if (bitcore_transaction.getSerializationError()) {    
                                            let error = bitcore_transaction.getSerializationError().message;
                                            if (error != undefined) {
                                                switch (error) {
                                                    case 'Some inputs have not been fully signed':
                                                        result.code = 100;
                                                        result.description = "Transaction cannot be signed. Please check your private key";
                                                        result.errorCode = 180;
                                                        res.end(JSON.stringify(result));
                                                        break;
                                                    default:
                                                        result.code = 100;
                                                        result.description = "Unknown error";
                                                        result.errorCode = 190;
                                                        res.end(JSON.stringify(result));
                                                }
                                            }
                                        }

                                        /*
										 * broadcast the transaction to the
										 * blockchain
										 */

                                        insight.broadcast(bitcore_transaction,function(error,hash) {
                                            
                                                    console.log('Transaction Hash - ' + hash);
                                                    result.transactionReciept = hash;
                                                    if (error) {
                                                        result.code = 100;
                                                        result.description = "Error in sending transaction";
                                                        result.errorCode = 110;
                                                    } else {
                                                        result.code = 200;
                                                        result.description = "success";
                                                    }
                                                    console.log("returning hash success")
                                                    res.end(JSON.stringify(result));
                                                    return;
                                                });
                                   }                                
                                }// inner else end
                            }// outer else end
                            
                        } catch (e) {
                            console.log("Exception occured " + e);
                            result.errorCode = 100;
                            result.errorDesc = e.stack;
                            result.code = 100;
                            res.end(JSON.stringify(result));
                        }
                    });
        });

app.post('/getTransactionStatus', function(req, res) {

    var hash = req.body.transactionHash;
    console.log("request  recieved for hash :" + hash);

    var receipt = web3.eth.getTransactionReceipt(hash).then((result) => {
        console.log(result);

        var status = result.status;
        var resultStatus = 0;

        if (status != null && status != undefined) {
            status = web3.utils.hexToNumber(status);
            resultStatus = status;
        }

        var successRes = {
            status: resultStatus,
            hash: hash
        };

        res.end(JSON.stringify(successRes));
    });
});

app.post('/check_token_details', async function(req,res) {

    res.writeHead(200, {
        'Content-Type': 'application/json'
    });
    var body = req.body;
   // var fromAddress = body.fromAddress;
    var contractAddress = body.tokenContractAddress; 
    var contractABI = properties.ERC20ABI;
    try{
        var tokenContract = new web3.eth.Contract(contractABI,contractAddress);
    }catch(err){
        // contract address is invalid
        var result = {
            resultCode : 100,
            description : "Invalid contract address.",
            errorCode : 817
        }
        res.end(JSON.stringify(result));
    }

    try{
        var tokenContract = new web3.eth.Contract(contractABI,contractAddress);
       
            var tokenDecimal = await tokenContract.methods.decimals().call();
            // var tokenBalance = await
            // tokenContract.methods.balanceOf(fromAddress).call();
           // console.log("Balance ::"+tokenBalance);
            // var adjustedBalance = tokenBalance / Math.pow(10, tokenDecimal)
            var tokenName = await tokenContract.methods.name().call();
            var tokenSymbol = await tokenContract.methods.symbol().call();
            var response = {
                tokenDecimal : tokenDecimal,
               // balance : adjustedBalance,
               tokenName : tokenName,
               tokenSymbol : tokenSymbol,
               tokenContractAddress : contractAddress,
               contractABI : contractABI
            }
          res.end(JSON.stringify(response));
        
    }catch (e) {
        console.log("Exception occured " +
            e);
            var result = {
                errorCode : 100,
                errorDesc : e.stack,
                code : 100
            }
       
        res.end(JSON.stringify(result));
    }
});