package models;

import play.db.jpa.*;

import com.google.gson.annotations.*;

import java.util.*;

public class Transaction extends Model {

	private static final long serialVersionUID = -4862526967785430696L;
	
	@SerializedName("hash") private String hash;	
	
	private double credit;
	
	@SerializedName("in") private Collection<Input> inputs;		
	@SerializedName("out") private Collection<Output> outputs;
		
	public Transaction(String hash){
		this.hash = hash;
	}
	
	/**
	 *Returns the total amount of bitcoins credited in this transaction.
	 *
	 */
	public double getCredit(){	
		credit = 0;
		
		// TODO: using the output of the transaction to determine
		// credit. this may not be accurate (how will transaction 
		// fees affect this for example?), should probably use
		// output of previous transactions.
		for(Output output: outputs){
			credit += output.getAmount();
		}
		
		return credit;
	}
	
	public String getHash() {
		return hash;
	}

	public void setOutputs(Collection<Output> outputs) {
		this.outputs = outputs;
	}

	public Collection<Output> getOutputs(){
		return outputs;
	}
	
	public Collection<Input> getInputs() {
		return inputs;
	}
	
	public String toString(){
		return hash.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
}