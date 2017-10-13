import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Decrypter{

    ArrayList<Integer> chromosome;
    String decryption;
    double fitness;
    static final double mutation = 0.1;

    public Decrypter(){

        Random rnd = new Random();
        long key = Cryptek.generateLongKey(rnd.nextInt(12));
        chromosome = crossArray(key);
    }

    public Decrypter(int length){

        long key = Cryptek.generateLongKey(length);
        chromosome = crossArray(key);
    }

    public Decrypter(Decrypter d){
        try {
            this.chromosome = Cloner.deepCopyIntegerArrayList(d.chromosome);
        }catch (Exception e){
            System.out.println("Could not create new decrypter");
        }
    }

    private Decrypter(ArrayList<Integer> chromosome){
        try{
            this.chromosome = Cloner.deepCopyIntegerArrayList(chromosome);
        }catch (Exception e){
            System.out.println("Could not create new decrypter from chromosome");
        }
    }

    private void updateChromosome(ArrayList<Integer> newArray){
        try {
            this.chromosome = Cloner.deepCopyIntegerArrayList(newArray);
        }catch(Exception e){
            System.out.println("Could not update chromosome");
        }
    }

    public void updateFitness(double newFitness){
        this.fitness = newFitness;
    }

    private static ArrayList<Integer> makeArray(int key){
        ArrayList<Integer> array = new ArrayList<>();
        String keyString = String.valueOf(key);
       // System.out.println("makeArray keyString: " + keyString + " with length " + keyString.length());
        //System.out.println("character at 0: " + keyString.charAt(0));
        for(int i = 0; i<keyString.length(); ++i){
            int val = Character.getNumericValue(keyString.charAt(i));
            array.add(val);
        }
        //System.out.println("makeArray array:" +array.toString());
        return array;
    }

    public void hack(ArrayList<ArrayList<NGram>> nGrams, String cipherText, int orderOfNGrams){
        this.decryption = this.decrypt(cipherText);
        this.fitness = sumOfAllFits(nGrams, orderOfNGrams);
        this.printAttempt();
    }

    public Decrypter mutate(int generation){
        try {
            double chance = (1 + generation) * mutation;
            chance = (mutation > 0.8 ? 0.8 : chance);
            if (ThreadLocalRandom.current().nextDouble(1) < chance) { // swap
                int first = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                int second = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                System.out.println("A mutation occured! Swapping two indices: " + first + " and " + second);
                while (second == first) {
                    second = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                }
                int temp = (int)Cloner.deepCopy(this.chromosome.get(first));
                this.chromosome.set(first, this.chromosome.get(second));
                this.chromosome.set(second, temp);
            }
            if(ThreadLocalRandom.current().nextDouble(1) < chance) {
                int places = ThreadLocalRandom.current().nextInt(10);
                System.out.println("A mutation occured! Shuffling chromosome " + places + " steps forward");
                ArrayList<Integer> newChromosome = Cloner.deepCopyIntegerArrayList(this.chromosome);
                for (int i = 0; i < places; ++i) {
                    newChromosome.add(0, newChromosome.get(newChromosome.size() - 1));
                    newChromosome.remove(newChromosome.size() - 1);
                }
                this.updateChromosome(newChromosome);
            }
            return this;
        }catch (Exception e){
            System.out.println("Could not copy");
        }
        return this;
    }

    private String decrypt(String cipherText){

        int len = this.chromosome.size();
        StringBuilder sb = new StringBuilder();

        ArrayList<String> cipher = decryptionSetup(cipherText);

        System.out.println("Key: " + this.chromosome.toString());
        System.out.println("Cipher: " + cipher.toString());

        int cipherlen = cipherText.length();
        ArrayList<Integer> accessOrder = this.decryptionOrder();
        //cipher = swapOrder(cipher, accessOrder);

        for(int i = 0; i < cipherlen; ++i){
            int index = i%len;
            sb.append(cipher.get(index).charAt(0));
            cipher.set(index, cipher.get(index).substring(1, cipher.get(index).length()));
        }

        /*
        for(int i = 0; i < len; ++i){
            sb.append(cipher.get(this.chromosome.indexOf(i)));
        }
        */

        return sb.toString();
    }

    private ArrayList<String> swapOrder(ArrayList<String> cipher, ArrayList<Integer> newOrder){
        ArrayList<String> newCipher = new ArrayList<>();
        System.out.println("cipher in swap: " + cipher.toString() + " and newOrder: " + newOrder.toString());
        for(int i = 0; i < newOrder.size(); ++i){
            newCipher.add(cipher.get(newOrder.get(i)));
        }
        System.out.println("newCipher in swap: " + newCipher.toString());
        return newCipher;
    }

    private ArrayList<Integer> decryptionOrder(){
        try{
            int lowest = 0;
            System.out.println("this.chromosome in decryptionOrder: " + this.chromosome.toString());
            ArrayList<Integer> order = Cloner.deepCopyIntegerArrayList(this.chromosome);
            ArrayList<Integer> newOrder = new ArrayList<>();
            ArrayList<Integer> remaining = Cloner.deepCopyIntegerArrayList(order);


            System.out.println("order in decryptionOrder: " + order.toString());

            while(remaining.size() > 0){
                //System.out.println("order size: " + order.size());
                for(int i = 0; i < remaining.size(); ++i){
                    //System.out.println("i in loop: " + i);
                    if(remaining.get(i) < remaining.get(lowest)){
                        lowest = remaining.get(i);
                        //System.out.println("lowest = " + lowest);
                    }
                }
                //System.out.println("lowest outside for loop: " + lowest);
                newOrder.add(order.indexOf(lowest));
                remaining.remove(lowest);
                lowest = remaining.get(0);
                System.out.println("order after removal: " + remaining.toString());
            }

            System.out.println("new Order is: " + newOrder.toString());

            return newOrder;

        }catch (Exception e){
            System.out.println("Could not copy in decryption order! Exception: " + e.getMessage());
        }
        return null;
    }


    /*
    * for(int i = 0; i < cipherText.length(); ++i){
            int index = i%len;
            sb.append(cipher.get(index).charAt(0));
            cipher.set(index,cipher.get(index).substring(1, cipher.get(index).length()));
            //sb.append(cipher.get(this.chromosome.indexOf(i)));
        }
    *
    * */

    public String cheatDecrypt(Cipher cipher, int key){

        try{
        int len = Tools.len(key);
        StringBuilder sb = new StringBuilder();
        ArrayList<String> cheatCipher = cipher.encryptionSetup(len);
        for(int i = 0; i < cipher.cipherText.length(); ++i){
            int index = i%len;
            sb.append(cheatCipher.get(index).charAt(0));
            cheatCipher.set(index, cheatCipher.get(index).substring(1, cheatCipher.get(index).length()));
        }
        return sb.toString();
        }catch (Exception e){
            System.out.println("Nope!");
        }
        return null;
    }


    private ArrayList<String> decryptionSetup(String cipherText){
        int cipherLength = cipherText.length();
        int keyLength = this.chromosome.size();

        ArrayList<String> cipher = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < keyLength; ++i){ // setup first row
            sb.append(cipherText.charAt(i));
            cipher.add(sb.toString());
            sb.setLength(0);
        }
        if(keyLength < cipherLength) {
            for (int i = keyLength; i < cipherLength; ++i) {
                sb.append(cipher.get(i%keyLength));
                sb.append(cipherText.charAt(i));
                cipher.set(i % keyLength, sb.toString());
                sb.setLength(0);
            }
        }

       // System.out.println("Cipher in dec setup: " + cipher.toString());

        return cipher;
    }

    private double sumOfAllFits(ArrayList<ArrayList<NGram>> ngrams, int orderOfNgrams){

        double fitness = 0;
        orderOfNgrams = (orderOfNgrams > 5 ? 5: orderOfNgrams);

        for(int i = 0; i < ngrams.size()-(5-orderOfNgrams); ++i){
            fitness+= fitness(ngrams.get(i), decryption);
        }
        return fitness;
    }

    public double fitness(ArrayList<NGram> ngram, String cipherText){

        double fitness = 0;
        int order = ngram.get(0).order;
        String substr = "";

        for(int i = 0; i < cipherText.length()-order; ++i){
            substr = cipherText.substring(i,i+order);
            if(containsNGram(substr, ngram)){
                fitness+= order*ngram.get(findIndex(substr, ngram)).freq;
            }
            if(!containsNGram(substr, ngram) && order > 2){
                fitness-=1;
            }
        }
        return fitness;
    }

    public static int findIndex(String str, ArrayList<NGram> ngrams){
        for(int i = 0; i < ngrams.size(); ++i){
            if(ngrams.get(i).equals(str)){
                return i;
            }
        }
        return -1;
    }

    public static boolean containsNGram(String str, ArrayList<NGram> ngrams){
        for(int i = 0; i < ngrams.size(); ++i){
            if(ngrams.get(i).equals(str)){
                return true;
            }
        }
        return false;
    }


    public Decrypter breed(Decrypter partner){

        ArrayList<Integer> crossing = this.generateCrossing();
        ArrayList<Integer> child = new ArrayList<>();

        ArrayList<Integer> fromThis = new ArrayList<>();
        ArrayList<Integer> fromOther = new ArrayList<>();

        //System.out.println("this.size: " + this.chromosome.size());
        //System.out.println("partner.size: " + partner.chromosome.size());


        for(int i = 0; i < this.chromosome.size(); ++i){
            if(crossing.get(i) == 1){
                fromThis.add(this.chromosome.get(i));
            }else{
                fromOther.add(this.chromosome.get(i));
            }
        }

        ArrayList<Integer> order = otherOrder(fromOther, partner);

        for(int i = 0; i < crossing.size(); ++i){
            if(crossing.get(i) == 1){
                child.add(fromThis.get(0));
                fromThis.remove(0);
            }else{
                child.add(order.get(0));
                order.remove(0);
            }
        }
        return new Decrypter(child);
    }

    private static ArrayList<Integer> otherOrder(ArrayList<Integer> fromOther, Decrypter other){
        ArrayList<Integer> order = new ArrayList<>();
        for(int i = 0; i < other.chromosome.size(); ++i){
            if(fromOther.contains(other.chromosome.get(i))){
                order.add(other.chromosome.get(i));
            }
        }
        return order;
    }

    public ArrayList<Integer> generateCrossing(){
        ArrayList<Integer> crossing = new ArrayList<>();
            for (int i = 0; i < this.chromosome.size(); ++i) {
                crossing.add(ThreadLocalRandom.current().nextInt(0,2));
            }
            return crossing;
    }


    private ArrayList<Integer> crossArray(double key){

        double length = Tools.dlen(key);
        ArrayList<Integer> crossing = new ArrayList<>();
        for(int i = 0; i < length; ++i){
            crossing.add(i);
        }
        Collections.shuffle(crossing);
        return crossing;
    }

    public void print(){
        System.out.println("Chromosome: " + this.toString() + " resulting in decryption: " + this.decryption + " with a fitness of " + this.fitness);
    }

    private void printAttempt(){
        System.out.println("Decryption attempt: " + this.decryption);
    }


    @Override
    public boolean equals(Object o){
        if(o != null){
            //System.out.println("Equals: decrypter. this:" + this.toString() + " equals " + o.toString() + " is " + this.toString().equals(o.toString()));
            return this.toString().equals(o.toString());
        }
        return false;
    }
    @Override
    public int hashCode(){return Objects.hash(chromosome);}

    public String toString(){
        return chromosome.toString();
    }
}
