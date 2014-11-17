package spark.utils;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ExceptionUtils {

    private static String notFound;
    private static String internalError;

    public static String getNotFound()  {
        String NOT_FOUND;
        try {
            NOT_FOUND = FileUtils.readFileToString(new File(notFound));
            System.out.println(NOT_FOUND);
        } catch (IOException e) {
            NOT_FOUND = "<html><body><h2>Sparkie says - 404 Page Not found</h2></body></html>";
        }
        return NOT_FOUND;
    }

    public static String getInternalError() {
        String INTERNAL_ERROR;
        try {
            INTERNAL_ERROR = FileUtils.readFileToString(new File(internalError));
            System.out.println(INTERNAL_ERROR);
        } catch (IOException e) {
            INTERNAL_ERROR = "<html><body><h2>Sparkie says - 500 Internal Error</h2></body></html>";
        }

        return INTERNAL_ERROR;
    }

    public static synchronized void setNotFound(String notFound) {
        ExceptionUtils.notFound = notFound;
    }

    public static synchronized  void setInternalError(String internalError) {
        ExceptionUtils.internalError = internalError;
    }

}
