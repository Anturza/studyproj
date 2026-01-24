package mod1;

public class CollisionKey {

    private final int id;
    private final String name;

    public CollisionKey(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        // Always the same result returns
        return 85;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CollisionKey other = (CollisionKey) obj;
        return id == other.id && name.equals(other.name);
    }
}

