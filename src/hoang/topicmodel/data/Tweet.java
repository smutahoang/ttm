package hoang.topicmodel.data;

public class Tweet {
	public String tweetID;
	// public String userID;
	public int[] words;
	public int[] coins;// for TwitterLDA with background topic	
	//
	public int topic;
	public int inferedTopic;
	public boolean isTopicFixed;
	public int fixedTopic;// for STwitterLDA
	//		
	public int batch;
	public double inferedLikelihood;
}
