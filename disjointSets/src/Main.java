import disjointSet.disjointSet;
public class Main {
    public static void main(String[] args) {
        disjointSet ds = new disjointSet(10);
        ds.connect(0,1);
        ds.connect(1,2);
        ds.connect(0,4);
        ds.connect(3,5);
        System.out.println(ds.isConnected(2,4));
        System.out.println(ds.isConnected(3,0));
    }
}
