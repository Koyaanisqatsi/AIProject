import javax.print.attribute.SupportedValuesAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class Cryptek {

    ArrayList<ArrayList<NGram>> NGrams;

    public Cryptek(){
        this.NGrams = loadNGrams();
    }


    public static void main(String args[]){

        // key: 41369
        Cipher c = new Cipher("the test text to decrypt", 39713,true);
       // System.out.println("Ciphertext before encryption: ");
       // c.print();
        c.encrypt();
        //System.out.println("Ciphertext after encryption: " );
        c.print();

        Cryptek cryptek = new Cryptek();
        ArrayList<Decrypter> population = populate(20);
        cryptek.runDecryption(population, 0, 2, c, 3);

        //Decrypter d = new Decrypter();
        //d.hack(cryptek.NGrams, c.cipherText, 3);
        //System.out.println("cheat decipher: " + d.cheatDecrypt(c, 39713));

        /*
        Decrypter d = new Decrypter();
        d.print();

        ArrayList<ArrayList<NGram>> ngrams = loadNGrams();

        d.hack(ngrams, c.cipherText);

        System.out.println("Decryption attempt of decrypter " + d.toString() + ": fitness =  " + d.fitness);

        */
    }


    public static ArrayList<ArrayList<NGram>> loadNGrams(){

        ArrayList<NGram> bigram = readNGram("english_bigrams").getNGrams();
        ArrayList<NGram> trigram = readNGram("english_trigrams").getNGrams();
        ArrayList<NGram> quadgram = readNGram("english_quadgrams").getNGrams();
        ArrayList<NGram> quintgram = readNGram("english_quintgrams").getNGrams();

        ArrayList<ArrayList<NGram>> ngrams = new ArrayList<>();

        ngrams.add(bigram);
        ngrams.add(trigram);
        ngrams.add(quadgram);
        ngrams.add(quintgram);

        return ngrams;
    }

    public static NGramReader readNGram(String fileName){
        try{
            File ngramfile = new File(System.getProperty("user.dir") + "/N-grams/" + fileName + ".txt");
            if(ngramfile.exists()){
                return new NGramReader(new BufferedReader(new FileReader(ngramfile)));
            }
        }catch (IOException ierr){
            System.out.println("Could not read file with path: " + fileName);
        }
        return null;
    }

    public static int generateKey(int keyLength){

        System.out.println("Generating key of length: " + keyLength);
        Random rnd = new Random();
        if(keyLength < 2){
            keyLength = 2;
        }
        int key = (int)(Math.pow(10, keyLength-1) + rnd.nextInt((int)Math.pow(10, keyLength-1)*9));
        System.out.println("Generated key: " + key);

        return (key > 0 ? key : generateKey(keyLength));
    }

    public static long generateLongKey(int keyLength){
        keyLength = (keyLength > 3 ? keyLength : 3);

        //System.out.println("keyLength in generation: " + keyLength);
        long key = (long)Math.pow(10, keyLength-1) + ThreadLocalRandom.current().nextLong((long)Math.pow(10, keyLength-1)*9);
        //long key = ThreadLocalRandom.current().nextLong((long)Math.pow(10, keyLength-1)*9);
        System.out.println("Generated long key: " + key);
        return key ;
    }

    public static ArrayList<Decrypter> populate(int populationSize){

        populationSize = (populationSize < 10 ? 10 : populationSize);

        int perKeyType = populationSize/10; // population of 100 means 10 keys of length 2..12

        //System.out.println("perKeyType: " + perKeyType);
        //System.out.println("populationSize: " + populationSize);



        ArrayList<Decrypter> population = new ArrayList<>();
        for(int i = 0; i < populationSize/perKeyType; ++i){
            for(int j = 0; j < perKeyType; ++j){
                population.add(new Decrypter( ((i%10)+3)));
            }

           // population.add(new Decrypter());
        }
        return population;
    }

    // TODO: run hack to get cipher attempts, fitness
    // TODO: normalize fitness to prepare for crossing
    // TODO: breed according to fitness distrubution, more fit are more likely to breed
    // TODO: setup new generation
    // TODO: run for X generations


    public void runDecryption(ArrayList<Decrypter> population, int generation, int totalGenerations, Cipher cipher, int orderOfNGrams){

        System.out.println("\n\nEntering generation: " + generation+"\n");

        // Current generation tries hacking
        for (int i = 0; i < population.size(); ++i) {
            population.get(i).hack(this.NGrams, cipher.cipherText, orderOfNGrams);
        }
        population = normalizeFitness(population, cipher, this.NGrams);

        if(generation < totalGenerations) {

            // Generate the next generation

            System.out.println("Generation " + generation + " has concluded. Preparing next");

            ArrayList<Decrypter> theNextGeneration = survivalOfTheFittest(population, generation, cipher);

            ArrayList<Decrypter> newArrivals = populate(population.size()/(1+totalGenerations-generation));
            theNextGeneration.addAll(newArrivals);

            System.out.println("\nSurvival of the fittest has concluded. Seeding next generation.\n");
            runDecryption(theNextGeneration, generation+1, totalGenerations,cipher, orderOfNGrams);
        }
        else{
        System.out.println("The program has run for its allotted generations.");
        System.out.println("The result is as follows: ");// + population.toString());

        for(int i = 0; i < population.size(); ++i){
            population.get(i).print();
        }
        }
    }


    private ArrayList<Decrypter> survivalOfTheFittest(ArrayList<Decrypter> population, int generation, Cipher cipher){

        //System.out.println("Preparing for the survival of the fittest. Normalizing fitness");
        //population = normalizeFitness(population, cipher, this.NGrams); //
        //System.out.println("Fitness normalized. Computing survival rate.");
        ArrayList<SurvivalDistribution> survival = survivalRate(population); // Calculates rate of survival depending on fitness

        // The longer this goes on, i.e the larger generation, the less breeding occurs. Generation 0 (init) starts with 100. When these breed, breed 50 times creating 100 children. Generation 2 creates ,

        ArrayList<Decrypter> theNextGeneration = new ArrayList<>();

        System.out.println("Preparations complete, finding candidates for breeding");

        System.out.println("Current generation has " + population.size() + " members");
        System.out.println("Elitist jerks always survive!");
        ArrayList<Decrypter> eliteChildren = elitistJerks(population);

        theNextGeneration.add(eliteChildren.get(0));
        theNextGeneration.add(eliteChildren.get(1));

        for(int i = 0; i < population.size()/(1+generation); ++i){
            ArrayList<Decrypter> parents = pickBreeders(survival);

            System.out.println("Parents found: " + parents.get(0).chromosome.toString() + " and " + parents.get(1).chromosome.toString() + " were found to be a match.");

            // TODO: breedSmaller, breedBigger. breed is for spouses of the same chromosome size

            ArrayList<Decrypter> children = breed(parents);
            theNextGeneration.add(children.get(0).mutate(generation));
            theNextGeneration.add(children.get(1).mutate(generation));
            System.out.println("The two resulting children are: " + theNextGeneration.get(0).chromosome.toString() + " and " + theNextGeneration.get(1).chromosome.toString());
        }

        System.out.println("Next generation will have " + theNextGeneration.size() + " members");

        // TODO: Mutations
        return theNextGeneration;
    }

    private ArrayList<Decrypter> breed(ArrayList<Decrypter> parents){

        ArrayList<Decrypter> children = new ArrayList<>();

        children.add(parents.get(0).breed(parents.get(1)));
        children.add(parents.get(1).breed(parents.get(0)));

        return children;
    }

    // the fittest ALWAYS survives, leave nothing to chance

    private ArrayList<Decrypter> elitistJerks(ArrayList<Decrypter> parents){

        ArrayList<Decrypter> eliteChildren = new ArrayList<>();

        double highestFitness = -10000;
        int bestParent = 0;
        int size = 0;

        for(int i = 0; i < parents.size(); ++i){
            if(highestFitness < parents.get(i).fitness){
                bestParent = i;
                size = parents.get(i).chromosome.size();
            }
        }

        int bestSpouse = 0;

        for(int i = 0; i < parents.size(); ++i){
            if(parents.get(i).chromosome.size() == size){
                if(i != bestParent){
                    bestSpouse = i;
                }
            }
        }

        eliteChildren.add(parents.get(bestParent).breed(parents.get(bestSpouse)));
        eliteChildren.add(parents.get(bestSpouse).breed(parents.get(bestParent)));

        return eliteChildren;
   }



    private ArrayList<Decrypter> pickBreeders(ArrayList<SurvivalDistribution> participants){
        try {
        ArrayList<Decrypter> partners = new ArrayList<>();
        Collections.sort(participants);

        ArrayList<SurvivalDistribution> copyOfParticipants = Cloner.deepCopySurvivalDistribution(participants);
        /*
        System.out.println("Participants in breeding: " );

        for(int i = 0; i < copyOfParticipants.size(); ++i){
            System.out.println(copyOfParticipants.get(i).decrypter.chromosome.toString());
        }
        */


        partners.add(getRandomDecrypter(copyOfParticipants).decrypter);
        //System.out.println("First lucky parent: " + partners.get(0).chromosome.toString());
        int iterations = 0;

        while(partners.size() < 2) {
            if(iterations > 100){
                System.out.println("Couldn't find a partner :(");
                System.out.println("But since i am nice, i am generating one!");
                partners.add(new Decrypter(partners.get(0).chromosome.size()));
            }

            SurvivalDistribution spouse = getRandomDecrypter(copyOfParticipants);
            //System.out.println("Possible spouse? : " + spouse.decrypter.chromosome.toString());
            //System.out.println("checking equals: " + partners.get(0).chromosome.toString() + " with possible partner: " +  spouse.decrypter.chromosome.toString());

            if (spouse.decrypter.chromosome.size() == partners.get(0).chromosome.size()) {
                if (!(partners.get(0).equals(spouse.decrypter))) {
                    //System.out.println("Found partner in: " + spouse.decrypter.chromosome.toString());
                    partners.add(spouse.decrypter);
                    return partners;
                }
            } else {
                //System.out.println("Spouse not of same length, removing from pool");
                copyOfParticipants.remove(spouse);
                //System.out.println("possible spouses from pool: " + copyOfParticipants.toString());
            }
            iterations++;
        }
            //System.out.println("Found two spouses!\n");
            return partners;

        }catch (Exception e){
            System.out.println("Could not copy participants :(");
        }
        return null;
    }

    private ArrayList<Decrypter> extractDecrypters(ArrayList<SurvivalDistribution> survivalDistributions){
        ArrayList<Decrypter> onlyDecrypters = new ArrayList<>();
        for(int i = 0; i < survivalDistributions.size(); ++i){
            onlyDecrypters.add(survivalDistributions.get(i).decrypter);
        }
        return onlyDecrypters;
    }

    private SurvivalDistribution getRandomDecrypter(ArrayList<SurvivalDistribution> participants){

        int pick = ThreadLocalRandom.current().nextInt(0, 100);

        double current = participants.get(0).survivalRate;
        double next = participants.get(1).survivalRate;

        for(int i = 0; i < participants.size()-1; ++i){

            if(current < pick && pick < current+next){
                return participants.get(i);
            }
            next+=current;
            current+=participants.get(i+1).survivalRate;
        }
        return participants.get(participants.size()-1); // i.e fitness == 100, done?
    }


    // Normalize fitting to be from 0 (worst) to 100 (true fitness of the real plainText)

    private static ArrayList<Decrypter> normalizeFitness(ArrayList<Decrypter> population, Cipher cipher, ArrayList<ArrayList<NGram>> ngrams){

        double trueMax = 0;
        //System.out.println("cipher plaintext:" + cipher.getPlainText());
        for(int i = 0; i < ngrams.size(); ++i) {
            trueMax += cipher.fitness(ngrams.get(i), cipher.getPlainText());
        }
       // System.out.println("trueMax: " + trueMax);
        double newMin = 0;
        double newMax = 100;

        double min = 1000;
        double max = abs(trueMax);

        for(int i = 0; i < population.size(); ++i){
            min = (population.get(i).fitness < min ? population.get(i).fitness: min);
           // max = (population.get(i).fitness > max ? population.get(i).fitness: max);
        }
        double scale = (newMax - newMin)/(max-min);
        double newFitness = 0;

        for(int i = 0; i < population.size(); ++i){
            newFitness = Math.round(((population.get(i).fitness-min)*scale)+newMin);
            population.get(i).updateFitness(newFitness);
        }
        return population;
    }

    private ArrayList<SurvivalDistribution> survivalRate(ArrayList<Decrypter> population){

        ArrayList<SurvivalDistribution> rates = new ArrayList<>();
        double freq = 0;
        for(int i = 0; i< population.size(); ++i){
            freq+= population.get(i).fitness;
        }
          double scale = freq/100d;

        //System.out.println("Scaling with " + scale);

        for(int i = 0; i < population.size(); ++i){
            rates.add(new SurvivalDistribution(population.get(i), Math.round(population.get(i).fitness/scale)));
            //System.out.println("Survival rate: " + rates.get(i).survivalRate);
        }
        return rates;
    }


}
