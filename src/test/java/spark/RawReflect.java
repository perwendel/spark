package spark;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RawReflect {

    private static final Map<Class, Map<String, Field>> cache = new HashMap<>();

    public static Field getField(Object o, String fieldName){
        try {
            Class tgt = null;
            if (o instanceof Class<?>) {
                tgt = ((Class<?>) o);
            } else {
                tgt = o.getClass();
            }
            if ( !cache.containsKey(tgt) ){
                Map<String,Field> fmap = new HashMap<>();
                Field[] fields = tgt.getDeclaredFields();
                for ( Field f : fields ){
                    f.setAccessible(true);
                    fmap.put(f.getName(),f);
                }
                cache.put(tgt,fmap);
            }
            Map<String,Field> fmap = cache.get(tgt);
            if ( !fmap.containsKey( fieldName) ){
                throw new IllegalArgumentException(String.format("Field [%s] missing in Class [%s]", fieldName, tgt));
            }
            return fmap.get(fieldName);

        }catch (Exception e){
            System.err.println(e);
        }
        return null;
    }

    public static <T> T getInternalState(Object o, String fieldName, T def){
        Field f = getField(o,fieldName);
        if ( f == null ){
            return def;
        }
        try {
            Object r = f.get(o);
            return (T)r;
        } catch (Throwable t){
            return def;
        }
    }

    public static <T> T getInternalState(Object o, String fieldName){
        return getInternalState(o,fieldName,null);
    }

    public static <T> void setInternalState(Object o, String fieldName, T value){
        Field f = getField(o,fieldName);
        if ( f == null ){
            return ;
        }
        try {
           f.set(o,value);
        } catch (Throwable t){
            return ;
        }
    }
}
