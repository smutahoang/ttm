/****
 TwitterLDA without background topic 
*/

package hoang.topicmodel.model.twitterLDA;

import hoang.topicmodel.data.Tweet;
import hoang.topicmodel.data.User;
import hoang.larc.tooler.RankingTool;
import hoang.larc.tooler.WeightedElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;

public class TwitterLDA {
	//
	public String dataPath;
	public String outputPath;
	public int nTopics;

	public int burningPeriod;
	public int maxIteration;
	public int samplingGap;
	public int testBatch;

	public Random rand;
	// hyperparameters
	private double[] alpha;
	private double sum_alpha;
	private double[][] beta;
	private double[] sum_beta;
	// data
	private User[] users;
	private String[] tweetVocabulary;
	// parameters
	private double[][] tweetTopics;

	// Gibbs sampling variables
	// user - topic count
	private int[][] n_zu; // n_zu[k,u]: number of times topic z is observed in
							// tweets by user u
	private int[] sum_nzu;// sum_nzu[u]: total number of topics that are
							// observed
							// in tweets by user u
	// topic - word count
	private int[][] n_w;// n_w[w,z]: number of times word w is generated by a
						// topic z in all tweets
	private int[] sum_nw;// sum_nw[z]: total number of words that are generated
							// by a topic z in tweets

	private int[][] final_n_zu;
	private int[] final_sum_nzu;
	private int[][] final_n_w;
	private int[] final_sum_nw;

	private double tweetLogLikelidhood;
	private double tweetLogPerplexity;

