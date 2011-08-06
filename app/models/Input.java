package models;

import play.db.jpa.Model;

import com.google.gson.annotations.SerializedName;

public class Input extends Model {

	private static final long serialVersionUID = -6768505835012192894L;

	@SerializedName("prev_out") public Transaction previousTransaction;
	@SerializedName("scriptSig") public String scriptSig;
		
	public Input(Transaction previousTransaction){
		this.previousTransaction = previousTransaction;
	}

	public Transaction getPreviousTransaction() {
		return previousTransaction;
	}
}