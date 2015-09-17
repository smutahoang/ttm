1. TwitterLDA
===============

1.1 Reference
=============
Comparing twitter and traditional media using topic models, Zhao et al., ECIR 2011

1.2 Input format
=============
+ Each user's tweets are put in a file. The file is named by the user's user_id. Each line in the file has format:
	<tweetid><space><label><w1><space><w2><space>...<space><wT>
where wj is index of a word in tweet vocabulary. The <label> is used for dividing tweets into batches for cross validation, or for labeling (part of) the tweets by pre-defined topics in semi-supervised learning (see STwitterLDA below)
+ All users' tweet files are put in "users" folder	
+ Tweet vocabulary is put in "vocabulary.txt" file	
+ The "users" folder and the "vocabulary.txt" file are put in "tweet" folder
+ The path to 	"tweet" folder is input for the program

1.3 Output format
=============
+ All output files are put in a given output folder
+ File "coinBias.csv" contains (global) bias of users toward choosing words from background topic. Format:
<bias to background topic><bias to personal topics> 
+ File "likelihood-perplexity.csv" contains likelihood and perplexity of the model
+ File "tweetTopics.csv" contains topics' word distribution. Each line in the file is a topic. Format of a line:
	<topic_number>,<p_0>,...,<p_N>
where p_j is probability of the j-th word in the tweet vocabulary
+ File "tweetTopicTopTweets.csv" contains top 20 tweets of the topic. Format:
	<topic_0>
	,<tweet_id>,<perplexity>
	...
	,<tweet_id>,<perplexity>
	<topic_1>
	,<tweet_id>,<perplexity>
	...
	,<tweet_id>,<perplexity>
	...
+ File "tweetTopicTopWords.csv" contains top 20 words of the topic. Format:
	<topic_0>
	,<word>,<perplexity>
	...
	,<word>,<perplexity>
	<topic_1>
	,<word>,<perplexity>
	...
	,<word>,<perplexity>
	...
+ File "userTopics.csv" contains users' topic distribution. Each line in the file is a user. Format of a line:
	<user_id>,<p_0>,...,<p_N>
where p_k is probability of the k-th topic

1.4 Variants
===============
+ STwitterLDA: TwitterLDA with semi-supervised learning: part of tweets are labeled by pre-defined topics. The <label> in tweets input are topic_id or -1 if the tweets are not labeled. 
+ NBTwitterLDA: TwitterLDA without background topic.


2. BehaviorLDA
==================================

2.1 Reference
=============
It is not just what we say, but how we say them: LDA-based behavior-topic model, Qiu et al., SDM 2013

2.2 Input format
=============
+ Each user's tweets are put in a file. The file is named by the user's user_id. Each line in the file has format:
	<tweetid><space><label><w1><space><w2><space>...<space><wT>
where wj is index of a word in tweet vocabulary. The <label> is used for dividing tweets into batches for cross validation
+ Each user's retweets are put in a file with the same format with the tweet files
+ All users' tweet files are put in "tweets" folder, and all users' retweets are put in "retweets" folder
	
+ Tweet vocabulary is put in "vocabulary.txt" file	
+ The "tweets", "retweets" folders and the "vocabulary.txt" file are put in "data" folder
+ The path to the "data" folder is input for the program

2.3 Output format
=============
+ All output files are put in a given output folder
+ File "coinBias.csv" contains (global) bias of users toward choosing words from background topic. Format:
	<bias to background topic><bias to personal topics> 
+ File "likelihood-perplexity.csv" contains likelihood and perplexity of the model
+ File "behaviorBias.csv" contains (global) bias of users toward tweeting or retweeting specific to topics. Each line is a topic. Format:
	<topic_id><bias to tweeting><bias to retweeting>
+ File "tweetTopics.csv" contains topics' word distribution. Each line in the file is a topic. Format of a line:
<topic_number>,<p_0>,...,<p_N>
where p_j is probability of the j-th word in the tweet vocabulary
+ File "tweetTopicTopTweets.csv" contains top 20 tweets of the topic. Format:
<topic_0>
,<tweet_id>,<perplexity>
...
,<tweet_id>,<perplexity>
<topic_1>
,<tweet_id>,<perplexity>
...
,<tweet_id>,<perplexity>
...
+ File "tweetTopicTopWords.csv" contains top 20 words of the topic. Format:
<topic_0>
,<word>,<perplexity>
...
,<word>,<perplexity>
<topic_1>
,<word>,<perplexity>
...
,<word>,<perplexity>
...
+ File "userTopics.csv" contains users' topic distribution. Each line in the file is a user. Format of a line:
<user_id>,<p_0>,...,<p_N>
where p_k is probability of the k-th topic

3. Generalized Topic Model
==================================
3.1 Reference
=============
Modeling Topics and Behaviors of Microbloggers: An Integrated Approach, Hoang et al., Preprint

2.2 Input format
=============
+ Each user's tweets are put in a file. The file is named by the user's user_id. Each line in the file has format:
	<tweetid><space><label><w1><space><w2><space>...<space><wT>
where wj is index of a word in tweet vocabulary. The <label> is used for dividing tweets into batches for cross validation
+ All users' tweet files are put in "users" folder	
+ Tweet vocabulary is put in "vocabulary.txt" file	
+ The "users" folder and the "vocabulary.txt" file are put in "tweets" folder
+ For each type of behavior (e.g., hashtag, mention), the users' behavior adoptions are put in "user.txt" file. Each line is a user. Format of a line:
	<user_id><space><b1:number of adoptions of b1><space>...<space><bM:number of adoptions of bM>
where bj is index of the behavior in the behavior vocabulary. The behavior vocabulary is put in "vocabulary.txt" file. The "user.txt" and "vocabulary.txt" files are put in folder named by behavior type
+ "tweets" and all behavior folders are put in "data" folder
+ The path to the "data" folder is input for the program

2.3 Output format
=============
+ All output files are put in a given output folder
+ File "likelihood-perplexity.csv" contains likelihood and perplexity of the model
+ File "tweetTopics.csv" contains topics' word distribution. Each line in the file is a topic. Format of a line:
<topic_number>,<p_0>,...,<p_N>
where p_j is probability of the j-th word in the tweet vocabulary
+ File "tweetTopicTopTweets.csv" contains top 20 tweets of the topic. Format:
<topic_0>
,<tweet_id>,<perplexity>
...
,<tweet_id>,<perplexity>
<topic_1>
,<tweet_id>,<perplexity>
...
,<tweet_id>,<perplexity>
...
+ File "tweetTopicTopWords.csv" contains top 20 words of the topic. Format:
<topic_0>
,<word>,<perplexity>
...
,<word>,<perplexity>
<topic_1>
,<word>,<perplexity>
...
,<word>,<perplexity>
...
+ For each type of behavior, topics' behavior distribution and top behaviors are put in "<behavior_name>Topics.csv" and "<behavior_name>TopicTopWords.csv" similar to topics' word distribution and top words files.
+ File "userRealms.csv" contains users' bias toward realms and their realm distribution. Each line in the file is a user. Format of a line:
<user_id>,<bias to personal interest>,<bias toward realms>,<p_0>,...,<p_N>
where p_r is probability of the r-th realm
+ File "userTopics.csv" contains users' topic distribution. Each line in the file is a user. Format of a line:
<user_id>,<p_0>,...,<p_N>
where p_k is probability of the k-th topic

