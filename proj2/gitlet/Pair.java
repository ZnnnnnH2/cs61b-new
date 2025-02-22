package gitlet;

public class Pair<K, V> {
    private K x;
    private V y;

    public Pair(K x, V y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {
        this.x = null;
        this.y = null;
    }

    public K getX() {
        return x;
    }

    public void setX(K x) {
        this.x = x;
    }

    public V getY() {
        return y;
    }

    public void setY(V y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair<K, V> other = (Pair<K, V>) o;
            return x.equals(other.x) && y.equals(other.y);
        }
        return false;
    }

    public int hashCode() {
        return x.hashCode() * 31 + y.hashCode();
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
