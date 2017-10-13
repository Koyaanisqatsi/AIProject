import java.util.Objects;

public class NGram {

   public String ngram;
   public double freq;
   public int order; // trivia

   public NGram(String ngram, double freq){
       this.ngram = ngram;
       this.freq = freq;
       this.order = ngram.length();
   }

   public String toString(){
       return this.ngram.toString() + " " + this.freq;
   }

   @Override
   public boolean equals(Object o){
       //System.out.println("equals: " + o.toString());
       if(o != null){
           return this.ngram.equals(o.toString());
       }
       return false;
   }

   @Override
    public int hashCode(){return Objects.hash(ngram);}
}
