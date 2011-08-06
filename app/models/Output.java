package models;

import play.db.jpa.*;

import java.util.*;

import com.google.gson.annotations.*;

public class Output extends Model {

	private static final long serialVersionUID = -7087813282192000321L;
	
	@SerializedName("value") private double amount;	
	@SerializedName("scriptPubKey") private String scriptPublicKey;	
	@SerializedName("address") private String address;	
	private transient Collection<Transaction> outputTransactions;
	
	public Output(){
		outputTransactions = new LinkedList<Transaction>();
	}
	
	public Output(double amount){
		this();
		
		if(amount < 0)
			throw new IllegalArgumentException("Amount cannot be less than zero.");
	
		this.amount = amount;
	}
	
	public double getAmount(){
		return amount;
	}
	
	public String getScriptPublicKey(){
		return scriptPublicKey;
	}
	
	public void addOutputTransaction(Transaction tx){
		outputTransactions.add(tx);
	}
	
	public String getAddress() {
		return address;
	}

	public Collection<Transaction> getOutputTransactions(){
		return outputTransactions;
	}
}