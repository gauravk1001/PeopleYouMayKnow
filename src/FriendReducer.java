import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendReducer extends Reducer<Text,Text,IntWritable,Text> {
	
	//private final IntWritable result = new IntWritable();
	//private final ArrayList<HashMap<Integer, Integer>> suggestionList = new ArrayList<HashMap<Integer, Integer>>();
	HashMap<String, Integer> recommendedList;
	String [] pairStr;
	int [] pair = new int [2];
	//private Text user = new Text();
	//private Text suggestionList = new Text();
	List<String> inputVals;
	StringBuffer suggestionList = new StringBuffer();
	StringBuffer tmp = new StringBuffer();
	String recList= new String();
	IntWritable k;
	
	public void reduce(Text key, Iterable<Text> values,  Context context) throws IOException, InterruptedException {
		recommendedList = new HashMap<String, Integer>();
		suggestionList = new StringBuffer();
		tmp = new StringBuffer();
		//System.out.println("Kye in reducer " + key.toString());
		
		inputVals = new ArrayList<String>();
		for (Text val : values) {
			inputVals.add(val.toString());
			//System.out.println("getting vals here" + val);
		}
		//System.out.println("getting vals here" + inputVals);
		
		//loop for getting all the values and storing them in hashmap
		//after this loop, the hashmap has all the recommended friends in it
		for (String val : inputVals) {
			if(val.contains("-1")) {
				recommendedList.put(pairStr[0], new Integer(-1));
			}
			
			pairStr = val.toString().split(",");
			
			if(recommendedList.containsKey(pairStr[0])){
				if (recommendedList.get( pairStr[0]) != -1){
					recommendedList.put(pairStr[0],recommendedList.get(pairStr[0]) + 1);
				}
			}
			else {
				recommendedList.put(pairStr[0],1);
			}
			//System.out.println("in for "+val);
		}
		
		//sorting
		//suggestionList.append(key.toString());
		/*suggestionList.append("\t");
		for (Entry<String, Integer> e : recommendedList.entrySet()){
			//suggestionList.append(e.getKey());
			tmp.append("(").append(e.getKey()).append(",").append(e.getValue()).append(")");
		}
		suggestionList.append(tmp).append("\n");
		*/
		
		//call sortFriends
		recList = sortFriends(recommendedList);
		System.out.println(key + "\t" + recList);
		k = new IntWritable(Integer.parseInt(key.toString()));
		context.write(k, new Text(recList));
		
	}
	
	/*protected void cleanup(Context context)  throws IOException, InterruptedException {
		context.write(k,  new Text(recList));
	}*/
	
	
	public String sortFriends (HashMap a){
		Object[] recomm =  a.keySet().toArray();
		Object recommCount[] = a.values().toArray();
		//System.out.println("in sort, first value: " + recomm[0] + "\t" + recommCount[0]);
		
		//sort the arrays
		int i, j;
		Object temp, tempcount;
		StringBuffer returnlist = new StringBuffer();
		for(i=0; i<recommCount.length; i++) {
			for(j=0; j<recommCount.length-1 ; j++){
				if(Integer.parseInt(recommCount[j].toString()) < Integer.parseInt(recommCount[j+1].toString()) ) {
					//swap counts
					tempcount = recommCount[j+1];
					recommCount [j+1] = recommCount[j];
					recommCount [j] = tempcount;
					
					//swap the freinds
					temp = recomm[j+1];
					recomm [j+1] = recomm[j];
					recomm [j] = temp;
 				}
			}
		}
		//put the friends in a string
		returnlist.append("\t");
		for(i=0; i<10 && i<recomm.length; i++) {
			returnlist.append(recomm[i].toString()).append(",");
		}
		 
		
		
		return returnlist.toString();
	}
}
