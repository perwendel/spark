package spark.resource;

import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright 2016 - Per thesnowgoose
 */
public class UriPathTest {

    @Test
    public void testCanonicalPath()
    {
        String[][] canonical =
                {
                        {"/aaa/bbb/","/aaa/bbb/"},
                        {"/aaa//bbb/","/aaa//bbb/"},
                        {"/aaa///bbb/","/aaa///bbb/"},
                        {"/aaa/./bbb/","/aaa/bbb/"},
                        {"/aaa/../bbb/","/bbb/"},
                        {"/aaa/./../bbb/","/bbb/"},
                        {"/aaa/bbb/ccc/../../ddd/","/aaa/ddd/"},
                        {"./bbb/","bbb/"},
                        {"./aaa/../bbb/","bbb/"},
                        {"./",""},
                        {".//",".//"},
                        {".///",".///"},
                        {"/.","/"},
                        {"//.","//"},
                        {"///.","///"},
                        {"/","/"},
                        {"aaa/bbb","aaa/bbb"},
                        {"aaa/","aaa/"},
                        {"aaa","aaa"},
                        {"/aaa/bbb","/aaa/bbb"},
                        {"/aaa//bbb","/aaa//bbb"},
                        {"/aaa/./bbb","/aaa/bbb"},
                        {"/aaa/../bbb","/bbb"},
                        {"/aaa/./../bbb","/bbb"},
                        {"./bbb","bbb"},
                        {"./aaa/../bbb","bbb"},
                        {"aaa/bbb/..","aaa/"},
                        {"aaa/bbb/../","aaa/"},
                        {"/aaa//../bbb","/aaa/bbb"},
                        {"/aaa/./../bbb","/bbb"},
                        {"./",""},
                        {".",""},
                        {"",""},
                        {"..",null},
                        {"./..",null},
                        {"aaa/../..",null},
                        {"/foo/bar/../../..",null},
                        {"/../foo",null},
                        {"/foo/.","/foo/"},
                        {"a","a"},
                        {"a/","a/"},
                        {"a/.","a/"},
                        {"a/..",""},
                        {"a/../..",null},
                        {"/foo/../../bar",null},
                        {"/foo/../bar//","/bar//"},
                };

        for (int t=0;t<canonical.length;t++)
            assertEquals( "canonical "+canonical[t][0],
                    canonical[t][1],
                    UriPath.canonical(canonical[t][0])
            );

    }
}