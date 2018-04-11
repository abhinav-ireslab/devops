var properties = require('./properties');
var hdPrivXprivkey = properties.hdPrivXprivkey;

var bodyParser = require('body-parser');
var express = require('express');
var app = express();
var bitcore = require('bitcore-lib');
var explorers = require('bitcore-explorers');
var Mnemonic = require('bitcore-mnemonic');

var Address = bitcore.Address;
var PrivateKey = bitcore.PrivateKey;
const unit = bitcore.Unit;

var BigNumber = require('bignumber.js');

const insight = new explorers.Insight(properties.insightBitcoinUrl,
		properties.network);
const parseJson = require('parse-json');
app.use(bodyParser.json());

app.get('/', function(req, res) {
	res.send("Great News server is up .........");
})

var server = app.listen(8081, function() {
	var host = server.address().address
	var port = server.address().port

	console.log("Example app listening at http://%s:%s", host, port)
});

function getRootKey() {
	/** ************************************************************ */
	/* Generating HD Wallet using memonic data */
	/** ************************************************************ */
	var memonicString = properties.memonic;
	var code = new Mnemonic(memonicString);
	var passphrase = properties.password;
	var network = properties.network
	var hdpriv = code.toHDPrivateKey(passphrase, network);
	return hdpriv;
}

