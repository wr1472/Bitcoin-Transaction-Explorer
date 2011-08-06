import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import models.Input;
import models.Output;
import models.Transaction;

import org.junit.Test;

import play.test.FunctionalTest;

import com.xqoob.bitcoin.blockexplorer.BlockExplorerFacade;
import com.xqoob.bitcoin.blockexplorer.TransactionExplorerException;

public class BlockExplorerFacadeTest extends FunctionalTest {

	private static final String [] VALID_HASHES = {
		"ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf"
		,"c45f3990204c9bd666de19c50a8ebd0c7487aceeee62573e8f96a8f71d0643a0"
	};
	
	// Valid for ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf
	private static final String [] VALID_INPUTS = {
		 "c501c1eeada1d702cb3264e8fe0312fc60b7f094bc5f4d9af3714e7ba7f01005"
		,"6b80edda3a95158d22312bc3eb4c5e950bab19a40ac03a3d2fe566ddbc367bb9"
		,"18b9a3b6e70a97c7055e21a5bab57d2e43304b20a42de1a4b11335c09b32dfec"
		,"90768d21afb19bc74c517ba97cb5ed52cb2b2dc3dd6181b3d3ad7b002d8086b9"
	};

	// Valid for ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf
	private static final String [] VALID_OUTPUTS = {
		 "OP_DUP OP_HASH160 72d069d811e6205ec1967d37e0e97e459d6efade OP_EQUALVERIFY OP_CHECKSIG"
		,"OP_DUP OP_HASH160 d749458940296596ff39ed2f9e2306ec9ba36cde OP_EQUALVERIFY OP_CHECKSIG"
	};

	// Valid for ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf
	private static final String [] VALID_ADDRESSES = {
		 "1BU5hgygVqXWKbK7qKpbmbR5jKykn2wXvL"
		,"1LdL4FEJT7Uzs3tLb7tQkbrx33uJwC5UrW"
	};
	
	// Valid for 1BU5hgygVqXWKbK7qKpbmbR5jKykn2wXvL
	private static final String [] VALID_TRANSACTIONS = {
		 "ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf"
		,"c45f3990204c9bd666de19c50a8ebd0c7487aceeee62573e8f96a8f71d0643a0"
	};
	
	// Valid for 1BU5hgygVqXWKbK7qKpbmbR5jKykn2wXvL
	private static final String [] VALID_OUTPUT_TRANSACTIONS = {
		"c45f3990204c9bd666de19c50a8ebd0c7487aceeee62573e8f96a8f71d0643a0"
	};
	
	// Valid for c45f3990204c9bd666de19c50a8ebd0c7487aceeee62573e8f96a8f71d0643a0
	private static final double [] VALID_OUTPUT_CREDIT = {
		0.34000000
	};
	
	private static final String INVALID_HASH = "ee4637bdff8824e74936a3f1";
	
	// Valid for scriptSig 
	// 04b6201d68c2a1ef070e09c4797f6c9934bed9875c30372bf6add8f83f59eb32144dcb19dcc16b4c7a7f3fbb07897d2f92ace708563ab9a4f1769857a7c584e1fd
	private static final String VALID_HASH160 = "1jPxMtLDDSd9UpSmLuP1Y9es5VqDneTCo";
	
	// Valid for scriptSig 
	// 04b6201d68c2a1ef070e09c4797f6c9934bed9875c30372bf6add8f83f59eb32144dcb19dcc16b4c7a7f3fbb07897d2f92ace708563ab9a4f1769857a7c584e1fd
	// and scriptsigpubkey
	// OP_DUP OP_HASH160 0804a1bfd96fb3c0dbe3baa75b72fb211a639b88 OP_EQUALVERIFY OP_CHECKSIG
	private static final String VALID_ADDRESS = "ee4637bdff8824e74936a3f1";
	
	private static final String GENERATIONAL_TRANSACTION = "4a995a8fb8eeefab4a7a5bb9765f711291b4c7930941870e52ba7a5452bce0f4";

	private BlockExplorerFacade facade = new BlockExplorerFacade();
	
	@Test
	public void shouldReturnTransactionForValidHash() throws TransactionExplorerException{
		for(int i = 0; i < VALID_HASHES.length; i++)
			Assert.assertNotNull(facade.getTransaction(VALID_HASHES[i]));
	}
		
	@Test
	public void shouldHaveCorrectHashForTransaction() throws TransactionExplorerException{
		for(int i = 0; i < VALID_HASHES.length; i++)
			Assert.assertEquals(
					VALID_HASHES[i], facade.getTransaction(VALID_HASHES[i]).getHash());
	}
			
	@Test(expected = TransactionExplorerException.class)
	public void shouldThrowExceptionForInvalidHash() throws TransactionExplorerException{
		final String hash = INVALID_HASH;
		Transaction tx = facade.getTransaction(hash);
	}

