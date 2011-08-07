import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class TransactionTest extends UnitTest {

    @Test
    public void shouldReturnTransactionIdItIsCreatedWith() {
		String txId = "1234567890";
        Transaction tx = new Transaction(txId);
		assertEquals(txId, tx.getHash());
    }

}
