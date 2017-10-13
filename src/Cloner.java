import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Cloner {
	
	private Cloner(){
		
	}
	
	public static Object deepCopy(Object object) throws Exception {
		
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		
		try{
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			
			return ois.readObject();
					
		}catch(Exception e){
			System.out.println("Exception during cloning: " + e);
		}
		finally{
			oos.close();
			ois.close();
		}
		return null;
	}

    public static ArrayList<Object> deepCopyArrayList(ArrayList<Object> objectArray) throws Exception{
	    ArrayList<Object> copy = new ArrayList<Object>();
	    for(int i=0;i<objectArray.size();++i){
	        Object obj = deepCopy(objectArray.get(i));
	        copy.add(obj);
        }
        return copy;
    }

    public static ArrayList<Integer> deepCopyIntegerArrayList(ArrayList<Integer> intArrayList) throws Exception{
	    ArrayList<Integer> copy = new ArrayList<Integer>();
	    for(int i = 0; i<intArrayList.size();++i){
	        copy.add((int)deepCopy(intArrayList.get(i)));
        }
        return copy;
    }
    public static ArrayList<SurvivalDistribution> deepCopySurvivalDistribution(ArrayList<SurvivalDistribution> survivalDistributionArrayList){
	    try{
	        ArrayList<SurvivalDistribution> copy = new ArrayList<>();
	        for(int i = 0; i < survivalDistributionArrayList.size(); ++i){
	            copy.add(survivalDistributionArrayList.get(i));
            }
            return copy;
        }catch (Exception e){
	        System.out.println("Was not able to copy ArrayList<SurvivalDistribution>");
        }
        return null;
    }
}
