import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Mapper.Context;

public class FriendMapper extends Mapper<LongWritable, Text, Text, Text>{
	
	//final IntWritable one = new IntWritable(1);
	//final IntWritable minusOne = new IntWritable(-1);
	Text one = new Text("1");
	Text minusOne = new Text("-1");
	Text keyUser = new Text();
	Text suggTuple = new Text();
	Text existingFriend = new Text();
	String [] userRow,friendList;
	int i,j;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		//System.out.println("in mapper, input "+ key + " " + value + ";");
		//userRow = null;
		userRow = value.toString().split("\\s");
		if (userRow.length==1){
			userRow = null;
			return;
		}
		//friendList = null;
		friendList = userRow[1].split(",");
		for (i=0; i<friendList.length; i++) {
			keyUser.set(new Text(friendList[i]));
			for(j=0; j<friendList.length; j++){
				if(j==i) {
					continue;
				}
				suggTuple.set(friendList[j] + ",1");
				context.write(keyUser, suggTuple);
				//System.out.println(keyUser + ",(" + suggTuple + ")");
			}
			existingFriend.set(userRow[0] + ",-1");
			context.write(keyUser, existingFriend);
			//System.out.println(keyUser + ",(" + existingFriend + ")");
			
		}
		
		/*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Mapper done at: " + dateFormat.format(date)); //2014/08/06 15:59:48*/
	}
}
