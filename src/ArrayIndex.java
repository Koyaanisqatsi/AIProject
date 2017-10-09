public class ArrayIndex implements Comparable<ArrayIndex> {

    private int value;
    private int index;

    public ArrayIndex(int value, int index){
        this.value = value;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getValue(){
        return value;
    }

    public String toString(){
        return this.value + " " + this.index;
    }

    @Override
    public int compareTo(ArrayIndex b){
        return Integer.compare(this.value,b.value);
    }
}
