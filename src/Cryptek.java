import java.util.Random;

public class Cryptek {


    public static void main(String args[]){

        // key: 41369
        Cipher c = new Cipher("pyramids and stargates", 39713,true);
        System.out.println("Ciphertext before encryption: ");
        c.print();
        c.encrypt();
        System.out.println("Ciphertext after encryption: " );
        c.print();

        Decrypter d = new Decrypter();
        d.print();







    }

    public static int generateKey(int keyLength){

        System.out.println("Generating key of length: " + keyLength);
        Random rnd = new Random();
        int key = (int)(Math.pow(10, keyLength-1) + rnd.nextInt((int)Math.pow(10, keyLength-1)*9));
        System.out.println("Generated key: " + key);
        return key;

    }



}
