package disjointSet;
public class disjointSet {
    private int[] father;
    public disjointSet(int maxMun){
        father = new int[maxMun];
        for(int i = 0; i < maxMun; i++){
            father[i] = i;
        }
    }
    private int find(int x){
        if(father[x] == x){
            return x;
        }
        return find(father[x]);
    }
    public void connect(int x,int y){
        int fatherx = find(x);
        int fathery = find(y);
        if(fatherx != fathery){
            father[fatherx] = fathery;
        }
    }
    public boolean isConnected(int x,int y){
        return find(x) == find(y);
    }

}
