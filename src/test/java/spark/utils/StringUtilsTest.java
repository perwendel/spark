package spark.utils;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testIsBlank() {
        Assert.assertFalse(StringUtils.isBlank("fooBar"));

        Assert.assertTrue(StringUtils.isBlank(""));
        Assert.assertTrue(StringUtils.isBlank(" "));
        Assert.assertTrue(StringUtils.isBlank(null));
    }

    @Test
    public void testIsNotBlank() {
        Assert.assertFalse(StringUtils.isNotBlank(null));

        Assert.assertTrue(StringUtils.isNotBlank("!   "));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertFalse(StringUtils.isEmpty("foo"));

        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertTrue(StringUtils.isNotEmpty("!"));

        Assert.assertFalse(StringUtils.isNotEmpty(null));
        Assert.assertFalse(StringUtils.isNotEmpty(""));
    }

    @Test
    public void testHasLength() {
        Assert.assertFalse(StringUtils.hasLength(""));
        Assert.assertFalse(StringUtils.hasLength(null));

        Assert.assertTrue(StringUtils.hasLength("fooBar"));
    }

    @Test
    public void testReplace() {
        Assert.assertNull(StringUtils.replace(null, null, null));

        Assert.assertEquals("foofoo", StringUtils.replace("fooBar", "Bar", "foo"));
    }

    @Test
    public void testDeleteAny() {
        Assert.assertEquals("#", StringUtils.deleteAny("#", "!!!!!!!!"));
        Assert.assertEquals("", StringUtils.deleteAny("#", "\"\"\"#!!!!"));
        Assert.assertEquals("\"", StringUtils.deleteAny("\"", null));

        Assert.assertNull(StringUtils.deleteAny(null, null));
    }

    @Test
    public void testGetFilename() {
        Assert.assertEquals("", StringUtils.getFilename(""));
        Assert.assertEquals("Bar.java", StringUtils.getFilename("foo/Bar.java"));

        Assert.assertNull(StringUtils.getFilename(null));
    }

    @Test
    public void testApplyRelativePath() {
        Assert.assertEquals("./foo",
            StringUtils.applyRelativePath("./bar", "foo"));
        Assert.assertEquals("!!",
            StringUtils.applyRelativePath("!!!!!!!", "!!"));
    }

    @Test
    public void testCleanPath() {
        Assert.assertNull(StringUtils.cleanPath(null));

        Assert.assertEquals("foo:/../Bar", StringUtils.cleanPath("foo:/foo/../../Bar"));
    }

    @Test
    public void testToStringArray() {
        Assert.assertArrayEquals(new String[] {},
            StringUtils.toStringArray(new ArrayList<>()));

        Assert.assertNull(StringUtils.toStringArray(null));
    }

    @Test
    public void testDelimitedListToStringArray() {
        Assert.assertArrayEquals(new String[] {},
            StringUtils.delimitedListToStringArray(null, null, null));
        Assert.assertArrayEquals(new String[] { "fooBar" },
            StringUtils.delimitedListToStringArray("fooBar", null, null));
        Assert.assertArrayEquals(new String[] { "foo" , ""},
            StringUtils.delimitedListToStringArray("fooBar", "a", "Bar"));
        Assert.assertArrayEquals(new String[] { "" , "Bar"},
            StringUtils.delimitedListToStringArray("fooBar", "foo", ""));
        Assert.assertArrayEquals(new String[] { "f", "o", "o", "B", "a", "r"},
            StringUtils.delimitedListToStringArray("fooBar", "", ""));
    }

    @Test
    public void testDelimitedListToStringArray2() {
        Assert.assertArrayEquals(new String[] { "" , "Bar"},
            StringUtils.delimitedListToStringArray("fooBar", "foo"));
    }

    @Test
    public void testCollectionToDelimitedString() {
        Assert.assertEquals("",
            StringUtils.collectionToDelimitedString(new ArrayList(), null, null, null));

        ArrayList coll = new ArrayList();
        coll.add(10);

        Assert.assertEquals("10!!!!",
            StringUtils.collectionToDelimitedString(coll, null, "", "!!!!"));

        coll.add(null);
        coll.add(-2_113_912_831);

        Assert.assertEquals("!10?\\!null?\\!-2113912831?",
            StringUtils.collectionToDelimitedString(coll, "\\", "!", "?"));
        Assert.assertEquals("",
            StringUtils.collectionToDelimitedString(null, null));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("foo",
            StringUtils.toString(new byte[] {102, 111, 111}, "UTF-8"));
        Assert.assertEquals("foo",
            StringUtils.toString(new byte[] {102, 111, 111}, "ISO646-US"));
        Assert.assertEquals("foo",
            StringUtils.toString(new byte[] {102, 111, 111}, null));
    }

    @Test
    public void testRemoveLeadingAndTrailingSlashesFrom() {
        Assert.assertEquals("\\\\",
            StringUtils.removeLeadingAndTrailingSlashesFrom("/\\\\\\"));
        Assert.assertEquals("a\\\\",
            StringUtils.removeLeadingAndTrailingSlashesFrom("a\\\\\\"));
        Assert.assertEquals("",
            StringUtils.removeLeadingAndTrailingSlashesFrom("//"));
        Assert.assertEquals(",,,-",
            StringUtils.removeLeadingAndTrailingSlashesFrom("/,,,-"));
    }
}
