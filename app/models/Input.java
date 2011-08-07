package models;

import play.db.jpa.Model;

import com.google.gson.annotations.SerializedName;

public class Input extends Model {

	private static final long serialVersionUID = -6768505835012192894L;

	@SerializedName("prev_out") private Transaction previousTransaction;
	@SerializedName("scriptSig") private String scriptSig;
	@SerializedName("address") private String fromAddress;
		
	public Input(Transaction previousTransaction){
		this.previousTransaction = previousTransaction;
	}

	public String getHash() {
		return previousTransaction.getHash();
	}

	public String getScriptSig() {
		return scriptSig;
	}

	public String getFromAddress() {
		return fromAddress;
	}
}