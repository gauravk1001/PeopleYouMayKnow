import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*class FriendMapper extends Mapper<LongWritable, Text, Text, Text>{
	
	//final IntWritable one = new IntWritable(1);
	//final IntWritable minusOne = new IntWritable(-1);
	Text one = new Text("1");
	Text minusOne = new Text("-1");
	Text pair = new Text();
	Text existingFriend = new Text();
	String [] userRow,friendList;
	int i,j;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		//System.out.println("in mapper, input "+ key + " " + value + ";");
		userRow = value.toString().split("\\s");
		if (userRow.length==1){
			return;
		}
		friendList = userRow[1].split(",");
		for (i=0; i<friendList.length-1; i++) {
			for(j=i+1; j<friendList.length; j++){
				pair.set(friendList[i] + "," +friendList[j]);
				context.write(pair, one);
				//System.out.println(pair + " " + one.toString());
			}
			
			existingFriend.set(userRow[0] + "," + friendList[i]);
			context.write(existingFriend, minusOne);
			//System.out.println(existingFriend + " " + minusOne.toString());
		}
	}
}*/

/*class FriendReducer extends Reducer<Text,Text,Text,Text> {
	
	private final IntWritable result = new IntWritable();
	private final ArrayList<HashMap<Integer, Integer>> suggestionList = new ArrayList<HashMap<Integer, Integer>>();
	HashMap<Integer, Integer> tmp;
	String [] pairStr;
	int [] pair = new int [2];
	private Text user = new Text();
	private Text suggestedFriend = new Text();
	int sum = 0;
	String finallist;
	
	public void reduce(Text key, Iterable<Text> values,  Context context) throws IOException, InterruptedException {
		
		for (Text val : values) {
			if(val.toString().equals("-1")) {
				sum=0;
				return;
			}
			sum += 1;
		}
			
		result.set(sum);
		
		pairStr=key.toString().split(",");
		pair[0]=Integer.parseInt(pairStr[0]);
		pair[1]=Integer.parseInt(pairStr[1]);
		
		
		
		try {
			//add 5->(9,20)
			tmp = suggestionList.get(pair[0]);
			tmp.put(pair[1], sum);
			suggestionList.set(pair[0], tmp);
			//sortByValue(tmp);
			
			//add 9->(5,20)
			tmp = suggestionList.get(pair[1]);
			tmp.put(pair[0], sum);
			suggestionList.set(pair[1], tmp);
			//sortByValue(tmp);
		} catch ( Exception e ) {
			suggestionList.add(pair[0], new HashMap<Integer,Integer>(pair[1], sum));
			suggestionList.add(pair[1], new HashMap<Integer,Integer>(pair[0], sum));
		}
		
		System.out.println("Reducer Parsed (" + pair[0] + "," + pair[1] + " - " +sum);
		
		//SortedSet<Integer> recomm = new TreeSet<Integer>(suggestionList.get(pair[0]).values());
		//user.set(Integer.toString(pair[0]));
		//suggestedFriend.set(Integer.toString(pair[1]) + "," + Integer.toString(sum));
		
		
		//context.write(pair[0], );
	}
	
	public void cleanup(Context context) throws IOException, InterruptedException {
		
		for ( HashMap<Integer, Integer> h: suggestionList) {
			sortByValue(h);
			finallist = new String("\t");
			for (Integer val: h.values()){
				finallist = finallist + val.toString() + ",";
			}
			user = new Text(Integer.toString(suggestionList.indexOf(h)));
			suggestedFriend = new Text(finallist);
			context.write(user, suggestedFriend);
			/*try {
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
/*		}
		
	}
*/
	/*public <Integer,Integer1 extends Comparable<? super Integer1>> HashMap<Integer1, Integer1> sortByValue( HashMap<Integer1, Integer1> map )
    {
    	List<Entry<Integer1, Integer1>> list = (List) new LinkedList<Entry<Integer1, Integer1>>( (Collection<? extends Entry<Integer1, Integer1>>) map.entrySet());
	    
    	Collections.sort( list, new Comparator<Map.Entry<Integer1, Integer1>>() {
	        @Override
	        public int compare( Map.Entry<Integer1, Integer1> o1, Map.Entry<Integer1, Integer1> o2 )
	        {
	            return (o1.getValue()).compareTo(o2.getValue() );
	        }
	    } );

        HashMap<Integer1, Integer1> result = new LinkedHashMap<Integer1, Integer1>();
        for (Entry<Integer1, Integer1> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    
    }
	
}
*/


public class PeopleYouMayKnow {
	
	public static void main(String ar[]) throws IOException, InterruptedException, ClassNotFoundException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Job started: " + dateFormat.format(date)); //2014/08/06 15:59:48
		
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "People_You_May_Know");
	    
	    job.setJarByClass(PeopleYouMayKnow.class);
	    job.setMapperClass(FriendMapper.class);
	    //job.setCombinerClass(FriendReducer.class);
	    job.setReducerClass(FriendReducer.class);
	    
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    
	    FileInputFormat.addInputPath(job, new Path(ar[0]));
	    FileOutputFormat.setOutputPath(job, new Path(ar[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
