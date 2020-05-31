package spark;

import java.util.HashMap;

/**
 * Represents the datastore
 * you can use it to store some data and find whether it is existence.
 */
public final class DataStore {
    static HashMap<String, String> Store = new HashMap<String, String>();

    private DataStore(){

    }

    /**.
     * find whether key is existence
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     *
     * @param key just like key in hashmap
     * @return return the result, true or false
     */
    public static boolean existence(String key) {
        return Store.containsKey(key);
    }

    /**.
     * find the corresponding value
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     *
     * @param key just like "key" in hashmap
     * @return return the corresponding value result
     */
    public static String findvalue(String key) {
        return Store.get(key);
    }

    /**.
     * clear the store
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     */
    public static void clearstore() {
        Store.clear();
    }

    /**.
     * put a pair into store
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     *
     * @param key just like "key" in hashmap
     * @param value just like "value" in hashmap
     */
    public static void putstore(String key,String value) {
        Store.put(key,value);
    }

    /**.
     * clear the store
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     *
     * @return return the size of the store
     */
    public static int getstoresize() {
        return Store.size();
    }

    /**.
     * find whether the store is empty
     * CS304 Issue link: https://github.com/perwendel/spark/issues/966
     *
     * @return return the result, true or false
     */
    public static boolean storeisempty() {
        return Store.isEmpty();
    }

}
