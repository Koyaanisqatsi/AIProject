import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

public class Cipher implements Serializable{


    /*input: string ciphertext as text to be encrypted
    *
    * int key is either keylength or the actual key depending on the boolean isKey input
    * if isKey == false, produce a random key with length keyLength
    * else key == keyLength
    *
    * */

    private String plainText;
    private long key;
    public String cipherText;

    public Cipher(String plainText, int keyLength, boolean isKey){

        this.plainText = plainText.toUpperCase();
        if(isKey){
            this.key = keyLength;
        }else{
            this.key = Cryptek.generateLongKey(keyLength);
        }
        this.cipherText = "";
    }

    public int keyLength(){
        return (int)Tools.longLen(this.key);
    }

    // For test purposes only

    public String getPlainText(){
        return this.plainText;
    }

    public double fitness(ArrayList<NGram> ngram, String cipherText){

        double fitness = 0;
        int order = ngram.get(0).order;
        String substr = "";

        for(int i = 0; i < cipherText.length()-order; ++i){
            substr = cipherText.substring(i,i+order);
            if(Decrypter.containsNGram(substr, ngram)){
                fitness+= (order-1)*(order-1)*ngram.get(Decrypter.findIndex(substr, ngram)).freq;
            }
            if(!Decrypter.containsNGram(substr, ngram) && order > 2){
                fitness-=order;
            }
        }
        return fitness;
    }



    private void setCipherText(String newNext){
        this.cipherText = newNext.toUpperCase();
    }

    public void encrypt(){
        ArrayList<String> plaintext = this.encryptionSetup(this.keyLength());
        //System.out.println("Cipher in encrypt: " + plaintext.toString());
        String ciphertext = transposition(plaintext);
        this.setCipherText(ciphertext);
    }

    private String transposition(ArrayList<String> plaintext){

        String ciphertext = "";

        ArrayList<Integer> indices = transpositionIndices();

      //  System.out.println("Indices: " + indices.toString());

        StringBuilder sb = new StringBuilder(ciphertext);

        for(int i = 0; i < indices.size(); ++i){
            sb.append(plaintext.get(indices.get(i)));
        }
        return sb.toString();
    }

    private ArrayList<Integer> transpositionIndices(){
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for(int i = 0; i<this.keyLength();++i){
            indices.add(Character.getNumericValue(String.valueOf(this.key).charAt(i)));
        }
        return accessOrder(indices);
    }

    public ArrayList<Integer> accessOrder(ArrayList<Integer> indices){

        try{
        ArrayList<ArrayIndex> sorted = accessIndex(indices);
        ArrayList<Integer> access = new ArrayList<Integer>();
        Collections.sort(sorted);


        for(int i = 0; i<sorted.size();++i){
            access.add(sorted.get(i).getIndex());
        }

        return access;}
        catch (Exception e){
            System.out.println("Something went wrong, unable to copy array");
            return null;
        }
    }

    /*Input: ArrayList representing the key
    * Output: ArrayList of type <Integer, Integer> to help map identical elements to their original index
    * */


    private ArrayList<ArrayIndex> accessIndex(ArrayList<Integer> key){

        try{
        ArrayList<ArrayIndex> indexArray = new ArrayList<ArrayIndex>();

        for(int i = 0; i < key.size();++i){
            if(!indexArray.contains(key.get(i))){
                indexArray.add(new ArrayIndex((int)Cloner.deepCopy(key.get(i)), i));
            }
        }
        return indexArray;}
        catch (Exception e){
            System.out.println("Couldn't clone :(");
            return null;
        }
    }


    private ArrayList<String> initCipher(int keyLength){
        ArrayList<String> cipher = new ArrayList<String>();
        for(int i = 0; i<keyLength;++i){
            cipher.add("");
        }
        return cipher;
    }

    public ArrayList<String> encryptionSetup(int keyLength){
        int cipherLength = this.plainText.length();

        ArrayList<String> cipher = initCipher(keyLength);

       //System.out.println("cipher in setup: " + cipher.toString());

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < cipherLength; ++i){
            sb.append(cipher.get(i%keyLength).toString());
            sb.append(this.plainText.charAt(i));
            cipher.set(i%keyLength, sb.toString());
            sb.setLength(0);
        }
        return cipher;
    }

    public void print(){
        System.out.println(this.toString());
    }

    public String toString(){
        return "PlainText: " + this.plainText + " \nCipherText: " + this.cipherText;
    }












































}
