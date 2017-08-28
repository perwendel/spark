package spark.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

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

    @Test
    public void testStream_null() {
        Stream<?> stream = CollectionUtils.stream(null);

        assertNotNull(stream);
        assertEquals(0, stream.count());
    }

    @Test
    public void testStream_list() {
        List<String> items = Arrays.asList("foo", "bar", "baz");

        // Make sure you get something back
        Stream<String> stream = CollectionUtils.stream(items);
        assertNotNull(stream);

        // Make sure the stream contains the same items
        List<String> streamedItems = stream.collect(Collectors.toList());
        assertTrue(items.equals(streamedItems));
    }
}