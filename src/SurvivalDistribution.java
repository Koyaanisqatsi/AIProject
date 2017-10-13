public class SurvivalDistribution implements Comparable<SurvivalDistribution>{

    Decrypter decrypter;
    double survivalRate;

    public SurvivalDistribution(Decrypter decrypter, double survivalRate){
        this.decrypter = decrypter;
        this.survivalRate = survivalRate;
    }

    @Override
    public int compareTo(SurvivalDistribution b){
        return Double.compare(this.survivalRate, b.survivalRate);
    }

}
