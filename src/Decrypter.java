import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Decrypter implements Comparable<Decrypter>{

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

    public void hack(ArrayList<ArrayList<NGram>> nGrams, String cipherText, int orderOfNGrams){
        this.decryption = this.decrypt(cipherText);
        this.fitness = sumOfAllFits(nGrams, orderOfNGrams);
        //this.printAttempt();
    }

    public Decrypter mutate(int generation){
        try {
            double chance = (1 + generation) * mutation;
            chance = (mutation > 0.8 ? 0.8 : chance);
            if (ThreadLocalRandom.current().nextDouble(1) < chance) { // swap
                int first = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                int second = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                //System.out.println("A mutation occured! Swapping two indices: " + first + " and " + second);
                while (second == first) {
                    second = ThreadLocalRandom.current().nextInt(this.chromosome.size());
                }
                int temp = (int)Cloner.deepCopy(this.chromosome.get(first));
                this.chromosome.set(first, this.chromosome.get(second));
                this.chromosome.set(second, temp);
            }
            if(ThreadLocalRandom.current().nextDouble(1) < chance) {
                int places = ThreadLocalRandom.current().nextInt(10);
                //System.out.println("A mutation occured! Shuffling chromosome " + places + " steps forward");
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

        ArrayList<String> cipher = decryptionSetup(cipherText);
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < cipherText.length(); ++i){
            int index = i%this.chromosome.size();
            sb.append(cipher.get(index).charAt(0));
            cipher.set(index, cipher.get(index).substring(1, cipher.get(index).length()));
        }
        return sb.toString();
    }

    private ArrayList<String> decryptionSetup(String cipherText){


        int cipherLen = cipherText.length();
        int keyLen = this.chromosome.size();

        StringBuilder sb = new StringBuilder(cipherText);

        int regularity = cipherLen%keyLen; // if regularity = 0, all columns are filled and of same length. Regular disposition cipher
        int fullColumns = (regularity == 0 ? keyLen : regularity);
        int partialColumns = keyLen - fullColumns; // == 0 if regular cipher, i.e 25 letters divided evenly on 5 rows for instance.

        ArrayList<Integer> readOrder = this.decryptionOrder(); // check key to see in which order the columns will arrive. Key = 39713 gives order: 30421
        ArrayList<Integer> partial = new ArrayList<>();
        // depending on partialColumns, one or more columns will have a lesser length than some, find which ones
        //System.out.println("Cipher length: " + cipherLen);
        //System.out.println("Key length: " + keyLen);
        //System.out.println("Regularity: " + regularity);
        //System.out.println("Full columns: " + fullColumns);
        //System.out.println("Partial columns: " + partialColumns);

        while(partial.size() < partialColumns) {
            int highest = -1;
            for (int i = 0; i < readOrder.size(); ++i) {
                if(readOrder.get(i) > highest && !partial.contains(readOrder.get(i))){
                    highest = readOrder.get(i);
                }
            }
            partial.add(highest);
        }

        //System.out.println("Indices with partial columns: " + partial.toString());

        ArrayList<String> cipher = new ArrayList<>();
        cipher.ensureCapacity(keyLen);
        while(cipher.size() < keyLen){
            cipher.add(null);
        }
        int readLen = cipherLen/keyLen;
        //System.out.println("ReadLength: " + readLen);

        for(int i = 0; i < readOrder.size(); ++i){

            //System.out.println("ReadOrder: " + readOrder.get(i));
            if(partial.contains(readOrder.get(i))){ // read partial column
               // System.out.println("Partial column detected, reading " + readLen + " letters");
                String toSet = sb.substring(0, readLen);
               // System.out.println("Reading partial column as " + toSet + " with length " + toSet.length());
                cipher.set(readOrder.get(i), toSet);
                sb.delete(0,readLen);
            }else if(regularity != 0){
               // System.out.println("Full column detected, reading " + keyLen + " letters");
                String toSet = sb.substring(0, readLen+1);
                //System.out.println("Reading full column as " + toSet + " with length " + toSet.length());
                cipher.set(readOrder.get(i), toSet);
                sb.delete(0,readLen+1);
            }
            else{
                //System.out.println("Regular cipher. Reading " + readLen + " letters for each column");
                String toSet = sb.substring(0, readLen);
                cipher.set(readOrder.get(i), toSet);
                sb.delete(0, readLen);
            }
        }

        //System.out.println("Resulting decryption: " + cipher.toString());

        return cipher;
    }


    private ArrayList<Integer> decryptionOrder(){
        try{
            ArrayList<Integer> key = Cloner.deepCopyIntegerArrayList(this.chromosome);
            ArrayList<Integer> order = new ArrayList<>();
            ArrayList<ArrayIndex> toSort = new ArrayList<>();

            for(int i = 0; i < key.size(); ++i){
                toSort.add(new ArrayIndex(key.get(i), i));
            }

            Collections.sort(toSort);

            for(int i = 0; i < toSort.size(); ++i){
                order.add(toSort.get(i).getIndex());
            }
            return order;

        }catch (Exception e){
            System.out.println("Could not copy in decryption order! Exception: " + e.getMessage());
        }
        return null;
    }

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
            else{
                fitness = (order > 2 ? fitness-(order-1) : fitness);
            }
        }
        //System.out.println("fitness: " + fitness);
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
    public boolean equals(Object o){ if(o != null){ return this.toString().equals(o.toString()); }return false; }
    @Override
    public int hashCode(){return Objects.hash(chromosome);}
    @Override
    public int compareTo(Decrypter b){
        return Double.compare(this.fitness, b.fitness);
    }
    public String toString(){
        return chromosome.toString();
    }
}
