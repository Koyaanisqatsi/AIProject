import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Decrypter{

    ArrayList<Integer> chromosome;
    double fitness;
    static final double mutation = 0.05;

    public Decrypter(){

        Random rnd = new Random();
        int key = Cryptek.generateKey(rnd.nextInt(10));
        chromosome = crossArray(key);
    }

    public Decrypter(int length){

        Random rnd = new Random();
        int key = Cryptek.generateKey(rnd.nextInt(length));
        chromosome = crossArray(key);
    }

    private void updateChromosome(ArrayList<Integer> newArray){
        try {
            this.chromosome = Cloner.deepCopyIntegerArrayList(newArray);
        }catch(Exception e){
            System.out.println("Could not update chromosome");
        }
    }

    private static ArrayList<Integer> makeArray(int key){
        ArrayList<Integer> array = new ArrayList<>();
        String keyString = String.valueOf(key);
        for(int i = 0; i<keyString.length(); ++i){
            array.add(Integer.valueOf(keyString.charAt(i)));
        }
        return array;
    }

    private double fitness(ArrayList<Character> gene){

        double fitness = 0;


        return fitness;
    }

    private ArrayList<Integer> breed(Decrypter partner){

        ArrayList<Integer> child = new ArrayList<>();
        return child;
    }

    private ArrayList<Integer> crossArray(int length){

        ArrayList<Integer> crossing = new ArrayList<>();
        for(int i = 0; i < length; ++i){
            crossing.add(i);
        }
        Collections.shuffle(crossing);
        return crossing;
    }

    public void print(){
        System.out.println("Chromosome: " + this.toString());
    }

    public String toString(){
        return chromosome.toString();
    }
}
