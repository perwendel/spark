/*
 * Copyright 2016 - Per thesnowgoose
 */
package spark.resource;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Used for converting URL paths.
 */
public class UriPath {

    private static final Pattern __PATH_SPLIT = Pattern.compile("(?<=\\/)");

    /**
     * Convert a path to a canonical form.
     * All instances of "." and ".." are factored out.  Null is returned
     * if the path tries to .. above its root.
     *
     * @param path the path to convert
     * @return path or null.
     */
    public static String canonical(String path)
    {
        if (path == null || path.isEmpty() || !path.contains("."))
            return path;

        if(path.startsWith("/.."))
            return null;

        List<String> directories = new LinkedList<>();
        Collections.addAll(directories, __PATH_SPLIT.split(path));

        for(ListIterator<String> iterator = directories.listIterator(); iterator.hasNext();)
        {
            switch (iterator.next()) {
                case "./":
                case ".":
                    if (iterator.hasNext() && directories.get(iterator.nextIndex()).equals("/"))
                        break;

                    iterator.remove();
                    break;
                case "../":
                case "..":
                    if(iterator.previousIndex() == 0)
                        return null;
                    
                    iterator.remove();
                    if(iterator.previous().equals("/") && iterator.nextIndex() == 0)
                        return null;

                    iterator.remove();
                    break;
            }
        }
        
        return String.join("", directories);
    }
}