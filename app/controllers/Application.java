package controllers;

import java.util.Collection;
import java.util.LinkedList;

import models.Transaction;
import play.Logger;
import play.mvc.Controller;

import com.xqoob.bitcoin.blockexplorer.BlockExplorerFacade;
import com.xqoob.bitcoin.blockexplorer.TransactionExplorerException;

public class Application extends Controller {

	private static BlockExplorerFacade facade = new BlockExplorerFacade(); 
	
    public static void index() {
        render();
    }

	public static void search(String transactionId){
		// TODO: Validate transaction is valid hash.
	}
	
	public static void transaction(String hash){
		Transaction tx = null;
		
		try{
			tx = facade.getTransaction(hash);
		}
		catch (TransactionExplorerException e) {
			Logger.error(e.getMessage());
		}
		
		render(tx);
	}
		
	public static void outputTransaction(String scriptSigPubKey){
		Collection<Transaction> TXs = new LinkedList<Transaction>();
		
		try{
			TXs = facade.getOutputTransactions(
				facade.getAddress(scriptSigPubKey)); 
		}
		catch (TransactionExplorerException e) {
			 Logger.error(e.getMessage());
		}
		
		renderJSON(TXs);
	}
	
	public static void address(String key){
		String address = null;
		
		try {
			address = facade.getAddress(key); 
		}
		catch (TransactionExplorerException e) {
			 Logger.error(e.getMessage());
		}
		
		renderJSON(address);
	}
}