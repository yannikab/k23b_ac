package k23b.am.cc;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * A linked hash map with an optional maximum size and a less recently used policy for entry removal.
 * 
 * @param <K> the type of keys
 * @param <V> the type of values
 */
@SuppressWarnings("serial")
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final Logger log = Logger.getLogger(LRUHashMap.class);

    private long maxSize;

    public LRUHashMap() {
        super();

        this.maxSize = 0;
    }

    public LRUHashMap(long maxSize) {
        super();

        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {

        // if the map has a maximum size, remove and replace the entry thus preserving the map's LRU property
        if (maxSize > 0)
            remove(key);

        return super.put(key, value);
    };

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {

        V value = super.get(key);

        // when an existing entry is found, it is moved to the end of the map thus preserving its LRU property
        if (value != null) {
            put((K) key, value);
        }

        return value;
    };

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {

        if (maxSize == 0)
            return false;

        boolean removeEldest = this.size() > maxSize;

        if (removeEldest)
            log.debug("About to remove eldest entry: " + eldest);

        return removeEldest;
    }
}
