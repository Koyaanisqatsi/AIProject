import java.io.*;
import java.util.ArrayList;

public class NGramReader {

    private BufferedReader inputFile;
    public ArrayList<NGram> ngrams;
    private int total;

    public NGramReader(BufferedReader inputFile){
        this.inputFile = inputFile;
        this.ngrams = new ArrayList<>();
        this.total = 0;
        try {
            //System.out.println("Trying to read file:" + this.inputFile.toString());
            this.readNGram();
        }catch (IOException ierr){
            System.out.println("Constructor: Unable to read file");
        }
    }

    public void readNGram() throws IOException{

        try{
            String line;
            long total = 0;
            while((line = this.inputFile.readLine()) != null){
                String[] tokens = line.split(" ");
                String ngram = tokens[0];
                int freq = Integer.valueOf(tokens[1]);
                this.ngrams.add(new NGram(ngram, freq));
                total+=freq;
            }
            this.inputFile.close();

            //System.out.println("Total number of words: " + total);

            for(int i = 0; i < this.ngrams.size(); ++i){
                this.ngrams.get(i).freq/=total/100;
            }
        }catch (IOException ierr){
            System.out.println("Something is wrong with the file");
        }
    }

    public void print(){
        System.out.println(this.ngrams.toString());
    }

    public ArrayList<NGram> getNGrams(){
        return this.ngrams;
    }
}
