import java.lang.Number.*;

public class Tools {


    public static int len(int n){
        return (n<100000)?((n<100)?((n<10)?1:2):(n<1000)?3:((n<10000)?4:5)):((n<10000000)?((n<1000000)?6:7):((n<100000000)?8:((n<1000000000)?9:10)));
    }

    public static double dlen(double n){
        return (n<100000)?((n<100)?((n<10)?1:2):(n<1000)?3:((n<10000)?4:5)):((n<10000000)?((n<1000000)?6:7):((n<100000000)?8:((n<1000000000)?9:10)));
    }

    public static long longLen(long n){
        return Long.toString(n).length();
    }


}
