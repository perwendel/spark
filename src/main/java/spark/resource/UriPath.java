/*
 * Copyright 2016 - Per thesnowgoose
 */
package spark.resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used for converting URL paths.
 */
public class UriPath {

    /**
     * Convert a path to a canonical form.
     * All instances of "." and ".." are factored out.  Null is returned
     * if the path tries to .. above its root.
     *
     * @param path the path to convert
     * @return path or null.
     */
    public static String canonical(String path) {
        if (path == null || path.isEmpty()){
            return path;
        }
        if(path.startsWith("/..")) {
            return null;
        }

        List<String> directories = new ArrayList<>(Arrays.asList(path.split("(?<=\\/)")));
        directories.removeIf(dir-> dir.isEmpty() || dir.equals("."));

        for(ListIterator<String> iterator = directories.listIterator(); iterator.hasNext();){
            String currentDir = iterator.next();
            if (currentDir.equals("./")){
                if (iterator.nextIndex()>1)
                    iterator.remove();
                else if (iterator.hasNext()){
                    if(!iterator.next().equals("/")) {
                        iterator.previous();
                        iterator.previous();
                        iterator.remove();
                    }
                } else {
                    return "";
                }
            }
            else if(currentDir.equals("../") || currentDir.equals("..")) {
                iterator.remove();
                if (iterator.hasPrevious()) {
                    iterator.previous();
                    iterator.remove();
                    if (directories.isEmpty() && path.startsWith("/"))
                        return null;
                } else {
                    return null;
                }
            }
        }
        return directories.stream().collect(Collectors.joining());
    }
}
