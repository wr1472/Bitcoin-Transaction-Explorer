package com.xqoob.bitcoin.blockexplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import models.Output;
import models.Transaction;
import play.Logger;
import play.libs.WS;
import play.libs.WS.WSRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BlockExplorerFacade {
	
	private static final String OP_DUP_OP_HASH160 = "OP_DUP OP_HASH160 ";
	private static final String OP_EQUALVERIFY = " OP_EQUALVERIFY";
	private static final String OP_CHECKSIG = " OP_CHECKSIG";
	
	private static final String BLOCK_EXPLORER_REQ_FORMAT = "json";	
	private static final String BLOCK_EXPLORER_HOST = 
		"http://blockexplorer.com";

	private static final String BLOCK_EXPLORER_URL = 
		BLOCK_EXPLORER_HOST + "/rawtx/";

	private static final String HASH_TO_ADDRESS_URL = 
		BLOCK_EXPLORER_HOST + "/q/hashtoaddress/";

	private static final String TX_FOR_ADDRESS_URL = 
		BLOCK_EXPLORER_HOST + "/q/mytransactions/";
	
	private static final String SCRIPT_SIG_TO_HASH_URL = 
		BLOCK_EXPLORER_HOST + "/q/hashpubkey/";
	
	public Transaction getTransaction(String hash) throws TransactionExplorerException {
		Logger.info("Retrieving transaction from " + 
			BLOCK_EXPLORER_HOST + ": " + hash);
		
		WSRequest req = makeCall(hash, BLOCK_EXPLORER_URL,
			"BlockExplorer returned 'ERROR'. Is this a valid transaction hash?");
				
		// Convert to model.
		return new Gson().fromJson(
			req.get().getString(), Transaction.class);
	}

	public String getAddressUsingScriptSigPubKey(String scriptSigPubKey) throws TransactionExplorerException {		
		scriptSigPubKey = scriptSigPubKey.
			replace(OP_DUP_OP_HASH160,"")
			.replace(OP_EQUALVERIFY,"")
			.replace(OP_CHECKSIG,"");
		
		Logger.info("Public key hash is " + scriptSigPubKey);
		
		WSRequest req = makeCall(scriptSigPubKey, HASH_TO_ADDRESS_URL, 
			"Error returned from " + HASH_TO_ADDRESS_URL +
			". Did you supply a valid scriptSigPublicKey?");
		
		String address = req.get().getString();
		Logger.info("Output address for scriptSigPubKey " + scriptSigPubKey + " is " + address);
		
		return address;
	}

	public String getAddressUsingScriptSig(String scriptSig) throws TransactionExplorerException {
		if(scriptSig == null)
			throw new IllegalArgumentException("scriptSig is null.");
		
		scriptSig = scriptSig.replace(OP_CHECKSIG, "");
		String [] tokens = scriptSig.split(" ");
		scriptSig = tokens[tokens.length - 1];
		Logger.info("Using script signature: " + scriptSig);
		
		WSRequest req = makeCall(scriptSig, SCRIPT_SIG_TO_HASH_URL, 
			"Error returned from " + SCRIPT_SIG_TO_HASH_URL +
			". Did you supply a valid scriptSig?");
		
		String address = req.get().getString();
		Logger.info("Address is: " + address);
				
		// All Bitcoin hash160 values should start with '04'.
		// See http://blockexplorer.com/q/hashpubkey
		assert(address.startsWith("04"));
		
		return getAddressUsingScriptSigPubKey(address);
	}
	
	public Collection<Transaction> getTransactions(String address) throws TransactionExplorerException {
		WSRequest req = makeCall(address, TX_FOR_ADDRESS_URL, 
			"Error returned from " + TX_FOR_ADDRESS_URL +
			". Did you supply a valid bitcoin address?");
		
		String request = req.get().getString();
		
		// Parse the JSON response to collection of transactions.
		JsonObject json = new JsonParser().parse(request).getAsJsonObject();		
		Set<Map.Entry<String,JsonElement>> members = json.entrySet();		
		Collection<Transaction> transactions = new ArrayList<Transaction>();
		
		for(Map.Entry<String,JsonElement> member : members){
			Transaction outTx = new Gson().fromJson(member.getValue(), Transaction.class);
			transactions.add(outTx);
		}
		
		return transactions;
	}

	public Collection<Transaction> getOutputTransactions(String address) throws TransactionExplorerException {
		Collection<Transaction> filteredTXs = new LinkedList<Transaction>();
		
		// Get all transactions associated with address
		Collection<Transaction> transactions = getTransactions(address);
		
		// For each transaction in address.
		for(Transaction tx : transactions){
			// TODO: Unless this method is called explicitly will not 
			// set attribute in Transaction instance. Therefore will 
			// not be available when serialised as json object.
			tx.getCredit();
			
			// Check if out address matches this address.
			for(Output output : tx.getOutputs()){					
				if(output.getAddress().equals(address)){
					Logger.info("Found input transaction: " + tx.getHash());
					filteredTXs.add(tx);
				}
			}
		}
		
		// Get rid of output transactions not having current transaction as input.
		transactions.removeAll(filteredTXs);		
		Logger.info("Number of output transactions: " + transactions.size());
		
		return transactions;
	}

	private WSRequest makeCall(String param, String url, String exceptionMsg) throws TransactionExplorerException {
		// Make the call to block explorer.
		WSRequest req = WS.url(url + param).
			setHeader("Accept", "application/" + BLOCK_EXPLORER_REQ_FORMAT);
				
		// Return null if error returned.
		if(req.get().getString().startsWith("ERROR"))
			throw new TransactionExplorerException(exceptionMsg);
		return req;
	}

	public String getAddress(String value) throws TransactionExplorerException {
		try{
			return this.getAddressUsingScriptSig(value);
		}
		catch (TransactionExplorerException e) {
			Logger.warn("Error getting address assuming script sig. Trying as script sig pub key...");
			return this.getAddressUsingScriptSigPubKey(value);
		}
	}
}