app
		.post(
				'/address_gen',
				function(req, res) {
					console
							.log('POST REQUEST FOR GENERATING ETHER ADDRESS AND BITCOIN ADDRESS');
					console.log(req.body);
					res.writeHead(200, {
						'Content-Type' : 'application/json'
					});
					/** ************************************************************ */
					/* Read request data */
					/** ************************************************************ */
					var body = req.body;
					var index = body.index;

					console.log("index : " + index)

					var hdpriv = getRootKey();
					/** ************************************************************ */
					/* BITCOIN ADDRESS */
					/** ************************************************************ */
					// var hdprivIndex = hdpriv.derive(parseInt(index), false);
					var bitcoinAddress = getBitcoinAddress(hdpriv, index);

					// hdprivIndex.privateKey.toAddress().toString();
					console.log("bitcoin address : " + bitcoinAddress);

					/** ************************************************************ */
					/* GENERATING RESPONSE */
					/** ************************************************************ */

					var result = {
						/* uniqueEtherAddress : etherAddress, */
						uniqueBitcoinAddress : bitcoinAddress,
						index : index
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

/** ************************************************************ */
/* CHECKING BITCOIN BALANCE BY ADDRESS */
/** ************************************************************ */
app.post('/checkbtcbalance', function(req, res) {

	var body = req.body;
	var fromAddress = body.fromAddress;
	var balance = 0;

	console.log('POST REQUEST TO CHECK BALANCE FOR ADDRESS - ' + fromAddress);

	/** ************************************************************ */
	/* CHECKING BITCOIN UNSPEND TRANSACTION OBJECT */
	/** ************************************************************ */
	insight.getUnspentUtxos(fromAddress, function(error, utxos) {
		balance = unit.fromSatoshis(0).toSatoshis();

		/** ************************************************************ */
		/* CALCULATION BALANCE IF TRANSACTION IS CONFIRMED ATLEAST ONE */
		/** ************************************************************ */
		for (var i = 0; i < utxos.length; i++) {
			if (utxos[i]['confirmations'] > 0) {
				balance += unit.fromSatoshis(parseInt(utxos[i]['satoshis']))
						.toSatoshis();
			}
		}

		var result = {
			fromAddress : fromAddress,
			amount : balance
		}
		res.end(JSON.stringify(result));
	});

});

/** ************************************************************ */
/* send bitcoin transaction data */
/** ************************************************************ */

app
		.post(
				'/bitcoin_transfer',
				function(req, res) {

					res.writeHead(200, {
						'Content-Type' : 'application/json'
					});

					var body = req.body;

					var bitcoinFromAddress = body.fromAddress;
					var bitcoinToAddress = body.toAddress;
					var index = body.index;
					var balance = body.amount;

					console.log('\nBitcoin transfer request FROM - '
							+ bitcoinFromAddress);
					console.log('To - ' + bitcoinToAddress);

					const unit = bitcore.Unit;
					const minerFee = unit.fromMilis(0.128).toSatoshis();

					const bitcore_transaction = ""; //

					console.log('Miner Fee - ' + minerFee);

					var result = {
						fromAddress : bitcoinFromAddress,
						index : index,
						amount : balance,
						transactionReciept : null,
						resultCode : null,
						description : null,
						errorCode : null
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
												// any other error
												result.code = 100;
												result.description = "Error while calling unpend transaction";
												result.errorCode = 120;

												res.end(JSON.stringify(result));
											} else {

												if (utxos.length == 0) {
													// if no transactions have
													// happened, there is no
													// balance
													// on the address.
													result.code = 100;
													result.description = "There is no balance on the address";
													result.errorCode = 140;
													// errorBody.errorDesc =
													// "You don't have enough
													// utox
													// Satoshis to cover the
													// miner fee.";
													res.end(JSON
															.stringify(result));
												}

												var readBalance = 0;
												for (var i = 0; i < utxos.length; i++) {
													if (utxos[i]['confirmations'] > 0) {
														readBalance += unit
																.fromSatoshis(
																		parseInt(utxos[i]['satoshis']))
																.toSatoshis();
													}
												}

												// get balance
												if (balance <= 0
														|| readBalance <= 0) {
													result.code = 100;
													result.description = "There is no balance on the address";
													result.errorCode = 150;

													// errorBody.errorDesc =
													// "You don't have enough
													// balance.";
													res.end(JSON
															.stringify(result));

												}
												if (readBalance < balance) {
													result.code = 100;
													result.description = "Do not have sufficient balance";
													result.errorCode = 160;

													// errorBody.errorDesc = "";
													res.end(JSON
															.stringify(result));
												} else {

													/** ************************************************************ */
													/* BITCOIN Private key */
													/** ************************************************************ */

													var xpriv = getRootKey();
													const bitcoinPrivatekey = getBitcoinPrivateKey(
															xpriv, index);

													console
															.log("creating transaction ");

													// END

													var transactionAmount = ""; // =
													// unit.fromMilis(transaction.amount).toSatoshis();

													if (balance > 0
															&& balance
																	- minerFee > 0) {
														balance = balance
																- minerFee;
														transactionAmount = balance;// unit.fromMilis(balance).toSatoshis();
														console
																.log('transactionAmount '
																		+ transactionAmount)
														// our transaction code
														// will come here
													} else {
														result.code = 100;
														result.description = "You don't have enough Satoshis to cover the miner fee.";
														result.errorCode = 170;
														// errorBody.errorDesc =
														// "You don't have
														// enough
														// Satoshis to cover the
														// miner fee.";
														res
																.end(JSON
																		.stringify(result));
													}

													let bitcore_transaction = new bitcore.Transaction()
															.from(utxos)
															.to(
																	bitcoinToAddress,
																	transactionAmount)
															.fee(minerFee)
															.change(
																	bitcoinFromAddress)
															.sign(
																	bitcoinPrivatekey);

													bitcore_transaction
															.serialize();

													console
															.log("BITCORE TRANSACTION - "
																	+ bitcore_transaction);

													if (bitcore_transaction
															.getSerializationError()) {
														let error = bitcore_transaction
																.getSerializationError().message;
														if (error != undefined) {
															switch (error) {
															case 'Some inputs have not been fully signed':
																result.code = 100;
																result.description = "Wrong key";
																result.errorCode = 180;
																// result.errorDesc
																// = 'Please
																// check
																// your private
																// key';
																res
																		.end(JSON
																				.stringify(result));
																break;
															default:
																result.code = 100;
																result.description = "Unknow error";
																result.errorCode = 190;
																res
																		.end(JSON
																				.stringify(result));
															}
														}
													}

													// broadcast the transaction
													// to the blockchain

													insight
															.broadcast(
																	bitcore_transaction,
																	function(
																			error,
																			hash) {
																		console
																				.log('hash'
																						+ hash);
																		result.transactionReciept = hash;
																		if (error) {
																			result.code = 100;
																			result.description = "Error in sending transaction";
																			result.errorCode = 110;

																		} else {
																			result.code = 200;
																			result.description = "success";

																		}
																		console
																				.log("returning hash success")
																		res
																				.end(JSON
																						.stringify(result));
																		return;
																	});

												}

											}

											/*
											 * if(errorBody.errorDesc != ''){
											 * res.end(JSON.stringify(errorBody));
											 * }else if (balance <= 0){
											 * res.end(JSON.stringify(successRes)); }
											 */

										} catch (e) {
											console.log("Exception occured "
													+ e);
											result.errorCode = 100;
											result.errorDesc = e.stack;
											result.code = 100;
											res.end(JSON.stringify(result));
										}
									});
				});
