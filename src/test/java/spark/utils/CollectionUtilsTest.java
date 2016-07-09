package spark.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionUtilsTest {

    @Test
    public void testIsEmpty_whenCollectionIsEmpty_thenReturnTrue() throws Exception {

        Collection<Object> testCollection = new ArrayList<>();

        assertTrue("Should return true because collection is empty", CollectionUtils.isEmpty(testCollection));
    }

    @Test
    public void testIsEmpty_whenCollectionIsNotEmpty_thenReturnFalse() throws Exception {

        Collection<Integer> testCollection = new ArrayList<>();
        testCollection.add(1);
        testCollection.add(2);

        assertFalse("Should return false because collection is empty", CollectionUtils.isEmpty(testCollection));

    }
}