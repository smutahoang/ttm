ttm
===
Topic models for microblogging content:
==================================
1. TwitterLDA
===============

1.1 Reference
===============
Comparing twitter and traditional media using topic models, Zhao et al., ECIR 2011

1.2 Input format
===============
+Each user's tweets are put in a file. The file is named by the user's user_id. Each line in the file has format:
<tweetid><space><label><w1><space><w2><space>...<space><wT>
where wj is index of a word in tweet vocabulary.
+All users' tweet files are put in "users" folder	
+Tweet vocabulary is put in "vocabulary.txt" file	
+ The "users" folder and the "vocabulary.txt" file are put in "tweet" folder
+ The path to 	"tweet" folder is input for the program

1.3 Output format
===============
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
+ File "userTopics.csv" contains users' topic distribution. Each line in the file is a topic. Format of a line:
<user_id>,<p_0>,...,<p_N>
where p_k is probability of the k-th topic

2. BehaviorLDA
==================================

3. Community Topic Behavior
==================================

4. Generalized Topic Model
==================================
