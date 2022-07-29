package spark;

import org.junit.Assert;
import org.junit.Test;
import spark.http.matching.Configuration;

public class DataStoreTest {
    /**.
     * test put some info into the store
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/966
     */
    @Test
    public void putinstore() throws Exception {
        String book = "daughter of sea";
        String author ="Andersen";
        DataStore.putstore(book,author);
        Assert.assertTrue(DataStore.existence(book));
        Assert.assertEquals("Andersen",DataStore.findvalue(book));
    }

    /**.
     * test clear to the store
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/966
     */
    @Test
    public void clearstoreandaddvalue() throws Exception {
        DataStore.clearstore();
        Assert.assertEquals(0,DataStore.getstoresize());
        String book = "daughter of sea";
        String author ="Andersen";
        DataStore.putstore(book,author);
        Assert.assertEquals("Andersen",DataStore.findvalue(book));
    }

}
