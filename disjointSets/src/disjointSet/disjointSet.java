package disjointSet;
public class disjointSet { //Weight Quick Union Performance
    private int[] father;
    private int[] size;
    public disjointSet(int maxMun){
        father = new int[maxMun];
        size = new int[maxMun];
        for(int i = 0; i < maxMun; i++){
            father[i] = i;
            size[i] = 1;
        }
    }
    private int find(int x){
        if(father[x] == x){
            return x;
        }
        return father[x] = find(father[x]);
    }
    private void doConnect(int x,int y){
        father[x] = y;
        size[y] += size[x];
        size[x] = 0;
    }
    public void connect(int x,int y){
        int fatherx = find(x);
        int fathery = find(y);
        if(fatherx != fathery){
            if(size[fatherx] > size[fathery]) {
                doConnect(fathery, fatherx);
            }
            else{
                doConnect(fatherx,fathery);
            }
        }
    }
    public boolean isConnected(int x,int y){
        return find(x) == find(y);
    }

}
