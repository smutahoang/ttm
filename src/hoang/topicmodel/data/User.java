package hoang.topicmodel.data;

public class User {
	public String userID;
	public String screenName;
	public double[] communityDistribution;
	public double[] realmDistribution;
	public double[] topicDistribution;
	public double[] communityBias;
	public double[] realmBias;
	// groundtruth for model checking
	public double[] trueCommunityDistribution;
	public double[] trueTopicDistribution;
	public double[] trueCommunityConformity;

	public double[] empiricalCommunityDistribution;
	public double[] empiricalTopicDistribution;
	public double[] empiricalCommunityConformity;
	//
	public Tweet[] tweets;
	public Tweet[] retweets; //for Qiu Minghui's BTwitterLDA 
	public Behavior[][] trainBehaviors;
	public Behavior[][] testBehaviors;
	public int batch;
	

}
