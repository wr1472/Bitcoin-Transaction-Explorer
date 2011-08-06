package models;

import play.db.jpa.*;

import com.google.gson.annotations.*;

import java.util.*;

public class Address extends Model {

	public String hash;	
	public Collection<Transaction> transactions;
		
	public Address(String hash){
		this.hash = hash;
	}
	
	public String toString(){
		return hash.toString();
	}
}