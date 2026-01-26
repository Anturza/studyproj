package mod1;

import java.util.Iterator;

public class TryChained {
    public static void main(String[] args) {
        CustomMap<String, Integer> mp = new ChainedHashMap<>();
        mp.put("AAAA", 12);
        mp.put("BBBBBBB", 13);
        mp.put("CCC", 16);
        mp.put("DDDDQQ", 42);
        mp.put(null, 17);
        mp.put(null, 85);
        mp.put("EEEEE", null);
        mp.put("FF", null);
        mp.put("GGG", null);
        mp.put("HHHH", null);
        mp.put("IIIII", null);
        mp.put("JJJJJJ", null);
        mp.put("KKKKKKK", null);
        mp.put("L", null);
        System.out.println(mp.containsKey("AAAA"));
        System.out.println(mp.containsKey("CCC"));
        System.out.println(mp.containsKey(null));
        System.out.println(mp.containsValue(null));
        System.out.println(mp.containsValue(85));
        System.out.println(mp.get("FF"));
        System.out.println(mp.get("AAAA"));
        System.out.println(mp.get(null));
        System.out.println(mp.remove("CCC"));
        System.out.println(mp.remove(null));
        System.out.println(mp.remove(null));

        System.out.println(mp.containsKey("AAAA"));
        System.out.println(mp.containsKey("CCC"));
        System.out.println(mp.containsValue(null));
        System.out.println(mp.capacity());
        System.out.println(mp.size());

        Iterator<CustomMap.Entry<String, Integer>> iter = mp.iterator();
        while (iter.hasNext()) {
            CustomMap.Entry<String, Integer> cell = iter.next();
            System.out.println("Key: " + cell.getKey() + ", Value: " + cell.getValue());

        //Different objects, but with the same result of hashCode()
        CollisionKey k1 = new CollisionKey(1, "One");
        CollisionKey k2 = new CollisionKey(2, "Two");
        CollisionKey k3 = new CollisionKey(3, "Three");

        CustomMap<CollisionKey, String> mp2 = new ChainedHashMap<>();
        mp2.put(k1, "AAAA");
        mp2.put(k2, "QQQQQ");
        mp2.put(k3, "DDD");


        }
    }
}
