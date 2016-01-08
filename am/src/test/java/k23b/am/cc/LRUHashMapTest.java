package k23b.am.cc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class LRUHashMapTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPutWithMaxSize() {

        long maxSize = 3;
        LRUHashMap<Integer, Integer> map = new LRUHashMap<>(maxSize);

        map.put(1, 1);

        int[] dump = dumpMap(map);
        Assert.assertEquals(1, dump.length);
        Assert.assertEquals(1, dump[0]);

        map.put(2, 2);

        dump = dumpMap(map);
        Assert.assertEquals(2, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);

        map.put(3, 3);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(3, dump[2]);

        map.put(4, 4);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(2, dump[0]);
        Assert.assertEquals(3, dump[1]);
        Assert.assertEquals(4, dump[2]); // new item must be at end of linked map

        map.put(3, 3);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(2, dump[0]);
        Assert.assertEquals(4, dump[1]);
        Assert.assertEquals(3, dump[2]); // placed existing item, it must move at end of linked map

        map.remove(4);

        dump = dumpMap(map);
        Assert.assertEquals(2, dump.length);
        Assert.assertEquals(2, dump[0]);
        Assert.assertEquals(3, dump[1]); // removal should preserve order of linked map
    }

    @Test
    public void testGetWithMaxSize() {

        long maxSize = 3;
        LRUHashMap<Integer, Integer> map = new LRUHashMap<>(maxSize);

        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.get(2);

        int[] dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(3, dump[1]);
        Assert.assertEquals(2, dump[2]); // 2 must have moved to the end of the linked map

        map.put(4, 4);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(3, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(4, dump[2]); // new item at end of the linked map

        map.remove(2);

        dump = dumpMap(map);
        Assert.assertEquals(2, dump.length);
        Assert.assertEquals(3, dump[0]);
        Assert.assertEquals(4, dump[1]); // removal should preserve order of linked map
    }

    @Test
    public void testPutWithoutMaxSize() {

        LRUHashMap<Integer, Integer> map = new LRUHashMap<>();

        map.put(1, 1);

        int[] dump = dumpMap(map);
        Assert.assertEquals(1, dump.length);
        Assert.assertEquals(1, dump[0]);

        map.put(2, 2);

        dump = dumpMap(map);
        Assert.assertEquals(2, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);

        map.put(3, 3);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(3, dump[2]);

        map.put(4, 4);

        dump = dumpMap(map);
        Assert.assertEquals(4, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(3, dump[2]);
        Assert.assertEquals(4, dump[3]);

        map.put(3, 3);

        dump = dumpMap(map);
        Assert.assertEquals(4, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(3, dump[2]); // insertion of existing item should not affect order
        Assert.assertEquals(4, dump[3]);

        map.remove(2);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(3, dump[1]);
        Assert.assertEquals(4, dump[2]); // removal should preserve order of linked map
    }

    @Test
    public void testGetWithoutMaxSize() {

        LRUHashMap<Integer, Integer> map = new LRUHashMap<>();

        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.get(2);

        int[] dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]); // get should not affect order of linked map
        Assert.assertEquals(3, dump[2]);

        map.put(4, 4);

        dump = dumpMap(map);
        Assert.assertEquals(4, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(3, dump[2]);
        Assert.assertEquals(4, dump[3]);
        
        map.remove(3);

        dump = dumpMap(map);
        Assert.assertEquals(3, dump.length);
        Assert.assertEquals(1, dump[0]);
        Assert.assertEquals(2, dump[1]);
        Assert.assertEquals(4, dump[2]); // removal should preserve order of linked map
    }

    private int[] dumpMap(LRUHashMap<Integer, Integer> map) {

        int[] dump = new int[map.size()];

        int c = 0;
        for (int i : map.values())
            dump[c++] = i;

        return dump;
    }
}