	@Test
	public void shouldHaveCorrectInputsForTransaction() throws TransactionExplorerException{
		Collection<Input> inputs = facade.getTransaction(VALID_HASHES[0]).getInputs();
		
		Assert.assertEquals(VALID_INPUTS.length,inputs.size());
		
		for(Input input : inputs)
			Assert.assertTrue(Arrays.asList(
				VALID_INPUTS).contains(input.getPreviousTransaction().getHash()));
	}
	
	@Test
	public void shouldReturnCorrectOutputsForValidTransaction() throws TransactionExplorerException{
		Collection<Output> outputs = facade.getTransaction(VALID_HASHES[0]).getOutputs();

		Assert.assertEquals(VALID_OUTPUTS.length,outputs.size());
		
		for(Output output : outputs)
			Assert.assertTrue(Arrays.asList(
				VALID_OUTPUTS).contains(output.getScriptPublicKey()));
	}
	
	@Test
	public void shouldReturnCorrectAddressForValidTransactionCornerCase() throws TransactionExplorerException{
		final String validTX = "4a995a8fb8eeefab4a7a5bb9765f711291b4c7930941870e52ba7a5452bce0f4";
		final String expectedAddress = "16JciXSja2LYCqnaihYNzNCLQnKHrMP7xx";

		Collection<Output> outputs = facade.getTransaction(validTX).getOutputs();
		Assert.assertEquals(expectedAddress,facade.getAddress(outputs.iterator().next().getScriptPublicKey()));
	}
	
	@Test
	public void shouldReturnCorrectAddressForValidScriptSigPublicKey() throws TransactionExplorerException {
		for(int i = 0; i < VALID_OUTPUTS.length; i++)
			Assert.assertEquals(VALID_ADDRESSES[i], facade.getAddressUsingScriptSigPubKey(VALID_OUTPUTS[i]));
	}

	@Test(expected = TransactionExplorerException.class)
	public void shouldThrowErrorForInvalidScriptSigPublicKey() throws TransactionExplorerException {
		// Modify the public key to make it invalid and try and retrieve an address
		facade.getAddressUsingScriptSigPubKey(VALID_OUTPUTS[0].replace('2', '~'));
	}
	
	@Test
	public void shouldReturnAllTransactionsForValidAddress() throws TransactionExplorerException{
		Collection<Transaction> transactions = facade.getTransactions(VALID_ADDRESSES[0]);

		Assert.assertEquals(VALID_TRANSACTIONS.length,transactions.size());
		
		for(Transaction tx : transactions)
			Assert.assertTrue(Arrays.asList(
				VALID_TRANSACTIONS).contains(tx.getHash()));
	}
	
	@Test(expected = TransactionExplorerException.class)
	public void shouldThrowErrorForInvalidAddress() throws TransactionExplorerException{
		facade.getTransactions("0987654321iuytresdgbhnfds");
	}
	
	@Test
	public void shouldReturnOnlyOutputTransactionsForValidAddress() throws TransactionExplorerException{
		Collection<Transaction> outputTransactions = facade.getOutputTransactions(VALID_ADDRESSES[0]);

		for(Transaction tx : outputTransactions)
			Assert.assertTrue(Arrays.asList(
				VALID_OUTPUT_TRANSACTIONS).contains(tx.getHash()));
	}

	@Test
	public void shouldHaveCorrectCreditForOutputTransaction() throws TransactionExplorerException{
		List<Transaction> outputTransactions = (List<Transaction>) facade.getOutputTransactions(VALID_ADDRESSES[0]);
		Assert.assertTrue(VALID_OUTPUT_CREDIT[0] == outputTransactions.get(0).getCredit());
	}
	
	@Test
	public void shouldReturnCorrectHash160ForValidPublicKey() throws TransactionExplorerException{
		Assert.assertEquals(VALID_HASH160,
			facade.getAddressUsingScriptSig("04b6201d68c2a1ef070e09c4797f6c9934bed9875c30372bf6add8f83f59eb32144dcb19d" +
							  "cc16b4c7a7f3fbb07897d2f92ace708563ab9a4f1769857a7c584e1fd"));
	}
	
	@Test
	public void shouldReturnValidAddressIfScriptSigOrScriptSigPubKeyIsUsed() throws TransactionExplorerException{
		Assert.assertEquals(VALID_HASH160, facade.getAddress("04b6201d68c2a1ef070e09c4797f6c9934bed9875c30372bf6add8f83f59eb32144dcb19d" +
		  	"cc16b4c7a7f3fbb07897d2f92ace708563ab9a4f1769857a7c584e1fd"));
		
		Assert.assertEquals(VALID_HASH160, facade.getAddress(
			"OP_DUP OP_HASH160 0804a1bfd96fb3c0dbe3baa75b72fb211a639b88 OP_EQUALVERIFY OP_CHECKSIG"));
	}
	
	@Test
	public void shouldHandleGenerationTypeTransactions() throws TransactionExplorerException{
		Assert.assertNotNull(facade.getTransaction(GENERATIONAL_TRANSACTION));
	}
}
