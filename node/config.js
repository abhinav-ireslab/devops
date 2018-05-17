const fs = require('fs-extra')
const insightFilePath = './node_modules/bitcore-explorers-cash/lib/insight.js';
const bitCoreLibUnspentOutputSourceFilePath = './unspentoutput.js';
const bitCoreLibUnspentOutputDestFilePath = './node_modules/bitcore-lib/lib/transaction/unspentoutput.js';
const bitCoreLibCashUnspentOutputSourceFilePath = './unspentoutput.js';
const bitCorelibCashUnspentOutputDestFilePath = './node_modules/bitcore-lib-cash/lib/transaction/unspentoutput.js';

updateExplorers=function(){
  /*  fs.move('./node_modules/bitcore-explorers/node_modules/bitcore-lib', './node_modules/bitcore-explorers/node_modules/bitcore-lib_1', err => {
		if (err) return console.error(err)
		console.log('bitcore-explorers/node_modules/bitcore-lib successfully moved to bitcore-lib_1 !')
	})*/
    //setTimeout(copyBitcoreExplore, 3000);
  //  setTimeout(updateInsightFile, 6000);
	setTimeout(copyBitcoreLibUnspentOutputFile, 7000);
	//setTimeout(copyBitcoreLibCashUnspentOutputFile, 9000);
     
}

copyBitcoreExplore=function(){
	fs.copy('./node_modules/bitcore-explorers', './node_modules/bitcore-explorers-cash', err => {
		if (err) return console.error(err)
		console.log('bitcore-explorers successfully copied to bitcore-explorers-cash')
	})
}

updateInsightFile=function(){
		fs.readFile(insightFilePath, 'utf8', function (err,data) {
			if (err) {
				return console.log(err);
			}
 			 var result = data.replace(/bitcore-lib/g, 'bitcore-lib-cash');

			fs.writeFile(insightFilePath, result, 'utf8', function (err) {
				if (err) return console.log(err);
				console.log("insight file successfully updated");
			});
		});
}

copyBitcoreLibUnspentOutputFile=function(){
	fs.copy(bitCoreLibUnspentOutputSourceFilePath, bitCoreLibUnspentOutputDestFilePath, { replace: true }, function (err) {
  if (err) {
    return console.log(err);
  }
 
  console.log("Copied Bitcore lib 'unspentoutput.js' ");
});
}

copyBitcoreLibCashUnspentOutputFile=function(){
	fs.copy(bitCoreLibCashUnspentOutputSourceFilePath, bitCorelibCashUnspentOutputDestFilePath, { replace: true }, function (err) {
  if (err) {
    return console.log(err);
  }
 
  console.log("Copied BitcoreLibCash 'unspentoutput.js' ");
});
}
updateExplorers();