	public void readData() {
		BufferedReader br = null;
		String line = null;
		HashMap<String, Integer> userId2Index = null;
		HashMap<Integer, String> userIndex2Id = null;
		// read tweet
		try {
			String folderName = dataPath + "/tweet/users";
			File tweetFolder = new File(folderName);
			// read number of users
			int nUser = tweetFolder.listFiles().length;
			users = new User[nUser];

			userId2Index = new HashMap<String, Integer>(nUser);
			userIndex2Id = new HashMap<Integer, String>(nUser);
			int u = -1;
			for (File tweetFile : tweetFolder.listFiles()) {
				u++;
				users[u] = new User();
				// index of the user
				String userId = FilenameUtils.removeExtension(tweetFile
						.getName());
				userId2Index.put(userId, u);
				userIndex2Id.put(u, userId);
				users[u].userID = userId;
				// read the tweet
				int nTweet = 0;
				br = new BufferedReader(new FileReader(
						tweetFile.getAbsolutePath()));
				while (br.readLine() != null) {
					nTweet++;
				}
				br.close();

				users[u].tweets = new Tweet[nTweet];

				br = new BufferedReader(new FileReader(
						tweetFile.getAbsolutePath()));
				int j = -1;
				while ((line = br.readLine()) != null) {
					j++;
					users[u].tweets[j] = new Tweet();
					String[] tokens = line.split(" ");
					users[u].tweets[j].tweetID = tokens[0];
					users[u].tweets[j].batch = Integer.parseInt(tokens[1]);
					users[u].tweets[j].words = new int[tokens.length - 2];
					for (int i = 0; i < tokens.length - 2; i++)
						users[u].tweets[j].words[i] = Integer
								.parseInt(tokens[i + 2]);
				}
				br.close();
			}

			// read tweet vocabulary
			String tweetVocabularyFileName = dataPath + "/tweet/vocabulary.txt";

			br = new BufferedReader(new FileReader(tweetVocabularyFileName));
			int nTweetWord = 0;
			while (br.readLine() != null) {
				nTweetWord++;
			}
			br.close();
			tweetVocabulary = new String[nTweetWord];

			br = new BufferedReader(new FileReader(tweetVocabularyFileName));
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(",");
				int index = Integer.parseInt(tokens[0]);
				tweetVocabulary[index] = tokens[1];
			}
			br.close();

		} catch (Exception e) {
			System.out.println("Error in reading tweet from file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void declareFinalCounts() {
		final_n_zu = new int[nTopics][users.length];
		final_sum_nzu = new int[users.length];
		for (int u = 0; u < users.length; u++) {
			for (int z = 0; z < nTopics; z++)
				final_n_zu[z][u] = 0;
			final_sum_nzu[u] = 0;
		}
		final_n_w = new int[tweetVocabulary.length][nTopics];
		final_sum_nw = new int[nTopics];
		for (int z = 0; z < nTopics; z++) {
			for (int w = 0; w < tweetVocabulary.length; w++)
				final_n_w[w][z] = 0;
			final_sum_nw[z] = 0;
		}
	}

	private void initilize() {
		// init coin and topic for each tweet and each behavior
		for (int u = 0; u < users.length; u++) {
			// tweet
			for (int j = 0; j < users[u].tweets.length; j++) {
				if (users[u].tweets[j].batch == testBatch)
					continue;
				users[u].tweets[j].topic = rand.nextInt(nTopics);
			}
		}
		// declare and initiate counting tables
		n_zu = new int[nTopics][users.length];
		sum_nzu = new int[users.length];
		for (int u = 0; u < users.length; u++) {
			for (int z = 0; z < nTopics; z++)
				n_zu[z][u] = 0;
			sum_nzu[u] = 0;
		}
		n_w = new int[tweetVocabulary.length][nTopics];
		sum_nw = new int[nTopics];
		for (int z = 0; z < nTopics; z++) {
			for (int w = 0; w < tweetVocabulary.length; w++)
				n_w[w][z] = 0;
			sum_nw[z] = 0;
		}
		// update counting tables
		for (int u = 0; u < users.length; u++) {
			// tweet
			for (int j = 0; j < users[u].tweets.length; j++) {
				if (users[u].tweets[j].batch == testBatch)
					continue;
				int z = users[u].tweets[j].topic;
				// user-topic and community-topic
				n_zu[z][u]++;
				sum_nzu[u]++;
				// tweet word - topic
				for (int i = 0; i < users[u].tweets[j].words.length; i++) {
					int w = users[u].tweets[j].words[i];
					n_w[w][z]++;
					sum_nw[z]++;
				}
			}
		}
	}

	// sampling
	private void setPriors() {
		// user topic prior
		alpha = new double[nTopics];
		sum_alpha = 0;
		for (int z = 0; z < nTopics; z++) {
			alpha[z] = 50.0 / nTopics;
			sum_alpha += alpha[z];
		}
		// topic tweet word prior
		beta = new double[nTopics][tweetVocabulary.length];
		sum_beta = new double[nTopics];
		for (int z = 0; z < nTopics; z++) {
			sum_beta[z] = 0;
			for (int w = 0; w < tweetVocabulary.length; w++) {
				beta[z][w] = 0.01;
				sum_beta[z] += beta[z][w];
			}
		}
	}

	private void sampleTweetTopic(int u, int t) {
		// sample the topic for tweet number t of user number u
		// get current topic
		int currz = users[u].tweets[t].topic;
		// sampling based on user interest
		n_zu[currz][u]--;
		sum_nzu[u]--;
		for (int i = 0; i < users[u].tweets[t].words.length; i++) {
			int w = users[u].tweets[t].words[i];
			n_w[w][currz]--;
			sum_nw[currz]--;
		}
		double sump = 0;
		double[] p = new double[nTopics];
		for (int z = 0; z < nTopics; z++) {
			p[z] = (n_zu[z][u] + alpha[z]) / (sum_nzu[u] + sum_alpha);
			for (int i = 0; i < users[u].tweets[t].words.length; i++) {
				int w = users[u].tweets[t].words[i];
				p[z] = p[z] * (n_w[w][z] + beta[z][w])
						/ (sum_nw[z] + sum_beta[z]);
			}
			// cummulative
			p[z] = sump + p[z];
			sump = p[z];
		}
		sump = rand.nextDouble() * sump;
		for (int z = 0; z < nTopics; z++) {
			if (sump > p[z])
				continue;
			users[u].tweets[t].topic = z;
			n_zu[z][u]++;
			sum_nzu[u]++;
			for (int i = 0; i < users[u].tweets[t].words.length; i++) {
				int w = users[u].tweets[t].words[i];
				n_w[w][z]++;
				sum_nw[z]++;
			}
			return;
		}
		System.out.println("bug in sampleTweetTopic");
		System.exit(-1);
	}

	private void updateFinalCounts() {
		for (int u = 0; u < users.length; u++) {
			for (int z = 0; z < nTopics; z++)
				final_n_zu[z][u] += n_zu[z][u];
			final_sum_nzu[u] += sum_nzu[u];
		}

		for (int z = 0; z < nTopics; z++) {
			for (int w = 0; w < tweetVocabulary.length; w++)
				final_n_w[w][z] += n_w[w][z];
			final_sum_nw[z] += sum_nw[z];
		}
	}

	private void gibbsSampling() {
		System.out.println("Runing Gibbs sampling");
		System.out.print("Setting prios ...");
		setPriors();
		System.out.println(" Done!");
		declareFinalCounts();
		System.out.print("Initializing ... ");
		initilize();
		System.out.println("... Done!");
		for (int i = 0; i < burningPeriod + maxIteration; i++) {
			System.out.print("iteration " + i);
			for (int u = 0; u < users.length; u++) {
				// tweet
				for (int t = 0; t < users[u].tweets.length; t++) {
					if (users[u].tweets[t].batch == testBatch)
						continue;
					sampleTweetTopic(u, t);
				}
			}
			System.out.println(" done!");
			if (samplingGap <= 0)
				continue;
			if (i < burningPeriod)
				continue;
			if ((i - burningPeriod) % samplingGap == 0) {
				updateFinalCounts();
			}
		}
		if (samplingGap <= 0)
			updateFinalCounts();
	}

	// inference
	private void inferingModelParameters() {
		// user
		for (int u = 0; u < users.length; u++) {
			// topic distribution
			users[u].topicDistribution = new double[nTopics];
			for (int z = 0; z < nTopics; z++) {
				users[u].topicDistribution[z] = (final_n_zu[z][u] + alpha[z])
						/ (final_sum_nzu[u] + sum_alpha);
			}
		}
		// tweet topics
		tweetTopics = new double[nTopics][tweetVocabulary.length];
		for (int z = 0; z < nTopics; z++) {
			for (int w = 0; w < tweetVocabulary.length; w++)
				tweetTopics[z][w] = (final_n_w[w][z] + beta[z][w])
						/ (final_sum_nw[z] + sum_beta[z]);
		}
	}

	public void learnModel() {
		gibbsSampling();
		inferingModelParameters();
		inferTweetTopic();
		getLikelihoodPerplexity();
	}

	private double getTweetLikelihood(int u, int t) {
		// compute likelihood of tweet number t of user number u
		double p = 0;
		for (int z = 0; z < nTopics; z++) {
			double p_z = 1;
			for (int i = 0; i < users[u].tweets[t].words.length; i++) {
				int w = users[u].tweets[t].words[i];
				p_z = p_z * tweetTopics[z][w];
			}
			p += p_z * users[u].topicDistribution[z];
		}
		double logLikelihood = Math.log10(p);
		return logLikelihood;
	}

	private void getLikelihoodPerplexity() {
		tweetLogLikelidhood = 0;
		tweetLogPerplexity = 0;
		int nTestTweet = 0;
		for (int u = 0; u < users.length; u++) {
			// tweet
			for (int t = 0; t < users[u].tweets.length; t++) {
				double logLikelihood = getTweetLikelihood(u, t);
				if (users[u].tweets[t].batch != testBatch)
					tweetLogLikelidhood += logLikelihood;
				else {
					tweetLogPerplexity += (-logLikelihood);
					nTestTweet++;
				}
			}
		}
		tweetLogPerplexity /= nTestTweet;
	}

	private void inferTweetTopic() {
		for (int u = 0; u < users.length; u++) {
			for (int t = 0; t < users[u].tweets.length; t++) {
				users[u].tweets[t].inferedTopic = 0;
				users[u].tweets[t].inferedLikelihood = 0;
				for (int i = 0; i < users[u].tweets[t].words.length; i++) {
					int w = users[u].tweets[t].words[i];
					users[u].tweets[t].inferedLikelihood += Math
							.log10(tweetTopics[0][w]);
				}
				users[u].tweets[t].inferedLikelihood += Math
						.log10(users[u].topicDistribution[0]);

				for (int z = 1; z < nTopics; z++) {
					double p_z = 0;
					for (int i = 0; i < users[u].tweets[t].words.length; i++) {
						int w = users[u].tweets[t].words[i];
						p_z += Math.log10(tweetTopics[z][w]);
					}
					p_z += Math.log10(users[u].topicDistribution[z]);

					if (users[u].tweets[t].inferedLikelihood < p_z) {
						users[u].tweets[t].inferedLikelihood = p_z;
						users[u].tweets[t].inferedTopic = z;
					}
				}
			}
		}
	}

	private void outputTweetTopics() {
		try {
			String fileName = outputPath + "/tweetTopics.csv";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (int z = 0; z < nTopics; z++) {
				bw.write("" + z);
				for (int w = 0; w < tweetVocabulary.length; w++)
					bw.write("," + tweetTopics[z][w]);
				bw.write("\n");
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error in writing out tweet topics to file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void outputTweetTopicTopWords(int k) {
		try {
			String fileName = outputPath + "/tweetTopicTopWords.csv";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (int z = 0; z < nTopics; z++) {
				bw.write(z + "\n");
				RankingTool rankTool = new RankingTool();
				WeightedElement[] topWords = rankTool.getTopKbyWeight(
						tweetVocabulary, tweetTopics[z], k);
				for (int j = 0; j < k; j++)
					bw.write("," + topWords[j].name + "," + topWords[j].weight
							+ "\n");
			}
			bw.close();
		} catch (Exception e) {
			System.out
					.println("Error in writing out tweet topic top words to file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void outputTweetTopicTopTweets(int k) {
		int[] tweetPerTopicCount = new int[nTopics];
		for (int z = 0; z < nTopics; z++)
			tweetPerTopicCount[z] = 0;
		for (int u = 0; u < users.length; u++) {
			for (int t = 0; t < users[u].tweets.length; t++) {
				if (users[u].tweets[t].batch == testBatch)
					continue;
				tweetPerTopicCount[users[u].tweets[t].inferedTopic]++;
			}
		}

		String[][] tweetID = new String[nTopics][];
		double[][] perTweetPerplexity = new double[nTopics][];
		for (int z = 0; z < nTopics; z++) {
			tweetID[z] = new String[tweetPerTopicCount[z]];
			perTweetPerplexity[z] = new double[tweetPerTopicCount[z]];
			tweetPerTopicCount[z] = 0;
		}

		for (int u = 0; u < users.length; u++) {
			for (int t = 0; t < users[u].tweets.length; t++) {
				if (users[u].tweets[t].batch == testBatch)
					continue;
				int z = users[u].tweets[t].inferedTopic;
				tweetID[z][tweetPerTopicCount[z]] = users[u].tweets[t].tweetID;
				perTweetPerplexity[z][tweetPerTopicCount[z]] = users[u].tweets[t].inferedLikelihood
						/ users[u].tweets[t].words.length;
				tweetPerTopicCount[z]++;
			}
		}

		try {
			String fileName = outputPath + "/tweetTopicTopTweets.csv";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));

			for (int z = 0; z < nTopics; z++) {
				bw.write(z + "\n");
				RankingTool rankTool = new RankingTool();
				WeightedElement[] topTweets = rankTool.getTopKbyWeight(
						tweetID[z], perTweetPerplexity[z], k);
				for (int j = 0; j < k; j++)
					bw.write("," + topTweets[j].name + ","
							+ topTweets[j].weight + "\n");
			}

			bw.close();
		} catch (Exception e) {
			System.out
					.println("Error in writing out tweet topic top tweets to file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void outputUserTopicDistribution() {
		try {
			String fileName = outputPath + "/userTopics.csv";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (int u = 0; u < users.length; u++) {
				bw.write("" + users[u].userID);
				for (int z = 0; z < nTopics; z++)
					bw.write("," + users[u].topicDistribution[z]);
				bw.write("\n");
			}
			bw.close();
		} catch (Exception e) {
			System.out
					.println("Error in writing out user topic distributions to file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void outputLikelihoodPerplexity() {
		try {
			String fileName = outputPath + "/likelihood-perplexity.csv";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			bw.write("tweetLogLikelihood,tweetLogPerplexity\n");
			bw.write("" + tweetLogLikelidhood + "," + tweetLogPerplexity);
			bw.close();
		} catch (Exception e) {
			System.out.println("Error in writing out tweets to file!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void outputAll() {
		outputTweetTopics();
		outputTweetTopicTopWords(20);
		outputTweetTopicTopTweets(200);
		outputUserTopicDistribution();
		outputLikelihoodPerplexity();
	}
}