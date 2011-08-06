import org.junit.*;
import play.test.*;
import play.Logger;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import controllers.*;

import java.util.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }
    
//	@Test
//	public void shouldReturnValidTransaction(){
//		Logger.info("Starting Test: shouldReturnValidTransaction");
//		final String txId = "ee4637bdff8824e74936a3f1683a65c19d34cf19e9b20021fca45d50dfe633cf";
//		Transaction tx = Application.transaction(txId);
//		
//		Assert.assertNotNull(tx);
//	}
	
//	@Test
//	public void shouldReturnValidOutputTransaction(){
//		final String scriptSigPublicKey = "72d069d811e6205ec1967d37e0e97e459d6efade";
//		final String expectedAddress = "1BU5hgygVqXWKbK7qKpbmbR5jKykn2wXvL";
//		final String outAddress = Application.getOutputAddress(scriptSigPublicKey);
//		
//		// Ensure we get the correct transaction hash for key.
//		Assert.assertEquals(expectedAddress,outAddress);
//		
//		// Get the transaction for the hash.
//		Logger.info("Getting transactions for address.");
//		Collection<Transaction> txs = Application.getTransactions(outAddress);
//		Assert.assertNotNull(txs);
//	}
}