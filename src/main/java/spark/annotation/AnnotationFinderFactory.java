package spark.annotation;

public class AnnotationFinderFactory {

    // TODO: Does the scannotation route finder work better in Windows???
    private static AnnotationFinder annotationFinder = new ScannotationFinder();
    
    public static AnnotationFinder get() {
        return annotationFinder;
    }

}
