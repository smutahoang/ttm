package hoang.topicmodel.data;

public class User {
	public String userID;
	public String screenName;	
	public double[] topicDistribution;	
	
	public Tweet[] tweets;
	public Tweet[] retweets; //for Qiu Minghui's BTwitterLDA 

}
