package mod1;

import java.util.Iterator;

public class TryOpenAddr {
    public static void main(String[] args) {
        CustomMap<String, Integer> mp = new OpenAddressingHashMap<>();
        mp.put("Sun", 1);
        mp.put("Mercury", 2);
        mp.put("Venus", 3);
        mp.put("Earth", 4);
        mp.put(null, 17);
        mp.put(null, 85);
        mp.put("Mars", 81);
        mp.put("Jupiter", 81);
        mp.put("Saturn", null);
        mp.put("Uranus", null);
        mp.put("Neptune", null);
        mp.put("Moon", null);
        mp.put("Pluto", null);
        mp.put("BlackHole", null);
        mp.remove("Mars");
        mp.remove(null);
        mp.remove(null);
        System.out.println(mp.containsKey(null));
        System.out.println(mp.containsKey("A"));
        System.out.println(mp.remove("A"));
        System.out.println(mp.get(null));
        System.out.println(mp.get("G"));

        System.out.println(mp.size());
        System.out.println(mp.capacity());

        System.out.println(mp.containsValue(85));
        System.out.println(mp.containsValue(81));
        System.out.println(mp.containsValue(null));
        System.out.println(mp.isEmpty());

//        mp.clear();

        Iterator<CustomMap.Entry<String, Integer>> iter = mp.iterator();
        while (iter.hasNext()) {
            CustomMap.Entry<String, Integer> cell = iter.next();
            System.out.println("Key: " + cell.getKey() + ", Value: " + cell.getValue());
        }

        // Collision - different objects, but with the same result of hashCode()
        CollisionKey k1 = new CollisionKey(1, "One");
        CollisionKey k2 = new CollisionKey(2, "Two");
        CollisionKey k3 = new CollisionKey(3, "Three");

        CustomMap<CollisionKey, String> mp2 = new OpenAddressingHashMap<>();
        mp2.put(k1, "AAAA");
        mp2.put(k2, "QQQQQ");
        mp2.put(k3, "DDD");

    }

}
